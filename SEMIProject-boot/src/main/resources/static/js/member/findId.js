const sendFindAuthKeyBtn = document.getElementById("sendFindAuthKeyBtn");
const checkFindAuthKey = document.getElementById("checkFindAuthKey");
const findEmailInput = document.getElementById("findEmail");
const findAuthKeyMessage = document.getElementById("findAuthKeyMessage");
const findIdForm = document.getElementById("findIdForm");

let checkObj = { email: false, authKey: false };

// 인증번호 요청
sendFindAuthKeyBtn.addEventListener("click", () => {
  const email = findEmailInput.value.trim();

  if (email === "") {
    alert("이메일을 입력해주세요.");
    return;
  }

  fetch("/member/findCheckEmail?memberEmail=" + email)
    .then(resp => resp.text())
    .then(count => {
      if (count === "0") {
        alert("가입된 이메일이 아닙니다.");
        checkObj.email = false;
      } else {
        return fetch("/email/sendFindAuthKey", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: email,
        });
      }
    })
    .then(resp => resp?.text())
    .then(result => {
      if (result == "1") {
        alert("인증번호가 이메일로 전송되었습니다.");
        checkObj.email = true;
      } else if (result != null) {
        alert("인증번호 전송 실패");
      }
    })
    .catch(err => {
      console.error(err);
      alert("인증 요청 중 오류가 발생했습니다.");
    });
});

// 인증번호 확인
checkFindAuthKey.addEventListener("input", () => {
  const email = findEmailInput.value.trim();
  const inputKey = checkFindAuthKey.value.trim();

  if (inputKey.length !== 6) return;

  fetch("/email/checkFindAuthKey", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, authKey: inputKey }),
  })
    .then(resp => resp.text())
    .then(result => {
      if (result == "1") {
        findAuthKeyMessage.innerText = "인증되었습니다.";
        findAuthKeyMessage.classList.add("confirm");
        findAuthKeyMessage.classList.remove("error");
        checkObj.authKey = true;
      } else {
        findAuthKeyMessage.innerText = "인증번호가 일치하지 않습니다.";
        findAuthKeyMessage.classList.add("error");
        findAuthKeyMessage.classList.remove("confirm");
        checkObj.authKey = false;
      }
    })
    .catch(err => {
      console.error(err);
      findAuthKeyMessage.innerText = "인증 중 오류 발생.";
    });
});

// 인증 완료 후에만 아이디 찾기
findIdForm.addEventListener("submit", e => {
  if (!checkObj.email || !checkObj.authKey) {
    e.preventDefault();
    alert("이메일 인증을 완료해주세요.");
  } else {
    // form에 email hidden input으로 포함시키기
    const hidden = document.createElement("input");
    hidden.type = "hidden";
    hidden.name = "email";
    hidden.value = findEmailInput.value.trim();
    findIdForm.append(hidden);
  }
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
