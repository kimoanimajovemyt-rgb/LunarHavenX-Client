# Launcher Android LunarHavenX

## Estado real

Esta branch prepara o app Android separadamente do cliente Forge e do launcher Swing de PC. A fonte completa do Zalith Launcher 2 fica vinculada como submódulo no commit 5822eb4fde29c97e95723eb6c045a0b593c3e5f7 (linha upstream 2.8.2). O workflow aplica as alterações locais de marca e licença e tenta compilar um APK debug. Isso ainda não é um APK de distribuição nem foi testado em um celular.

Fonte upstream: https://github.com/ZalithLauncher/ZalithLauncher2
Licença: GPL-3.0, com termos adicionais no README upstream.

## Alterações de marca

O script de preparação muda o nome mostrado para LunarHavenX, usa identificador Android próprio, acrescenta à tela principal a frase “LunarHavenX — Unofficial Modified Versions” exigida pelos termos adicionais e mantém os avisos de copyright do código original.

A área Android é uma aplicação derivada do Zalith; não reutiliza a interface Swing do PC. O launcher-core atual não é ligado diretamente nesta etapa porque seu processo Java 8/ProcessBuilder é específico de desktop. Reuso de lógica pura poderá ser avaliado depois.

## Segurança de assinatura

O upstream contém keystores e valores de assinatura em sua configuração pública. O script remove esses arquivos e propriedades do checkout temporário antes do build e desliga a assinatura customizada debug do upstream. O APK de CI usa a assinatura debug padrão criada pelo Android Gradle Plugin. Não há chave de release nem assinatura de distribuição nesta branch.

Não configure tokens pessoais no repositório. O OAuth Microsoft não está configurado pelo LunarHavenX; login e jogo não estão verificados. Para um futuro release será necessário registrar/configurar o cliente OAuth apropriado e criar uma chave de assinatura própria via GitHub Secrets.

## Compilar pelo GitHub Actions usando o celular

1. Abra a branch dev/launcher no GitHub.
2. Acesse Actions e selecione Build LunarHavenX Android debug APK.
3. Toque em Run workflow e escolha a branch dev/launcher.
4. Quando terminar, abra o run e baixe o artefato LunarHavenX-Android-debug.
5. Instale somente para teste em um aparelho compatível; o Actions não substitui teste real de toque, GPU, login e inicialização do Minecraft.

O workflow compila quatro ABIs via Rust/cargo-ndk e exige Android SDK/NDK. O fonte upstream é grande e precisa ser baixado como submódulo. Nenhum APK é declarado pronto até o Actions passar; mesmo um build verde ainda não confirma que o jogo funciona em aparelhos reais.

## Licença

A fonte derivada do Zalith permanece sob GPL-3.0 e precisa conservar a licença e avisos de copyright. Os termos adicionais upstream também exigem um nome distinto (sem “ZalithLauncher”/“ZL”), identificação visível como versão modificada não oficial e não copiar os documentos proprietários de Termos/Privacidade do Zalith. A licença foi aplicada somente ao trabalho derivado Android; esta branch não altera a licença do mod Forge nem do launcher PC independente.
