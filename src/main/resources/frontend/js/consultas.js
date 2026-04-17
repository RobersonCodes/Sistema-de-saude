let currentConsultaPage = 0;
let lastConsultaPage = false;

async function loadConsultas(page = 0) {
  redirectIfNotAuthenticated();
  bindLogoutButton();

  const params = new URLSearchParams({
    page: String(page),
    size: "10",
    sortBy: "id",
    direction: "desc"
  });

  try {
    const data = await apiFetch(`/consultas?${params.toString()}`);

    currentConsultaPage = data.number ?? data.page ?? 0;
    lastConsultaPage = data.last ?? false;

    const content = data.content || [];

    document.getElementById("page-info").textContent =
      `Página ${(currentConsultaPage || 0) + 1} de ${data.totalPages || 1}`;

    document.getElementById("consultas-table").innerHTML = renderTable(
      ["ID", "Data/Hora", "Status", "Observações", "Paciente", "Profissional", "Unidade"],
      content.map((item) => [
        item.id,
        item.dataHora,
        `<span class="status-pill">${item.status}</span>`,
        item.observacoes,
        item.pacienteId,
        item.profissionalId,
        item.unidadeId
      ])
    );
  } catch (error) {
    if (error.status === 401 || error.status === 403) {
      logout();
      return;
    }

    document.getElementById("consultas-table").innerHTML =
      `<p>Erro ao carregar consultas.</p>`;
  }
}

document.getElementById("prev-page")?.addEventListener("click", () => {
  if (currentConsultaPage > 0) {
    loadConsultas(currentConsultaPage - 1);
  }
});

document.getElementById("next-page")?.addEventListener("click", () => {
  if (!lastConsultaPage) {
    loadConsultas(currentConsultaPage + 1);
  }
});

loadConsultas();