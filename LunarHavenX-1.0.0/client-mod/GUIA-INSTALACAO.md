# LunarHavenX Client 0.5.9-alpha — Guia de instalação

## Requisitos

- Minecraft Java Edition 1.8.9.
- Forge **1.8.9-11.15.1.2318** instalado no launcher.
- Java 8 para executar essa cadeia legada com maior compatibilidade.
- Uma pasta `.minecraft` acessível.

A página oficial do Forge para 1.8.9 está em:

<https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html>

## Instalação manual no PC

1. Instale e execute o perfil Forge 1.8.9 pelo menos uma vez.
2. Feche o Minecraft.
3. Abra a pasta `.minecraft`:
   - Windows: `%APPDATA%\\.minecraft`
   - Linux: `~/.minecraft`
   - macOS: `~/Library/Application Support/minecraft`
4. Crie a pasta `mods` se ela não existir.
5. Mova versões antigas `LunarHavenX-Client-1.8.9-*.jar` para fora de `mods`; não deixe duas versões do mod instaladas juntas.
6. Copie `LunarHavenX-Client-1.8.9-0.5.9-alpha.jar` para `mods`.
7. Inicie o perfil Forge 1.8.9.
8. Em desktop, a arte deverá aparecer na splash do Forge e na tela de transição. Em Android, a splash padrão do Forge é mantida por compatibilidade; a arte LunarHavenX aparece após a inicialização.

Não renomeie o JAR para outra versão de Minecraft. Não misture o arquivo com Forge de outra versão.

## Instalação pelos scripts

No Linux/macOS, a partir da pasta que contém o JAR:

```bash
chmod +x install-lunarhavenx.sh
./install-lunarhavenx.sh
```

O script procura `.minecraft`, cria `mods` quando necessário e arquiva com timestamp todas as versões anteriores do LunarHavenX encontradas. Ele não altera outros mods. Em Android, o transformer nativo da splash é desativado para reduzir riscos com GL4ES; a tela de transição usa a imagem incluída.

No Windows, execute `install-lunarhavenx.bat` e informe o caminho da pasta `.minecraft` quando solicitado.

## Android — MJLauncher, Zalith Launcher 2 e MCinaBox

O arquivo é um mod Forge Java, não um APK. Em qualquer launcher, crie ou selecione uma instância **Minecraft 1.8.9 + Forge 11.15.1.2318**, inicie essa instância uma vez e copie o JAR para a pasta `mods` do diretório daquela instância. Remova versões antigas do LunarHavenX dessa pasta antes de iniciar; não use o perfil Vanilla.

### MJLauncher / MojoLauncher

Use uma instalação Forge 1.8.9 no MJLauncher e coloque o JAR na pasta `mods` da instância. O projeto oficial declara suporte a versões legadas e Forge/Fabric. Configure teclado virtual ou gamepad para RSHIFT, F6, F7, F8 e F9; o mod não cria botões Android próprios.

### Zalith Launcher 2 Plus

Use o aplicativo oficial e uma instância Forge 1.8.9. O projeto oficial é separado do antigo Zalith Launcher e informa Android mínimo API 26. Configure o layout de toque no próprio launcher e deixe pelo menos 1–2 GB livres para o runtime e o jogo, conforme a capacidade do aparelho.

### MCinaBox

Importe um runtime Java 8 compatível com a arquitetura do aparelho. Instale o Forge usando o instalador universal JAR na função de Forge Installer e depois copie o LunarHavenX para o `mods` do diretório de jogo. O projeto oficial documenta limitações de runtime e possível crash de animação de carregamento do Forge; se ocorrer, teste uma instância limpa, outro runtime e o perfil Forge 1.8.9 sem outros mods.

### Perfil recomendado no Android

Na primeira execução Android, sem perfil escolhido anteriormente, o LunarHavenX recomenda um perfil pelo limite máximo de heap Java e quantidade de processadores lógicos: CELULAR FRACO para heaps de até 1,5 GB ou até 2 processadores, BAIXO até 2 GB ou 4 processadores, ALTO FPS para heaps de 4 GB ou mais e 8+ processadores, e BALANCEADO nos demais casos. Isso é uma heurística conservadora, não um benchmark físico; um perfil já salvo é respeitado. Os perfis Android limitam o jogo a 30, 60, 90 ou 120 FPS conforme a escolha, e continuam editáveis em PERFIL. O FPS real depende do aparelho, renderer, temperatura e mods.

## Primeiro uso

- **RSHIFT:** abre configurações visuais.
- **F6:** alterna o HUD.
- **F7:** alterna o perfil de desempenho.
- **F8:** abre cosméticos.
- **F9:** abre servidores.
- **T:** abre o chat customizado durante o jogo.

As configurações ficam em `config/lunarhavenx/`. O cliente não salva senhas, tokens de sessão ou gravações de voz.

## Solução de problemas

### O jogo não inicia

Confirme que o perfil é exatamente Minecraft 1.8.9 com Forge 11.15.1.2318 e que o launcher está usando Java 8. Remova temporariamente outros mods para identificar conflitos.

### O menu não aparece

Verifique `logs/latest.log` e confirme se o JAR está em `.minecraft/mods`. Não coloque o JAR dentro de `resourcepacks` ou `versions`.

### O HUD não aparece

Entre em um mundo e confira se o HUD está ativado em CONFIGURAÇÕES VISUAIS. F6 alterna a visibilidade.

### A capa não aparece para outros jogadores

As capas LunarHavenX são seleções locais. Para skins Ely.by serem vistas pelos outros jogadores, configure sua skin na conta Ely.by e use um servidor com o sistema oficial Ely.by instalado e configurado. Consulte <https://docs.ely.by/en/skins-system.html>. O mod não altera autenticação e não força skins em servidores incompatíveis.

### O chat não mostra símbolos

Algumas fontes podem não conter todos os glifos. Os códigos `:gg:`, `:fire:`, `:wave:` e `:check:` usam textos compatíveis; `:lunar:`, `:heart:` e `:star:` podem depender da fonte disponível.

## Limitações conhecidas

A execução gráfica completa em aparelho Android, testes em servidores reais, gamepads, chat de voz e compatibilidade com outros mods não foram certificados neste sandbox. O suporte Android foi implementado de forma agnóstica ao launcher e precisa ser validado no aparelho/renderer do usuário.
