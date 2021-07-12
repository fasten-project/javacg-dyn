/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.fasten.javacgdyn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import eu.fasten.javacgdyn.testUtils.CommentParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

class ProfilerTest {

    @Test
    void testAllCases() throws IOException, InterruptedException {
        var annotated = new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                .getResource("annotated")).getFile());
        var agent = new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                .getResource("javacg-dyn-0.0.1-SNAPSHOT.jar")).getFile());

        var config = File.createTempFile("config", ".ini");
        var path = config.getParent() + File.separator + "dyncg.json";
        var content = "output: " + path;
        Files.write(config.toPath(), content.getBytes(StandardCharsets.UTF_8));
        config.deleteOnExit();

        var expected = new CommentParser()
                .extractComments(
                        Path.of(annotated.getAbsolutePath(), "app", "src", "main", "java").toAbsolutePath().toString(),
                        "org.test.annotated.app");
        var dep1Expected = new CommentParser()
                .extractComments(
                        Path.of(annotated.getAbsolutePath(), "dep1", "src", "main", "java").toAbsolutePath().toString(),
                        "org.test.annotated.dep1");
        expected.putAll(dep1Expected);
        expected = cleanParsedComments(expected);


        var processBuilder = new ProcessBuilder();
        processBuilder.command("mvn", "test", "-f", Path.of(annotated.getAbsolutePath(), "app").toAbsolutePath().toString(),
                "-DargLine=-javaagent:" + agent.getAbsolutePath() + "=" + config.getAbsolutePath());
        var process = processBuilder.start();
        process.waitFor();
        process.destroy();

        var cg = new File(path);
        cg.deleteOnExit();

        if (Files.notExists(Path.of(cg.getAbsolutePath()))) {
            return;
        }
        var tokener = new JSONTokener(new FileReader(cg.getAbsolutePath()));
        var consumedJson = new JSONObject(tokener);
        var actual = convertDynCGToMap(consumedJson);
        actual = cleanCG(actual);

        processBuilder.command("mvn", "clean", "-f", annotated.getAbsolutePath());
        process = processBuilder.start();
        process.waitFor();
        process.destroy();

        assertEqualMaps(expected, actual);
    }

    private static void assertEqualMaps(final Map<String, Set<String>> expected,
                                        final Map<String, Set<String>> actual) {
        assertEquals(expected.size(), actual.size());
        expected.forEach((key, value) -> assertEquals(value, actual.get(key)));
    }

    private Map<String, Set<String>> convertDynCGToMap(final JSONObject dyncg) {
        var result = new HashMap<String, Set<String>>();

        var methods = new HashMap<Integer, String>();
        var types = dyncg.getJSONObject("nodes");

        for (var typeName : types.keySet()) {
            var type = types.getJSONObject(typeName);
            var methodList = type.getJSONObject("methods");
            for (var methodId : methodList.keySet()) {
                methods.put(Integer.valueOf(methodId), methodList.getString(methodId));
            }
        }
        var edges = dyncg.getJSONArray("graph");

        for (int i = 0; i < edges.length(); i++) {
            var edge = edges.getJSONArray(i);
            var source = edge.getInt(0);
            var target = edge.getInt(1);

            result.computeIfAbsent(removePackageFromParameters(methods.get(source)), k -> new HashSet<>()).add(methods.get(target));
        }
        return result;
    }

    private String removePackageFromParameters(final String methodURI) {
        var start = methodURI.indexOf("(");
        var end = methodURI.indexOf(")");
        if (end - start == 1) return methodURI;

        var parameters = methodURI.substring(start + 1, end).split(",");
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = parameters[i].substring(parameters[i].lastIndexOf(".") + 1);
        }
        return methodURI.substring(0, start + 1) + String.join(",", parameters)
                + methodURI.substring(end);
    }

    private Map<String, Set<String>> cleanParsedComments(Map<String, Set<String>> map) {
        return map.entrySet().stream().filter(e -> !e.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Set<String>> cleanCG(Map<String, Set<String>> map) {
        return map.entrySet().stream().filter(e -> !e.getKey().contains("Test")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}