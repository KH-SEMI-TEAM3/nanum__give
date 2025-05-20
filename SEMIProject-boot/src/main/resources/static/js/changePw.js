document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");
    const currentPw = document.getElementById("currentPw");
    const newPw = document.getElementById("newPw");
    const confirmPw = document.getElementById("confirmPw");

    form.addEventListener("submit", function(e) {
        const cur = currentPw.value.trim();
        const newPass = newPw.value.trim();
        const confirm = confirmPw.value.trim();

        // 현재 비밀번호 비어있는지 확인
        if (cur.length === 0) {
            alert("현재 비밀번호를 입력해주세요.");
            currentPw.focus();
            e.preventDefault();
            return;
        }

        // 새 비밀번호 유효성 검사
        const pwRegex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#_\-])[A-Za-z\d!@#_\-]{8,20}$/;
        if (!pwRegex.test(newPass)) {
            alert("새 비밀번호는 영문/숫자/특수문자 포함 8~20자여야 합니다.");
            newPw.focus();
            e.preventDefault();
            return;
        }

        // 새 비밀번호와 확인 비밀번호가 일치하는지 검사
        if (newPass !== confirm) {
            alert("새 비밀번호가 서로 일치하지 않습니다.");
            confirmPw.focus();
            e.preventDefault();
        }
    });
});