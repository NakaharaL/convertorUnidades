Versão final - Descrição do Projeto

O Conversor de Unidades Mobile é um aplicativo Android desenvolvido em Kotlin projetado para realizar conversões rápidas de diversas grandezas físicas (comprimento, velocidade, temperatura, volume e peso). O sistema integra serviços baseados em localização para registrar a posição geográfica exata do usuário no momento da conversão, persistência local de dados (SharedPreferences, banco SQLite e arquivos internos/externos) e recursos de integração com a API do Google Maps para navegação, geocodificação e geocodificação reversa. Além disso, conta com consumo de serviços web externos em formato JSON via HTTP.

Declaração do Problema

Usuários que precisam converter unidades de medida em campo frequentemente precisam de ferramentas operacionais que associem o cálculo ao contexto geográfico onde a operação foi realizada. As soluções existentes limitam-se à matemática do cálculo, sem registrar dados espaciais de auditoria, sem permitir a identificação de locais associados no mapa ou sem oferecer suporte para o compartilhamento e consumo de dados via rede em tempo real.

Plataforma

1) Plataforma alvo: Android (API Level 24+ / Android 7.0 até Android 15/16).
2) Linguagem de Programação: Kotlin.
3) Ambiente de Desenvolvimento: Android Studio.
4) Bibliotecas e APIs de Terceiros: Google Play Services (Maps e Location API), OkHttp3 (Requisições HTTP), Org.JSON e Android Material Components.

Interface do Usuário e Interface do Administrador

1) Interface do Usuário (Mobile):
   a) Barra Superior (MaterialToolbar): Acesso rápido às opções do aplicativo (Google Maps, Sobre, Limpar Histórico).
   b) Seleção de Categorias (GridView): Grade interativa com ícones e rótulos para escolha do tipo de unidade.
   c) Controles de Conversão: Campos de texto (EditText) para entrada e saída de dados, acoplados a seletores (Spinner) para definição das unidades de origem e destino.
   d) Painel de Ações: Botões operacionais para alternar unidades, limpar campos, efetuar conversão e enviar resultados via SMS.
   e) Tela de Mapeamento (MapActivity): Layout dedicado com controles para busca textual de endereços (Geocodificação), entrada manual de coordenadas (Geocodificação Reversa) e consumo de API JSON externa.

2) Interface do Administrador:

   Por se tratar de um aplicativo mobile standalone focado no cliente, não há um painel administrativo Web/Server dedicado nesta versão. O gerenciamento de dados e do histórico é feito localmente pelo próprio usuário através de ações diretas na interface (limpeza de banco SQLite e SharedPreferences).

Funcionalidade

1) Conversão de Grandezas: Cálculo instantâneo entre diferentes unidades de medida organizadas por categorias.
2) Persistência Multi-nível: Armazenamento automático das preferências do usuário (SharedPreferences), gravação em arquivos de texto internos e externos e inserção em banco de dados SQLite (MeuDatabaseHelper).
3) Monitoramento de Localização: Captura da latitude e longitude do dispositivo no instante em que o usuário executa uma conversão através da API FusedLocationProviderClient.
4) Mapeamento e Geolocalização (Google Maps):
5) Visualização interactiva do mapa com marcadores dinâmicos.
6) Geocodificação Direta: Conversão de nomes de ruas/endereços em coordenadas geográficas com atualização da câmera.
7) Geocodificação Reversa: Clique no mapa ou digitação de coordenadas (Lat/Lng) para obter o endereço completo correspondente.
8) Comunicação de Rede e JSON: Conexão HTTP assíncrona para requisição e leitura de payloads JSON de APIs externas.
9) Integração do Sistema: Disparo de mensagens contendo os resultados das conversões via aplicativo padrão de SMS (ACTION_SENDTO).

Design (Wireframes e Layouts de Página)
1) activity_main.xml (Tela Principal):

   a) Topo: MaterialToolbar contendo o título e o menu de opções.
   b) Centro Superior: GridView (3 colunas) exibindo ícones das categorias.
   c) Centro: Dois blocos horizontais (EditText + Spinner) divididos pelo botão de alternância vertical, seguidos pela barra de botões principais ("Limpar", "Converter", "Enviar SMS").
   d) Rodapé: Lista visual (ListView) e rótulo fixo para apresentação do histórico de conversões efetuadas.

2) activity_map.xml (Tela do Google Maps):

   a) Topo: Caixa de entrada de endereço com botão "Buscar" alinhado à direita.
   b) Sub-topo: Dois campos de texto lado a lado (Lat e Lng) com botão "Ir Coord". 
   c) Centro: Botão largo para acionar a requisição da API externa JSON.
   d) Área Principal: Componente <fragment> do SupportMapFragment ocupando todo o espaço restante inferior para exibição do mapa interativo.

------------------------------------------

Versão 1 - original - # Projeto de Conversão de Unidades

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

Tela Principal → Seleção de Grandeza Física → Entrada de Valor e Escolha de Unidades → Exibição do Resultado e Histórico

Esquema de Disposição da Tela Principal:

1. Cabeçalho com Identificador do Aplicativo
2. Seletores Laterais de Categoria (Massa, Comprimento, Temperatura)
3. Campo de Entrada Numérica com Menu de Unidade de Origem
4. Botão Central de Inversão de Sentido
5. Campo de Saída Numérica com Menu de Unidade de Destino
6. Painel Inferior de Histórico Recente e Favoritos

Interação 1

Estruturação da interface utilizando layouts em XML.
Uso de EditText para entrada de dados do usuário.

Interação 2

Uso de GridView para apresentar um conjunto de imagens em formato de grade (galeria).
Criação e exibição de um Menu de contexto (ações relacionadas a um componente específico, acionadas por pressionar e segurar).

Interação 3

Uso de SharedPreferences para salvar e recuperar dados simples do usuário.
Compartilhamento de dados entre diferentes Activities utilizando o método getSharedPreferences().
Salvamento de arquivos no armazenamento interno do aplicativo utilizando FileOutputStream.
Leitura de dados a partir de arquivos internos utilizando FileInputStream e InputStreamReader.
Salvamento de dados no armazenamento externo, utilizando o método getExternalStorageDirectory().
Acesso a arquivos localizados na pasta res/raw utilizando o método openRawResource().
Criação de uma classe auxiliar de banco de dados (Database Helper) estendendo a classe SQLiteOpenHelper.

Interação 4

Envio de mensagens SMS diretamente pelo aplicativo, utilizando recursos nativos da plataforma Android.
Envio de mensagens SMS por meio do aplicativo de mensagens do sistema, utilizando integração com intents.

Interação 5

Integração do Google Maps ao seu aplicativo.
Uso de localização geográfica, obtida por diferentes meios.
Permissão de interação do usuário com o mapa, selecionando locais e navegando entre regiões.
Utilização de geocodificação e geocodificação reversa para transformar coordenadas em endereços e vice-versa.
Implementação de funcionalidades básicas de monitoramento de localização.
Conexão do aplicativo à internet utilizando HTTP.
Consumo de dados externos em formato JSON.
