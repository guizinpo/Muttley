# 🎓 Muttley — Back-end

Sistema de gerenciamento de eventos acadêmicos da Fatec.  
Back-end desenvolvido em **Java 21 + Spring Boot 3.5 + Hibernate + MySQL**.

---

## ⚙️ Configuração inicial (faça isso uma vez)

### 1. Clone o repositório

```bash
git clone https://github.com/guizinpo/Muttley.git
cd Muttley
```

### 2. Crie o arquivo `application.properties`

O arquivo de configuração **não está no repositório** (por segurança), então cada um precisa criar o seu.

Crie o arquivo em:
```
src/main/resources/application.properties
```

Cole o conteúdo abaixo e ajuste com **seu usuário e senha do MySQL**:

```properties
spring.application.name=Muttley

spring.datasource.url=jdbc:mysql://localhost:3306/db_muttley?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Sao_Paulo
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

server.port=8080
```

> O banco `db_muttley` será criado automaticamente na primeira vez que rodar o projeto.

### 3. Abra no IntelliJ

Abra a pasta do projeto no IntelliJ. Quando aparecer o popup **"Maven build script found"**, clique em **Load** para baixar as dependências.

### 4. Rode o projeto

Execute a classe `MuttleyApplication.java`. Se aparecer `Started MuttleyApplication` no console, tá funcionando.

---

## 🌿 Como trabalhar no projeto sem dar conflito

Nunca trabalhem direto na branch `main`. O fluxo é sempre:

### Antes de começar a trabalhar

Garanta que você está com a versão mais recente:

```bash
git checkout main
git pull origin main
```

### Crie sua branch para a tarefa

Cada funcionalidade/tela tem sua própria branch. Exemplos:

```bash
git checkout -b feature/entidade-participante
git checkout -b feature/controller-evento
git checkout -b feature/service-inscricao
```

### Enquanto trabalha

Salve seu progresso com commits pequenos e descritivos:

```bash
git add .
git commit -m "feat: adiciona entidade Participante com validações"
```

### Quando terminar

Suba sua branch pro GitHub:

```bash
git push origin feature/nome-da-sua-branch
```

Depois abra um **Pull Request** no GitHub para a branch `main`. Avise o grupo no grupo antes de fazer merge.

### Padrão de nomes de branch

| Tipo | Exemplo |
|---|---|
| Nova funcionalidade | `feature/entidade-evento` |
| Correção de bug | `fix/validacao-cpf` |
| Configuração | `config/cors` |

---

## 📁 Estrutura do projeto

```
src/main/java/br/com/fatec/muttley/
│
├── entity/          → Entidades do banco (Participante, Evento, Inscricao...)
├── repository/      → Interfaces JPA (consultas ao banco)
├── service/         → Regras de negócio
├── controller/      → Endpoints REST
├── dto/             → Objetos de transferência de dados
└── enums/           → Enumerações (TipoParticipante, TipoEvento...)
```

---

## 🔗 Endpoints principais (resumo)

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/dashboard` | Próximos eventos |
| GET | `/api/participantes` | Lista participantes |
| GET | `/api/participantes/{id}` | Detalhes do participante |
| PUT | `/api/participantes/{id}` | Editar participante |
| GET | `/api/eventos` | Lista eventos |
| GET | `/api/eventos/{id}` | Detalhes do evento |
| POST | `/api/eventos` | Cadastrar evento |
| PUT | `/api/eventos/{id}` | Editar evento |
| DELETE | `/api/eventos/{id}` | Excluir evento |
| POST | `/api/public/inscricao/{token}` | Inscrição via QR Code (público) |
| POST | `/api/public/participacao/{token}` | Confirmar presença via QR Code (público) |
| GET | `/api/medalhas/regras` | Regras de medalha |
| PUT | `/api/medalhas/regras` | Salvar regras |
| GET | `/api/medalhas/calcular` | Calcular medalhas do semestre |

---

## 🛠️ Tecnologias

- Java 21
- Spring Boot 3.5.14
- Spring Data JPA + Hibernate
- Spring Web
- Spring Validation
- MySQL
- Lombok
- Maven
