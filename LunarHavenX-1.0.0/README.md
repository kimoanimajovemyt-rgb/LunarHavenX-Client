# LunarHavenX — fontes disponíveis

Este pacote combina o ZIP do projeto completo (`LunarHavenX-1.0.0-fonte_1792865569119.zip`) com o projeto de cliente mais recente enviado (`LunarHavenX-Client-1.8.9-0.5.9-alpha-project_1792865820191.zip`). Os 25 fontes Java e recursos do cliente são idênticos nos dois pacotes; o segundo também fornece a referência histórica 0.5.2, agora preservada em `client-mod/reference/`.

## O que há no pacote

- `client-mod/`: código-fonte disponível do mod Minecraft Java 1.8.9/Forge, recursos e configuração ForgeGradle 2.1. Há 25 arquivos `.java`.
- `launcher-core/` e `launcher-desktop/`: código-fonte disponível para o núcleo compartilhável e a interface desktop Swing; juntos somam 15 arquivos `.java`.
- `tests/`: código de testes offline do núcleo.
- `scripts/`, `.github/workflows/` e `docs/`: scripts, fluxo de build Windows existente e documentação.
- `client-mod/prebuilt/`: um JAR pré-compilado do cliente 0.5.9-alpha. **Esse JAR não é código-fonte editável nem prova que os fontes atuais compilam.**

Foram acrescentados `settings.gradle`, `build.gradle` e `gradle.properties` para compilar o launcher desktop e executar os testes do núcleo. O mod Forge continua como build separado porque usa a cadeia legada ForgeGradle 2.1/Gradle 2.7.

## Estado de verificação

Esta sessão não conseguiu compilar nem executar o projeto: não há `java`, `javac` ou `gradle` instalados no ambiente. Portanto, não declaro os builds ou testes aprovados. Também não foram compilados nem testados `.exe` ou `.apk`.

## Compilar o launcher desktop

Requer JDK 17 ou superior e Gradle 8.7 ou superior. Na raiz do projeto:

```sh
gradle clean check :launcher-desktop:jar
```

O JAR desktop esperado fica em `launcher-desktop/build/libs/`. Para executar a interface em um ambiente com tela:

```sh
gradle :launcher-desktop:run
```

Também permanecem os scripts originais `scripts/build.sh` e `scripts/build.bat`; eles usam `javac`/`jar`, compilam as fontes do launcher, executam `tests/src/CoreTests.java` e copiam o JAR pré-compilado do mod para `dist/client/`.

## Compilar o mod Forge 1.8.9

Requer JDK 8, conexão com a internet e os repositórios do Forge. O wrapper incluído em `client-mod/` é Gradle 2.7 e só vale para esse módulo. No Linux/macOS:

```sh
cd client-mod
./gradlew setupDecompWorkspace build
```

No Windows, execute `gradlew.bat setupDecompWorkspace build` dentro de `client-mod`. O JAR de saída esperado, caso a compilação passe, fica em `client-mod/build/libs/`. Este build não foi executado nesta sessão.

## Continuar pelo Android, sem PC

O ZIP contém fontes Java do cliente e do launcher desktop, mas **não contém um aplicativo Android LunarHavenX**, módulo Android, configuração Android Gradle ou APK. O plano em `docs/PLANO-ANDROID-IOS.md` é apenas planejamento. O `client-mod` é um mod Forge, não um launcher Android.

Para continuar usando apenas o celular, você pode guardar os arquivos em um repositório GitHub pelo navegador/app, editar os fontes no editor web do GitHub e abrir **Actions** para executar o workflow desktop incluído. Esse workflow ainda precisa ser executado para confirmar seu resultado; ele não produz um APK. Para desenvolver o launcher Android independente, será necessário implementar um módulo Android e integrar/validar um runtime compatível antes de gerar e testar qualquer APK.

## Arquivo histórico de referência

O arquivo `client-mod/reference/LunarHavenX-source-recovered-0.5.2.zip` foi incluído a partir do ZIP mais recente do cliente. Ele contém a fonte recuperada da versão 0.5.2, não substitui os fontes atuais 0.5.9-alpha em `client-mod/src/main/java` e não é necessário para o build atual. Veja o inventário para os limites confirmados, incluindo a ausência do aplicativo Android independente.
