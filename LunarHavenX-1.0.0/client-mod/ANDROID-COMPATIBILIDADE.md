# Compatibilidade Android — LunarHavenX Client 0.5.9-alpha

## Conclusão técnica

O LunarHavenX é um mod Forge Java e não é um APK. Portanto, o suporte a MJLauncher, Zalith Launcher 2 e MCinaBox é obtido colocando o mesmo JAR em uma instância Forge 1.8.9 do launcher. O mod não pode controlar a tela de instalação, o runtime Java, o renderer ou o mapeamento de toque do aplicativo.

Foi adicionada uma camada de compatibilidade sem dependência de classes Android. Na primeira execução Android, se ainda não houver perfil salvo, o cliente recomenda um perfil usando o limite máximo do heap Java e o número de processadores lógicos anunciados pelo runtime. O heap é uma configuração do launcher, não a RAM física; a recomendação é ajustável e não representa benchmark. CELULAR FRACO usa 3 chunks, efeitos gráficos reduzidos e limite de 60 FPS. Os perfis Android aplicam limites de 30/60/90/120 FPS conforme o perfil escolhido; uma escolha salva pelo usuário é respeitada.

Essas são otimizações próprias de configurações do Minecraft; não incluem nem substituem o renderer, shaders ou texturas conectadas do OptiFine.

A detecção verifica propriedades do runtime, `os.version`, as variáveis `ANDROID_ROOT`/`ANDROID_DATA` e a presença de `android.os.Build`, pois launchers podem expor Android como Linux em `os.name`. Em Android ou runtime desconhecido, o mod tenta aumentar o buffer de eventos de teclado do LWJGL 2 para pelo menos 65536 bytes durante a inicialização e novamente nos ticks até conseguir. Ao trocar o buffer, preserva tipo (heap/direto), ordem dos bytes e eventos ainda pendentes; se a reflexão falhar, volta a tentar sem interromper o jogo. Perfis não forçam VBO: o Minecraft/launcher conserva sua detecção de suporte gráfico.

Esta proteção é direcionada a estouros causados por rajadas de eventos de entrada. Ela não substitui correções no launcher/runtime e ainda precisa ser validada em aparelhos Android reais.

## Situação por launcher

### MJLauncher / MojoLauncher

O repositório oficial informa que o launcher é baseado em PojavLauncher, executa Minecraft Java no Android, cobre quase todas as versões e suporta mod loaders como Forge e Fabric. A release oficial também registra suporte a teclado, chat e importação de modpacks. O cliente deve ser instalado em uma instância Forge 1.8.9 e Java 8.

### Zalith Launcher 2 Plus

O repositório oficial descreve o aplicativo como um launcher de Minecraft Java para Android baseado no núcleo PojavLauncher. O README informa requisito mínimo Android API 26, equivalente a Android 8, e não autoriza assumir que qualquer site de terceiros seja oficial. A instalação do Forge e dos mods deve ser feita pela instância 1.8.9 gerenciada pelo launcher; o mapeamento de toque precisa ser configurado no próprio launcher.

### MCinaBox

A documentação oficial descreve a instalação do Forge pelo instalador universal em formato JAR e possui configuração de diretório de jogo e teclado virtual. Ela também lista limitações: Microsoft Authentication API ausente, runtime incompleto/incompatível como causa de falha e possível crash da animação de carregamento do Forge. Por isso, o suporte é experimental e deve ser testado com um perfil Forge 1.8.9 isolado.

## Limites conhecidos

- Não há teste gráfico real em um aparelho Android neste ambiente.
- Gamepad, toque, renderer GL4ES/Zink, arquitetura ARM e autenticação são responsabilidades do launcher/runtime.
- Capas LunarHavenX continuam locais. Skins Ely.by dependem do sistema oficial instalado e configurado no servidor; o cliente não força a visibilidade.
- O mod não inclui um APK, não altera nenhum launcher e não baixa credenciais.

## Referências

[1]: https://github.com/mojolauncher/mojolauncher "MojoLauncher/MJLauncher — repositório oficial"
[2]: https://github.com/mojolauncher/mojolauncher/releases "MJLauncher — releases oficiais"
[3]: https://github.com/ZalithLauncher/ZalithLauncher2/blob/main/README_EN_US.md "Zalith Launcher 2 — README oficial em inglês"
[4]: https://github.com/AOF-Dev/MCinaBox "MCinaBox — repositório oficial"
[5]: https://github.com/AOF-Dev/MCinaBox/blob/dev/docs/Q%26A.md "MCinaBox — Q&A oficial"
[6]: https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html "Forge oficial — Minecraft 1.8.9"
[7]: https://docs.ely.by/en/skins-system.html "Ely.by — documentação oficial do sistema de skins"

## Instalação resumida

1. Instale o launcher a partir de uma fonte oficial.
2. Crie uma instância Minecraft Java 1.8.9.
3. Instale Forge 1.8.9-11.15.1.2318 nessa instância.
4. Inicie Forge uma vez e feche o jogo.
5. Copie `LunarHavenX-Client-1.8.9-0.5.9-alpha.jar` para a pasta `mods` da instância.
6. Configure o teclado virtual/gamepad do launcher para os atalhos do mod.
7. Comece com a instância sem outros mods para isolar problemas de runtime, renderer ou memória.
