const loginForm = document.getElementById("login-form");
const loginMessage = document.getElementById("login-message");

function showLoginMessage(message, type = "error") {
  if (!loginMessage) return;

  loginMessage.classList.remove("hidden", "message-success", "message-error");
  loginMessage.classList.add(type === "success" ? "message-success" : "message-error");
  loginMessage.textContent = message;
}

if (loginForm) {
  loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const senha = document.getElementById("senha").value.trim();

    try {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, senha })
      });

      const data = await response.json();

      if (!response.ok) {
        throw data;
      }

      localStorage.setItem("token", data.token);

      localStorage.setItem(
        "user",
        JSON.stringify({
          email: data.email || "",
          role: data.role || ""
        })
      );

      showLoginMessage("Login realizado com sucesso.", "success");

      setTimeout(() => {
        window.location.href = "./dashboard.html";
      }, 500);
    } catch (error) {
      let message =
        error?.message ||
        "Não foi possível realizar login. Verifique suas credenciais.";

      if (error?.details?.length) {
        message = error.details.join(" | ");
      }

      showLoginMessage(message, "error");
    }
  });
}