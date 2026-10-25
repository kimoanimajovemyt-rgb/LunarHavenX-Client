#!/usr/bin/env bash
# Compila núcleo + launcher desktop, roda os testes e gera dist/LunarHavenX-Launcher.jar
# Requer JDK 17+ (javac e jar no PATH).
set -euo pipefail
cd "$(dirname "$0")/.."
rm -rf build dist && mkdir -p build/classes build/tests dist/client
javac --release 17 -d build/classes $(find launcher-core launcher-desktop -name '*.java')
javac --release 17 -cp build/classes -d build/tests tests/src/CoreTests.java
java -cp build/classes:build/tests CoreTests
cp -r launcher-desktop/src/main/resources/assets build/classes/
printf 'Main-Class: com.lunarhavenx.launcher.desktop.Main\n' > build/manifest.txt
jar --create --file dist/LunarHavenX-Launcher.jar --manifest build/manifest.txt -C build/classes .
cp client-mod/prebuilt/LunarHavenX-Client-*.jar dist/client/
echo "OK -> dist/LunarHavenX-Launcher.jar  (java -jar dist/LunarHavenX-Launcher.jar)"
