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
package org.jooq.meta;

public class DefaultArrayDefinition extends AbstractDefinition implements ArrayDefinition {

    private final DataTypeDefinition     definedType;
    private transient DataTypeDefinition type;
    private transient DataTypeDefinition resolvedType;

    public DefaultArrayDefinition(SchemaDefinition schema, String name, DataTypeDefinition type) {
        this(schema, null, name, type);
    }

    public DefaultArrayDefinition(SchemaDefinition schema, PackageDefinition pkg, String name, DataTypeDefinition type) {
        super(schema.getDatabase(), schema, pkg, name, "", null);

        this.definedType = type;
    }

    @Override
    public DataTypeDefinition getElementType() {
        if (type == null) {
            type = AbstractTypedElementDefinition.mapDefinedType(this, this, definedType, null);
        }

        return type;
    }

    @Override
    public DataTypeDefinition getElementType(JavaTypeResolver resolver) {
        if (resolvedType == null) {
            resolvedType = AbstractTypedElementDefinition.mapDefinedType(this, this, definedType, resolver);
        }

        return resolvedType;
    }
}
