let currentProfissionalPage = 0;
let lastProfissionalPage = false;

const profissionalForm = document.getElementById("profissional-form");
const profissionalMessageBox = document.getElementById("profissional-message");

function showProfissionalMessage(message, type = "success") {
  if (!profissionalMessageBox) return;

  profissionalMessageBox.classList.remove("hidden", "message-success", "message-error");
  profissionalMessageBox.classList.add(type === "success" ? "message-success" : "message-error");
  profissionalMessageBox.textContent = message;
}

async function loadProfissionais(page = 0) {
  redirectIfNotAuthenticated();
  bindLogoutButton();

  const nome = document.getElementById("filtro-nome")?.value.trim() || "";
  const cargo = document.getElementById("filtro-cargo")?.value.trim() || "";
  const especialidade = document.getElementById("filtro-especialidade")?.value.trim() || "";
  const unidadeId = document.getElementById("filtro-unidadeId")?.value.trim() || "";

  const params = new URLSearchParams({
    page: String(page),
    size: "10",
    sortBy: "id",
    direction: "desc"
  });

  if (nome) params.append("nome", nome);
  if (cargo) params.append("cargo", cargo);
  if (especialidade) params.append("especialidade", especialidade);
  if (unidadeId) params.append("unidadeId", unidadeId);

  try {
    const data = await apiFetch(`/profissionais?${params.toString()}`);

    currentProfissionalPage = data.page ?? 0;
    lastProfissionalPage = data.last ?? false;

    const totalPages = data.totalPages || 1;
    document.getElementById("page-info").textContent =
      `Página ${currentProfissionalPage + 1} de ${totalPages}`;

    document.getElementById("profissionais-table").innerHTML = renderTable(
      ["ID", "Nome", "Cargo", "Especialidade", "Telefone", "Unidade"],
      (data.content || []).map((item) => [
        item.id,
        item.nome,
        item.cargo,
        item.especialidade || "-",
        item.telefone || "-",
        item.unidadeId ?? "-"
      ])
    );
  } catch (error) {
    if (error.status === 401 || error.status === 403) {
      logout();
      return;
    }

    document.getElementById("profissionais-table").innerHTML =
      `<p>Erro ao carregar profissionais.</p>`;
  }
}

profissionalForm?.addEventListener("submit", async (e) => {
  e.preventDefault();

  const payload = {
    nome: document.getElementById("prof-nome").value.trim(),
    cargo: document.getElementById("prof-cargo").value.trim(),
    especialidade: document.getElementById("prof-especialidade").value.trim() || null,
    telefone: document.getElementById("prof-telefone").value.trim() || null,
    unidadeId: Number(document.getElementById("prof-unidadeId").value)
  };

  try {
    await apiFetch("/profissionais", {
      method: "POST",
      body: JSON.stringify(payload)
    });

    showProfissionalMessage("Profissional cadastrado com sucesso.", "success");
    profissionalForm.reset();
    loadProfissionais(0);
  } catch (error) {
    let msg = error.message || "Erro ao cadastrar profissional.";

    if (error?.data?.details?.length) {
      msg = error.data.details.join(" | ");
    }

    showProfissionalMessage(msg, "error");
  }
});

document.getElementById("profissionais-filter-form")?.addEventListener("submit", (e) => {
  e.preventDefault();
  loadProfissionais(0);
});

document.getElementById("prev-page")?.addEventListener("click", () => {
  if (currentProfissionalPage > 0) {
    loadProfissionais(currentProfissionalPage - 1);
  }
});

document.getElementById("next-page")?.addEventListener("click", () => {
  if (!lastProfissionalPage) {
    loadProfissionais(currentProfissionalPage + 1);
  }
});

loadProfissionais();