// ======================= placeholder 제거 처리 =======================
document.addEventListener("DOMContentLoaded", () => {
  const inputs = document.querySelectorAll("input[placeholder]");

  inputs.forEach((input) => {
    const originalPlaceholder = input.getAttribute("placeholder");

    input.addEventListener("focus", () => {
      input.dataset.placeholder = originalPlaceholder;
      input.removeAttribute("placeholder");
    });

    input.addEventListener("blur", () => {
      if (!input.value) {
        input.setAttribute("placeholder", input.dataset.placeholder);
      }
    });
  });
});

// 쿠키에서 매개변수로 전달받은 key와 일치하는 value 얻어와 반환하는 함수
const getCookie = (key) => {
  const cookies = document.cookie; // "K=V; K=V;..."

  // console.log(cookies);
  // cookie 에 저장된 문자열을 배열 형태로 변환
  const cookieList = cookies.split("; ").map((el) => el.split("=")); // ["K=V", "K=V"m, ..]

  // 배열.map(함수) : 배열의 각 요소를 이용해 콜백함수 수행 후
  // 결과 값으로 새로운 배열을 만들어서 반환하는 JS 내장 함수

  // 배열 -> JS 객체로 변환(그래야 다루기 쉬움)
  /* 
  [
    ['saveId', 'user01@kh.or.kr']
    ['test, 'testValue']
  ]
    2차원 배열 형태임
  */
  // 배열 -> JS 객체로 변환 (그래야 다루기 쉬움)

  const obj = {}; // 비어있는 객체 선언

  for (let i = 0; i < cookieList.length; i++) {
    const k = cookieList[i][0];
    const v = cookieList[i][1];
    obj[k] = v; // obj 객체에 K : V 형태로 추가
    // obj["saveId"] = 'user01@kh.or.kr';
    // obj["test"] = 'testValue';
  }

  // console.log(obj);
  return obj[key]; // 매개변수로 전달 받은 key와
  // obj 객체에 저장된 key가 일치하는 요소의 value값 반환
};

// ======================= 아이디 저장 체크 시 =======================

// // 실제 form 전송 시 서버로 값이 전달되는 숨겨진 진짜 체크박스 요소
// const saveIdCheckbox = document.querySelector("#saveIdCheckbox");
// // 시각적으로 보여지는 원형 체크박스 요소
// const customCheckbox = document.querySelector("#customCheckbox");

// // 아래 두 개의 이벤트 리스너 중 하나를 제거하고 통합합니다.
// // 아래와 같이 통합하는 것이 가장 좋습니다.

// saveIdCheckbox.addEventListener("click", () => {
//     // 1. 실제 체크박스의 상태를 토글
//     saveIdCheckbox.checked = !saveIdCheckbox.checked;

//     // 2. 시각적인 토글 효과 적용
//     customCheckbox.classList.toggle("active");
// });

// ======================= 로그인 창에 아이디 저장 =======================

// 아이디 작성 input 태그 요소
const loginMemberId = document.querySelector("#loginForm input[name='memberId']");

// 로그인 아이디 input 태그가 화면상에 존재할 때
if (loginMemberId != null) {
  // 쿠키 중 key 값이 "saveId"인 요소의 value 얻어오기
  const saveId = getCookie("saveId"); // 이메일 또는 undefiend

  if (saveId != undefined) {
    loginMemberId.value = saveId; // 쿠키에서 얻어온 이메일 값을 input 요소의 value에 세팅
    // 아이디 저장 체크박스에 체크해두기
    document.querySelector("input[name='saveId']").checked = true;
    // 시각적 표시까지 유지
    document.querySelector(".checkbox-custom").classList.add("active");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");

  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      const idInput = loginForm.querySelector("input[name='memberId']");
      const pwInput = loginForm.querySelector("input[name='memberPw']");

      const id = idInput.value.trim();
      const pw = pwInput.value.trim();

      if (id === "" || pw === "") {
        e.preventDefault(); // 폼 제출 막기
        alert("아이디 또는 비밀번호를 입력해 주세요.");
      }
    });
  }
});

document.querySelector(".close-button").addEventListener("click", () => {
  window.location.href = "/";
});