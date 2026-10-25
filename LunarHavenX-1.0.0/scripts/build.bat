@echo off
rem Compila nucleo + launcher desktop, roda os testes e gera dist\LunarHavenX-Launcher.jar
rem Requer JDK 17+ (javac e jar no PATH).
setlocal enabledelayedexpansion
cd /d "%~dp0\.."
if exist build rmdir /s /q build
if exist dist rmdir /s /q dist
mkdir build\classes build\tests dist\client
set SRC=
for /r launcher-core %%f in (*.java) do set SRC=!SRC! "%%f"
for /r launcher-desktop %%f in (*.java) do set SRC=!SRC! "%%f"
javac --release 17 -encoding UTF-8 -d build\classes %SRC% || exit /b 1
javac --release 17 -encoding UTF-8 -cp build\classes -d build\tests tests\src\CoreTests.java || exit /b 1
java -cp build\classes;build\tests CoreTests || exit /b 1
xcopy /e /i /q launcher-desktop\src\main\resources\assets build\classes\assets >nul
echo Main-Class: com.lunarhavenx.launcher.desktop.Main> build\manifest.txt
jar --create --file dist\LunarHavenX-Launcher.jar --manifest build\manifest.txt -C build\classes . || exit /b 1
copy client-mod\prebuilt\LunarHavenX-Client-*.jar dist\client\ >nul
echo OK: dist\LunarHavenX-Launcher.jar
