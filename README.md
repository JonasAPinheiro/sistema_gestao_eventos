## Grupo

- Jonas Eduardo de Almeida Pinheiro
- Ronaldo Silva Ramos Junior
- José Henrique Falsarella Lima Oliveira

## Mapeamento SOLID no projeto

- **SRP (responsabilidade única):** controllers cuidam das requisições HTTP; services coordenam casos de uso; repositories concentram acesso aos dados. Responsabilidades especializadas ficam em componentes como `CalculadoraValorInscricao` (preço), `ComprovanteService` (emissão) e `GlobalExceptionHandler` (respostas de erro).
- **OCP (aberto/fechado):** regras variáveis são representadas por estratégias: `ModalidadeEvento` para modalidades, `LoteInscricao` para preços por lote, `EmissorComprovante` para comprovantes e `Notificador` para canais de notificação. Novos canais podem ser adicionados por uma nova implementação de `Notificador`, sem alterar o fluxo de inscrição.
- **LSP (substituição de Liskov):** implementações de `ModalidadeEvento`, `LoteInscricao` e `EmissorComprovante` podem ser usadas pelos respectivos contratos nos services e factories, mantendo o fluxo sem depender da classe concreta.
- **ISP (segregação de interfaces):** contratos expressam capacidades específicas. `ValidadorElegibilidade` é implementado apenas por modalidades com essa regra; `GeradorQrCode`, `GeradorHashInscricao`, `EscritorArquivo` e `CarregadorRecurso` separam capacidades de comprovantes e exportação.
- **DIP (inversão de dependência):** controllers recebem services; services recebem interfaces de repository, factories ou estratégias. Por exemplo, `InscricaoService` usa `NotificacaoService`, que recebe uma lista de `Notificador`, e não instancia e-mail ou SMS diretamente. `ComprovanteService` usa `ComprovanteFactory` e `EmissorComprovante`, sem criar diretamente seus emissores concretos.

## Notificações

Ao confirmar ou cancelar uma inscrição, o sistema dispara notificações simuladas por e-mail e SMS. Os canais implementam o contrato `Notificador`; o Spring injeta todos os canais cadastrados no `NotificacaoService`, permitindo adicionar novos meios sem alterar o fluxo de inscrição.

Cada tentativa é registrada em memória e pode ser consultada em `GET /api/notificacoes`. Se um canal falhar, a falha é registrada em log e no histórico, sem reverter o status da inscrição nem impedir os demais canais. O telefone é obrigatório no cadastro e na atualização de participantes para permitir o envio de SMS.

## Executar os testes

Com Java 21 configurado, execute:

```powershell
.\mvnw.cmd test
```

