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
package org.jooq.impl;

import org.jooq.DiagnosticsContext;
import org.jooq.DiagnosticsListener;

/**
 * A publicly available default implementation of {@link DiagnosticsListener}.
 * <p>
 * Use this to stay compatible with future API changes (i.e. added methods to
 * <code>DiagnosticsListener</code>)
 *
 * @author Lukas Eder
 */
public class DefaultDiagnosticsListener implements DiagnosticsListener {

    @Override
    public void tooManyRowsFetched(DiagnosticsContext ctx) {}

    @Override
    public void tooManyColumnsFetched(DiagnosticsContext ctx) {}

    @Override
    public void unnecessaryWasNullCall(DiagnosticsContext ctx) {}

    @Override
    public void missingWasNullCall(DiagnosticsContext ctx) {}

    @Override
    public void duplicateStatements(DiagnosticsContext ctx) {}

    @Override
    public void repeatedStatements(DiagnosticsContext ctx) {}

}
