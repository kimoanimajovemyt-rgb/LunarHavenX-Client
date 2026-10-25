# Testes realizados (sessão de 24/10/2026)
- Compilação de `launcher-core` e `launcher-desktop` com JDK 21 (`-source/-target 17`): OK.
- `CoreTests`: **30 ok, 0 falhas** (JSON, coordenadas Maven, regras de SO/feature, detecção de versão do Java, merge Forge+vanilla, dedup de bibliotecas, URL normalizada, comando de lançamento 1.8.9, ocultação de token, persistência de config).
- Jar empacotado e executado: `--version` OK. Abrir a janela: **não testado** (ambiente sem display).
- **Não testado:** downloads reais, login Microsoft, início do Minecraft, `jpackage`/`.exe`, workflow do GitHub.
