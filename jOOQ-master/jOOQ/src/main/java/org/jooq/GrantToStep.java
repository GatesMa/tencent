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
package org.jooq;

// ...
// ...
// ...
import static org.jooq.SQLDialect.DERBY;
import static org.jooq.SQLDialect.H2;
import static org.jooq.SQLDialect.HSQLDB;
import static org.jooq.SQLDialect.MARIADB;
// ...
import static org.jooq.SQLDialect.MYSQL;
// ...
import static org.jooq.SQLDialect.POSTGRES;
// ...

/**
 * The step in the creation of a <code>GRANT</code> statement where the
 * <code>TO</code> clause can be added.
 *
 * @author Timur Shaidullin
 * @author Lukas Eder
 */
public interface GrantToStep {

    /**
     * Grant a privilege to a user.
     */
    @Support({ DERBY, H2, HSQLDB, MARIADB, MYSQL, POSTGRES })
    GrantWithGrantOptionStep to(User user);

    /**
     * Grant a privilege to a role.
     */
    @Support({ DERBY, H2, HSQLDB, MARIADB, MYSQL, POSTGRES })
    GrantWithGrantOptionStep to(Role role);

    /**
     * Grant a privilege to <code>PUBLIC</code>.
     */
    @Support({ DERBY, H2, HSQLDB, MARIADB, MYSQL, POSTGRES })
    GrantWithGrantOptionStep toPublic();
}
