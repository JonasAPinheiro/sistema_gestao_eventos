## Grupo

- Jonas Eduardo de Almeida Pinheiro
- Ronaldo Silva Ramos Junior
- José Henrique Falsarella Lima Oliveira

## Mapeamento SOLID no projeto

- **SRP (responsabilidade única):** controllers cuidam das requisições HTTP; services coordenam casos de uso; repositories concentram acesso aos dados. Responsabilidades especializadas ficam em componentes como `CalculadoraValorInscricao` (preço), `ComprovanteService` (emissão) e `GlobalExceptionHandler` (respostas de erro).
- **OCP (aberto/fechado):** regras variáveis são representadas por estratégias: `ModalidadeEvento` para modalidades, `LoteInscricao` para preços por lote e `EmissorComprovante` para comprovantes. `ModalidadeEventoFactory` e `ComprovanteFactory` encontram as implementações registradas, permitindo adicionar alternativas por novas classes.
- **LSP (substituição de Liskov):** implementações de `ModalidadeEvento`, `LoteInscricao` e `EmissorComprovante` podem ser usadas pelos respectivos contratos nos services e factories, mantendo o fluxo sem depender da classe concreta.
- **ISP (segregação de interfaces):** contratos expressam capacidades específicas. `ValidadorElegibilidade` é implementado apenas por modalidades com essa regra; `GeradorQrCode`, `GeradorHashInscricao`, `EscritorArquivo` e `CarregadorRecurso` separam capacidades de comprovantes e exportação.
- **DIP (inversão de dependência):** controllers recebem services; services recebem interfaces de repository, factories ou estratégias. Por exemplo, `InscricaoService` recebe `InscricaoRepository` e `CalculadoraValorInscricao`; esta última recebe implementações de `LoteInscricao`. `ComprovanteService` usa `ComprovanteFactory` e `EmissorComprovante`, sem criar diretamente seus emissores concretos.

