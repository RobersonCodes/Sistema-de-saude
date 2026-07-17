# Sistema de Gestão de Saúde Municipal (SUS Ready)

Sistema completo de gestão de saúde municipal inspirado na arquitetura do SUS, desenvolvido com foco em escalabilidade, segurança e padrão corporativo.

Projeto full stack preparado para interoperabilidade com plataformas nacionais como **e-SUS APS** e **RNDS**, incluindo integração com **Inteligência Artificial** para interpretação de linguagem natural do cidadão.

---

## Visão Geral

Este sistema simula um fluxo real de atendimento municipal, permitindo o gerenciamento completo de:

- pacientes
- profissionais de saúde
- unidades de saúde (UBS, hospitais)
- agenda médica
- consultas
- autenticação segura
- chatbot com IA
- integração com padrão SUS

A arquitetura segue padrões corporativos utilizados em sistemas hospitalares e healthtechs.

---

## Arquitetura

Arquitetura em camadas:

Controller → Service → Repository → Entity → DTO

Aplicação organizada seguindo princípios SOLID e boas práticas de desenvolvimento backend.

---

## Tecnologias Utilizadas

### Backend
- Java 17
- Spring Boot 3
- Spring Security
- JWT Authentication
- Spring Data JPA
- MySQL
- Lombok
- Swagger OpenAPI
- DTO Pattern
- Specification Pattern
- Global Exception Handler

### Integração com IA
- OpenAI API
- interpretação de linguagem natural
- identificação de intenção do usuário
- triagem automática

### Integração SUS
- arquitetura preparada para e-SUS APS
- estrutura compatível com RNDS
- log de interoperabilidade
- client mock de integração

### Frontend
- HTML
- CSS
- JavaScript
- Fetch API
- JWT em localStorage

---

## Estrutura do Projeto
src/main/java/com/saude/saomunicipal

controller
service
repository
entity
dto
exception
security
config

integration/sus
├── client
├── dto
├── entity
├── enums
├── mapper
├── repository
└── service

---

## Funcionalidades

### Pacientes
- cadastro completo
- validação de CPF
- vínculo com unidade de saúde
- paginação
- filtros dinâmicos

### Profissionais de Saúde
- especialidade
- vínculo com unidade
- controle de status ativo

### Unidades de Saúde
- UBS
- hospitais
- cadastro estruturado

### Agenda médica
- horários disponíveis
- vínculo com profissional
- controle de disponibilidade

### Consultas
- agendamento
- remarcação
- cancelamento
- histórico de atendimento

Agendar, remarcar ou cancelar uma consulta reserva/libera atomicamente o
horário correspondente na agenda do profissional (lock pessimista a nível de
banco): só é possível marcar uma consulta em um horário que já foi publicado
na agenda e que ainda esteja disponível, o que é o mecanismo que efetivamente
impede duas pessoas de ocuparem o mesmo horário — o problema que este projeto
existe para resolver.

status disponíveis e transições permitidas:

AGENDADA → CONFIRMADA, CANCELADA, FALTOU
CONFIRMADA → EM_ATENDIMENTO, CANCELADA, FALTOU
EM_ATENDIMENTO → REALIZADA, CANCELADA
REMARCADA → CONFIRMADA, CANCELADA, FALTOU
REALIZADA, CANCELADA, FALTOU → estados finais

REMARCADA só é atingido pelo endpoint dedicado de remarcação (que exige uma
nova data/hora); não é um destino válido de PATCH /consultas/{id}/status.

---

## Integração com Inteligência Artificial

O sistema interpreta mensagens do cidadão automaticamente.

Exemplo:

Entrada:
quero marcar consulta com cardiologista amanhã de manhã


Saída estruturada:

