# LunarHavenX Client — Forge 1.8.9

Cliente LunarHavenX para Minecraft Java Edition 1.8.9 sobre Forge. A versão `0.5.9-alpha` parte da build unificada 0.5.8 e mantém a arte enviada como tela de transição, mas deixa o splash inicial padrão do Forge em Android para evitar o caminho de transformação OpenGL no GL4ES.

O projeto mais recente enviado inclui `reference/LunarHavenX-source-recovered-0.5.2.zip`, com a fonte histórica recuperada da versão 0.5.2. Os fontes atuais do cliente 0.5.9-alpha permanecem em `src/main/java`; a referência não substitui nem altera esses arquivos. Consulte `../docs/INVENTARIO-RECUPERACAO.md`.

## O que foi implementado

- Estrutura de projeto baseada no MDK oficial do Forge 1.8.9.
- Mod Forge identificável como `lunarhavenx`.
- Em desktop, splash inicial do Forge substituído pela imagem LunarHavenX incluída no JAR; em Android, o transformer da splash fica desativado e o Forge usa a splash padrão.
- Tela de transição usa a mesma imagem fornecida, sem desenhar um segundo painel sobre a arte.
- Animação de entrada executada apenas no início; retornos ao menu não repetem a tela de loading e permanecem no menu Lunar.
- Fallback seguro caso a textura não possa ser carregada.
- Tela não pausa o jogo e transita para o menu principal customizado após as etapas locais de inicialização.
- Textura lunar original 1280×720 no menu inicial, configurações, listas e demais telas customizadas; painéis do HUD usam apenas uma faixa discreta enquanto o mundo permanece visível.
- A textura é decorativa e não renderiza itens; se o recurso falhar ao carregar, os painéis mantêm o fundo sólido e o HUD continua sem depender da imagem.
- Menu principal customizado com UM JOGADOR e a tela própria SERVIDORES; os botões JOGAR AGORA e MULTIJOGADOR padrão foram removidos. MODS, COSMÉTICOS, PERFIL, CONFIGURAÇÕES, CHAT DE VOZ e SAIR continuam disponíveis.
- Efeitos de partículas, brilho, animação contínua e feedback visual de hover.
- Interface unificada com botões e painéis redesenhados, conteúdo central responsivo e retorno correto às telas de origem.
- Integração de UM JOGADOR, SERVIDORES, MODS, PERFIL e SAIR com telas/ações reais do Forge/Minecraft.
- HUD in-game customizado com FPS, CPS, teclas pressionadas, coordenadas, vida, fome, armadura e efeitos de poção ativos.
- Renderização do HUD de armadura com busca do renderer no momento do desenho, verificações de nulos e fallback para um slot vazio em vez de derrubar o cliente.
- Menu de configurações visuais com toggles para CPS e teclas, além de HUD, coordenadas, status, armadura, animações e partículas.
- Sliders persistentes de opacidade do HUD e escala da interface.
- Atalho RSHIFT para abrir as configurações durante o jogo.
- Seis perfis de desempenho, incluindo CELULAR FRACO, com distância de renderização, partículas, nuvens, mipmaps e VSync; o suporte a VBO permanece sob controle do Minecraft/launcher.
- Perfil Android inicial recomendado pelo limite de heap Java e processadores lógicos; limite de 30/60/90/120 FPS acompanha o perfil e pode ser mudado pelo usuário.
- Lista de servidores customizada com favoritos persistentes, seleção, adição, remoção e conexão real.
- Lista de servidores com rolagem, seleção por duplo clique e estado de ping não medido apresentado sem valores inventados.
- Sistema de cosméticos locais com capas LUNAR, HAVEN, AURORA, NOVA e opção SEM CAPA.
- Chat customizado com painel LunarHavenX e códigos de emojis :lunar:, :heart:, :star:, :gg:, :fire:, :wave: e :check:.
- Sistema de keybinds remapeáveis e três macros de chat de execução única.
- Guia de instalação e scripts opcionais para copiar o JAR com backup.
- Camada de compatibilidade Android para runtimes Java usados por MJLauncher, Zalith Launcher 2 e MCinaBox.
- Detecção Android por propriedades, variáveis de ambiente e classe do runtime; expansão protegida do buffer do LWJGL 2 para pelo menos 65536 bytes, iniciada durante a inicialização e repetida nos ticks se o buffer ainda não existir.
- Os perfis deixam a opção de VBO sob controle do Minecraft/launcher, respeitando a capacidade gráfica detectada.
- Gerenciador de módulos, editor de HUD e perfis de jogo.
- Interface e mensagens localizadas por idioma.
- Coremod de splash condicionado à cópia bem-sucedida da textura; em runtimes Android, nenhum transformer é registrado. Em desktop, se o bytecode Forge não corresponder ao esperado, o transformer preserva a classe original.
- Catálogo com pré-visualização, seleção, equipamento e salvamento persistente.
- Em Android, recomendação automática de perfil apenas na primeira execução sem perfil escolhido; perfis e preferências gráficas continuam editáveis.
- Compatibilidade com skins Ely.by em servidores que instalam o sistema oficial de skins; o mod não altera autenticação nem força texturas.
- Compatibilidade Android mantida sem dependências de APIs Android; ajustes de escala do HUD agora são aplicados durante o jogo.
- Arquivos de localização iniciais em PT-BR, inglês, espanhol, russo e japonês.

