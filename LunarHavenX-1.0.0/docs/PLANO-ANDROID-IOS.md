# Próximas etapas

## Android (etapa 4 — base aprovada; APK ainda não validado)
O launcher Android será um app separado, derivado do Zalith Launcher 2 e fixado como submódulo em launcher-android/upstream. A licença oficial é GPL-3.0 com termos adicionais: nome distinto (sem “ZalithLauncher”/“ZL”), indicação visível de versão modificada não oficial, preservação dos avisos de copyright e sem copiar os documentos proprietários de Termos/Privacidade. O Android não reutiliza a interface Swing do PC nem o processo de runtime de desktop do launcher-core; o reuso de lógica pura fica para uma etapa posterior.

O workflow Android compila somente um APK debug, removendo do checkout temporário os keystores/propriedades de assinatura do upstream e usando a assinatura debug padrão. Não há chave de release, OAuth Microsoft do LunarHavenX configurado nem teste em aparelho real. Um Actions verde comprova compilação, não login nem jogo funcional. O plano de teste real continua incluindo ARM64, GPU/renderizador, controles de toque/gamepad e Forge 1.8.9.

## Migração do cliente (etapa 5)
Aplicar a arte de menu/loading nova nas classes GuiLunarMainMenu/GuiLunarLoading; corrigir a limitação do "72%" fixo (barra real); gamepad e módulos novos. Cada item testado dentro do jogo antes de ser listado como pronto.

## iOS (etapa 8)
Limitações: a Apple proíbe JIT em apps comuns (distribuição pela App Store), não há JVM oficial para iOS e o Minecraft Java depende de OpenGL (descontinuado no iOS; teria que ser traduzido para Metal). Viável só como experimento via sideload/TestFlight com JVM interpretada + camada GL→Metal, com desempenho baixo. **Não há compatibilidade iOS.**
