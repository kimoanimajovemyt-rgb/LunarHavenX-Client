# Autenticação (Microsoft)

Fluxo oficial *device code*: Microsoft → Xbox Live → XSTS → `api.minecraftservices.com`. O launcher **nunca vê sua senha**.

## O que você precisa fazer (uma vez)
1. No portal Azure (entra.microsoft.com → Registros de aplicativo) crie um app: contas pessoais Microsoft, **cliente público** ("Permitir fluxos de cliente público" = Sim).
2. Copie o **Application (client) ID**.
3. A Mojang exige aprovação do ID para usar a API do Minecraft. Sem ela, o passo `login_with_xbox` responde **403** (o launcher mostra essa mensagem). Solicite pelo formulário oficial descrito em wiki.vg / documentação da Mojang (procure "Minecraft API app review").
4. Informe o ID na primeira vez que clicar em "Entrar", ou defina `LHX_MS_CLIENT_ID`.

Não existe ID embutido no código: usar o ID de outro launcher violaria os termos dele.

## Armazenamento
`%APPDATA%\.lunarhavenx\account.json` guarda nome, UUID e tokens (sem senha). Em Linux/macOS o arquivo é 600. No Windows ainda não usa DPAPI/Credential Manager (item do plano de evolução).

## Contas sem o jogo / menores
Mensagens específicas para: sem Minecraft Java, sem perfil Xbox, país sem Xbox Live, conta infantil sem família.
