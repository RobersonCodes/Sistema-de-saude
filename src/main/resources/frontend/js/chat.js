redirectIfNotAuthenticated();
bindLogoutButton();

const chatBox = document.getElementById("chat-box");
const input = document.getElementById("chat-input");
const button = document.getElementById("send-btn");

function addMessage(text, type = "bot") {

  const div = document.createElement("div");

  div.classList.add("message", type);

  div.innerHTML = text;

  chatBox.appendChild(div);

  chatBox.scrollTop = chatBox.scrollHeight;

}


async function sendMessage(){

  const message = input.value.trim();

  if(!message) return;

  addMessage(message, "user");

  input.value = "";

  try{

    const response = await apiFetch("/chat",{
      method:"POST",
      body:JSON.stringify({
        mensagem: message
      })
    });

    addMessage(response.resposta || "Não entendi sua solicitação.");

  }catch(error){

    addMessage("Erro ao processar mensagem.", "bot");

  }

}


button.addEventListener("click", sendMessage);

input.addEventListener("keypress", function(e){

  if(e.key === "Enter"){
    sendMessage();
  }

});