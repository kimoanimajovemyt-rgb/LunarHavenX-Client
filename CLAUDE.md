# Contexto de continuidade para Claude

Este repositório agora contém uma árvore de fontes navegável em `LunarHavenX-1.0.0/`. Preserve a distinção entre launcher e mod ao planejar mudanças.

## Estado verificado

- `LunarHavenX-1.0.0/launcher-core/` e `launcher-desktop/` são as fontes disponíveis do launcher desktop; a configuração Gradle raiz declara 1.0.0.
- `LunarHavenX-1.0.0/client-mod/` é um mod Forge para Minecraft Java 1.8.9; o `build.gradle` e as fontes atuais declaram 0.5.9-alpha.
- A execução do GitHub Actions [#1](https://github.com/kimoanimajovemyt-rgb/LunarHavenX-Client/actions/runs/40146851092) compilou e publicou o artefato do mod 0.5.9-alpha. Isso não confirma execução dentro do Minecraft.
- Não há módulo Android do launcher nem APK neste snapshot. `docs/PLANO-ANDROID-IOS.md` descreve planejamento, não implementação.

## Direção do produto

O objetivo informado pelo proprietário é continuar o LunarHavenX como launcher independente para PC, com Android planejado. Trate isso como objetivo futuro: o mod Forge existente não é, por si só, um launcher independente. Inspecione o código antes de propor alterações e não declare suporte, builds ou testes que não foram realizados.
