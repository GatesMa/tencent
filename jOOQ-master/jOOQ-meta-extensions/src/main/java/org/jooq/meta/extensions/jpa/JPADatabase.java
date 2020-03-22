/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.meta.extensions.jpa;

import static org.jooq.tools.StringUtils.defaultIfBlank;
import static org.jooq.tools.StringUtils.isBlank;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.AttributeConverter;
import javax.persistence.Entity;

import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.impl.JPAConverter;
import org.jooq.meta.extensions.AbstractInterpretingDatabase;
import org.jooq.meta.h2.H2Database;
import org.jooq.meta.jaxb.ForcedType;
import org.jooq.tools.JooqLogger;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * The JPA database.
 * <p>
 * This jOOQ-meta schema source works on an undisclosed in-memory
 * {@link H2Database}, which is constructed from a set of JPA-annotated entities
 * using Spring and Hibernate:
 * <p>
 * <ul>
 * <li>Spring discovers all the JPA-annotated entities in the comma-separated
 * list of <code>packages</code> (configured in the code generator)</li>
 * <li>Those entities are passed to Hibernate's {@link SchemaExport} to generate
 * an empty database schema in the in-memory H2 database</li>
 * <li>A jOOQ {@link H2Database} is used to reverse-engineer this schema
 * again</li>
 * </ul>
 *
 * @author Lukas Eder
 */
public class JPADatabase extends AbstractInterpretingDatabase {

    static final String     HIBERNATE_DIALECT = SQLDialect.H2.thirdParty().hibernateDialect();
    static final JooqLogger log               = JooqLogger.getLogger(JPADatabase.class);

    Map<String, Object>     userSettings      = new HashMap<>();

    @Override
    protected void export() throws Exception {
        String packages = getProperties().getProperty("packages");

        if (isBlank(packages)) {
            packages = "";
            log.warn("No packages defined", "It is highly recommended that you provide explicit packages to scan");
        }

        // [#9058] Properties use camelCase notation.
        boolean useAttributeConverters = Boolean.valueOf(
            getProperties().getProperty("useAttributeConverters",
                getProperties().getProperty("use-attribute-converters", "true")
            )
        );


        // [#6709] Apply default settings first, then allow custom overrides
        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put("hibernate.dialect", HIBERNATE_DIALECT);
        settings.put("javax.persistence.schema-generation-connection", connection());
        settings.put("javax.persistence.create-database-schemas", true);

        // [#5607] JPADatabase causes warnings - This prevents them
        settings.put(AvailableSettings.CONNECTION_PROVIDER, connectionProvider());

        for (Entry<Object, Object> entry : getProperties().entrySet()) {
            String key = "" + entry.getKey();

            if (key.startsWith("hibernate.") || key.startsWith("javax.persistence."))
                userSettings.put(key, entry.getValue());
        }
        settings.putAll(userSettings);

        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(settings);

        MetadataSources metadata = new MetadataSources(builder.applySettings(settings).build());

        ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(true);

        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

        // [#5845] Use the correct ClassLoader to load the jpa entity classes defined in the user project
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        for (String pkg : packages.split(","))
            for (BeanDefinition def : scanner.findCandidateComponents(defaultIfBlank(pkg, "").trim()))
                metadata.addAnnotatedClass(Class.forName(def.getBeanClassName(), true, cl));

        // This seems to be the way to do this in idiomatic Hibernate 5.0 API
        // See also: http://stackoverflow.com/q/32178041/521799
        // SchemaExport export = new SchemaExport((MetadataImplementor) metadata.buildMetadata(), connection);
        // export.create(true, true);

        // Hibernate 5.2 broke 5.0 API again. Here's how to do this now:
        SchemaExport export = new SchemaExport();
        export.create(EnumSet.of(TargetType.DATABASE), metadata.buildMetadata());

        if (useAttributeConverters)
            loadAttributeConverters(metadata.getAnnotatedClasses());
    }

    @SuppressWarnings("serial")
    ConnectionProvider connectionProvider() {
        return new ConnectionProvider() {
            @SuppressWarnings("rawtypes")
            @Override
            public boolean isUnwrappableAs(Class unwrapType) {
                return false;
            }
            @Override
            public <T> T unwrap(Class<T> unwrapType) {
                return null;
            }
            @Override
            public Connection getConnection() {
                return JPADatabase.this.connection();
            }
            @Override
            public void closeConnection(Connection conn) {}

            @Override
            public boolean supportsAggressiveRelease() {
                return true;
            }
        };
    }

    private final void loadAttributeConverters(Collection<? extends Class<?>> classes) {
        try {
            AttributeConverterExtractor extractor = new AttributeConverterExtractor(this, classes);

            attributesLoop:
            for (Entry<Name, AttributeConverter<?, ?>> entry : extractor.extract().entrySet()) {
                Class<?> convertToEntityAttribute = null;

                for (Method method : entry.getValue().getClass().getMethods()) {
                    if (!method.isBridge() && "convertToEntityAttribute".equals(method.getName())) {
                        convertToEntityAttribute = method.getReturnType();
                        break;
                    }
                }

                if (convertToEntityAttribute == null) {
                    log.info("AttributeConverter", "Cannot use AttributeConverter: " + entry.getValue().getClass().getName());
                    continue attributesLoop;
                }

                // Tables can be fully or partially or not at all qualified. Let's just accept any prefix
                // to the available qualification
                String regex = "(.*?\\.)?" + entry.getKey().unquotedName().toString().replace(".", "\\.");
                ForcedType forcedType = new ForcedType()
                    .withIncludeExpression("(?i:" + regex + ")")
                    .withUserType(convertToEntityAttribute.getName())
                    .withConverter(String.format("new %s(%s.class)",
                        JPAConverter.class.getName(),
                        entry.getValue().getClass().getName()
                    ));

                log.info("AttributeConverter", "Configuring JPA AttributeConverter: " + forcedType);
                getConfiguredForcedTypes().add(forcedType);
            }
        }

        // AttributeConverter is part of JPA 2.1. Older JPA providers may not have this type, yet
        catch (NoClassDefFoundError e) {
            log.warn("AttributeConverter", "Cannot load AttributeConverters due to missing class: " + e.getMessage());
        }
    }
}
