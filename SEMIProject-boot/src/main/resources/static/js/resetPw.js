document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");
    const newPw = document.getElementById("newPw");
    const confirmPw = document.getElementById("confirmPw");

    form.addEventListener("submit", function(e) {
        const newPass = newPw.value.trim();
        const confirm = confirmPw.value.trim();

        // 새 비밀번호 유효성 검사
        const pwRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#_\-])[A-Za-z\d!@#_\-]{8,20}$/;
        if (!pwRegex.test(newPass)) {
            alert("비밀번호는 영문, 숫자, 특수문자 포함 8~20자여야 합니다.");
            newPw.focus();
            e.preventDefault();
            return;
        }

        // 새 비밀번호와 확인이 일치하는지 검사
        if (newPass !== confirm) {
            alert("비밀번호가 서로 일치하지 않습니다.");
            confirmPw.focus();
            e.preventDefault();
        }
    });
});