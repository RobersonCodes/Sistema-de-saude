async function loadDashboard() {
  redirectIfNotAuthenticated();
  bindLogoutButton();

  try {
    const user = await apiFetch("/auth/me");
    const pacientes = await apiFetch("/pacientes?page=0&size=5&sortBy=id&direction=desc");
    const consultas = await apiFetch("/consultas?page=0&size=5&sortBy=id&direction=desc");

    const userNameEl = document.getElementById("user-name");
    const userRoleEl = document.getElementById("user-role");
    const cardUserEmailEl = document.getElementById("card-user-email");
    const cardUserRoleEl = document.getElementById("card-user-role");
    const cardPacientesTotalEl = document.getElementById("card-pacientes-total");
    const cardConsultasTotalEl = document.getElementById("card-consultas-total");
    const pacientesBox = document.getElementById("dashboard-pacientes");
    const consultasBox = document.getElementById("dashboard-consultas");

    if (userNameEl) {
      userNameEl.textContent = user.nome || user.email || "Usuário";
    }

    if (userRoleEl) {
      userRoleEl.textContent = user.role || "-";
    }

    if (cardUserEmailEl) {
      cardUserEmailEl.textContent = user.email || "-";
    }

    if (cardUserRoleEl) {
      cardUserRoleEl.textContent = user.role || "-";
    }

    if (cardPacientesTotalEl) {
      cardPacientesTotalEl.textContent = pacientes.totalElements ?? 0;
    }

    if (cardConsultasTotalEl) {
      cardConsultasTotalEl.textContent = consultas.totalElements ?? 0;
    }

    if (pacientesBox) {
      pacientesBox.innerHTML = renderTable(
        ["ID", "Nome", "CPF", "Ativo", "Unidade"],
        (pacientes.content || []).map((item) => [
          item.id,
          item.nomeCompleto,
          item.cpf,
          item.ativo ? "Sim" : "Não",
          item.unidadeId ?? "-"
        ])
      );
    }

    if (consultasBox) {
      consultasBox.innerHTML = renderTable(
        ["ID", "Data/Hora", "Status", "Paciente", "Profissional", "Unidade"],
        (consultas.content || []).map((item) => [
          item.id,
          item.dataHora,
          `<span class="status-pill">${item.status}</span>`,
          item.pacienteId,
          item.profissionalId,
          item.unidadeId
        ])
      );
    }
  } catch (error) {
    console.error("Erro ao carregar dashboard:", error);

    if (error.status === 401 || error.status === 403) {
      logout();
      return;
    }

    const pacientesBox = document.getElementById("dashboard-pacientes");
    const consultasBox = document.getElementById("dashboard-consultas");

    if (pacientesBox) {
      pacientesBox.innerHTML = `<p>Não foi possível carregar os pacientes.</p>`;
    }

    if (consultasBox) {
      consultasBox.innerHTML = `<p>Não foi possível carregar as consultas.</p>`;
    }
  }
}

loadDashboard();