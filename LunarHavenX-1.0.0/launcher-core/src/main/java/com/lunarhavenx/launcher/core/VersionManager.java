package com.lunarhavenx.launcher.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Metadados de versões da Mojang: manifesto, JSON de versão (com inheritsFrom),
 * bibliotecas, natives, client.jar e assets. Os arquivos do jogo são baixados dos
 * servidores oficiais da Mojang; nada do Minecraft é distribuído com o launcher.
 */
public final class VersionManager {
    public static final String MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    public static final String LIB_BASE = "https://libraries.minecraft.net/";
    public static final String ASSET_BASE = "https://resources.download.minecraft.net/";

    private final Path root;

    public VersionManager(Path root) { this.root = root; }

    public Path versionJsonPath(String id) { return root.resolve("versions").resolve(id).resolve(id + ".json"); }
    public Path librariesDir() { return root.resolve("libraries"); }
    public Path assetsDir() { return root.resolve("assets"); }

    // ---------------------------------------------------------------- modelos
    public static final class Download {
        public final String url; public final Path dest; public final String sha1; public final long size; public final String label;
        public Download(String url, Path dest, String sha1, long size, String label) {
            this.url = url; this.dest = dest; this.sha1 = sha1; this.size = size; this.label = label;
        }
    }

    public static final class Lib {
        public final String name; public final Path file; public final String url; public final String sha1; public final long size;
        public final List<String> extractExclude;
        Lib(String name, Path file, String url, String sha1, long size, List<String> ex) {
            this.name = name; this.file = file; this.url = url; this.sha1 = sha1; this.size = size; this.extractExclude = ex;
        }
    }

    public static final class Resolved {
        public Map<String, Object> json;
        public String jarId;
        public List<Lib> classpath = new ArrayList<>();
        public List<Lib> natives = new ArrayList<>();
        public Path clientJar;
    }

    // ---------------------------------------------------------------- manifesto
    public void ensureVanillaJson(String id, Progress p) throws IOException {
        Path f = versionJsonPath(id);
        if (Files.exists(f)) return;
        p.update("Consultando versões da Mojang…", -1);
        Object manifest = Json.parse(Http.get(MANIFEST_URL, null));
        for (Object o : Json.list(Json.path(manifest, "versions"))) {
            if (id.equals(Json.str(o, "id"))) {
                String url = Json.str(o, "url");
                String sha1 = Json.str(o, "sha1");
                Http.download(url, f, null);
                if (sha1 != null && !sha1.equalsIgnoreCase(Platform.sha1(f))) {
                    Files.delete(f);
                    throw new IOException("SHA-1 inválido para o JSON da versão " + id);
                }
                return;
            }
        }
        throw new IOException("Versão não encontrada no manifesto da Mojang: " + id);
    }

    // ---------------------------------------------------------------- JSON de versão
    public Map<String, Object> loadMerged(String id) throws IOException {
        Path f = versionJsonPath(id);
        if (!Files.exists(f)) throw new IOException("JSON de versão ausente: " + f);
        Map<String, Object> child = Json.obj(Json.parse(new String(Files.readAllBytes(f), StandardCharsets.UTF_8)));
        if (child == null) throw new IOException("JSON de versão inválido: " + f);
        String parentId = Json.str(child, "inheritsFrom");
        Map<String, Object> out;
        if (parentId == null) {
            out = new LinkedHashMap<>(child);
            out.put("_jarId", child.containsKey("jar") ? child.get("jar") : id);
        } else {
            out = merge(child, loadMerged(parentId));
        }
        return out;
    }

