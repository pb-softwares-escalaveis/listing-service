# Listing Service

O Listing Service é o microsserviço focado em busca e indexação dentro do ecossistema de leilões. Ele é construído de forma assíncrona e altamente otimizada para garantir pesquisas de texto performáticas, suporte nativo ao idioma português e capacidades de autocompletar em tempo real.

## Funcionalidades e Domínio

A aplicação foca em prover a melhor experiência de descoberta de itens para os usuários:
*   **Sincronização de Lotes de Leilão (Auction Lots)**: Atua consumindo eventos de criação e atualização de lotes oriundos de outros serviços, mantendo a base de busca sempre atualizada.
*   **Busca Semântica e Autocompletar**: Provê endpoints focados na busca em texto livre, aplicando filtros linguísticos para garantir que buscas complexas e parciais retornem resultados precisos e relevantes.

## Tecnologias e Contexto de Uso

A stack foi escolhida para proporcionar o melhor desempenho em operações de leitura e busca textual:

*   **Java 25 e Spring Boot**: Base da aplicação, fornecendo os componentes REST, validações e forte integração com o ecossistema de dados através do Spring Data Elasticsearch.
*   **Elasticsearch**: O motor de busca principal. Diferente de bancos relacionais convencionais, o Elasticsearch é otimizado para pesquisas textuais profundas, agindo como a vitrine principal de leitura da plataforma de leilões.
*   **Apache Kafka**: Funciona como a ponte de comunicação de dados. O serviço escuta o tópico `auction_connect.public.auction_lots` (gerado via CDC/Debezium pelo serviço principal) de forma reativa, ingerindo as mudanças no Elasticsearch sem acoplamento de código.
*   **Netflix Eureka**: Atua como Service Discovery. O Listing Service se registra no Eureka para que as demais APIs (como os Gateways) possam rotear requisições de busca para ele de maneira dinâmica e transparente.
*   **Actuator**: Ferramenta de observabilidade para expor métricas, health checks corporativos e status dos serviços integrados.

## Estrutura e Indexação de Dados

Ao invés de tabelas relacionais, o Listing Service trabalha com índices otimizados no Elasticsearch:

*   **Índice de Lotes (Auction Lots)**: Armazena os dados desnormalizados dos lotes para leitura super-rápida.
    *   **Análise em Português (`pt_search`)**: Configurado com filtros de stop-words (palavras comuns e preposições ignoradas) e stemmer (extração de radicais, unificando plurais e conjugações verbais) específicos para o português brasileiro.
    *   **Filtros de Edge N-Grams (`autocomplete_index`)**: Mantém tokens indexados em pequenos fragmentos de caracteres (mínimo de 2 e máximo de 15), permitindo que consultas do tipo "autocompletar" operem instantaneamente conforme o usuário digita.

## Como Executar Localmente

1. Certifique-se de ter o Docker e Docker Compose instalados.
2. Caso ainda não exista, crie a rede do ecossistema:
   ```bash
   docker network create leilao-network
   ```
3. Duplique o arquivo `.env.example` renomeando-o para `.env` e preencha as variáveis de ambiente necessárias (como portas, URIs do Elastic, Kafka e Eureka).
4. Suba o Elasticsearch e o Listing Service utilizando o docker-compose:
   ```bash
   docker-compose up -d
   ```
