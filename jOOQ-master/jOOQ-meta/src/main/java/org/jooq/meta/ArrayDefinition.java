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


/**
 * A definition for an ARRAY type
 *
 * @author Lukas Eder
 */
public interface ArrayDefinition extends Definition {

    /**
     * The array's package. <code>null</code> if the UDT is not in a package
     */
    PackageDefinition getPackage();

    /**
     * @return The type of the ARRAY's elements
     */
    DataTypeDefinition getElementType();

    /**
     * @return The type of the ARRAY's elements
     */
    DataTypeDefinition getElementType(JavaTypeResolver resolver);

}
