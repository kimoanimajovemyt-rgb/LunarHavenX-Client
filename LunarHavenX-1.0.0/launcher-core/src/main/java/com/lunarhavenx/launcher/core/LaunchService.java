package com.lunarhavenx.launcher.core;

import com.lunarhavenx.launcher.core.auth.Account;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Consumer;

/** Orquestra: Java 8 → versão → Forge → bibliotecas → assets → mod LunarHavenX → processo do jogo. */
public final class LaunchService {
    private final Path root;
    private final LauncherConfig config;

    public LaunchService(Path root, LauncherConfig config) { this.root = root; this.config = config; }

    public Path instanceDir() { return root.resolve("instances").resolve("default"); }

    public Process prepareAndLaunch(Account account, Progress p, Consumer<String> log) throws IOException {
        VersionManager vm = new VersionManager(root);
        Path java = JavaRuntime.find8(root, config.javaPath, p);
        log.accept("Java: " + java);

        vm.ensureVanillaJson(ForgeInstaller.MC, p);
        String versionId = ForgeInstaller.MC;
        if (config.useForge) versionId = ForgeInstaller.ensureInstalled(root, p);

        VersionManager.Resolved r = vm.resolve(vm.loadMerged(versionId), new java.util.HashSet<String>());
        vm.ensureClientJar(r, p);
        vm.ensureLibraries(r, p);
        Path natives = root.resolve("versions").resolve(versionId).resolve("natives");
        vm.extractNatives(r, natives);
        VersionManager.AssetInfo assets = vm.ensureAssets(r.json, p);

        Path game = instanceDir();
        Files.createDirectories(game.resolve("mods"));
        if (config.useForge) installClientMod(game.resolve("mods"), log);

        LaunchEngine.Options o = new LaunchEngine.Options();
        o.java = java; o.gameDir = game; o.nativesDir = natives; o.account = account;
        o.ramMb = config.ramMb; o.extraJvmArgs = config.extraJvmArgs; o.assets = assets; o.versionId = versionId;
        List<String> cmd = LaunchEngine.buildCommand(r, o);
        p.update("Iniciando Minecraft…", 1.0);
        return LaunchEngine.launch(cmd, game, account, log);
    }

    /** Procura o JAR do mod em ./client, ao lado do launcher ou em LHX_CLIENT_DIR e copia para mods/. */
    void installClientMod(Path modsDir, Consumer<String> log) throws IOException {
        List<Path> dirs = new java.util.ArrayList<>();
        String env = System.getenv("LHX_CLIENT_DIR");
        if (env != null) dirs.add(Paths.get(env));
        dirs.add(Paths.get("client"));
        try {
            Path self = Paths.get(LaunchService.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path base = Files.isDirectory(self) ? self : self.getParent();
            dirs.add(base.resolve("client"));
            if (base.getParent() != null) dirs.add(base.getParent().resolve("client"));
        } catch (Exception ignored) { }
        Path found = null;
        for (Path d : dirs) {
            if (!Files.isDirectory(d)) continue;
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(d, "LunarHavenX-Client-*.jar")) {
                for (Path j : ds) if (found == null || j.getFileName().toString().compareTo(found.getFileName().toString()) > 0) found = j;
            }
            if (found != null) break;
        }
        if (found == null) { log.accept("AVISO: JAR do cliente LunarHavenX não encontrado (pasta 'client'); iniciando sem o mod."); return; }
        try (DirectoryStream<Path> old = Files.newDirectoryStream(modsDir, "LunarHavenX-Client-*.jar")) {
            for (Path o : old) Files.deleteIfExists(o);
        }
        Files.copy(found, modsDir.resolve(found.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        log.accept("Mod instalado: " + found.getFileName());
    }
}
