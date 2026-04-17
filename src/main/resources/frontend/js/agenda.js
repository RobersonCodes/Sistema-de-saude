const agendaForm = document.getElementById("agenda-form");
const agendaMessageBox = document.getElementById("agenda-message");

function showAgendaMessage(message, type = "success") {
  if (!agendaMessageBox) return;

  agendaMessageBox.classList.remove("hidden", "message-success", "message-error");
  agendaMessageBox.classList.add(type === "success" ? "message-success" : "message-error");
  agendaMessageBox.textContent = message;
}

async function buscarAgendaPorProfissional() {
  const profissionalId = document.getElementById("busca-profissionalId")?.value.trim() || "";
  const data = document.getElementById("busca-data")?.value || "";

  if (!profissionalId || !data) {
    document.getElementById("agenda-table").innerHTML =
      `<p>Informe o ID do profissional e a data para buscar.</p>`;
    return;
  }

  try {
    const result = await apiFetch(
      `/agenda/profissional?profissionalId=${profissionalId}&data=${data}`
    );

    document.getElementById("agenda-table").innerHTML = renderTable(
      ["ID", "Data", "Hora", "Disponível", "Profissional", "Unidade"],
      (result || []).map((item) => [
        item.id,
        item.data,
        item.horaInicio,
        item.disponivel ? "Sim" : "Não",
        item.profissionalId,
        item.unidadeId
      ])
    );
  } catch (error) {
    if (error.status === 401 || error.status === 403) {
      logout();
      return;
    }

    document.getElementById("agenda-table").innerHTML =
      `<p>Erro ao buscar agenda.</p>`;
  }
}

agendaForm?.addEventListener("submit", async (e) => {
  e.preventDefault();

  redirectIfNotAuthenticated();
  bindLogoutButton();

  const payload = {
    data: document.getElementById("agenda-data").value,
    horaInicio: `${document.getElementById("agenda-horaInicio").value}:00`,
    profissionalId: Number(document.getElementById("agenda-profissionalId").value),
    unidadeId: Number(document.getElementById("agenda-unidadeId").value)
  };

  try {
    await apiFetch("/agenda", {
      method: "POST",
      body: JSON.stringify(payload)
    });

    showAgendaMessage("Horário cadastrado com sucesso.", "success");
    agendaForm.reset();
    document.getElementById("agenda-table").innerHTML = "";
  } catch (error) {
    let msg = error.message || "Erro ao cadastrar horário.";

    if (error?.data?.details?.length) {
      msg = error.data.details.join(" | ");
    }

    showAgendaMessage(msg, "error");
  }
});

document.getElementById("agenda-busca-form")?.addEventListener("submit", async (e) => {
  e.preventDefault();

  redirectIfNotAuthenticated();
  bindLogoutButton();

  await buscarAgendaPorProfissional();
});

redirectIfNotAuthenticated();
bindLogoutButton();