const secession = document.querySelector("#secession");

if (secession != null) {

    secession.addEventListener("submit", e => {

        const memberPw = document.querySelector("#memberPw");
        const agree = document.querySelector("#agree");

        // - 비밀번호 입력 되었는지 확인
        if (memberPw.value.trim().length == 0) {
            alert("비밀번호를 입력해주세요.");
            e.preventDefault(); // 제출막기
            return;
        }

        // 약관 동의 체크 확인
        // checkbox 또는 radio checked 속성
        // - checked -> 체크 시 true, 미체크시 false 반환

        if (!agree.checked) { // 체크 안됐을 때
            alert("약관에 동의해주세요");
            e.preventDefault();
            return;
        }

        // 정말 탈퇴? 물어보기
        if (!confirm("정말 탈퇴 하시겠습니까?")) {
            alert("취소 되었습니다.");
            e.preventDefault();
            return;
        }
    });
}

// ======================= placeholder 제거 처리 =======================
document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll("input").forEach((input) => {
    input.addEventListener("focus", () => {
      const placeholder = input.getAttribute("placeholder");
      if (placeholder) {
        input.setAttribute("data-placeholder", placeholder); // 저장
        input.removeAttribute("placeholder"); // 제거
      }
    });

    input.addEventListener("blur", () => {
      if (input.value.trim().length === 0) {
        const savedPlaceholder = input.getAttribute("data-placeholder");
        if (savedPlaceholder) {
          input.setAttribute("placeholder", savedPlaceholder); // 복원
        }
      }
    });
  });
});