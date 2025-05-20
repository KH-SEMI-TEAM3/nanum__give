document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");
    const nicknameInput = document.getElementById("nickname");
    const telInput = document.getElementById("tel");
    const pwInput = document.getElementById("checkPw");

    form.addEventListener("submit", function(e) {
        const nickname = nicknameInput.value.trim();
        const tel = telInput.value.trim();
        const pw = pwInput.value.trim();

        // 닉네임 검사
        if (nickname.length === 0) {
            alert("닉네임을 입력해주세요.");
            nicknameInput.focus();
            e.preventDefault();
            return;
        }

        // 전화번호 검사 (숫자만, 길이 10~11자리)
        const telRegex = /^0\d{9,10}$/;
        if (!telRegex.test(tel)) {
            alert("올바른 전화번호 형식이 아닙니다.");
            telInput.focus();
            e.preventDefault();
            return;
        }

        // 비밀번호 확인 입력 여부
        if (pw.length === 0) {
            alert("비밀번호를 입력해야 수정이 가능합니다.");
            pwInput.focus();
            e.preventDefault();
        }
    });
});


// 주소 검색 API 연동
function searchAddress() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById("postcode").value = data.zonecode;
            document.querySelector("input[name='memberAddress']").value = data.roadAddress;
            document.querySelector("input[name='memberAddressDetail']").focus();
        }
    }).open();
}