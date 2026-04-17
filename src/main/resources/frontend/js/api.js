const API_BASE_URL = "http://localhost:8080/api/v1";

function getToken() {
  return localStorage.getItem("token");
}

function getAuthHeaders(extraHeaders = {}) {
  const token = getToken();

  return {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...extraHeaders
  };
}

async function apiFetch(endpoint, options = {}) {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: getAuthHeaders(options.headers || {})
  });

  const contentType = response.headers.get("content-type") || "";
  const isJson = contentType.includes("application/json");

  const data = isJson ? await response.json() : null;

  if (!response.ok) {
    const errorMessage =
      data?.message ||
      data?.error ||
      "Ocorreu um erro na comunicação com a API.";

    throw {
      status: response.status,
      data,
      message: errorMessage
    };
  }

  return data;
}

function redirectIfNotAuthenticated() {
  const token = getToken();

  if (!token) {
    window.location.href = "./login.html";
  }
}

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  window.location.href = "./login.html";
}

function bindLogoutButton() {
  const logoutBtn = document.getElementById("logout-btn");

  if (logoutBtn) {
    logoutBtn.addEventListener("click", logout);
  }
}

function renderTable(headers, rows) {
  if (!rows || !rows.length) {
    return `<p>Nenhum registro encontrado.</p>`;
  }

  return `
    <table>
      <thead>
        <tr>
          ${headers.map((header) => `<th>${header}</th>`).join("")}
        </tr>
      </thead>
      <tbody>
        ${rows
          .map(
            (row) => `
              <tr>
                ${row.map((cell) => `<td>${cell ?? "-"}</td>`).join("")}
              </tr>
            `
          )
          .join("")}
      </tbody>
    </table>
  `;
}