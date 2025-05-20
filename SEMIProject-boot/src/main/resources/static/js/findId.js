document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("findIdForm");
    const emailInput = document.getElementById("email");

    form.addEventListener("submit", function(e) {
        const email = emailInput.value.trim();
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (email.length === 0) {
            alert("이메일을 입력해주세요.");
            emailInput.focus();
            e.preventDefault();
            return;
        }

        if (!emailRegex.test(email)) {
            alert("올바른 이메일 형식이 아닙니다.");
            emailInput.focus();
            e.preventDefault();
            return;
        }

        // 필요 시: 이후 Ajax로 이메일 인증번호 요청도 가능
        // 예: fetch("/member/sendAuthKey", { method: "POST", body: ... });
    });
});