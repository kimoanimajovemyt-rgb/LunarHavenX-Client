import com.lunarhavenx.launcher.core.*;
import com.lunarhavenx.launcher.core.auth.Account;
import java.nio.file.*;
import java.util.*;

/** Testes offline da lógica pura do núcleo (sem rede). Saída ≠ 0 se algo falhar. */
public class CoreTests {
    static int failures = 0, passed = 0;

    static void check(boolean ok, String name) {
        if (ok) { passed++; System.out.println("  ok   " + name); }
        else { failures++; System.out.println("  FAIL " + name); }
    }

    static void eq(Object a, Object b, String name) {
        check(Objects.equals(a, b), name + (Objects.equals(a, b) ? "" : "  (obtido=" + a + ", esperado=" + b + ")"));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Json");
        Object j = Json.parse("{\"a\":[1,2.5,true,null,\"x\\n\\u00e9\"],\"b\":{\"c\":\"d\"}}");
        eq(Json.str(j, "b", "c"), "d", "path aninhado");
        eq(Json.list(Json.path(j, "a")).get(0), 1L, "inteiro vira Long");
        eq(Json.list(Json.path(j, "a")).get(4), "x\né", "escapes");
        eq(Json.write(Json.parse(Json.write(j))).equals(Json.write(j)), true, "ida e volta");
        boolean threw = false;
        try { Json.parse("{\"a\":1} lixo"); } catch (IllegalArgumentException e) { threw = true; }
        check(threw, "rejeita lixo após o JSON");

        System.out.println("Maven / regras");
        eq(VersionManager.mavenPath("net.minecraft:launchwrapper:1.12", null), "net/minecraft/launchwrapper/1.12/launchwrapper-1.12.jar", "mavenPath simples");
        eq(VersionManager.mavenPath("org.lwjgl.lwjgl:lwjgl-platform:2.9.4-nightly-20150209", "natives-windows"),
                "org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-windows.jar", "mavenPath com classifier");
        String os = Platform.osName();
        String other = os.equals("windows") ? "linux" : "windows";
        List<Object> allowOs = Json.list(Json.parse("[{\"action\":\"allow\",\"os\":{\"name\":\"" + os + "\"}}]"));
        List<Object> allowOther = Json.list(Json.parse("[{\"action\":\"allow\",\"os\":{\"name\":\"" + other + "\"}}]"));
        List<Object> allowAllDenyOs = Json.list(Json.parse("[{\"action\":\"allow\"},{\"action\":\"disallow\",\"os\":{\"name\":\"" + os + "\"}}]"));
        List<Object> feat = Json.list(Json.parse("[{\"action\":\"allow\",\"features\":{\"is_demo_user\":true}}]"));
        check(VersionManager.rulesAllow(null, new HashSet<String>()), "sem regras = permitido");
        check(VersionManager.rulesAllow(allowOs, new HashSet<String>()), "allow para o SO atual");
        check(!VersionManager.rulesAllow(allowOther, new HashSet<String>()), "allow para outro SO = bloqueado");
        check(!VersionManager.rulesAllow(allowAllDenyOs, new HashSet<String>()), "allow geral + disallow do SO atual = bloqueado");
        check(!VersionManager.rulesAllow(feat, new HashSet<String>()), "feature ausente = bloqueado");
        check(VersionManager.rulesAllow(feat, new HashSet<>(Arrays.asList("is_demo_user"))), "feature presente = permitido");

        System.out.println("Java");
        eq(JavaRuntime.parseMajor("openjdk version \"1.8.0_402\""), 8, "Java 1.8.0_402 → 8");
        eq(JavaRuntime.parseMajor("openjdk version \"17.0.10\" 2024-01-16"), 17, "Java 17");
        eq(JavaRuntime.parseMajor("java version \"21\""), 21, "Java 21");
        eq(JavaRuntime.parseMajor("lixo"), -1, "saída desconhecida");

        System.out.println("Merge Forge + vanilla, resolve e comando de lançamento (1.8.9, formato antigo)");
        Path tmp = Files.createTempDirectory("lhx-test");
        VersionManager vm = new VersionManager(tmp);
        Path vdir = tmp.resolve("versions/1.8.9");
        Files.createDirectories(vdir);
        Files.write(vdir.resolve("1.8.9.json"), ("{\"id\":\"1.8.9\",\"mainClass\":\"net.minecraft.client.main.Main\","
                + "\"minecraftArguments\":\"--username ${auth_player_name} --version ${version_name}\","
                + "\"assetIndex\":{\"id\":\"1.8\",\"url\":\"x\",\"sha1\":\"y\"},"
                + "\"downloads\":{\"client\":{\"url\":\"u\",\"sha1\":\"s\",\"size\":1}},"
                + "\"libraries\":[{\"name\":\"com.google.guava:guava:17.0\",\"downloads\":{\"artifact\":{\"path\":\"com/google/guava/guava/17.0/guava-17.0.jar\",\"url\":\"u\",\"sha1\":\"a\",\"size\":1}}},"
                + "{\"name\":\"org.lwjgl.lwjgl:lwjgl-platform:2.9.4\",\"natives\":{\"windows\":\"natives-windows\",\"linux\":\"natives-linux\",\"osx\":\"natives-osx\"},"
                + "\"extract\":{\"exclude\":[\"META-INF/\"]},\"downloads\":{\"classifiers\":{\"natives-windows\":{\"path\":\"p/win.jar\",\"url\":\"u\",\"sha1\":\"b\",\"size\":1},"
                + "\"natives-linux\":{\"path\":\"p/lin.jar\",\"url\":\"u\",\"sha1\":\"b\",\"size\":1},\"natives-osx\":{\"path\":\"p/osx.jar\",\"url\":\"u\",\"sha1\":\"b\",\"size\":1}}}}]}").getBytes("UTF-8"));
        Path fdir = tmp.resolve("versions/1.8.9-forge");
        Files.createDirectories(fdir);
        Files.write(fdir.resolve("1.8.9-forge.json"), ("{\"id\":\"1.8.9-forge\",\"inheritsFrom\":\"1.8.9\",\"jar\":\"1.8.9\","
                + "\"mainClass\":\"net.minecraft.launchwrapper.Launch\","
                + "\"minecraftArguments\":\"--username ${auth_player_name} --version ${version_name} --tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker\","
                + "\"libraries\":[{\"name\":\"net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9\",\"clientreq\":true},"
                + "{\"name\":\"net.minecraft:launchwrapper:1.12\",\"url\":\"http://files.minecraftforge.net/maven/\",\"clientreq\":true},"
                + "{\"name\":\"com.google.guava:guava:18.0\",\"clientreq\":true},"
                + "{\"name\":\"only.server:lib:1\",\"clientreq\":false}]}").getBytes("UTF-8"));
        Map<String, Object> merged = vm.loadMerged("1.8.9-forge");
        eq(Json.str(merged, "mainClass"), "net.minecraft.launchwrapper.Launch", "mainClass do Forge prevalece");
        eq(Json.str(merged, "_jarId"), "1.8.9", "jar do vanilla");
        VersionManager.Resolved r = vm.resolve(merged, new HashSet<String>());
        eq(r.classpath.size(), 3, "classpath: forge, launchwrapper, guava (sem lib server-only, sem duplicata)");
        check(r.classpath.get(2).name.contains("guava:18.0"), "guava 18 (Forge) vence guava 17 (vanilla)");
        eq(r.natives.size(), 1, "1 lib de natives para o SO atual");
        eq(r.classpath.get(1).url, "https://maven.minecraftforge.net/net/minecraft/launchwrapper/1.12/launchwrapper-1.12.jar", "URL do Forge normalizada para https");

        Account acc = new Account();
        acc.name = "Teste"; acc.uuid = "00000000000000000000000000000001"; acc.accessToken = "SEGREDO123";
        LaunchEngine.Options o = new LaunchEngine.Options();
        o.java = Paths.get("/jre8/bin/java"); o.gameDir = tmp.resolve("game"); o.nativesDir = tmp.resolve("natives");
        o.account = acc; o.ramMb = 3072; o.versionId = "1.8.9-forge";
        o.assets = new VersionManager.AssetInfo();
        o.assets.indexName = "1.8"; o.assets.assetsRoot = tmp.resolve("assets"); o.assets.gameAssets = tmp.resolve("assets");
        List<String> cmd = LaunchEngine.buildCommand(r, o);
        check(cmd.contains("-Xmx3072M"), "RAM aplicada");
        check(cmd.contains("net.minecraft.launchwrapper.Launch"), "mainClass no comando");
        check(cmd.indexOf("-cp") >= 0 && cmd.indexOf("-cp") < cmd.indexOf("net.minecraft.launchwrapper.Launch"), "-cp antes da mainClass");
        check(cmd.contains("--tweakClass") && cmd.contains("net.minecraftforge.fml.common.launcher.FMLTweaker"), "tweakClass do Forge");
        eq(cmd.get(cmd.indexOf("--username") + 1), "Teste", "placeholder auth_player_name");
        check(!LaunchEngine.redact(cmd, acc).contains("SEGREDO123"), "token removido do log");

        System.out.println("Config");
        LauncherConfig c = new LauncherConfig(tmp);
        c.ramMb = 4096; c.msClientId = "abc"; c.save();
        LauncherConfig c2 = LauncherConfig.load(tmp);
        eq(c2.ramMb, 4096, "config persiste RAM");

        System.out.println("\n" + passed + " ok, " + failures + " falhas");
        System.exit(failures == 0 ? 0 : 1);
    }
}
