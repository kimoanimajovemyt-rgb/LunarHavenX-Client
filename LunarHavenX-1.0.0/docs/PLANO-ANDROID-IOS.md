# Próximas etapas

## Android (etapa 4 — ainda não iniciada)
Minecraft Java não roda em app Android comum. Caminho viável: integrar um runtime já existente (PojavLauncher/Zalith: JVM + GL4ES + LWJGL adaptado; verificar licença LGPL-3 e exigências) em um app Kotlin, reutilizando `launcher-core` (já sem Swing e sem `java.net.http`). Itens: ABI arm64-v8a, RAM, controles de toque/gamepad (L1 atacar, R1 usar, L2/R2 trocar item), perfil "celular fraco". O APK só será entregue depois de compilar e instalar num aparelho real. Compilar exige Android SDK: usar GitHub Actions (ubuntu).

## Migração do cliente (etapa 5)
Aplicar a arte de menu/loading nova nas classes `GuiLunarMainMenu`/`GuiLunarLoading`; corrigir a limitação do "72%" fixo (barra real); gamepad e módulos novos. Cada item testado dentro do jogo antes de ser listado como pronto.

## iOS (etapa 8)
Limitações: a Apple proíbe JIT em apps comuns (distribuição pela App Store), não há JVM oficial para iOS e o Minecraft Java depende de OpenGL (descontinuado no iOS; teria que ser traduzido para Metal). Viável só como experimento via sideload/TestFlight com JVM interpretada + camada GL→Metal, com desempenho baixo. **Não há compatibilidade iOS.**