    /** Une um JSON "filho" (ex.: Forge) sobre o pai (Minecraft vanilla). */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> merge(Map<String, Object> child, Map<String, Object> parent) {
        Map<String, Object> out = new LinkedHashMap<>(parent);
        for (Map.Entry<String, Object> e : child.entrySet()) {
            String k = e.getKey();
            if (k.equals("libraries")) continue;
            if (k.equals("arguments")) continue;
            out.put(k, e.getValue());
        }
        List<Object> libs = new ArrayList<>();
        if (Json.list(child.get("libraries")) != null) libs.addAll(Json.list(child.get("libraries")));
        if (Json.list(parent.get("libraries")) != null) libs.addAll(Json.list(parent.get("libraries")));
        out.put("libraries", libs);
        Map<String, Object> ca = Json.obj(child.get("arguments"));
        Map<String, Object> pa = Json.obj(parent.get("arguments"));
        if (ca != null || pa != null) {
            Map<String, Object> args = new LinkedHashMap<>();
            for (String key : new String[]{"jvm", "game"}) {
                List<Object> l = new ArrayList<>();
                if (pa != null && Json.list(pa.get(key)) != null) l.addAll(Json.list(pa.get(key)));
                if (ca != null && Json.list(ca.get(key)) != null) l.addAll(Json.list(ca.get(key)));
                args.put(key, l);
            }
            out.put("arguments", args);
        }
        if (child.containsKey("jar")) out.put("_jarId", child.get("jar"));
        // sem "jar" no filho, _jarId do pai é preservado (copiado de 'parent')
        return out;
    }

    // ---------------------------------------------------------------- regras
    public static boolean rulesAllow(List<Object> rules, Set<String> features) {
        if (rules == null || rules.isEmpty()) return true;
        boolean allowed = false;
        for (Object o : rules) {
            Map<String, Object> r = Json.obj(o);
            boolean applies = true;
            Map<String, Object> os = Json.obj(r.get("os"));
            if (os != null) {
                String n = Json.str(os, "name");
                if (n != null && !n.equals(Platform.osName())) applies = false;
                String arch = Json.str(os, "arch");
                if (arch != null && arch.equals("x86") && Platform.is64()) applies = false;
            }
            Map<String, Object> ft = Json.obj(r.get("features"));
            if (ft != null) {
                for (Map.Entry<String, Object> e : ft.entrySet()) {
                    boolean want = Boolean.TRUE.equals(e.getValue());
                    if (want != features.contains(e.getKey())) applies = false;
                }
            }
            if (applies) allowed = "allow".equals(Json.str(r, "action"));
        }
        return allowed;
    }

    // ---------------------------------------------------------------- bibliotecas
    public static String mavenPath(String name, String classifierOverride) {
        String ext = "jar";
        int at = name.indexOf('@');
        if (at >= 0) { ext = name.substring(at + 1); name = name.substring(0, at); }
        String[] p = name.split(":");
        if (p.length < 3) throw new IllegalArgumentException("Coordenada Maven inválida: " + name);
        String classifier = classifierOverride != null ? classifierOverride : (p.length > 3 ? p[3] : null);
        String file = p[1] + "-" + p[2] + (classifier != null ? "-" + classifier : "") + "." + ext;
        return p[0].replace('.', '/') + "/" + p[1] + "/" + p[2] + "/" + file;
    }

    static String normalizeBase(String base) {
        if (base == null || base.isEmpty()) return LIB_BASE;
        String b = base.replace("http://", "https://").replace("files.minecraftforge.net/maven", "maven.minecraftforge.net");
        return b.endsWith("/") ? b : b + "/";
    }

    private Lib makeLib(Map<String, Object> lib, String name, String classifier, List<String> ex) {
        Object art = classifier == null ? Json.path(lib, "downloads", "artifact") : Json.path(lib, "downloads", "classifiers", classifier);
        String rel; String url; String sha1 = null; long size = 0;
        if (art != null) {
            rel = Json.str(art, "path");
            if (rel == null) rel = mavenPath(name, classifier);
            url = Json.str(art, "url");
            if (url != null && url.isEmpty()) url = null;
            sha1 = Json.str(art, "sha1");
            size = Json.num(art, 0, "size");
        } else {
            rel = mavenPath(name, classifier);
            url = normalizeBase(Json.str(lib, "url")) + rel;
        }
        return new Lib(name, librariesDir().resolve(rel), url, sha1, size, ex);
    }

