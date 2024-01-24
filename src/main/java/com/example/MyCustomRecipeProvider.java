/*
 * Copyright 2021 - 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.maven.AddProperty;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.xml.tree.Xml;
import org.springframework.cli.recipe.CustomRecipeProvider;
import org.springframework.rewrite.support.openrewrite.GenericOpenRewriteRecipe;

import java.nio.file.Path;
import java.util.List;

/**
 * @author Fabian Krüger
 */
public class MyCustomRecipeProvider implements CustomRecipeProvider {
    @Override
    public Recipe getRecipe() {
        return new AddProperty("myProperty", "my-value", false, false);
    }
}
