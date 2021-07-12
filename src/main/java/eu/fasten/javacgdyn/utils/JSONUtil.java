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

package eu.fasten.javacgdyn.utils;

import eu.fasten.javacgdyn.data.*;
import java.util.Arrays;
import java.util.Map;

public class JSONUtil {

    /**
     * Convert a CallGraph object to the JSON String.
     *
     * @param callGraph call graph to convert
     * @return JSON String of call graph
     */
    public static String toJSONString(final CallGraph callGraph) {
        var result = new StringBuilder("{");
        appendCha(result, callGraph.getClassHierarchy());
        appendGraph(result, callGraph.getGraph());
        if (result.charAt(result.length() - 1) == ',') {
            result.setLength(result.length() - 1);
        }
        return result.toString();
    }

    /**
     * Appends graph information of the Graph to the StringBuilder.
     *
     * @param graph  the graph object to extract the information from.
     * @param result the StringBuilder to append the information.
     */
    private static void appendGraph(StringBuilder result, final Graph graph) {
        result.append("\"graph\":[");
        for (final var entry : graph.getGraph()) {
            appendCall(result, entry);
        }
        removeLastIfNotEmpty(result, graph.getGraph().size());
        result.append("]},");
    }

    /**
     * Removes the last character of StringBuilder if the second parameter is not zero.
     * This method helps to remove extra "," from the end of multiple element lists.
     *
     * @param result the StringBuilder to remove from.
     * @param size   if the size of the list is zero there is no "," to be removed.
     */
    private static void removeLastIfNotEmpty(StringBuilder result, int size) {
        if (size != 0) {
            result.setLength(result.length() - 1);
        }
    }

    /**
     * Appends call information of the specified call to the StringBuilder.
     *
     * @param call   the call extract the information from.
     * @param result the StringBuilder to append the information.
     */
    private static void appendCall(StringBuilder result, final Call call) {
        result.append("[")
                .append(call.getSource())
                .append(",")
                .append(call.getTarget())
                .append("],");
    }

    /**
     * Appends cha information of the ClassHierarchy to the StringBuilder.
     *
     * @param cha    the ClassHierarchy to extract the information from.
     * @param result the StringBuilder to append information.
     */
    private static void appendCha(StringBuilder result, final ClassHierarchy cha) {
        result.append("\"nodes\":{");
        for (final var entry : cha.getTypes()) {
            appendType(result, entry);
        }
        removeLastIfNotEmpty(result, cha.getTypes().size());
        result.append("},");
    }

    /**
     * Appends information of the specified type to the StringBuilder.
     *
     * @param type   the Type to extract the information from.
     * @param result the StringBuilder to append the information.
     */
    private static void appendType(StringBuilder result, final Type type) {
        result.append(quote(type.getTypeName())).append(":{");
        result.append("\"methods\":{");
        appendMethods(result, type.getMethods());
        result.append("},");
    }

    /**
     * Appends methods of a type to the StringBuilder.
     *
     * @param methods Map of nodes to extract the information.
     * @param result  the StringBuilder to append the information.
     */
    private static void appendMethods(StringBuilder result, final Map<Integer, Method> methods) {
        for (final var entry : methods.entrySet()) {
            appendKeyValue(result, entry.getKey().toString(), methodToFastenURI(entry.getValue()).toString());
        }
        removeLastIfNotEmpty(result, methods.size());
        result.append("}");
    }

    private static String transformPrimitiveType(String type) {
        switch (type) {
            case "boolean":
                return "java.lang.BooleanType";
            case "byte":
                return "java.lang.ByteType";
            case "short":
                return "java.lang.ShortType";
            case "int":
                return "java.lang.IntegerType";
            case "long":
                return "java.lang.LongType";
            case "float":
                return "java.lang.FloatType";
            case "double":
                return "java.lang.DoubleType";
            case "char":
                return "java.lang.CharType";
            case "boolean[]":
                return "java.lang.BooleanType[]";
            case "byte[]":
                return "java.lang.ByteType[]";
            case "short[]":
                return "java.lang.ShortType[]";
            case "int[]":
                return "java.lang.IntegerType[]";
            case "long[]":
                return "java.lang.LongType[]";
            case "float[]":
                return "java.lang.FloatType[]";
            case "double[]":
                return "java.lang.DoubleType[]";
            case "char[]":
                return "java.lang.CharType[]";
            case "void":
                return "java.lang.VoidType";
            default:
                return type;
        }
    }

    private static String transformToFastenUriFormat(String type) {
        type = transformPrimitiveType(type);
        var strBuilder = new StringBuilder(type);
        if (type.lastIndexOf(".") >= 0) {
            strBuilder.replace(type.lastIndexOf("."), type.lastIndexOf(".") + 1, "/");
        }
        return "/" + strBuilder;
    }

    public static FastenURI methodToFastenURI(Method method) {
        var parameters = Arrays.stream(method.getParameters())
                .map(p -> FastenJavaURI.createWithoutFunction(transformToFastenUriFormat(p)))
                .toArray(FastenJavaURI[]::new);
        var returnType = FastenJavaURI.createWithoutFunction(transformToFastenUriFormat(method.getReturnType()));
        final var javaURIRaw = FastenJavaURI.create(null, null, null,
                method.getPackageName(), method.getClassName(), method.getName(), parameters, returnType);
        final var javaURI = javaURIRaw.canonicalize();
        return FastenURI.createSchemeless(javaURI.getRawForge(), javaURI.getRawProduct(),
                javaURI.getRawVersion(),
                javaURI.getRawNamespace(), javaURI.getRawEntity());
    }

    /**
     * Quotes a given String.
     *
     * @param s String to be quoted.
     * @return quoted String.
     */
    private static String quote(final String s) {
        return "\"" + s + "\"";
    }

    /**
     * Appends a key value to a given StringBuilder assuming the value is a String.
     *
     * @param result StringBuilder to append to the key value.
     * @param key    String key.
     * @param value  String value.
     */
    private static void appendKeyValue(StringBuilder result, final String key,
                                       final String value) {
        result.append(quote(key)).append(":").append(quote(value));
        result.append(",");
    }
}
