package com.lunarhavenx.launcher.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

public final class Platform {
    private Platform() { }

    /** "windows", "osx" ou "linux" (nomes usados nas regras dos JSON da Mojang). */
    public static String osName() {
        String n = System.getProperty("os.name", "").toLowerCase();
        if (n.contains("win")) return "windows";
        if (n.contains("mac") || n.contains("darwin")) return "osx";
        return "linux";
    }

    public static boolean is64() { return System.getProperty("os.arch", "").contains("64"); }

    public static boolean isArm() {
        String a = System.getProperty("os.arch", "").toLowerCase();
        return a.contains("aarch64") || a.contains("arm");
    }

    /** Pasta de dados do launcher. Pode ser trocada com a variável LHX_HOME. */
    public static Path dataDir() {
        String override = System.getenv("LHX_HOME");
        if (override != null && !override.trim().isEmpty()) return Paths.get(override);
        String home = System.getProperty("user.home");
        switch (osName()) {
            case "windows": {
                String app = System.getenv("APPDATA");
                return app != null ? Paths.get(app, ".lunarhavenx") : Paths.get(home, ".lunarhavenx");
            }
            case "osx": return Paths.get(home, "Library", "Application Support", "lunarhavenx");
            default: return Paths.get(home, ".lunarhavenx");
        }
    }

    public static String sha1(Path file) throws IOException {
        try (InputStream in = Files.newInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] buf = new byte[65536];
            int n;
            while ((n = in.read(buf)) > 0) md.update(buf, 0, n);
            StringBuilder b = new StringBuilder();
            for (byte x : md.digest()) b.append(String.format("%02x", x));
            return b.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
    }
}
