# Decisões de arquitetura

- **Java puro, sem dependências externas** no núcleo: compila em qualquer JDK, sem Gradle/Maven (que exigiriam rede) e reaproveitável no Android. Troca: JSON próprio mínimo (testado) em vez de Gson.
- **HttpURLConnection** em vez de `java.net.http`: a segunda não existe no Android.
- **Swing + jpackage** para o `.exe`: caminho mais curto até um executável real, sem runtime Electron/.NET. Troca: visual menos rico que JavaFX/Compose; dá para evoluir depois.
- **Forge sem o instalador gráfico**: lemos o `install_profile.json` (formato antigo do 1.8.9) e extraímos o jar; menos partes móveis que executar o instalador no Java moderno.
- **Java 8 obrigatório** para 1.8.9 (LaunchWrapper quebra no 9+); o launcher gerencia isso sozinho.
- **Instância única "default"** por ora (`instances/default`); `libraries/assets/versions` compartilhados na raiz.
- **Não foram criados módulos vazios**: `launcher-android`, `client-modules`, etc. só entram quando tiverem código.

## Riscos conhecidos
- Fluxo Forge 1.8.9 e URLs do Maven foram escritos de acordo com o formato conhecido, **sem teste real de download**.
- A aprovação do Application ID pela Mojang está fora do nosso controle.
- Sem LICENSE no projeto original: confirme a licença do seu código/arte antes de distribuir.
