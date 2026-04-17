# Arquitetura do Sistema

O sistema foi projetado utilizando arquitetura em camadas seguindo boas práticas corporativas e princípios SOLID.

## Camadas

Controller
Responsável por receber requisições HTTP e retornar respostas.

Service
Contém regras de negócio da aplicação.

Repository
Comunicação com banco de dados utilizando Spring Data JPA.

DTO
Objetos utilizados para entrada e saída de dados.

Entity
Representação das tabelas do banco de dados.

Security
Controle de autenticação e autorização utilizando JWT.

Integration
Camada responsável por comunicação com sistemas externos como e-SUS APS.

---

## Fluxo de requisição

Client → Controller → Service → Repository → Database

Response → DTO → Client

---

## Estrutura de pacotes

com.saude.saomunicipal

controller
service
repository
entity
dto
security
exception
config
integration.sus