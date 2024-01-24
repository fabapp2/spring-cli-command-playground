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

import com.example.MyCustomRecipeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.RecipeRun;
import org.openrewrite.SourceFile;
import org.openrewrite.internal.InMemoryLargeSourceSet;
import org.openrewrite.maven.tree.MavenResolutionResult;
import org.openrewrite.maven.tree.ResolvedPom;
import org.springframework.rewrite.parsers.RewriteExecutionContext;
import org.springframework.rewrite.parsers.RewriteProjectParsingResult;
import org.springframework.rewrite.parsers.SpringRewriteProperties;
import org.springframework.rewrite.test.util.DummyResource;
import org.springframework.rewrite.test.util.ParserExecutionHelper;
import org.springframework.rewrite.test.util.ParserParityTestHelper;
import org.springframework.rewrite.test.util.TestProjectHelper;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Fabian Krüger
 */
public class CustomRecipeTest {
    @Test
    @DisplayName("recipe should add property to pom")
    void recipeShouldAddPropertyToPom(@TempDir Path tempDir) {
        String pomXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                                
                    <groupId>org.springframework.rewrite</groupId>
                    <artifactId>recipe-project</artifactId>
                    <version>1.0-SNAPSHOT</version>
                                
                    <properties>
                        <maven.compiler.source>17</maven.compiler.source>
                        <maven.compiler.target>17</maven.compiler.target>
                        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    </properties>
                </project>
                """;

        TestProjectHelper.createTestProject(tempDir)
                .addResource("pom.xml", pomXml)
                .writeToFilesystem();

        RewriteProjectParsingResult parsingResult = new ParserExecutionHelper().parseWithRewriteProjectParser(tempDir, new SpringRewriteProperties());

        SourceFile sourceFile = parsingResult.sourceFiles().get(0);
        ResolvedPom pom = sourceFile.getMarkers().findFirst(MavenResolutionResult.class).get().getPom();
        assertThat(pom.getProperties().keySet().stream().toList()).doesNotContain("myProperty");

        String addedProperty = "<myProperty>my-value</myProperty>";
        assertThat(sourceFile.printAll()).doesNotContain(addedProperty);

        Recipe recipe = new MyCustomRecipeProvider().executeRecipe();
        ExecutionContext ctx = new RewriteExecutionContext();
        SourceFile after = recipe.run(new InMemoryLargeSourceSet(List.of(sourceFile)), ctx).getChangeset().getAllResults().get(0).getAfter();

        // The MavenModel is not updated, just checking the source which should be sufficient for this example
        String sourceAfter = after.printAll();
        assertThat(sourceAfter).contains(addedProperty);
    }
}
