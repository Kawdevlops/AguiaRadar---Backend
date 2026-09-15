# AguiaRadar — Backend (Sprint 2)

Este projeto foi desenvolvido para a Sprint 2 do desafio Aguia Branca. A ideia foi construir o backend real da plataforma AguiaRadar, deixando de trabalhar com mocks e implementando a comunicação com banco de dados, autenticação, controle de acesso e as principais funcionalidades da plataforma.

Também implementei uma integração com IA para ajudar na pontuação e priorização das ideias cadastradas pelos usuários.

## Tecnologias utilizadas

* Java 17
* Spring Boot 3.3
* Spring Security
* JWT para autenticação
* JJWT para geração e validação dos tokens
* Spring Data MongoDB
* MongoDB
* Maven
* Docker e Docker Compose
* Integração com APIs de IA compatíveis com o padrão OpenAI, utilizando o OpenRouter como opção

A aplicação também possui um fallback local para a funcionalidade de IA. Dessa forma, mesmo sem configurar uma chave de API, ainda é possível testar a funcionalidade utilizando uma pontuação heurística.

## Organização do projeto

Organizei o backend utilizando uma arquitetura em camadas, tentando separar bem as responsabilidades de cada parte da aplicação.

```text
src/main/java/br/com/fiap/aguiaradar
├── AguiaRadarApplication.java
├── config/
│   ├── SecurityConfig
│   ├── DataSeeder
│   └── RestTemplateConfig
├── security/
│   ├── JwtService
│   ├── JwtAuthFilter
│   ├── UserDetails
│   └── AutenticacaoUtil
├── model/
│   ├── Usuario
│   ├── OrientacaoEstrategica
│   ├── Ideia
│   └── Projeto
├── repository/
├── service/
├── controller/
├── dto/
└── exception/
```

A ideia dessa estrutura foi deixar cada camada responsável por uma parte específica da aplicação:

* `model`: representa as entidades armazenadas no MongoDB.
* `repository`: responsável pelo acesso aos dados.
* `service`: concentra as regras de negócio.
* `controller`: recebe as requisições da API e retorna as respostas.
* `dto`: define os objetos utilizados nas requisições e respostas.
* `security`: concentra a parte relacionada à autenticação e JWT.
* `config`: contém configurações gerais e a criação dos dados iniciais.
* `exception`: trata as exceções e padroniza as respostas de erro.

## Perfis de acesso

A aplicação possui três perfis de usuários: `OPERADOR`, `GESTOR` e `LIDERANCA`.

| Perfil    | Orientações estratégicas | Ideias                                       | Projetos              | Dashboard                 |
| --------- | ------------------------ | -------------------------------------------- | --------------------- | ------------------------- |
| OPERADOR  | Consulta                 | CRUD das próprias ideias                     | Sem acesso            | Sem acesso                |
| GESTOR    | Consulta                 | Consulta, priorização e aprovação/reprovação | CRUD completo         | Consulta                  |
| LIDERANCA | CRUD completo            | Consulta                                     | Consulta do andamento | Consulta + insights de IA |

O controle dessas permissões é feito através do Spring Security e das roles de cada usuário.

## Como executar com Docker

A forma mais simples de executar o projeto é utilizando Docker.

Primeiro, é necessário ter o Docker e o Docker Compose instalados.

Na pasta do backend, basta executar:

```bash
docker compose up --build
```

Com isso, o MongoDB e o backend são iniciados juntos.

A API fica disponível em:

```text
http://localhost:8080
```

Para parar os containers:

```bash
docker compose down
```

Caso também queira remover os volumes e os dados do MongoDB:

```bash
docker compose down -v
```

## Como executar localmente

Também é possível executar o projeto diretamente utilizando o Maven.

Para isso, é necessário ter:

* Java 17 ou superior
* Maven
* MongoDB rodando localmente na porta `27017`

Também é possível utilizar uma instância do MongoDB Atlas.

### MongoDB pelo Docker

Caso queira subir somente o MongoDB pelo Docker:

```bash
docker run -d --name aguiaradar-mongo -p 27017:27017 mongo:7
```

## Variáveis de ambiente

A aplicação já possui valores padrão no `application.yml`, mas algumas configurações podem ser alteradas através de variáveis de ambiente.

```bash
export MONGODB_URI="mongodb://localhost:27017/aguiaradar"

export JWT_SECRET="<uma-string-base64-com-pelo-menos-32-bytes>"

export IA_API_KEY="<sua-chave-da-OpenRouter-ou-outro-provedor>"

export IA_MODEL="meta-llama/llama-3.1-8b-instruct:free"
```

