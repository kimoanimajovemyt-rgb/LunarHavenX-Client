#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$SCRIPT_DIR/build/libs/LunarHavenX-Client-1.8.9-0.5.9-alpha.jar"
if [[ ! -f "$JAR" ]]; then
  echo "JAR não encontrado: $JAR" >&2
  exit 1
fi
if [[ -n "${MINECRAFT_DIR:-}" ]]; then
  MC_DIR="$MINECRAFT_DIR"
elif [[ -d "$HOME/.minecraft" ]]; then
  MC_DIR="$HOME/.minecraft"
else
  read -r -p "Informe o caminho da pasta .minecraft: " MC_DIR
fi
MODS="$MC_DIR/mods"
mkdir -p "$MODS"
TARGET="$MODS/$(basename "$JAR")"
STAMP="$(date +%Y%m%d-%H%M%S)"
shopt -s nullglob
for OLD in "$MODS"/LunarHavenX-Client-1.8.9-*.jar; do
  [[ -f "$OLD" ]] || continue
  BACKUP="$OLD.backup.$STAMP"
  mv -f -- "$OLD" "$BACKUP"
  echo "Backup criado: $BACKUP"
done
cp -f "$JAR" "$TARGET"
echo "LunarHavenX instalado em: $TARGET"
echo "Versões LunarHavenX anteriores foram arquivadas; nenhum outro mod foi alterado."
