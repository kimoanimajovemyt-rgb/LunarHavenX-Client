@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
set "JAR=%SCRIPT_DIR%build\libs\LunarHavenX-Client-1.8.9-0.5.9-alpha.jar"
if not exist "%JAR%" (
  echo JAR nao encontrado: %JAR%
  pause
  exit /b 1
)
set /p "MC_DIR=Informe o caminho da pasta .minecraft: "
if not exist "%MC_DIR%" (
  echo Pasta nao encontrada.
  pause
  exit /b 1
)
if not exist "%MC_DIR%\mods" mkdir "%MC_DIR%\mods"
set "STAMP=%DATE:/=-%_%TIME::=-%"
set "STAMP=%STAMP:.=-%"
set "STAMP=%STAMP: =0%"
for %%F in ("%MC_DIR%\mods\LunarHavenX-Client-1.8.9-*.jar") do (
  if exist "%%~fF" (
    copy /Y "%%~fF" "%%~fF.backup.%STAMP%" >nul
    if errorlevel 1 (
      echo Falha ao criar backup de %%~fF
      pause
      exit /b 1
    )
    del "%%~fF"
    if exist "%%~fF" (
      echo Falha ao retirar a versao antiga %%~fF
      pause
      exit /b 1
    )
    echo Backup criado para %%~fF
  )
)
set "TARGET=%MC_DIR%\mods\LunarHavenX-Client-1.8.9-0.5.9-alpha.jar"
copy /Y "%JAR%" "%TARGET%" >nul
echo LunarHavenX instalado em %TARGET%
echo Versoes antigas do LunarHavenX foram arquivadas; nenhum outro mod foi alterado.
pause
