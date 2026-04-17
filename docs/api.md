# Documentação da API

Base URL:

http://localhost:8080

Swagger:

http://localhost:8080/swagger-ui/index.html

---

## Autenticação

POST /api/v1/auth/register

Cria novo usuário.

POST /api/v1/auth/login

Retorna token JWT.

GET /api/v1/auth/me

Retorna usuário autenticado.

---

## Pacientes

GET /api/v1/pacientes

Lista pacientes com paginação.

POST /api/v1/pacientes

Cadastra paciente.

---

## Profissionais

GET /api/v1/profissionais

Lista profissionais.

POST /api/v1/profissionais

Cadastra profissional.

---

## Unidades de saúde

GET /api/v1/unidades

Lista unidades.

POST /api/v1/unidades

Cadastra unidade.

---

## Consultas

GET /api/v1/consultas

Lista consultas.

POST /api/v1/consultas

Cria consulta.

PATCH /api/v1/consultas/{id}/status

Atualiza status da consulta.

PATCH /api/v1/consultas/{id}/remarcacao

Remarca consulta.

PATCH /api/v1/consultas/{id}/cancelamento

Cancela consulta.

---

## Integração SUS

POST /api/v1/integracoes/sus/consultas/{id}/esus-aps

Envia consulta para integração e-SUS APS.

---

## IA

POST /api/v1/ia

Interpreta texto do cidadão.

POST /api/v1/chat

Chat com IA.

POST /api/v1/chatbot

Fluxo automatizado de atendimento.