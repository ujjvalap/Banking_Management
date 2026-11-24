const authView = document.querySelector("#authView");
const loginForm = document.querySelector("#loginForm");
const registerForm = document.querySelector("#registerForm");
const notice = document.querySelector("#notice");

function clearNotice() {
	if (!notice) return;
	notice.textContent = "";
	notice.hidden = true;
	notice.classList.remove("success");
}

function setMode(mode, clear = true) {
	if (!authView) return;
	const registering = mode === "register";
	loginForm.hidden = registering;
	registerForm.hidden = !registering;
	document.querySelector("#loginTab").classList.toggle("active", !registering);
	document.querySelector("#registerTab").classList.toggle("active", registering);
	document.querySelector("#loginTab").setAttribute("aria-selected", String(!registering));
	document.querySelector("#registerTab").setAttribute("aria-selected", String(registering));
	document.querySelector("#formEyebrow").textContent = registering ? "A GOOD PLACE TO BEGIN" : "WELCOME BACK";
	document.querySelector("#formTitle").innerHTML = registering ? "Make room for<br>what's next." : "Good to see<br>you again.";
	document.querySelector("#formSubtitle").textContent = registering
		? "Create your secure profile. It only takes a moment."
		: "Sign in to pick up right where you left off.";
	if (clear) clearNotice();
}

document.querySelector("#loginTab")?.addEventListener("click", () => setMode("login"));
document.querySelector("#registerTab")?.addEventListener("click", () => setMode("register"));
setMode(document.body.dataset.mode || "login", false);

document.querySelectorAll("[data-toggle-password]").forEach((button) => {
	button.addEventListener("click", () => {
		const password = button.closest(".input-wrap").querySelector("input");
		const visible = password.type === "password";
		password.type = visible ? "text" : "password";
		button.textContent = visible ? "Hide" : "Show";
		button.setAttribute("aria-label", visible ? "Hide password" : "Show password");
	});
});
