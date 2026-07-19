# Projeto de Conversão de Unidades

1. Descrição do Projeto
   Este projeto consiste no desenvolvimento de um aplicativo para o sistema operacional Android voltado à conversão instantânea de unidades de medida. O software realiza os cálculos localmente no dispositivo, permitindo a operação sem dependência de conexão com a internet. O escopo do sistema abrange os principais sistemas de mensuração utilizados globalmente.

2. Problema que o Aplicativo Pretende Resolver
   A coexistência de diferentes sistemas de medição, como o métrico e o imperial, gera problemas em atividades cotidianas, profissionais e acadêmicas. A busca manual por fatores de conversão ou o uso de navegadores web para esse fim demanda tempo e conectividade. Este aplicativo visa eliminar a necessidade de consultas externas e simplifica o processo de transição entre grandezas físicas distintas.

3. Plataforma Escolhida
   O sistema será desenvolvido exclusivamente para a plataforma Android. A escolha se justifica pela ampla distribuição de mercado do sistema operacional e pela viabilidade técnica de criar uma interface fluida por meio de ferramentas nativas de desenvolvimento. Posteriormente será realizada a criação de versão para iOS.

4. Interface do Usuário (UI)
   O aplicativo possui apenas a interface do usuário final. Por ser uma ferramenta de utilidade direta e operação local, não há necessidade de um painel de administração ou gerenciamento de dados em nuvem. A interface foca na exibição clara dos campos de entrada e saída de dados. O usuário poderá escolher as diversas áreas de conversão e escolher as unidades em que poderá executar a conversão.

5. Principais Funcionalidades do Aplicativo
   O software processa os dados em tempo real, atualizando o valor de saída à medida que o usuário insere os dados de origem. O sistema conta com uma função para inverter instantaneamente as unidades selecionadas. O aplicativo também mantém um registro local das últimas cinco conversões e permite a fixação de conversões frequentes na tela inicial.

6. Design e Esquema de Telas
   A navegação é estruturada em um fluxo direto que divide as ações entre a seleção de categorias e a inserção de dados.

Fluxo Lógico:
Tela Principal -> Seleção de Grandeza Física -> Entrada de Valor e Escolha de Unidades -> Exibição do Resultado e Histórico

Esquema de Disposição da Tela Principal:

1. Cabeçalho com Identificador do Aplicativo
2. Seletores Laterais de Categoria (Massa, Comprimento, Temperatura)
3. Campo de Entrada Numérica com Menu de Unidade de Origem
4. Botão Central de Inversão de Sentido
5. Campo de Saída Numérica com Menu de Unidade de Destino
6. Painel Inferior de Histórico Recente e Favoritos
