package com.lunarhavenx.launcher.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Minecraft 1.8.9 + Forge só roda em Java 8 (o LaunchWrapper converte o ClassLoader do sistema
 * para URLClassLoader, o que quebra no Java 9+). Este gerenciador procura um Java 8 e, se não achar,
 * baixa o JRE 8 (Eclipse Temurin) da API da Adoptium.
 */
public final class JavaRuntime {
    private JavaRuntime() { }

    private static final Pattern VERSION = Pattern.compile("version \"([^\"]+)\"");

    /** "1.8.0_402" → 8; "17.0.1" → 17; "21" → 21. Retorna -1 se não reconhecer. */
    public static int parseMajor(String versionOutput) {
        Matcher m = VERSION.matcher(versionOutput == null ? "" : versionOutput);
        if (!m.find()) return -1;
        String v = m.group(1);
        try {
            String[] p = v.split("[._\\-+]");
            if (p[0].equals("1") && p.length > 1) return Integer.parseInt(p[1]);
            return Integer.parseInt(p[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static int majorOf(Path java) {
        try {
            Process pr = new ProcessBuilder(java.toString(), "-version").redirectErrorStream(true).start();
            StringBuilder b = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(pr.getInputStream()))) {
                String line;
                while ((line = r.readLine()) != null) b.append(line).append('\n');
            }
            pr.waitFor(10, TimeUnit.SECONDS);
            return parseMajor(b.toString());
        } catch (Exception e) {
            return -1;
        }
    }

    private static String exe() { return Platform.osName().equals("windows") ? "java.exe" : "java"; }

    private static Path findBundled(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) return null;
        try (Stream<Path> s = Files.walk(dir, 4)) {
            return s.filter(p -> p.getFileName().toString().equals(exe()) && p.getParent().getFileName().toString().equals("bin"))
                    .findFirst().orElse(null);
        }
    }

    public static Path find8(Path root, String configured, Progress p) throws IOException {
        List<Path> candidates = new ArrayList<>();
        if (configured != null && !configured.trim().isEmpty()) candidates.add(Paths.get(configured.trim()));
        Path bundled = findBundled(root.resolve("runtime").resolve("java8"));
        if (bundled != null) candidates.add(bundled);
        String jh = System.getenv("JAVA_HOME");
        if (jh != null) candidates.add(Paths.get(jh, "bin", exe()));
        for (Path c : candidates) {
            if (Files.exists(c) && majorOf(c) == 8) return c;
        }
        p.update("Baixando Java 8 (Temurin)…", -1);
        return downloadTemurin8(root, p);
    }

    static Path downloadTemurin8(Path root, Progress p) throws IOException {
        String os = Platform.osName().equals("osx") ? "mac" : Platform.osName();
        String arch = Platform.isArm() ? "aarch64" : "x64";
        String url = "https://api.adoptium.net/v3/binary/latest/8/ga/" + os + "/" + arch + "/jre/hotspot/normal/eclipse";
        Path dir = root.resolve("runtime").resolve("java8");
        Files.createDirectories(dir);
        boolean zip = Platform.osName().equals("windows");
        Path archive = dir.resolve(zip ? "jre8.zip" : "jre8.tar.gz");
        final long[] got = {0};
        Http.download(url, archive, n -> { got[0] += n; p.update("Baixando Java 8… " + (got[0] / 1048576) + " MB", -1); });
        p.update("Extraindo Java 8…", -1);
        if (zip) {
            unzip(archive, dir);
        } else {
            Process pr = new ProcessBuilder("tar", "-xzf", archive.toString(), "-C", dir.toString()).inheritIO().start();
            try {
                if (pr.waitFor() != 0) throw new IOException("tar falhou ao extrair o Java 8");
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        }
        Files.deleteIfExists(archive);
        Path java = findBundled(dir);
        if (java == null) throw new IOException("Java 8 baixado, mas o executável não foi encontrado.");
        java.toFile().setExecutable(true);
        if (majorOf(java) != 8) throw new IOException("O Java baixado não é a versão 8.");
        return java;
    }

    static void unzip(Path zip, Path dest) throws IOException {
        try (ZipInputStream z = new ZipInputStream(Files.newInputStream(zip))) {
            ZipEntry e;
            while ((e = z.getNextEntry()) != null) {
                Path out = dest.resolve(e.getName()).normalize();
                if (!out.startsWith(dest.normalize())) throw new IOException("Entrada de zip suspeita: " + e.getName());
                if (e.isDirectory()) { Files.createDirectories(out); continue; }
                Files.createDirectories(out.getParent());
                try (OutputStream o = Files.newOutputStream(out)) {
                    byte[] buf = new byte[65536];
                    int n;
                    while ((n = z.read(buf)) > 0) o.write(buf, 0, n);
                }
            }
        }
    }
}
