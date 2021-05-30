# banking

O Sistema de gerenciamento de operações bancárias tem sua descrição no arquivo "DESCRICAO_DO_PROJETO.PDF" contido na raiz deste repositório!

O sistema de gerenciamento de operações bancárias "banking system" permite as operações básicas como: criação de conta para o titular, acesso aos dados da conta pelo número identificador, depósitos, saques e transferências entre contas.

Os depósitos e saques podem ser executados imediatamente ou agendados e as transferências precisam ser agendadas. É feita uma verificação em caso de saque (débito) se há valor disponível na conta maior ou igual ao valor a ser sacado.

A transferência agendada também envolve as operações de débito na conta origem e crédito na conta destino, logo, também está sujeita a possibilidade de a conta de origem não possuir valor suficiente para realização da trasferência.

È possível também executar as operações pela data. Por exemplo, executar todas as operações com data de execução para o dia atual ou outra data. A possibilidade de executar as operações marcadas para uma data futura pode ser utilizada para fins de teste. Em caso de haver operações que não tenham sido possíveis de executar por falta de valor suficiente na conta de origem, elas são retornadas pelo webservice para o cliente em formato json.

Também é possível a consulta pelas operações agendadas referentes a uma determinada conta pelo Identificador dela.

A taxa cobrada também está tratada na operação de transferência, conforme as regras definidas no desafio.

# testes de unidade

Foram feitos também testes de unidade para validar algumas funcionalidades. No entanto, datas foram utilizadas nos testes. Logo, a data atual pode ser uma data posterior a de hoje: 20/05/2021, dependendo da data em que o sistema for executado e, isso pode alterar o resultado dos testes.

# executando o sistema

Foi utilizada a versão 16 do jdk para compilar o projeto. O tomcat embutido na aplicação está executando na porta 8080 e o swagger também está configurado no projeto. Podendo o swagger ser acessado pela url: http://localhost:8080/swagger-ui.html

Para executar o sistema basta executar o jar da aplicação conforme a seguir:

java -jar banking.jar

O arquivo banking.jar está no diretório raiz do repositório.