O botão **PERFIL** percorre os perfis de desempenho disponíveis. **CHAT DE VOZ** informa que depende de uma integração externa ainda não incluída; não simula uma conexão de voz. A arte fornecida contém a indicação visual fixa “72%”; essa porcentagem impressa não acompanha o progresso real do Forge. No Android, a imagem aparece na tela de transição depois da splash padrão para evitar transformar a renderização inicial do GL4ES.

## HUD e segurança

O overlay é registrado no `RenderGameOverlayEvent` do Forge e aparece durante o jogo quando há jogador e mundo ativos. Ele usa dados locais expostos pelo Minecraft: FPS, coordenadas, vida, fome, slots de armadura e efeitos de poção. O HUD não automatiza ações, não altera pacotes e não exibe informações ocultas.

O menu customizado abre a seleção de mundo singleplayer e a tela própria SERVIDORES; o botão MULTIJOGADOR padrão não é exibido. O botão MODS abre a lista de mods do Forge. Cosméticos, servidores, configurações, chat e keybinds possuem implementações locais; chat de voz e visibilidade multiplayer das capas LunarHavenX continuam fora do escopo.

## Configurações visuais

A tela CONFIGURAÇÕES VISUAIS pode ser aberta pelo botão do menu principal ou com RSHIFT durante o jogo. As opções são gravadas em `config/lunarhavenx/visual.properties`. O HUD aplica as preferências sem alterar a jogabilidade.

## Desempenho

O seletor PERFIL percorre seis perfis e salva a escolha em `config/lunarhavenx/performance.properties`. Na primeira execução Android, a sugestão considera o limite de heap Java e processadores lógicos reportados pelo runtime; não mede diretamente a RAM física, GPU ou temperatura. No Android, os perfis limitam a 30/60/90/120 FPS, e CELULAR FRACO também reduz a distância para 3 chunks, partículas, nuvens, sombras e mipmaps. O usuário pode mudar o perfil; o ganho depende de CPU, GPU, renderer, memória, temperatura e outros mods, sem promessa de FPS fixo.

Esse ajuste é código próprio do LunarHavenX, não uma cópia do OptiFine. Ele não inclui shaders, connected textures ou mudanças no renderer do OptiFine; para usar OptiFine, instale uma versão compatível separadamente e sem modificá-la.

## Servidores

O botão SERVIDORES abre a interface customizada e usa o armazenamento nativo `servers.dat` do Minecraft para manter favoritos. É possível adicionar nome/endereço, selecionar, conectar, remover e atualizar a lista. A conexão passa pelo `GuiConnecting` real do Minecraft. O ping é exibido apenas quando já foi medido pelo cliente; a interface não inventa valores.

## Cosméticos

O botão COSMÉTICOS abre um catálogo com quatro capas gráficas originais e SEM CAPA. A seleção é salva em `config/lunarhavenx/cosmetics.properties` e a pré-visualização usa recursos incluídos no próprio JAR. Essas capas continuam locais.

