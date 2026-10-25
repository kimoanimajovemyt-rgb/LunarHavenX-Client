package com.lunarhavenx.launcher.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Instala o Forge 1.8.9 sem executar o instalador gráfico: lê o install_profile.json do
 * instalador oficial, grava o JSON de versão e extrai o jar do Forge. As demais bibliotecas
 * são baixadas pelo VersionManager. O instalador é baixado do Maven oficial do Forge.
 */
public final class ForgeInstaller {
    private ForgeInstaller() { }

    public static final String MC = "1.8.9";
    public static final String FORGE_FULL = "1.8.9-11.15.1.2318-1.8.9";
    public static final String INSTALLER_URL =
            "https://maven.minecraftforge.net/net/minecraftforge/forge/" + FORGE_FULL + "/forge-" + FORGE_FULL + "-installer.jar";

    /** @return id da versão Forge pronta em versions/. */
    public static String ensureInstalled(Path root, Progress p) throws IOException {
        Path installer = root.resolve("installers").resolve("forge-" + FORGE_FULL + "-installer.jar");
        if (!Files.exists(installer)) {
            p.update("Baixando instalador do Forge…", -1);
            Http.download(INSTALLER_URL, installer, null);
        }
        try (ZipFile zf = new ZipFile(installer.toFile())) {
            ZipEntry pe = zf.getEntry("install_profile.json");
            if (pe == null) throw new IOException("install_profile.json ausente no instalador do Forge");
            String text;
            try (InputStream in = zf.getInputStream(pe)) { text = new String(readAll(in), StandardCharsets.UTF_8); }
            Map<String, Object> profile = Json.obj(Json.parse(text));
            Map<String, Object> versionInfo = Json.obj(profile.get("versionInfo"));
            String id = Json.str(versionInfo, "id");
            if (id == null) throw new IOException("versionInfo.id ausente no instalador do Forge");
            Path vjson = root.resolve("versions").resolve(id).resolve(id + ".json");
            Files.createDirectories(vjson.getParent());
            Files.write(vjson, Json.write(versionInfo).getBytes(StandardCharsets.UTF_8));

            String filePath = Json.str(profile, "install", "filePath");
            String mavenName = Json.str(profile, "install", "path");
            if (filePath == null || mavenName == null) throw new IOException("install.filePath/path ausentes");
            Path dest = root.resolve("libraries").resolve(VersionManager.mavenPath(mavenName, null));
            if (!Files.exists(dest)) {
                ZipEntry fe = zf.getEntry(filePath);
                if (fe == null) throw new IOException("Jar do Forge não encontrado no instalador: " + filePath);
                Files.createDirectories(dest.getParent());
                try (InputStream in = zf.getInputStream(fe)) { Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING); }
            }
            return id;
        }
    }

    private static byte[] readAll(InputStream in) throws IOException {
        java.io.ByteArrayOutputStream o = new java.io.ByteArrayOutputStream();
        byte[] b = new byte[8192];
        int n;
        while ((n = in.read(b)) > 0) o.write(b, 0, n);
        return o.toByteArray();
    }
}