    public Resolved resolve(Map<String, Object> json, Set<String> features) {
        Resolved r = new Resolved();
        r.json = json;
        r.jarId = Json.str(json, "_jarId");
        String os = Platform.osName();
        Set<String> seen = new HashSet<>();
        List<Object> libs = Json.list(json.get("libraries"));
        if (libs != null) for (Object o : libs) {
            Map<String, Object> lib = Json.obj(o);
            if (Boolean.FALSE.equals(lib.get("clientreq"))) continue;
            if (!rulesAllow(Json.list(lib.get("rules")), features)) continue;
            String name = Json.str(lib, "name");
            Map<String, Object> natives = Json.obj(lib.get("natives"));
            if (natives != null) {
                String cls = Json.str(natives, os);
                if (cls != null) {
                    cls = cls.replace("${arch}", Platform.is64() ? "64" : "32");
                    List<String> ex = new ArrayList<>();
                    List<Object> exl = Json.list(Json.path(lib, "extract", "exclude"));
                    if (exl != null) for (Object x : exl) ex.add(x.toString());
                    r.natives.add(makeLib(lib, name, cls, ex));
                }
                if (Json.path(lib, "downloads", "artifact") != null) addClasspath(r, seen, lib, name);
            } else {
                addClasspath(r, seen, lib, name);
            }
        }
        r.clientJar = root.resolve("versions").resolve(r.jarId).resolve(r.jarId + ".jar");
        return r;
    }

    private void addClasspath(Resolved r, Set<String> seen, Map<String, Object> lib, String name) {
        String[] p = name.split("[:@]");
        String key = p[0] + ":" + p[1] + (p.length > 3 ? ":" + p[3] : "");
        if (seen.add(key)) r.classpath.add(makeLib(lib, name, null, null));
    }

