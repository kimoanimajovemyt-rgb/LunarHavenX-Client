# Inventário e limites do pacote recuperado

## Arquivos examinados

- `LunarHavenX-1.0.0-fonte_1792865569119.zip` (7.553.607 bytes; 83 entradas).
- `LunarHavenX-Client-1.8.9-0.5.9-alpha-project_1792865820191.zip` (4.462.045 bytes; 83 entradas).

Os dois ZIPs foram listados antes da extração; não continham caminhos absolutos nem componentes `..`. O conteúdo foi extraído para áreas temporárias de auditoria, sem sobrescrever outro projeto.

## Conteúdo encontrado

- 25 fontes Java em `client-mod/src/main/java/`.
- 15 fontes Java em `launcher-core/` e `launcher-desktop/`.
- 1 fonte de teste em `tests/src/CoreTests.java`.
- 18 arquivos de recursos em `client-mod/src/main/resources/` e `launcher-desktop/src/main/resources/`.
- Configuração `client-mod/build.gradle`, wrapper Gradle 2.7 e `client-mod/gradle/wrapper/gradle-wrapper.jar`.
- Referência histórica `client-mod/reference/LunarHavenX-source-recovered-0.5.2.zip` (62 entradas; contém fontes e recursos da versão recuperada 0.5.2).
- Um JAR pré-compilado: `client-mod/prebuilt/LunarHavenX-Client-1_8_9-0_5_9-alpha.jar`.
- Documentação, scripts de build e workflow GitHub Actions para desktop.

Os 25 fontes Java e recursos do mod no ZIP mais recente têm conteúdo idêntico aos correspondentes do ZIP combinado recebido anteriormente. A referência 0.5.2 é histórica; os fontes atuais do cliente continuam em `client-mod/src/main/java`.

## Ausências e limites confirmados

1. `settings.gradle` e `build.gradle` na raiz não estavam no ZIP combinado original. Foram acrescentados neste pacote para o build Gradle do launcher desktop e dos testes, sem alterar os fontes Java existentes.
2. Projeto Android independente do launcher (módulo Android, manifesto e arquivos Gradle Android) e APK — não presentes. O documento `docs/PLANO-ANDROID-IOS.md` descreve uma etapa futura, não um aplicativo implementado.

O ZIP mais recente é suficiente para os fontes disponíveis do mod 0.5.9-alpha e contém a referência histórica 0.5.2. Ele não contém o código de um launcher Android independente.

## Verificação de compilação

No ambiente desta sessão, `java`, `javac` e `gradle` não estão disponíveis. A estrutura e os arquivos do ZIP foram inspecionados, mas nenhum código foi compilado e nenhum teste foi executado aqui. Não há confirmação de funcionamento do launcher, do cliente, de `.exe` ou de `.apk`.
