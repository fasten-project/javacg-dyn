package eu.fasten.javacgdyn.utils;

import java.util.HashMap;
import java.util.Map;

public class DefaultConfig {

    public static final String exclusions = "^java.*,^jdk.*,^sun.*,^com.sun.*,^eu.fasten.javacgdyn.*," +
            "^org.xml.sax.*,^org.apache.maven.surefire.*,^org.apache.tools.*,^org.mockito.*," +
            "^org.easymock.internal.*,^org.junit.*,^junit.framework.*,^org.hamcrest.*,^org.objenesis.*," +
            "^org.apiguardian.api.*";

    public static final String output = "./javacg-dyn.json";

    public static final String rootProject = ".*";

    public static Map<String, String> create() {
        var map = new HashMap<String, String>();
        map.put("excl", exclusions);
        map.put("output", output);
        map.put("root-project", rootProject);
        return map;
    }
}
