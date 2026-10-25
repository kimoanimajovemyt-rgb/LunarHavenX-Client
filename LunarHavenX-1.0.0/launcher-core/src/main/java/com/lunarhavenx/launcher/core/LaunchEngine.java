package com.lunarhavenx.launcher.core;

import com.lunarhavenx.launcher.core.auth.Account;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/** Monta a linha de comando do Minecraft (formato antigo e novo) e inicia o processo. */
public final class LaunchEngine {
    private LaunchEngine() { }

    public static final String BRAND = "LunarHavenX";
    public static final String LAUNCHER_VERSION = "1.0.0";

    public static final class Options {
        public Path java;
        public Path gameDir;
        public Path nativesDir;
        public Account account;
        public int ramMb = 2048;
        public String extraJvmArgs = "";
        public VersionManager.AssetInfo assets;
        public String versionId;
        public Set<String> features = new HashSet<>();
    }

    public static List<String> buildCommand(VersionManager.Resolved r, Options o) {
        Map<String, String> v = new LinkedHashMap<>();
        v.put("auth_player_name", o.account.name);
        v.put("auth_uuid", o.account.uuid);
        v.put("auth_access_token", o.account.accessToken);
        v.put("auth_session", "token:" + o.account.accessToken + ":" + o.account.uuid);
        v.put("auth_xuid", "0");
        v.put("clientid", "");
        v.put("user_type", "msa");
        v.put("user_properties", "{}");
        v.put("version_name", o.versionId);
        v.put("version_type", "release");
        v.put("game_directory", o.gameDir.toString());
        v.put("assets_root", o.assets.assetsRoot.toString());
        v.put("game_assets", o.assets.gameAssets.toString());
        v.put("assets_index_name", o.assets.indexName);
        v.put("natives_directory", o.nativesDir.toString());
        v.put("launcher_name", BRAND);
        v.put("launcher_version", LAUNCHER_VERSION);
        v.put("library_directory", r.classpath.isEmpty() ? "" : o.gameDir.toString());
        v.put("classpath_separator", java.io.File.pathSeparator);
        StringBuilder cp = new StringBuilder();
        for (String s : VersionManager.classpathStrings(r)) {
            if (cp.length() > 0) cp.append(java.io.File.pathSeparator);
            cp.append(s);
        }
        v.put("classpath", cp.toString());

        List<String> cmd = new ArrayList<>();
        cmd.add(o.java.toString());
        cmd.add("-Xmx" + o.ramMb + "M");
        cmd.add("-Xms" + Math.min(512, o.ramMb) + "M");
        cmd.add("-XX:+UseG1GC");
        if (o.extraJvmArgs != null && !o.extraJvmArgs.trim().isEmpty()) {
            for (String t : o.extraJvmArgs.trim().split("\\s+")) cmd.add(t);
        }

        Map<String, Object> args = Json.obj(r.json.get("arguments"));
        if (args != null) {
            expand(Json.list(args.get("jvm")), v, o.features, cmd);
            cmd.add(Json.str(r.json, "mainClass"));
            expand(Json.list(args.get("game")), v, o.features, cmd);
        } else {
            // formato antigo (1.8.9): minecraftArguments é uma string única
            cmd.add("-Djava.library.path=" + v.get("natives_directory"));
            cmd.add("-Dminecraft.launcher.brand=" + BRAND);
            cmd.add("-Dminecraft.launcher.version=" + LAUNCHER_VERSION);
            cmd.add("-cp");
            cmd.add(v.get("classpath"));
            cmd.add(Json.str(r.json, "mainClass"));
            String legacy = Json.str(r.json, "minecraftArguments");
            if (legacy == null) throw new IllegalStateException("JSON sem arguments nem minecraftArguments");
            for (String t : legacy.trim().split("\\s+")) cmd.add(substitute(t, v));
        }
        return cmd;
    }

    private static void expand(List<Object> items, Map<String, String> vars, Set<String> features, List<String> out) {
        if (items == null) return;
        for (Object o : items) {
            if (o instanceof String) { out.add(substitute((String) o, vars)); continue; }
            Map<String, Object> m = Json.obj(o);
            if (m == null) continue;
            if (!VersionManager.rulesAllow(Json.list(m.get("rules")), features)) continue;
            Object val = m.get("value");
            if (val instanceof String) out.add(substitute((String) val, vars));
            else if (val instanceof List) for (Object x : Json.list(val)) out.add(substitute(x.toString(), vars));
        }
    }

    public static String substitute(String s, Map<String, String> vars) {
        String out = s;
        for (Map.Entry<String, String> e : vars.entrySet()) {
            if (out.contains("${" + e.getKey() + "}")) out = out.replace("${" + e.getKey() + "}", e.getValue() == null ? "" : e.getValue());
        }
        return out;
    }

    /** Remove tokens da linha de comando antes de logar. */
    public static String redact(List<String> cmd, Account a) {
        StringBuilder b = new StringBuilder();
        for (String s : cmd) {
            if (b.length() > 0) b.append(' ');
            String t = s;
            if (a.accessToken != null) t = t.replace(a.accessToken, "***");
            b.append(t.contains(" ") ? "\"" + t + "\"" : t);
        }
        return b.toString();
    }

    public static Process launch(List<String> cmd, Path gameDir, Account a, Consumer<String> log) throws IOException {
        log.accept("Comando: " + redact(cmd, a));
        ProcessBuilder pb = new ProcessBuilder(cmd).directory(gameDir.toFile()).redirectErrorStream(true);
        final Process p = pb.start();
        Thread t = new Thread(() -> {
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = r.readLine()) != null) log.accept(line.replace(a.accessToken == null ? "\u0000" : a.accessToken, "***"));
            } catch (IOException ignored) { }
        }, "mc-output");
        t.setDaemon(true);
        t.start();
        return p;
    }
}