    // ---------------------------------------------------------------- downloads
    private static boolean upToDate(Download d) {
        try {
            if (!Files.exists(d.dest)) return false;
            if (d.size > 0) return Files.size(d.dest) == d.size;
            if (d.sha1 != null) return d.sha1.equalsIgnoreCase(Platform.sha1(d.dest));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void downloadAll(List<Download> all, Progress p, String title) throws IOException {
        List<Download> todo = new ArrayList<>();
        for (Download d : all) if (!upToDate(d)) todo.add(d);
        if (todo.isEmpty()) return;
        final int total = todo.size();
        final AtomicInteger done = new AtomicInteger();
        ExecutorService ex = Executors.newFixedThreadPool(8);
        List<Future<?>> fs = new ArrayList<>();
        for (final Download d : todo) {
            fs.add(ex.submit(() -> {
                if (d.url == null) throw new IOException("Arquivo ausente e sem URL de download: " + d.label);
                IOException last = null;
                for (int attempt = 0; attempt < 3; attempt++) {
                    try {
                        Http.download(d.url, d.dest, null);
                        if (d.sha1 != null && !d.sha1.equalsIgnoreCase(Platform.sha1(d.dest))) {
                            Files.deleteIfExists(d.dest);
                            throw new IOException("SHA-1 não confere: " + d.label);
                        }
                        int n = done.incrementAndGet();
                        p.update(title + " (" + n + "/" + total + ")", n / (double) total);
                        return null;
                    } catch (IOException e) {
                        last = e;
                    }
                }
                throw last;
            }));
        }
        ex.shutdown();
        IOException failure = null;
        for (Future<?> f : fs) {
            try { f.get(); } catch (Exception e) {
                if (failure == null) failure = new IOException("Falha no download: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()), e);
            }
        }
        if (failure != null) throw failure;
    }

    public void ensureClientJar(Resolved r, Progress p) throws IOException {
        Object client = Json.path(r.json, "downloads", "client");
        if (client == null) throw new IOException("JSON sem downloads.client (versão " + r.jarId + ")");
        List<Download> l = new ArrayList<>();
        l.add(new Download(Json.str(client, "url"), r.clientJar, Json.str(client, "sha1"), Json.num(client, 0, "size"), "client.jar"));
        downloadAll(l, p, "Baixando Minecraft " + r.jarId);
    }

    public void ensureLibraries(Resolved r, Progress p) throws IOException {
        List<Download> l = new ArrayList<>();
        for (Lib x : r.classpath) l.add(new Download(x.url, x.file, x.sha1, x.size, x.name));
        for (Lib x : r.natives) l.add(new Download(x.url, x.file, x.sha1, x.size, x.name));
        downloadAll(l, p, "Baixando bibliotecas");
    }

    public void extractNatives(Resolved r, Path dir) throws IOException {
        Files.createDirectories(dir);
        for (Lib n : r.natives) {
            try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(n.file))) {
                ZipEntry e;
                while ((e = zin.getNextEntry()) != null) {
                    if (e.isDirectory()) continue;
                    boolean skip = false;
                    if (n.extractExclude != null) for (String ex : n.extractExclude) if (e.getName().startsWith(ex)) skip = true;
                    if (skip) continue;
                    Path out = dir.resolve(e.getName()).normalize();
                    if (!out.startsWith(dir.normalize())) throw new IOException("Entrada de zip suspeita: " + e.getName());
                    Files.createDirectories(out.getParent());
                    try (OutputStream os = Files.newOutputStream(out)) {
                        byte[] buf = new byte[65536];
                        int c;
                        while ((c = zin.read(buf)) > 0) os.write(buf, 0, c);
                    }
                }
            }
        }
    }

    /** Resultado dos assets: nome do índice e, se "virtual", a pasta virtual. */
    public static final class AssetInfo {
        public String indexName; public Path assetsRoot; public Path gameAssets;
    }

    public AssetInfo ensureAssets(Map<String, Object> json, Progress p) throws IOException {
        Object idx = json.get("assetIndex");
        if (idx == null) throw new IOException("JSON sem assetIndex");
        String id = Json.str(idx, "id");
        Path indexFile = assetsDir().resolve("indexes").resolve(id + ".json");
        List<Download> first = new ArrayList<>();
        first.add(new Download(Json.str(idx, "url"), indexFile, Json.str(idx, "sha1"), Json.num(idx, 0, "size"), "índice " + id));
        downloadAll(first, p, "Baixando índice de assets");
        Map<String, Object> index = Json.obj(Json.parse(new String(Files.readAllBytes(indexFile), StandardCharsets.UTF_8)));
        Map<String, Object> objects = Json.obj(index.get("objects"));
        List<Download> l = new ArrayList<>();
        for (Map.Entry<String, Object> e : objects.entrySet()) {
            String hash = Json.str(e.getValue(), "hash");
            String sub = hash.substring(0, 2);
            l.add(new Download(ASSET_BASE + sub + "/" + hash, assetsDir().resolve("objects").resolve(sub).resolve(hash),
                    hash, Json.num(e.getValue(), 0, "size"), e.getKey()));
        }
        downloadAll(l, p, "Baixando assets");
        AssetInfo info = new AssetInfo();
        info.indexName = id;
        info.assetsRoot = assetsDir();
        info.gameAssets = assetsDir();
        if (Boolean.TRUE.equals(index.get("virtual")) || Boolean.TRUE.equals(index.get("map_to_resources"))) {
            Path virt = assetsDir().resolve("virtual").resolve(id);
            for (Map.Entry<String, Object> e : objects.entrySet()) {
                String hash = Json.str(e.getValue(), "hash");
                Path src = assetsDir().resolve("objects").resolve(hash.substring(0, 2)).resolve(hash);
                Path dst = virt.resolve(e.getKey());
                if (!Files.exists(dst)) {
                    Files.createDirectories(dst.getParent());
                    Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            info.gameAssets = virt;
        }
        return info;
    }

    public static List<String> classpathStrings(Resolved r) {
        List<String> out = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        for (Lib l : r.classpath) seen.add(l.file.toString());
        seen.add(r.clientJar.toString());
        out.addAll(seen);
        return Collections.unmodifiableList(out);
    }
}
