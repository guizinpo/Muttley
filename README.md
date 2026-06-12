# 🎓 Muttley — Sistema de Gestão de Eventos Acadêmicos

Sistema desenvolvido para a **FATEC Zona Leste** com o objetivo de gerenciar eventos acadêmicos, inscrições, presenças e emissão de certificados para participantes e palestrantes.

---

## 👥 Integrantes

- Daiane da Silva
- Diogo Felix
- Fabio Gonçalves
- Gabriel Rodrigues Vieira Brandão
- Guilherme Rodrigues
- Gustavo Lacerda
- Otavio Gabriel Ribeiro Scabio

---

## 🛠️ Tecnologias

- **Java 21**
- **Spring Boot 3.5**
- **Spring Data JPA + Hibernate**
- **Spring Security + JWT**
- **Spring Mail**
- **MySQL**
- **iText 7** (geração de PDFs)
- **ZXing** (geração de QR Codes)
- **Lombok**
- **Maven**

---

## ⚙️ Configuração inicial

### 1. Clone o repositório

```bash
git clone https://github.com/guizinpo/Muttley.git
cd Muttley
```

### 2. Crie o arquivo `application.properties`

O arquivo de configuração **não está no repositório** por segurança. Crie-o em:

```
src/main/resources/application.properties
```

Cole o conteúdo abaixo e ajuste com seus dados:

```properties
spring.application.name=Muttley

# Banco de dados
spring.datasource.url=jdbc:mysql://localhost:3306/db_muttley?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Sao_Paulo
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

server.port=8080

# E-mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=SEU_EMAIL@gmail.com
spring.mail.password=SUA_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# JWT
jwt.secret=SUA_CHAVE_SECRETA
jwt.expiration=86400000

# Admin
admin.username=admin
admin.password=SUA_SENHA_ADMIN

# Medalhas
medalhas.geracao.meses-permitidos=6,12

# Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

> O banco `db_muttley` será criado automaticamente na primeira execução.

### 3. Rode o projeto

Execute a classe `MuttleyApplication.java`. Se aparecer `Started MuttleyApplication` no console, está funcionando.

---

## 📁 Estrutura do projeto

```
src/main/java/br/com/fatec/muttley/
│
├── config/          → Configurações gerais
├── controller/      → Endpoints REST
├── dto/             → Objetos de transferência de dados
├── entity/          → Entidades do banco
├── enums/           → Enumerações
├── exception/       → Tratamento global de exceções
├── repository/      → Interfaces JPA
├── security/        → Filtro JWT e configuração de segurança
└── service/         → Regras de negócio
```

---

## 🔗 Principais endpoints

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/auth/login` | Login do administrador |
| GET | `/api/participantes` | Lista participantes |
| GET | `/api/eventos` | Lista eventos |
| POST | `/api/eventos` | Cadastrar evento |
| PUT | `/api/eventos/{id}` | Editar evento |
| POST | `/api/eventos/{id}/upload-assinatura` | Upload de assinatura do coordenador |
| GET | `/api/palestrantes` | Lista palestrantes |
| POST | `/api/palestrantes` | Cadastrar palestrante |
| POST | `/api/palestrantes/{id}/upload-foto` | Upload de foto do palestrante |
| POST | `/api/palestrantes/{id}/enviar-certificado` | Enviar certificado ao palestrante |
| POST | `/api/public/inscricao/{token}` | Inscrição via QR Code (público) |
| POST | `/api/public/participacao/{token}` | Confirmar presença via QR Code (público) |
| GET | `/api/medalhas` | Lista medalhas |
| GET | `/api/medalhas/calcular` | Calcular medalhas do semestre |
| POST | `/api/medalhas/enviar-certificados` | Enviar certificados de medalha |
| POST | `/api/medalhas/configuracao/ativar` | Ativar geração de certificados de medalha |
| POST | `/api/medalhas/configuracao/desativar` | Desativar geração de certificados de medalha |

---

## ✨ Funcionalidades

- Cadastro e gestão de eventos, palestrantes e participantes
- Inscrição em eventos via QR Code
- Confirmação de presença via QR Code com envio automático de certificado por e-mail
- Geração de certificados em PDF com assinatura do coordenador
- Sistema de medalhas por pontuação semestral
- Exportação de relatórios em CSV
- Autenticação com JWT
