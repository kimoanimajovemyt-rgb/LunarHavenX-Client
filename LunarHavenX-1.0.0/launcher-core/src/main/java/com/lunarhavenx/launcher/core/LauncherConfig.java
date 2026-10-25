package com.lunarhavenx.launcher.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Configurações persistentes do launcher (launcher.json). */
public final class LauncherConfig {
    public int ramMb = 2048;
    public String javaPath = "";
    public String msClientId = "";
    public boolean useForge = true;
    public String extraJvmArgs = "";

    private final Path file;

    public LauncherConfig(Path root) { this.file = root.resolve("launcher.json"); }

    public static LauncherConfig load(Path root) {
        LauncherConfig c = new LauncherConfig(root);
        try {
            if (Files.exists(c.file)) {
                Map<String, Object> m = Json.obj(Json.parse(new String(Files.readAllBytes(c.file), StandardCharsets.UTF_8)));
                if (m != null) {
                    c.ramMb = (int) Json.num(m, 2048, "ramMb");
                    if (c.ramMb < 512) c.ramMb = 512;
                    c.javaPath = m.get("javaPath") == null ? "" : Json.str(m, "javaPath");
                    c.msClientId = m.get("msClientId") == null ? "" : Json.str(m, "msClientId");
                    c.useForge = !Boolean.FALSE.equals(m.get("useForge"));
                    c.extraJvmArgs = m.get("extraJvmArgs") == null ? "" : Json.str(m, "extraJvmArgs");
                }
            }
        } catch (Exception ignored) { /* mantém padrões */ }
        String env = System.getenv("LHX_MS_CLIENT_ID");
        if (env != null && !env.trim().isEmpty()) c.msClientId = env.trim();
        return c;
    }

    public void save() throws IOException {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("ramMb", ramMb);
        m.put("javaPath", javaPath);
        m.put("msClientId", msClientId);
        m.put("useForge", useForge);
        m.put("extraJvmArgs", extraJvmArgs);
        Files.createDirectories(file.getParent());
        Files.write(file, Json.write(m).getBytes(StandardCharsets.UTF_8));
    }
}