A chave da IA é opcional.

Caso `IA_API_KEY` não seja configurada, a aplicação utiliza automaticamente o fallback heurístico local. Assim, ainda é possível demonstrar a funcionalidade de pontuação das ideias sem depender de uma API externa.

## Executando o projeto

Depois de configurar o MongoDB, basta executar:

```bash
mvn clean install

mvn spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

Também é possível gerar o JAR:

```bash
mvn clean package
```

E depois executar:

```bash
java -jar target/aguiaradar-backend.jar
```

## Usuários para demonstração

Para facilitar os testes, criei um `DataSeeder` que cadastra automaticamente alguns usuários na primeira execução da aplicação.

| Perfil    | E-mail                                                      | Senha  |
| --------- | ----------------------------------------------------------- | ------ |
| Operador  | [operador@aguiaradar.com](mailto:operador@aguiaradar.com)   | 123456 |
| Gestor    | [gestor@aguiaradar.com](mailto:gestor@aguiaradar.com)       | 123456 |
| Liderança | [lideranca@aguiaradar.com](mailto:lideranca@aguiaradar.com) | 123456 |

Caso não queira que esses usuários sejam criados automaticamente, basta configurar:

```bash
SEED_ENABLED=false
```

## Testando a API

Depois que o backend estiver rodando, primeiro é necessário fazer o login para receber o JWT.

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"gestor@aguiaradar.com","senha":"123456"}'
```

A resposta retorna o token.

Depois, posso armazenar esse token em uma variável:

```bash
TOKEN="<token-recebido>"
```

Com o token, consigo acessar as rotas protegidas.

Por exemplo, para consultar as orientações estratégicas:

```bash
curl http://localhost:8080/api/v1/orientacoes-estrategicas \
  -H "Authorization: Bearer $TOKEN"
```

E para consultar o dashboard:

```bash
curl http://localhost:8080/api/v1/dashboard/resumo-geral \
  -H "Authorization: Bearer $TOKEN"
```

## Principais endpoints

### Autenticação

As rotas de autenticação são públicas.

```text
POST /api/v1/auth/login
POST /api/v1/auth/registrar
```

No login, envio o e-mail e a senha e recebo as informações do usuário junto com o JWT.

Exemplo:

```json
{
  "email": "gestor@aguiaradar.com",
  "senha": "123456"
}
```

A resposta contém informações como:

```text
token
tipo
id
nome
email
perfil
expiraEmMs
```

### Orientações estratégicas

```text
GET    /api/v1/orientacoes-estrategicas
GET    /api/v1/orientacoes-estrategicas/{id}
POST   /api/v1/orientacoes-estrategicas
PUT    /api/v1/orientacoes-estrategicas/{id}
DELETE /api/v1/orientacoes-estrategicas/{id}
```

Qualquer usuário autenticado pode consultar as orientações.

A criação, alteração e desativação ficam disponíveis para o perfil `LIDERANCA`.

O `DELETE` não apaga realmente o registro. A orientação é desativada para manter o histórico.

### Ideias

```text
GET    /api/v1/ideias
GET    /api/v1/ideias/minhas
GET    /api/v1/ideias/{id}
POST   /api/v1/ideias
PUT    /api/v1/ideias/{id}
DELETE /api/v1/ideias/{id}
PUT    /api/v1/ideias/{id}/avaliar
PUT    /api/v1/ideias/{id}/priorizar
```

O operador consegue cadastrar e gerenciar as próprias ideias.

O gestor consegue consultar as ideias, avaliar, aprovar ou reprovar e definir a prioridade.

### Projetos

```text
GET    /api/v1/projetos
GET    /api/v1/projetos/{id}
POST   /api/v1/projetos
PUT    /api/v1/projetos/{id}
PATCH  /api/v1/projetos/{id}/resultados
DELETE /api/v1/projetos/{id}
```

Os projetos são gerenciados pelo perfil `GESTOR`, enquanto a liderança consegue acompanhar o andamento.

Nos projetos também são armazenadas informações como investimento, prazo, retorno financeiro, ROI e aumento de produtividade.

### Dashboard

```text
GET /api/v1/dashboard/resumo-geral
GET /api/v1/dashboard/por-estrategia
GET /api/v1/dashboard/insight-ia
```

