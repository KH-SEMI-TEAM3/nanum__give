const checkObj = {
  findMemberEmail: false,
  findAuthKey: false,
};

const findMemberEmail = document.querySelector("#findMemberEmail");
const sendFindAuthKeyBtn = document.querySelector("#sendFindAuthKeyBtn");
const findAuthKey = document.querySelector("#findAuthKey");
const findAuthKeyMessage = document.querySelector("#findAuthKeyMessage");
const findAuthKeyTimer = document.querySelector("#findAuthKeyTimer");

const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

let authTimer;

const initMin = 4;
const initSec = 59;
const initTime = "05:00";

let min = initMin;
let sec = initSec;

function addZero(number) {
  return number < 10 ? "0" + number : number;
}

// 이메일 인증 요청
sendFindAuthKeyBtn.addEventListener("click", () => {
  const inputEmail = findMemberEmail.value.trim();

  if (inputEmail.length === 0 || !regExp.test(inputEmail)) {
    alert("정확한 이메일을 입력해주세요.");
    return;
  }

  // 1. 이메일이 회원인지 확인
  fetch("/member/findCheckEmail?memberEmail=" + inputEmail)
    .then((resp) => resp.text())
    .then((result) => {
      if (result === "0") {
        alert("가입된 회원의 이메일이 아닙니다.");
        checkObj.findMemberEmail = false;
        clearInterval(authTimer);
        findAuthKeyTimer.innerText = "";

        return;
      }

      checkObj.findMemberEmail = true;
      alert("인증번호가 이메일로 발송되었습니다.");

      // 2. 인증 메일 발송
      fetch("/email/findId", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: findMemberEmail.value,
      })
        .then((resp) => resp.text())
        .then((result) => {
          if (result == 1) {
            console.log("인증 번호 발송 성공");
          } else {
            console.log("인증 번호 발송 실패..");
          }
        });
    })
    .catch((err) => {
      console.log("fetch 실패:", err);
      alert("이메일 확인 중 오류가 발생했습니다.");
      checkObj.findMemberEmail = false;
    });

  // 타이머 초기화 및 실행
  clearInterval(authTimer);

  min = initMin;
  sec = initSec;

  findAuthKey.disabled = false;
  findAuthKey.value = "";
  findAuthKeyTimer.innerText = initTime;
  findAuthKeyTimer.classList.remove("confirm", "error");
  findAuthKeyMessage.classList.remove("confirm", "error");

  findAuthKey.addEventListener("input", authKeyInputHandler);

  authTimer = setInterval(() => {
    findAuthKeyTimer.innerText = `${addZero(min)}:${addZero(sec)}`;

    if (min === 0 && sec === 0) {
      clearInterval(authTimer);
      checkObj.findAuthKey = false;
      findAuthKeyTimer.innerText = "";
      findAuthKeyMessage.innerText =
        "인증 제한시간이 초과되었습니다. 다시 시도해주세요.";
      findAuthKeyMessage.classList.add("error");
      findAuthKeyMessage.classList.remove("confirm");
      return;
    }

    if (sec === 0) {
      sec = 60;
      min--;
    }

    sec--;
  }, 1000);
});

// 인증번호 입력 시 검증
// findAuthKey.addEventListener("input", () => {
const authKeyInputHandler = () => {
  const inputKey = findAuthKey.value.trim();

  // 입력 비면 메시지 삭제
  if (inputKey.length === 0) {
    authKeyMessage.innerText = "";
    return;
  }
  // 6자리 입력 전에는 아무것도 안 함
  if (inputKey.length !== 6) {
    findAuthKeyMessage.innerText = "인증키 형식이 유효하지 않습니다.";
    findAuthKeyMessage.classList.remove("error", "confirm");
    checkObj.findAuthKey = false;
    return;
  }

  // 인증번호 확인 요청
  fetch("/email/checkAuthKey", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      email: findMemberEmail.value.trim(),
      authKey: inputKey,
    }),
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result === "1") {
        clearInterval(authTimer);
        findAuthKey.disabled = true;
        findAuthKeyMessage.innerText = "인증되었습니다.";
        findAuthKeyMessage.classList.add("confirm");
        findAuthKeyMessage.classList.remove("error");
        checkObj.findAuthKey = true;
      } else {
        findAuthKeyMessage.innerText = "인증번호가 일치하지 않습니다.";
        findAuthKeyMessage.classList.add("error");
        findAuthKeyMessage.classList.remove("confirm");
        checkObj.findAuthKey = false;
      }
    })
    .catch((err) => {
      console.log("인증 요청 에러", err);
      findAuthKeyMessage.innerText = "서버 오류로 인증 실패.";
      findAuthKeyMessage.classList.add("error");
      findAuthKeyMessage.classList.remove("confirm");
    });
};

// 매개변수 전달 받은 숫자가 10미만인 경우(한자리) 앞에 0을 붙여서 반환
function addZero(number) {
  if (number < 10) return "0" + number;
  else return number;
}

// ======================= 아이디찾기 버튼 클릭시 유효성 검사 여부 확인 ====================
const findIdForm = document.querySelector("#findIdForm");

// findIdForm.addEventListener("submit", (e) => {
//   // checkObj의 저장된 값 중
//   // 하나라도 false가 있으면 제출 X
//   // for ~ in (객체 전용 향상된 for문)
//   // for ~ of (배열 전용 향상된 for문)

//   for (let key in checkObj) {
//     // checkObj 요소의 key 값을 순서대로 꺼내옴

//     if (!checkObj[key]) {
//       // 현재 접근중인 checkObj[key]의 value 값이 false인 경우
//       let str; // 출력할 메시지를 저장할 변수
//       switch (key) {
//         case "findMemberEmail":
//           str = "가입하신 이메일을 작성해주세요.";
//           break;

//         case "findAuthKey":
//           str = "인증번호를 입력해주세요.";
//           break;
//       }

//       alert(str);
//       document.getElementById(key).focus(); // 해당 input 초점 이동

//       e.preventDefault(); // form 태그 기본 이벤트(제출) 막기

//       return;
//     }
//   }
// });

findIdForm.addEventListener("submit", (e) => {
  for (let key in checkObj) {
    if (!checkObj[key]) {
      let str;
      switch (key) {
        case "findMemberEmail":
          str = "이메일 인증을 먼저 완료해주세요.";
          break;
        case "findAuthKey":
          str = "인증번호를 확인해주세요.";
          break;
      }

      console.log("최종 checkObj 상태:", checkObj);

      alert(str);
      document.getElementById(key).focus();
      e.preventDefault();
      return;
    }
  }
});

// ======================= placeholder 제거 처리 =======================
document.querySelectorAll("input").forEach((input) => {
  const placeholder = input.getAttribute("placeholder");

  input.addEventListener("focus", () => {
    if (placeholder) {
      input.setAttribute("data-placeholder", placeholder);
      input.removeAttribute("placeholder");
    }
  });

  input.addEventListener("blur", () => {
    if (input.value.trim().length === 0) {
      const savedPlaceholder = input.getAttribute("data-placeholder");
      if (savedPlaceholder) {
        input.setAttribute("placeholder", savedPlaceholder);
      }
    }
  });
});