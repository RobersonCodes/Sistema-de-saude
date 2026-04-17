let currentPage = 0;
let lastPage = false;

async function loadPacientes(page = 0) {
  redirectIfNotAuthenticated();
  bindLogoutButton();

  const nome = document.getElementById("nome").value.trim();
  const cpf = document.getElementById("cpf").value.trim();
  const unidadeId = document.getElementById("unidadeId").value.trim();
  const ativo = document.getElementById("ativo").value;

  const params = new URLSearchParams({
    page: String(page),
    size: "10",
    sortBy: "id",
    direction: "desc"
  });

  if (nome) params.append("nome", nome);
  if (cpf) params.append("cpf", cpf);
  if (unidadeId) params.append("unidadeId", unidadeId);
  if (ativo) params.append("ativo", ativo);

  try {
    const data = await apiFetch(`/pacientes?${params.toString()}`);

    currentPage = data.page;
    lastPage = data.last;

    document.getElementById("page-info").textContent =
      `Página ${data.page + 1} de ${data.totalPages || 1}`;

    document.getElementById("pacientes-table").innerHTML = renderTable(
      ["ID", "Nome", "CPF", "Telefone", "E-mail", "Ativo", "Unidade"],
      (data.content || []).map((item) => [
        item.id,
        item.nomeCompleto,
        item.cpf,
        item.telefone,
        item.email,
        item.ativo ? "Sim" : "Não",
        item.unidadeId
      ])
    );
  } catch (error) {
    if (error.status === 401 || error.status === 403) {
      logout();
      return;
    }

    document.getElementById("pacientes-table").innerHTML =
      `<p>Erro ao carregar pacientes.</p>`;
  }
}

document.getElementById("pacientes-filter-form")?.addEventListener("submit", (e) => {
  e.preventDefault();
  loadPacientes(0);
});

document.getElementById("prev-page")?.addEventListener("click", () => {
  if (currentPage > 0) {
    loadPacientes(currentPage - 1);
  }
});

document.getElementById("next-page")?.addEventListener("click", () => {
  if (!lastPage) {
    loadPacientes(currentPage + 1);
  }
});

loadPacientes();