```json
{
  "intencao": "AGENDAR_CONSULTA",
  "especialidade": "CARDIOLOGIA",
  "data": "2026-04-20",
  "periodo": "MANHA"
}

Permite:

chatbot inteligente
triagem automática
agendamento automatizado
integração futura com WhatsApp
Integração com SUS

O sistema possui estrutura preparada para integração com:

e-SUS APS
RNDS (Rede Nacional de Dados em Saúde)
prontuário eletrônico municipal

Inclui:

DTOs padronizados
log de integração
client mock de interoperabilidade
arquitetura preparada para homologação institucional

Tabela:

integracao_log

Registra:

payload enviado
resposta recebida
status da integração
histórico de envio
Segurança
autenticação JWT
criptografia de senha
controle de roles
proteção de endpoints
tratamento global de exceções
Perfis de acesso (RBAC)

O primeiro usuário cadastrado no sistema vira automaticamente ADMIN
(bootstrap); todo auto-cadastro seguinte entra como ATENDENTE. Perfis com
mais privilégio (GESTOR, MEDICO, ou outro ADMIN) só podem ser atribuídos por
um ADMIN já autenticado, via POST /api/v1/auth/usuarios ou
PATCH /api/v1/auth/usuarios/{id}/role — o cliente nunca escolhe o próprio
perfil no auto-cadastro.

| Perfil     | Pode |
|------------|------|
| ADMIN      | Tudo, incluindo criar usuários e alterar perfis |
| GESTOR     | Unidades, profissionais, pacientes, consultas, agenda |
| ATENDENTE  | Pacientes e consultas (agendar, remarcar, cancelar, status) |
| MEDICO     | Agenda própria e consultas (status, remarcação, cancelamento) |

Os endpoints de chat/IA (`/api/v1/chat`, `/api/v1/chatbot`, `/api/v1/ia`)
são públicos de propósito — são o canal de autoatendimento do cidadão, que
não tem conta no sistema.

Variáveis de ambiente (produção)

| Variável | Obrigatória | Descrição |
|----------|-------------|-----------|
| `JWT_SECRET` | Sim | Chave de assinatura do JWT (≥256 bits). O valor em `application.properties` é só um fallback de dev. |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Sim | Conexão com o MySQL. |
| `OPENAI_API_KEY` | Para os recursos de IA | Sem ela, `/api/v1/ia/**` e `/api/v1/chat/**` respondem com uma mensagem de indisponibilidade em vez de derrubar a aplicação. |
| `OPENAI_MODEL` | Não | Default `gpt-4o-mini`. |

Endpoints principais
Auth

POST /api/v1/auth/register (auto-cadastro; perfil nunca é escolhido pelo cliente)
POST /api/v1/auth/login
GET /api/v1/auth/me
POST /api/v1/auth/usuarios (ADMIN — cria usuário com perfil explícito)
PATCH /api/v1/auth/usuarios/{id}/role (ADMIN — altera o perfil de um usuário)

Pacientes

GET /api/v1/pacientes
POST /api/v1/pacientes

Profissionais

GET /api/v1/profissionais
POST /api/v1/profissionais

Unidades

GET /api/v1/unidades
POST /api/v1/unidades

Consultas

GET /api/v1/consultas
POST /api/v1/consultas

PATCH /api/v1/consultas/{id}/status
PATCH /api/v1/consultas/{id}/remarcacao
PATCH /api/v1/consultas/{id}/cancelamento

Integração SUS (mock)

POST /api/v1/integracoes/sus/consultas/{id}/esus-aps

Testes

Suíte de testes de integração (JUnit + Spring Boot Test) rodando contra H2
em memória — não precisa de MySQL nem de OPENAI_API_KEY real:

.\mvnw.cmd test

Cobre autenticação/bootstrap de perfil, publicação de agenda, agendamento,
bloqueio de duplo agendamento, cancelamento/liberação de horário, remarcação
e a máquina de estados da consulta.

Como executar o projeto

Clonar repositório:

git clone https://github.com/RobersonCodes/Sistema-de-saude.git

Entrar na pasta:

cd Sistema-de-saude

Rodar aplicação:

Windows:

.\mvnw spring-boot:run

Linux/Mac:

./mvnw spring-boot:run
Documentação da API

Swagger:

http://localhost:8080/swagger-ui/index.html
Objetivo do Projeto

Demonstrar desenvolvimento de backend corporativo para sistemas de saúde pública, incluindo:

arquitetura escalável
interoperabilidade entre sistemas
uso de IA aplicada ao negócio
segurança robusta
organização profissional de código
Evoluções futuras
aplicativo mobile
chatbot WhatsApp
integração real com RNDS
dashboard analítico
previsão de demanda com IA
dockerização
deploy em nuvem
Autor

Roberson de Oliveira

Backend Developer

GitHub:
https://github.com/RobersonCodes

LinkedIn:
https://www.linkedin.com/in/roberson-de-oliveira-tecnologia

Licença

Projeto desenvolvido para fins educacionais e demonstração de portfólio.


---

## Como subir README

```powershell
git add README.md
git commit -m "docs: adiciona README profissional"
git push