# Compilação

Leia também o `README.md` da raiz e `INVENTARIO-RECUPERACAO.md`.

## Launcher desktop e núcleo

Requer JDK 17+ e Gradle 8.7+. Na raiz:

```sh
gradle clean check :launcher-desktop:jar
```

Para tentar abrir a interface em um ambiente com tela:

```sh
gradle :launcher-desktop:run
```

Os scripts `scripts/build.sh` (Linux/macOS) e `scripts/build.bat` (Windows) permanecem como alternativa baseada em `javac` e `jar`. Eles requerem JDK 17+.

## Mod Forge 1.8.9

É um build independente e legado: requer JDK 8, internet, ForgeGradle 2.1 e usa o Gradle Wrapper 2.7 que está dentro de `client-mod/`.

Linux/macOS:

```sh
cd client-mod
./gradlew setupDecompWorkspace build
```

Windows: entre em `client-mod` e execute `gradlew.bat setupDecompWorkspace build`.

## Desenvolvimento pelo celular

É possível enviar os arquivos a um repositório GitHub usando o celular, editar pelo editor web e acionar **Actions** para tentar o workflow desktop já incluído. O workflow ainda precisa ser executado para validar a compilação e o empacotamento; nenhum `.exe` é declarado pronto antes de um resultado bem-sucedido.

Não existe projeto Android nem fluxo para gerar APK neste pacote. A implementação do launcher Android independente ainda precisa ser desenvolvida; o plano está em `PLANO-ANDROID-IOS.md`. O JAR do mod não é um APK.
