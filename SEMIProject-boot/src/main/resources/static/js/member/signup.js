// ======================= 회원 가입 유효성 검사 =======================

// 필수 입력 항목의 유효성 검사 여부를 체크하기 위한 JS 객체
// - false : 해당 항목은 유효하지 않은 형식으로 작성됨
// - true : 해당 항목은 유효한 형식으로 작성됨
const checkObj = {
  memberId: false,
  memberPw: false,
  memberPwConfirm: false,
  memberNickname: false,
  memberEmail: false,
  memberTel: false,
  authKey: false,
};

// ======================= 아이디 유효성 검사 =======================
const memberId = document.querySelector("#memberId");
const memberIdMessage = document.querySelector("#memberIdMessage");

memberId.addEventListener("input", (e) => {
  const inputMemberId = e.target.value.trim();

  // 입력을 안 한 경우
  if (inputMemberId.length === 0) {
    memberIdMessage.innerText = "";
    memberIdMessage.classList.remove("confirm", "error");
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

  // 중복 검사
  fetch(`/member/checkMemberId?memberId=${inputMemberId}`)
    .then((resp) => resp.text())
    .then((count) => {
      if (count == 1) {
        // 중복O
        memberIdMessage.innerText = "이미 사용중인 아이디입니다.";
        memberIdMessage.classList.add("error");
        memberIdMessage.classList.remove("confirm");
        checkObj.memberId = false;
        return;
      }

      // 중복X
      memberIdMessage.innerText = "사용 가능한 아이디입니다.";
      memberIdMessage.classList.add("confirm");
      memberIdMessage.classList.remove("error");
      checkObj.memberId = true;
    })
    .catch((err) => console.log(err));
});

// ======================= 이메일 유효성 검사 =======================
const sendAuthKeyBtn = document.querySelector("#sendAuthKeyBtn");
const memberEmail = document.querySelector("#memberEmail");

const checkAuthKey = document.querySelector("#checkAuthKey");
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
  fetch("/member/checkEmail?memberEmail=" + inputEmail)
    .then((resp) => resp.text())
    .then((count) => {
      if (count == "1") {
        alert("이미 사용중인 이메일입니다.");
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
        header: { "Content-Type": "application/json" },
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
checkAuthKey.addEventListener("input", () => {
  const inputKey = checkAuthKey.value.trim();

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

// ======================= 닉네임 유효성 검사 =======================
const memberNickname = document.querySelector("#memberNickname");
const nickMessage = document.querySelector("#nickMessage");

memberNickname.addEventListener("input", (e) => {
  const inputNickname = e.target.value;
  // 1) 입력 안한 경우
  if (inputNickname.trim().length === 0) {
    nickMessage.innerText = "";
    nickMessage.classList.remove("confirm", "error");
    checkObj.memberNickname = false;
    return;
  }

  // 2) 정규식 검사
  const regExp = /^[가-힣\w\d]{2,10}$/;

  if (!regExp.test(inputNickname)) {
    // 유효하지 않은 경우
    nickMessage.innerText = "유효하지 않은 닉네임 형식입니다.";
    nickMessage.classList.add("error");
    nickMessage.classList.remove("confirm");
    checkObj.memberNickname = false;
    return;
  }

  // 3) 중복 검사
  fetch("/member/checkNickname?memberNickname=" + inputNickname)
    .then((resp) => resp.text())
    .then((count) => {
      if (count == 1) {
        // 중복O
        nickMessage.innerText = "이미 사용중인 닉네임 입니다.";
        nickMessage.classList.add("error");
        nickMessage.classList.remove("confirm");
        checkObj.memberNickname = false;
        return;
      }

      // 중복X
      nickMessage.innerText = "사용가능한 닉네임 입니다.";
      nickMessage.classList.add("confirm");
      nickMessage.classList.remove("error");
      checkObj.memberNickname = true;
    })
    .catch((err) => console.log(err));
});

// ======================= 휴대폰 번호 유효성 검사 =======================
const memberTel = document.querySelector("#memberTel");
const telMessage = document.querySelector("#telMessage");

memberTel.addEventListener("input", (e) => {
  const inputTel = e.target.value;
  if (inputTel.trim().length === 0) {
    telMessage.innerText = "";
    telMessage.classList.remove("error", "confirm");
    checkObj.memberTel = false;
    memberTel.value = "";
    return;
  }

  const regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;

  if (!regExp.test(inputTel)) {
    telMessage.innerText = "유효하지 않은 전화번호 형식입니다.";
    telMessage.classList.add("error");
    telMessage.classList.remove("confirm");
    checkObj.memberTel = false;
    return;
  }

  telMessage.innerText = "유효한 전화번호 형식입니다.";
  telMessage.classList.add("confirm");
  telMessage.classList.remove("error");
  checkObj.memberTel = true;
});

// ======================= 다음 주소 API 다루기 =======================
function execDaumPostcode() {
  new daum.Postcode({
    oncomplete: function (data) {
      // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

      // 각 주소의 노출 규칙에 따라 주소를 조합한다.
      // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
      var addr = ""; // 주소 변수

      //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
      if (data.userSelectedType === "R") {
        // 사용자가 도로명 주소를 선택했을 경우
        addr = data.roadAddress;
      } else {
        // 사용자가 지번 주소를 선택했을 경우(J)
        addr = data.jibunAddress;
      }

      // 우편번호와 주소 정보를 해당 필드에 넣는다.
      document.getElementById("postcode").value = data.zonecode;
      document.getElementById("address").value = addr;
      // 커서를 상세주소 필드로 이동한다.
      document.getElementById("detailAddress").focus();
    },
  }).open();
}

// 주소 검색 버튼 클릭 시
document
  .querySelector("#searchAddress")
  .addEventListener("click", execDaumPostcode);

// ======================= 회원가입 버튼 클릭시 유효성 검사 여부 확인 =======================
const signUpForm = document.querySelector("#signUpForm"); // form 태그

// 회원 가입 폼 제출 시
signUpForm.addEventListener("submit", (e) => {
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

        case "memberPw":
          str = "비밀번호가 유효하지 않습니다.";
          break;

        case "memberPwConfirm":
          str = "비밀번호가 일치하지 않습니다.";
          break;

        case "memberNickname":
          str = "닉네임이 유효하지 않습니다.";
          break;

        case "memberEmail":
          str = "이메일이 유효하지 않습니다.";
          break;

        case "memberTel":
          str = "전화번호가 유효하지 않습니다";
          break;

        case "authKey":
          str = "이메일이 인증되지 않습니다.";
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
