const checkAuthKey = document.getElementById("checkFindAuthKey");
const authKeyMessage = document.getElementById("findAuthKeyMessage");
const memberEmail = document.getElementById("findEmail");

const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

let authTimer; // 타이머 역할을 할 setInterval 함수를 저장할 변수

const initMin = 4; // 타이머 초기값 (분)
const initSec = 59; // 타이머 초기값 (초)
const initTime = "05:00";

// 실제 줄어드는 시간을 저장할 변수
let min = initMin;
let sec = initSec;

// 인증번호 요청
sendFindAuthKeyBtn.addEventListener("click", () => {
  const inputEmail = memberEmail.value.trim();

  if (inputEmail.length === 0 || !regExp.test(inputEmail)) {
    alert("정확한 이메일을 입력해주세요.");
    return;
  }

});

// 인증번호 입력 시 검증
checkAuthKey.addEventListener("input", () => {
  const inputKey = checkAuthKey.value.trim();

  // 시간 초과 체크 (타이머를 쓰는 경우에만 필요)
  if (min === 0 && sec === 0) {
    authKeyMessage.innerText = "인증 제한시간이 초과되었습니다. 다시 시도해주세요.";
    authKeyMessage.classList.add("error");
    authKeyMessage.classList.remove("confirm");
    return;
  }

  // 6자리 전에는 메시지 출력 안 함
  if (inputKey.length !== 6) {
    authKeyMessage.innerText = "";
    authKeyMessage.classList.remove("error", "confirm");
    return;
  }

  // 인증번호 검증 요청 (백엔드 필요 시)
  fetch("/email/checkAuthKey", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      email: memberEmail.value.trim(),
      authKey: inputKey,
    }),
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result === "1") {
        authKeyMessage.innerText = "인증되었습니다.";
        authKeyMessage.classList.add("confirm");
        authKeyMessage.classList.remove("error");
        clearInterval(authTimer); // 타이머 종료
      } else {
        authKeyMessage.innerText = "인증번호가 일치하지 않습니다.";
        authKeyMessage.classList.add("error");
        authKeyMessage.classList.remove("confirm");
      }
    })
    .catch((err) => {
      console.log("에러 발생", err);
      authKeyMessage.innerText = "서버 오류로 인증 실패.";
      authKeyMessage.classList.add("error");
      authKeyMessage.classList.remove("confirm");
    });
});


// ======================= placeholder 제거 처리 =======================
const inputs = document.querySelectorAll("input"); // 모든 input 요소 선택

inputs.forEach(input => {
  
  // 기존 placeholder 값을 data 속성으로 저장
  const placeholder = input.getAttribute("placeholder");

  // focus 시 placeholder 제거
  input.addEventListener("focus", () => {
    // placeholder가 존재할 때만 백업
    if (placeholder) {
      input.setAttribute("data-placeholder", placeholder);
      input.removeAttribute("placeholder");
    }
  });

  // blur(포커스 잃을 때) 시 값이 없으면 placeholder 복구
  input.addEventListener("blur", () => {
    if (input.value.trim().length === 0) {
      const savedPlaceholder = input.getAttribute("data-placeholder");
      if (savedPlaceholder) {
        input.setAttribute("placeholder", savedPlaceholder);
      }
    }
  });
});