Para skins Ely.by, configure a skin na sua conta e entre em um servidor que tenha o sistema oficial Ely.by instalado e configurado. A documentação do Ely.by informa que o servidor fornece as texturas aos clientes Minecraft 1.8 ou mais recentes, para que outros jogadores também as vejam. Este mod não baixa skins por apelido, não injeta texturas e não modifica autenticação. [Documentação oficial do Ely.by](https://docs.ely.by/en/skins-system.html).

## Chat

Ao abrir o chat durante o jogo, o cliente usa `GuiLunarChat`, com painel visual e ajuda dos códigos de emoji. O envio é encaminhado pelo método vanilla compatível com Minecraft 1.8.9. Antes do envio, os códigos são convertidos em símbolos/textos simples para manter compatibilidade com servidores; o cliente não armazena conversas.

Códigos disponíveis: `:lunar:`, `:heart:`, `:star:`, `:gg:`, `:fire:`, `:wave:` e `:check:`.

## Atalhos e macros

A tela KEYBINDS E MACROS fica dentro de CONFIGURAÇÕES VISUAIS. Os atalhos padrão são RSHIFT para configurações, F6 para alternar HUD, F7 para ciclar desempenho, F8 para cosméticos e F9 para servidores. Há três slots de macro inicialmente desativados. Cada macro envia uma única mensagem/comando de chat quando pressionado e não executa cliques, ataques, movimentos ou repetição automática. Os valores são salvos em `config/lunarhavenx/keybinds.properties`.

## Distribuição

O pacote final inclui `GUIA-INSTALACAO.md`, `install-lunarhavenx.sh` e `install-lunarhavenx.bat`. Os scripts criam a pasta `mods` quando necessário, arquivam versões anteriores do LunarHavenX com timestamp para evitar carregar duas versões juntas e não alteram outros mods.

## Android

O mesmo JAR Forge 1.8.9 pode ser colocado em uma instância Forge de MJLauncher/MojoLauncher, Zalith Launcher 2 ou MCinaBox. O mod detecta runtimes Android por propriedades Java e reduz render distance, partículas, gráficos sofisticados, ambient occlusion, sombras e VSync. Não há código Android específico, então o mod não altera nem depende do APK do launcher.

Referências oficiais: [MJLauncher](https://github.com/mojolauncher/mojolauncher), [Zalith Launcher 2](https://github.com/ZalithLauncher/ZalithLauncher2), [MCinaBox](https://github.com/AOF-Dev/MCinaBox) e [Forge 1.8.9](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html).

## Compatibilidade

O projeto usa Forge **1.8.9 — 11.15.1.2318**, ForgeGradle 2.1 e Gradle Wrapper 2.7. Essa cadeia legada requer Java 8 para a compilação.

## Compilação

Use um JDK 8 para esta linha legada e execute:

```bash
./gradlew setupDecompWorkspace
./gradlew build
```

O resultado:

```text
build/libs/LunarHavenX-Client-1.8.9-0.5.9-alpha.jar
```

Os logs enviados mostram dois problemas distintos: `NullPointerException` ao desenhar o painel de armadura em 0.5.3/0.5.5 e `BufferOverflowException` no teclado do LWJGL 2.9.4 em 0.3.0. Esta versão busca o renderer do HUD apenas quando necessário, ignora itens/estados nulos e usa um slot de fallback; também amplia o buffer do teclado e tenta fazê-lo já na inicialização e novamente nos ticks. A proteção do teclado é uma mitigação dentro do mod, não uma correção do LWJGL ou do launcher.

Não foi possível confirmar o resultado em aparelho Android, MJLauncher ou servidor real neste ambiente. O build de código-fonte pode ser executado com Java 8; validação gráfica final ainda depende do launcher e dispositivo.

A execução gráfica requer uma configuração de cliente Forge 1.8.9. O modo Android é detectado sem dependência de APIs Android e aplica um perfil gráfico conservador para runtimes ARM/GL4ES/Zink. Gamepad, toque, renderer e autenticação continuam sendo responsabilidades do launcher.

## Estrutura

```text
src/main/java/com/lunarhavenx/client/LunarHavenXMod.java
src/main/java/com/lunarhavenx/client/gui/GuiLunarLoading.java
src/main/resources/assets/lunarhavenx/textures/gui/lunarhavenx_loading_screen.png
src/main/resources/assets/lunarhavenx/textures/gui/lunarhavenx_menu_background.png
src/main/resources/assets/lunarhavenx/lang/*.json
```

## Limitações conhecidas

- O JAR é um mod para Forge 1.8.9, não um launcher nem um APK; não altera a autenticação nem contorna a licença de Minecraft.
- A barra de carregamento representa apenas a preparação local do mod; não mede o carregamento interno do Minecraft.
- Compatibilidade visual em Android, renderers móveis, servidores e combinações com outros mods precisa de validação em cada ambiente.
