# Banco de Dados

Banco utilizado:

MySQL

Nome:

saude_municipal_sl

---

## Principais tabelas

Paciente

id
nomeCompleto
cpf
dataNascimento
sexo
telefone
email
endereco

---

ProfissionalSaude

id
nome
especialidade
telefone
unidade_id

---

UnidadeSaude

id
nome
tipo
bairro
endereco
telefone

---

Consulta

id
dataHora
status
observacoes
paciente_id
profissional_id
unidade_id

---

Usuario

id
email
senha
role
ativo

---

IntegracaoLog

id
tipo_integracao
entidade
entidade_id
payload
resposta
sucesso
data_envio