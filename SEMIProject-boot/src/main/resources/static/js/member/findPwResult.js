// ======================= 회원 가입 유효성 검사 =======================

// 필수 입력 항목의 유효성 검사 여부를 체크하기 위한 JS 객체
// - false : 해당 항목은 유효하지 않은 형식으로 작성됨
// - true : 해당 항목은 유효한 형식으로 작성됨
const checkObj = {
  memberPw: false,
  memberPwConfirm: false,
};

// ======================= 비밀번호 / 비밀번호확인 유효성 검사 =======================
// 1) 비밀번호 관련 요소 얻어오기
const memberPw = document.querySelector("#memberPw");
const memberPwConfirm = document.querySelector("#memberPwConfirm");
const pwMessage = document.querySelector("#pwMessage");
const PwConfirmMessage = document.querySelector("#PwConfirmMessage");

// 5) 비밀번호, 비밀번호 확인 같은지 검사하는 함수
const checkPw = () => {
  // 같을 경우
  if (memberPw.value === memberPwConfirm.value) {
    PwConfirmMessage.innerText = "비밀번호가 일치합니다.";
    PwConfirmMessage.classList.add("confirm");
    PwConfirmMessage.classList.remove("error");
    checkObj.memberPwConfirm = true; // 비밀번호 확인 true
    return;
  }

  // 다를 경우
  PwConfirmMessage.innerText = "비밀번호가 일치하지 않습니다.";
  PwConfirmMessage.classList.add("error");
  PwConfirmMessage.classList.remove("confirm");
  checkObj.memberPwConfirm = false; // 비밀번호 확인 false
};

// 2) 비밀번호 유효성 검사
memberPw.addEventListener("input", (e) => {
  // 입력 받은 비밀번호 값
  const inputPw = e.target.value;

  // 3) 입력되지 않은 경우
  if (inputPw.trim().length === 0) {
    pwMessage.innerText = "";
    pwMessage.classList.remove("confirm", "error");
    checkObj.memberPw = false; // 비밀번호가 유효하지 않다고 표시
    memberPw.value = ""; // 첫 글자 띄어쓰기 입력 못하게 막기
    return;
  }

  // 4) 입력 받은 비밀번호 정규식 검사
  // 비밀번호 정규표현식
  const regExp = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#_-])[A-Za-z\d!@#_-]{4,20}$/;

  if (!regExp.test(inputPw)) {
    // 유효하지 않으면
    pwMessage.innerText = "비밀번호가 유효하지 않습니다.";
    pwMessage.classList.add("error");
    pwMessage.classList.remove("confirm");
    checkObj.memberPw = false;
    return;
  }

  // 유효한 경우
  pwMessage.innerText = "유효한 비밀번호 형식입니다.";
  pwMessage.classList.add("confirm");
  pwMessage.classList.remove("error");
  checkObj.memberPw = true; // 유효한 비밀번호임을 명시

  // 비밀번호 입력 시 비밀번호 확인란의 값과 비교하는 코드 추가
  // 비밀번호 확인에 값이 작성되었을 때
  if (memberPwConfirm.value.length > 0) {
    checkPw();
  }
});

// 6) 비밀번호 확인 유효성 검사
memberPwConfirm.addEventListener("input", () => {
  if (checkObj.memberPw) {
    // memberPw가 유효한 경우
    checkPw(); // 비교하는 함수 수행
    return;
  }

  // memberPw가 유효하지 않은 경우
  // memberPwConfirm 유효하지 않아야 함
  checkObj.memberPwConfirm = false;
});

// ======================= 변경하기 버튼 클릭시 유효성 검사 여부 확인 =======================
const newPwForm = document.querySelector("#newPwForm"); // form 태그

// 회원 가입 폼 제출 시
newPwForm.addEventListener("submit", (e) => {
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
        case "memberPw":
          str = "비밀번호가 유효하지 않습니다.";
          break;

        case "memberPwConfirm":
          str = "비밀번호가 일치하지 않습니다.";
          break;
      }

      alert(str);
      document.getElementById(key).focus(); // 해당 input 초점 이동

      e.preventDefault(); // form 태그 기본 이벤트(제출) 막기

      return;
    }
  }
});

// ======================= placeholder 제거 처리 =======================
const inputs = document.querySelectorAll("input");

inputs.forEach((input) => {
  const placeholder = input.getAttribute("placeholder");

  input.addEventListener("focus", () => {
    input.setAttribute("data-placeholder", placeholder);
    input.removeAttribute("placeholder");
  });

  input.addEventListener("blur", () => {
    if (!input.value) {
      input.setAttribute("placeholder", input.getAttribute("data-placeholder"));
    }
  });
});