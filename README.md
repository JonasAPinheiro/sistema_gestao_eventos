## Grupo

- Jonas Eduardo de Almeida Pinheiro
- Ronaldo Silva Ramos Junior
- José Henrique Falsarella Lima Oliveira

## Funcionalidade inovadora — RF27: Reembolso de inscrições

O sistema oferece reembolso para inscrições confirmadas em eventos pagos. A regra é aplicada
no momento da solicitação, considerando a data e a hora de início do evento:

| Situação | Resultado |
| --- | --- |
| Solicitação com 72 horas ou mais de antecedência | Reembolso integral (100%). |
| Solicitação entre 72 horas (exclusive) e 24 horas (inclusive) | Reembolso parcial (50%). |
| Solicitação com menos de 24 horas de antecedência | Solicitação rejeitada. |
| Evento gratuito ou inscrição com valor zero | Não há reembolso. |
| Evento cancelado pelo organizador/sistema | Reembolso integral automático, creditado na carteira do participante. |

O participante solicita o reembolso por `POST /eventos/{eventoId}/inscricoes/{participanteId}/reembolso`,
informando o destino `CARTEIRA` ou `FORMA_ORIGINAL`. No cancelamento de um evento, o
`CancelamentoEventoService` aciona automaticamente o `ReembolsoService`; nesse fluxo o destino
é sempre `CARTEIRA` e o motivo registrado é `EVENTO_CANCELADO`.

As regras percentuais foram isoladas nas estratégias `ReembolsoIntegral` e `ReembolsoParcial`,
que implementam `PoliticaReembolso`. A `CalculadoraReembolso` seleciona a política aplicável e
impede reembolsos fora das condições previstas.

## Mapeamento SOLID no projeto

- **SRP (responsabilidade única):** controllers cuidam das requisições HTTP; services coordenam casos de uso; repositories concentram acesso aos dados. Responsabilidades especializadas ficam em componentes como `CalculadoraValorInscricao` (preço), `ComprovanteService` (emissão) e `GlobalExceptionHandler` (respostas de erro).
- **OCP (aberto/fechado):** regras variáveis são representadas por estratégias: `ModalidadeEvento` para modalidades, `LoteInscricao` para preços por lote, `EmissorComprovante` para comprovantes e `Notificador` para canais de notificação. Novos canais podem ser adicionados por uma nova implementação de `Notificador`, sem alterar o fluxo de inscrição.
- **OCP no RF27:** as faixas de reembolso usam o contrato `PoliticaReembolso`. Uma nova faixa ou política pode ser incluída por outra implementação, sem alterar `CalculadoraReembolso`.
- **LSP (substituição de Liskov):** implementações de `ModalidadeEvento`, `LoteInscricao` e `EmissorComprovante` podem ser usadas pelos respectivos contratos nos services e factories, mantendo o fluxo sem depender da classe concreta.
- **ISP (segregação de interfaces):** contratos expressam capacidades específicas. `ValidadorElegibilidade` é implementado apenas por modalidades com essa regra; `GeradorQrCode`, `GeradorHashInscricao`, `EscritorArquivo` e `CarregadorRecurso` separam capacidades de comprovantes e exportação.
- **DIP (inversão de dependência):** controllers recebem services; services recebem interfaces de repository, factories ou estratégias. Por exemplo, `InscricaoService` usa `NotificacaoService`, que recebe uma lista de `Notificador`, e não instancia e-mail ou SMS diretamente. `ComprovanteService` usa `ComprovanteFactory` e `EmissorComprovante`, sem criar diretamente seus emissores concretos.
- **DIP no RF27:** `CalculadoraReembolso` depende da abstração `PoliticaReembolso` e recebe a lista de políticas por injeção; `ReembolsoService` depende das interfaces `InscricaoRepository` e `ReembolsoRepository`.

## Notificações

Ao confirmar ou cancelar uma inscrição, o sistema dispara notificações simuladas por e-mail e SMS. Os canais implementam o contrato `Notificador`; o Spring injeta todos os canais cadastrados no `NotificacaoService`, permitindo adicionar novos meios sem alterar o fluxo de inscrição.

Cada tentativa é registrada em memória e pode ser consultada em `GET /api/notificacoes`. Se um canal falhar, a falha é registrada em log e no histórico, sem reverter o status da inscrição nem impedir os demais canais. O telefone é obrigatório no cadastro e na atualização de participantes para permitir o envio de SMS.

## Executar os testes

Pré-requisito: Java 21 e nenhuma configuração adicional de banco de dados, pois os testes usam
repositórios em memória e mocks.

Para executar toda a suíte, na raiz do projeto execute:

```powershell
.\mvnw.cmd test
```

### Cobertura automatizada do RF27

Os testes dedicados à funcionalidade estão em:

- `CalculadoraReembolsoTest`: valida reembolso integral com mais de 72h, parcial entre 72h e
  24h, rejeição com menos de 24h e a vedação para evento gratuito/inscrição sem valor.
- `PoliticasReembolsoTest`: valida os limites exatos das faixas (72h e 24h) e os percentuais de
  100% e 50%.
- `ReembolsoServiceTest`: verifica crédito em carteira, destino original, prevenção de segundo
  reembolso e o reembolso integral em carteira após cancelamento do evento.
- `CancelamentoEventoServiceTest`: verifica que inscrições confirmadas de eventos pagos são
  encaminhadas ao reembolso automático, enquanto inscrições pendentes, gratuitas ou já
  reembolsadas não são reembolsadas novamente.

