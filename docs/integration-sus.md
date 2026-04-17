# Integração SUS

O sistema possui arquitetura preparada para interoperabilidade com sistemas nacionais de saúde.

Compatível com:

e-SUS APS
RNDS

---

Fluxo de integração

Consulta criada
↓
DTO padrão SUS gerado
↓
Client envia payload
↓
Log de auditoria salvo
↓
Resposta registrada

---

Tabela de auditoria

integracao_log

Registra:

payload enviado
resposta recebida
status da integração
tentativas de envio

---

Estrutura de integração

integration/sus

client
dto
entity
enums
mapper
repository
service