function togglePassword(id) {
    const input = document.getElementById(id);
    input.type = input.type === "password" ? "text" : "password";
}

function validateForm() {
    const inputs = document.querySelectorAll("input");
    for (let i of inputs) {
        if (i.value.trim() === "") {
            i.style.borderColor = "red";
            i.classList.add("shake");
            setTimeout(() => i.classList.remove("shake"), 300);
            return false;
        }
    }
    return true;
}


// Footer fade-in when visible
window.addEventListener("scroll", function () {
    const footer = document.querySelector(".main-footer");
    const footerPosition = footer.getBoundingClientRect().top;
    const screenPosition = window.innerHeight;

    if (footerPosition < screenPosition - 100) {
        footer.classList.add("show-footer");
    }
});
