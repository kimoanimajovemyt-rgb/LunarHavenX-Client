#!/usr/bin/env python3
"""Apply LunarHavenX Android branding and remove upstream signing material in CI."""
from pathlib import Path
import re
import sys

if len(sys.argv) != 2:
    raise SystemExit("Uso: prepare_upstream.py <caminho-do-submodulo>")
root = Path(sys.argv[1]).resolve()
props = root / "ZalithLauncher" / "gradle.properties"
gradle = root / "ZalithLauncher" / "build.gradle.kts"
main_screen = root / "ZalithLauncher" / "src/main/java/com/movtery/zalithlauncher/ui/screens/main/MainScreen.kt"
for path in (props, gradle, main_screen):
    if not path.is_file():
        raise SystemExit(f"Arquivo upstream esperado ausente: {path}")

branding = {
    "launcher_name": "LunarHavenX",
    "launcher_app_name": "LunarHavenX",
    "launcher_short_name": "LHX",
    "url_home": "https://github.com/kimoanimajovemyt-rgb/LunarHavenX-Client",
}
lines = props.read_text(encoding="utf-8").splitlines()
seen = set()
updated = []
for line in lines:
    key = line.split("=", 1)[0].strip() if "=" in line and not line.lstrip().startswith("#") else ""
    if key in ("default_store_password", "default_key_password"):
        continue
    if key in branding:
        updated.append(f"{key}={branding[key]}")
        seen.add(key)
    else:
        updated.append(line)
missing = set(branding) - seen
if missing:
    raise SystemExit("Propriedades de marca não encontradas: " + ", ".join(sorted(missing)))
props.write_text("\n".join(updated) + "\n", encoding="utf-8")

text = gradle.read_text(encoding="utf-8")
for key in ("defaultStorePassword", "defaultKeyPassword"):
    pattern = rf"(?m)^val {key} = .*\n"
    text, count = re.subn(pattern, "", text)
    if count != 1:
        raise SystemExit(f"Esperava uma declaração {key}; encontrei {count}")
debug_block = '''        create("debugBuild") {
            storeFile = file("zalith_launcher_debug.jks")
            storePassword = defaultStorePassword
            keyAlias = "movtery_zalith_debug"
            keyPassword = defaultKeyPassword
        }
'''
if text.count(debug_block) != 1:
    raise SystemExit("Configuração debug upstream mudou; revisão manual necessária")
text = text.replace(debug_block, "")
line = '            signingConfig = signingConfigs.getByName("debugBuild")\n'
if text.count(line) != 1:
    raise SystemExit("Assinatura debug upstream mudou; revisão manual necessária")
text = text.replace(line, "")
line = '        applicationId = zalithPackageName\n'
if text.count(line) != 1:
    raise SystemExit("applicationId upstream mudou; revisão manual necessária")
text = text.replace(line, '        applicationId = "com.lunarhavenx.launcher.android"\n')
line = '        applicationIdSuffix = ".v2"\n'
if text.count(line) != 1:
    raise SystemExit("applicationIdSuffix upstream mudou; revisão manual necessária")
text = text.replace(line, "", 1)
gradle.write_text(text, encoding="utf-8")

for name in ("zalith_launcher.jks", "zalith_launcher_debug.jks"):
    key_file = root / "ZalithLauncher" / name
    if key_file.exists():
        key_file.unlink()

screen = main_screen.read_text(encoding="utf-8")
marker = '''            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
'''
notice = '''            Text(
                text = "LunarHavenX — Unofficial Modified Versions",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall
            )

'''
if screen.count(marker) != 1:
    raise SystemExit("Ponto de inserção da identificação legal mudou; revisão manual necessária")
if "LunarHavenX — Unofficial Modified Versions" in screen:
    raise SystemExit("Identificação legal já aplicada; evite duplicar")
screen = screen.replace(marker, notice + marker, 1)
main_screen.write_text(screen, encoding="utf-8")
print("Preparação Android concluída: marca aplicada, assinatura upstream removida, sem chaves de release.")
