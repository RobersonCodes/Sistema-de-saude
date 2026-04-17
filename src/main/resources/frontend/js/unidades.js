let currentUnidadePage = 0;
let lastUnidadePage = false;

const unidadeForm = document.getElementById("unidade-form");
const unidadeMessageBox = document.getElementById("unidade-message");

function showUnidadeMessage(message, type = "success") {
  if (!unidadeMessageBox) return;

  unidadeMessageBox.classList.remove("hidden", "message-success", "message-error");
  unidadeMessageBox.classList.add(type === "success" ? "message-success" : "message-error");
  unidadeMessageBox.textContent = message;
}

async function loadUnidades(page = 0) {
  redirectIfNotAuthenticated();
  bindLogoutButton();

  const nome = document.getElementById("filtro-nome")?.value.trim() || "";
  const tipo = document.getElementById("filtro-tipo")?.value.trim() || "";
  const bairro = document.getElementById("filtro-bairro")?.value.trim() || "";
  const ativa = document.getElementById("filtro-ativa")?.value || "";

  const params = new URLSearchParams({
    page: String(page),
    size: "10",
    sortBy: "id",
    direction: "desc"
  });

  if (nome) params.append("nome", nome);
  if (tipo) params.append("tipo", tipo);
  if (bairro) params.append("bairro", bairro);
  if (ativa) params.append("ativa", ativa);

  try {
    const data = await apiFetch(`/unidades?${params.toString()}`);

    currentUnidadePage = data.page ?? 0;
    lastUnidadePage = data.last ?? false;

    const totalPages = data.totalPages || 1;
    document.getElementById("page-info").textContent =
      `Página ${currentUnidadePage + 1} de ${totalPages}`;

    document.getElementById("unidades-table").innerHTML = renderTable(
      ["ID", "Nome", "Tipo", "Bairro", "Endereço", "Telefone", "Ativa"],
      (data.content || []).map((item) => [
        item.id,
        item.nome,
        item.tipo,
        item.bairro,
        item.endereco,
        item.telefone || "-",
        item.ativa ? "Sim" : "Não"
      ])
    );
  } catch (error) {
    if (error.status === 401 || error.status === 403) {
      logout();
      return;
    }

    document.getElementById("unidades-table").innerHTML =
      `<p>Erro ao carregar unidades.</p>`;
  }
}

unidadeForm?.addEventListener("submit", async (e) => {
  e.preventDefault();

  const payload = {
    nome: document.getElementById("unidade-nome").value.trim(),
    tipo: document.getElementById("unidade-tipo").value.trim(),
    bairro: document.getElementById("unidade-bairro").value.trim(),
    endereco: document.getElementById("unidade-endereco").value.trim(),
    telefone: document.getElementById("unidade-telefone").value.trim() || null
  };

  try {
    await apiFetch("/unidades", {
      method: "POST",
      body: JSON.stringify(payload)
    });

    showUnidadeMessage("Unidade cadastrada com sucesso.", "success");
    unidadeForm.reset();
    loadUnidades(0);
  } catch (error) {
    let msg = error.message || "Erro ao cadastrar unidade.";

    if (error?.data?.details?.length) {
      msg = error.data.details.join(" | ");
    }

    showUnidadeMessage(msg, "error");
  }
});

document.getElementById("unidades-filter-form")?.addEventListener("submit", (e) => {
  e.preventDefault();
  loadUnidades(0);
});

document.getElementById("prev-page")?.addEventListener("click", () => {
  if (currentUnidadePage > 0) {
    loadUnidades(currentUnidadePage - 1);
  }
});

document.getElementById("next-page")?.addEventListener("click", () => {
  if (!lastUnidadePage) {
    loadUnidades(currentUnidadePage + 1);
  }
});

loadUnidades();