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

import eu.fasten.javacgdyn.data.CallGraph;
import eu.fasten.javacgdyn.utils.Config;
import eu.fasten.javacgdyn.utils.JSONUtil;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Profiler {

    public static final CallGraph callGraph = new CallGraph();
    public static Config config;

    private static FileOutputStream fileWriter;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try {
                fileWriter.write(JSONUtil.toJSONString(callGraph).getBytes(StandardCharsets.UTF_8));
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void premain(String agentArgs, Instrumentation inst) throws IOException {
        config = new Config(agentArgs);

        fileWriter = new FileOutputStream(config.getProperty("output"));

        var root = config.getProperty("root-project");
        MethodStack.rootProject = Pattern.compile(root);

        var transformer = new Transformer();
        inst.addTransformer(transformer);
    }
}