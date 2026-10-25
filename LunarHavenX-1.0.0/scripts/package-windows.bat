@echo off
rem Gera o instalador .exe (jpackage). Requer JDK 17+ e WiX Toolset 3.x no PATH (para --type exe).
rem Sem WiX, use:  package-windows.bat app-image   (gera pasta executavel LunarHavenX\LunarHavenX.exe)
cd /d "%~dp0\.."
call scripts\build.bat || exit /b 1
set TYPE=%1
if "%TYPE%"=="" set TYPE=exe
if exist release rmdir /s /q release
jpackage --type %TYPE% --input dist --main-jar LunarHavenX-Launcher.jar ^
  --main-class com.lunarhavenx.launcher.desktop.Main --name LunarHavenX --app-version 1.0.0 ^
  --vendor "LunarHavenX" --description "LunarHavenX Client Launcher" ^
  --icon launcher-desktop\src\main\resources\assets\icon.ico ^
  --win-menu --win-shortcut --win-dir-chooser --dest release || exit /b 1
echo Pronto: pasta release\
