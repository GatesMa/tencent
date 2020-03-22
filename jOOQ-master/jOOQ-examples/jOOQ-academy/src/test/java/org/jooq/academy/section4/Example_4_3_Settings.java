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
package org.jooq.academy.section4;

import static java.lang.System.out;
import static org.jooq.SQLDialect.H2;
import static org.jooq.example.db.h2.Tables.AUTHOR;
import static org.jooq.impl.DSL.using;

import org.jooq.Select;
import org.jooq.academy.tools.Tools;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.MappedTable;
import org.jooq.conf.RenderKeywordCase;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import org.junit.Test;

public class Example_4_3_Settings {

    @Test
    public void run() {
        Select<?> select =
        DSL.select()
           .from(AUTHOR)
           .where(AUTHOR.ID.eq(3));

        Tools.title("A couple of settings at work - Formatting");
        out.println(using(H2, new Settings().withRenderFormatted(false)).render(select));
        out.println(using(H2, new Settings().withRenderFormatted(true)).render(select));

        Tools.title("A couple of settings at work - Schema");
        out.println(using(H2, new Settings().withRenderSchema(false)).render(select));
        out.println(using(H2, new Settings().withRenderSchema(true)).render(select));

        Tools.title("A couple of settings at work - Name case");
        out.println(using(H2, new Settings().withRenderNameCase(RenderNameCase.AS_IS)).render(select));
        out.println(using(H2, new Settings().withRenderNameCase(RenderNameCase.UPPER)).render(select));
        out.println(using(H2, new Settings().withRenderNameCase(RenderNameCase.LOWER)).render(select));

        Tools.title("A couple of settings at work - Name quoting");
        out.println(using(H2, new Settings().withRenderQuotedNames(RenderQuotedNames.ALWAYS)).render(select));
        out.println(using(H2, new Settings().withRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_QUOTED)).render(select));
        out.println(using(H2, new Settings().withRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED)).render(select));
        out.println(using(H2, new Settings().withRenderQuotedNames(RenderQuotedNames.NEVER)).render(select));

        Tools.title("A couple of settings at work - Keyword case");
        out.println(using(H2, new Settings().withRenderKeywordCase(RenderKeywordCase.AS_IS)).render(select));
        out.println(using(H2, new Settings().withRenderKeywordCase(RenderKeywordCase.LOWER)).render(select));
        out.println(using(H2, new Settings().withRenderKeywordCase(RenderKeywordCase.UPPER)).render(select));
        out.println(using(H2, new Settings().withRenderKeywordCase(RenderKeywordCase.PASCAL)).render(select));

        Tools.title("A couple of settings at work - Mapping");
        out.println(using(H2, new Settings()
            .withRenderMapping(new RenderMapping()
                .withSchemata(new MappedSchema()
                    .withInput("PUBLIC")
                    .withOutput("test")
                    .withTables(new MappedTable()
                        .withInput("AUTHOR")
                        .withOutput("test-author"))
                )
            )).render(select));

    }
}
