// ======================= 회원 가입 유효성 검사 =======================

// 필수 입력 항목의 유효성 검사 여부를 체크하기 위한 JS 객체
// - false : 해당 항목은 유효하지 않은 형식으로 작성됨
// - true : 해당 항목은 유효한 형식으로 작성됨
const checkObj = {
  memberId: false,
  memberEmail: false,
  authKey: false,
};

// ======================= 가입한 회원 아이디 확인 =======================
const memberId = document.querySelector("#memberId");

memberId.addEventListener("input", (e) => {

  const inputMemberId = e.target.value.trim();

  if(inputMemberId.length === 0) {
    checkObj.memberId = false;
    return;
  }

  // 정규식 검사
  const regExp = /^[a-zA-Z0-9]{4,15}$/;

  if (!regExp.test(inputMemberId)) {
    // 유효하지 않은 경우
    memberIdMessage.innerText = "4~15자 사이의 영문과 숫자만 가능합니다";
    memberIdMessage.classList.add("error");
    memberIdMessage.classList.remove("confirm");
    checkObj.memberId = false;
    return;
  }
  
  // 입력된 회원 아이디 체크
  fetch(`/member/checkMemberId?memberId=${inputMemberId}`)
  .then((resp) => resp.text())
  .then((result) => {
    if(result == 0) {
      return;
    }
    checkObj.memberId = true;
  })

});

// ======================= 이메일 유효성 검사 =======================
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn");
const memberEmail = document.querySelector("#memberEmail");

const authKey = document.querySelector("#authKey");
const authKeyMessage = document.querySelector("#authKeyMessage");

const regExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

let authTimer; // 타이머 역할을 할 setInterval 함수를 저장할 변수

const initMin = 4; // 타이머 초기값 (분)
const initSec = 59; // 타이머 초기값 (초)
const initTime = "05:00";

// 실제 줄어드는 시간을 저장할 변수
let min = initMin;
let sec = initSec;

// 인증 버튼 클릭 시 이메일 유효성 검사
sendAuthKeyBtn.addEventListener("click", () => {
  const inputEmail = memberEmail.value.trim();

  if (inputEmail.length === 0 || !regExp.test(inputEmail)) {
    alert("정확한 이메일을 입력해주세요.");
    checkObj.memberEmail = false;
    return;
  }

  // 중복 검사
  fetch("/member/findCheckEmail?memberEmail=" + inputEmail)
    .then((resp) => resp.text())
    .then((count) => {
      if (count == 0) {
        alert("가입된 회원의 이메일이 아닙니다.");
        checkObj.memberEmail = false;
        return;
      }

      // 사용 가능
      alert("인증번호가 이메일로 발송되었습니다.");
      checkObj.memberEmail = true;

      min = initMin;
      sec = initSec;

      // 이전 동작중인 인터벌 클리어(없애기)
      clearInterval(authTimer);

      fetch("/email/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: memberEmail.value,
      })
        .then((resp) => resp.text())
        .then((result) => {
          if (result == 1) {
            console.log("인증 번호 발송 성공");
          } else {
            console.log("인증 번호 발송 실패..");
          }
        });

      authKeyMessage.innerText = initTime; // 5:00 세팅
      authKeyMessage.classList.remove("confirm", "error");

      authTimer = setInterval(() => {
        authKeyMessage.innerText = `${addZero(min)}:${addZero(sec)}`;

        // 0분 0초인 경우("00:00 출력 후")
        if (min == 0 && sec == 0) {
          checkObj.authKey = false; // 인증 못함
          clearInterval(authTimer); // interval 멈춤
          authKeyMessage.classList.add("error");
          authKeyMessage.classList.remove("confirm");
          return;
        }

        // 0초인 경우(0초를 출력한 후)
        if (sec == 0) {
          sec = 60;
          min--;
        }

        sec--; // 1초 감소
      }, 1000); // 1초 지연시간
    })

    .catch((err) => {
      console.log(err);
      alert("이메일 확인 중 오류가 발생했습니다.");
      checkObj.memberEmail = false;
    });
});

// ======================= 인증번호 확인 =======================
authKey.addEventListener("input", () => {
  const inputKey = authKey.value.trim();

  // 시간 초과 시 처리
  if (min === 0 && sec === 0) {
    authKeyMessage.innerText = "인증 제한시간이 초과되었습니다. 다시 시도해주세요.";
    authKeyMessage.classList.add("error");
    authKeyMessage.classList.remove("confirm");
    checkObj.authKey = false;
    return;
  }

  // 6자리 입력 전에는 아무것도 안 함
  if (inputKey.length !== 6) {
    authKeyMessage.innerText = "";
    authKeyMessage.classList.remove("error", "confirm");
    checkObj.authKey = false;
    return;
  }

  // 인증번호 확인 요청
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
        clearInterval(authTimer);
        authKeyMessage.innerText = "인증되었습니다.";
        authKeyMessage.classList.add("confirm");
        authKeyMessage.classList.remove("error");
        checkObj.authKey = true;
      } else {
        authKeyMessage.innerText = "인증번호가 일치하지 않습니다.";
        authKeyMessage.classList.add("error");
        authKeyMessage.classList.remove("confirm");
        checkObj.authKey = false;
      }
    })
    .catch((err) => {
      console.log(err);
      authKeyMessage.innerText = "인증 중 오류가 발생했습니다.";
      authKeyMessage.classList.add("error");
      authKeyMessage.classList.remove("confirm");
      checkObj.authKey = false;
    });
});

// 매개변수 전달 받은 숫자가 10미만인 경우(한자리) 앞에 0을 붙여서 반환
function addZero(number) {
  if (number < 10) return "0" + number;
  else return number;
}

// ======================= 회원가입 버튼 클릭시 유효성 검사 여부 확인 =======================
const findPwForm = document.querySelector("#findPwForm"); // form 태그

// 회원 가입 폼 제출 시
findPwForm.addEventListener("submit", (e) => {
  // checkObj의 저장된 값 중
  // 하나라도 false가 있으면 제출 X
  // for ~ in (객체 전용 향상된 for문)
  // for ~ of (배열 전용 향상된 for문)

  for (let key in checkObj) {
    // checkObj 요소의 key 값을 순서대로 꺼내옴

    if (!checkObj[key]) {
      // 현재 접근중인 checkObj[key]의 value 값이 false인 경우
      let str; // 출력할 메시지를 저장할 변수
      switch (key) {
        case "memberId":
          str = "아이디가 유효하지 않습니다.";
          break;

        case "memberEmail":
          str = "이메일이 유효하지 않습니다.";
          break;

        case "authKey":
          str = "이메일이 인증되지 않습니다.";
          break;
      }

      console.log("최종 checkObj 상태:", checkObj);

      alert(str);
      document.getElementById(key).focus(); // 해당 input 초점 이동

      e.preventDefault(); // form 태그 기본 이벤트(제출) 막기

      return;
    }
  }
});

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