O dashboard apresenta informações como:

* ROI médio
* investimento total
* retorno total
* lucro
* produtividade média
* dados agrupados por orientação estratégica

Também existe um endpoint que utiliza IA para gerar um insight textual a partir dos números atuais do dashboard.

## Integração com IA

Uma das partes que implementei como diferencial foi a utilização de IA para ajudar na priorização das ideias de inovação.

Existem dois endpoints principais:

```text
POST /api/v1/ia/priorizar-ideias
POST /api/v1/ia/ideias/{id}/pontuar
```

O primeiro analisa todas as ideias que estão pendentes e atribui uma pontuação de `0 a 10`. Depois disso, as ideias são marcadas como priorizadas e reorganizadas de acordo com a pontuação.

O segundo permite pontuar uma ideia específica sem alterar o status dela.

Para realizar a integração, utilizei o padrão de APIs de chat compatíveis com OpenAI. Como opção, utilizei o OpenRouter, que disponibiliza modelos gratuitos.

A configuração pode ser feita através da variável:

```bash
IA_API_KEY
```

Também deixei a aplicação preparada para trocar o provedor caso seja necessário.

### Fallback da IA

Para não deixar a funcionalidade dependendo exclusivamente de uma API externa, implementei um fallback local.

Quando não existe uma chave de API configurada ou quando o provedor está indisponível, a aplicação utiliza uma lógica heurística para calcular a pontuação.

Essa pontuação considera informações como o nível de detalhamento da ideia e o relacionamento dela com a estratégia vigente.

Dessa forma, a funcionalidade continua funcionando mesmo sem uma chave de IA.

## Integração com o aplicativo Android

Na Sprint 1, o aplicativo Android foi desenvolvido utilizando Kotlin e Retrofit, mas ainda estava trabalhando com uma API mock.

O backend dessa Sprint 2 foi desenvolvido com contratos e rotas reais, então foi necessário considerar essa diferença na integração.

Para conectar o aplicativo ao backend, é necessário alterar a `BASE_URL` do Retrofit.

No emulador Android:

```text
http://10.0.2.2:8080/
```

Em um dispositivo físico, é necessário utilizar o IP da máquina onde o backend está sendo executado.

Também é necessário atualizar os arquivos `ApiService.kt` e `Models.kt` para utilizar as rotas e DTOs reais do backend.

Outra alteração importante é adicionar um `AuthInterceptor` no OkHttp para enviar automaticamente o JWT nas requisições autenticadas.

O token deve ser enviado no formato:

```text
Authorization: Bearer <token>
```

## Segurança

A autenticação foi implementada utilizando Spring Security e JWT.

As senhas não são armazenadas diretamente no banco. Utilizei BCrypt para gerar o hash das senhas.

A autenticação é stateless, utilizando o JWT através do header:

```text
Authorization: Bearer <token>
```

O token é assinado utilizando HMAC-SHA256.

Também utilizei `@PreAuthorize` para controlar o acesso aos endpoints de acordo com o perfil do usuário.

Durante o desenvolvimento, o CORS ficou liberado para facilitar os testes utilizando o aplicativo, Postman e outras ferramentas. Em um ambiente de produção, essa configuração deve ser restringida.

## Testes

Para executar os testes automatizados:

```bash
mvn test
```

## Possíveis problemas

Caso apareça `Connection refused` ao conectar com o MongoDB, é necessário verificar se o banco está realmente rodando e se a variável `MONGODB_URI` está apontando para o endereço correto.

Se aparecer `401 Unauthorized`, normalmente é necessário verificar se o JWT está sendo enviado corretamente:

```text
Authorization: Bearer <token>
```

Também é importante verificar se o token ainda está válido.

Já o erro `403 Forbidden` significa que o usuário conseguiu se autenticar, mas o perfil dele não possui permissão para acessar aquela funcionalidade.

Nesse caso, é necessário verificar as permissões definidas para cada perfil.

## O que foi desenvolvido nesta Sprint

Nesta Sprint 2, saímos de uma aplicação baseada em mock para um backend funcional, com persistência real no MongoDB, autenticação utilizando JWT, diferentes níveis de acesso e regras de negócio.

Também implementei o gerenciamento de orientações estratégicas, ideias, projetos e dashboards, além da integração com IA para pontuação e priorização das ideias.

A estrutura foi organizada pensando na separação das responsabilidades e na possibilidade de continuar evoluindo a aplicação nas próximas etapas do projeto.
