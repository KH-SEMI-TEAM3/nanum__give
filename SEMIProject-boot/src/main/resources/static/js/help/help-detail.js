// ----------------------- 게시글 삭제 버튼 GET방식--------------------

const deleteBtn = document.querySelector("#deleteBtn");

if (deleteBtn !== null) {
  deleteBtn.addEventListener("click", () => {
    if (!confirm("삭제하시겠습니까?")) {
      alert("취소됨!");
      return;
    }

    const url = location.pathname + "/delete"; //
    const queryString = location.search; // ?cp=1

    location.href = url + queryString; // 최종 URL: /help/4/394/delete?cp=1
  });
}

// ----------------------- 게시글 수정버튼--------------------

const updateBtn = document.querySelector("#updateBtn");

if (updateBtn != null) {
  // <th:block th:if="${board.memberNo == session.loginMember?.memberNo}">가 아니면 랜더링 자체가 안 되기 때문

  updateBtn.addEventListener("click", () => {
    ///board/1/2004?cp=1 가 현재인데
    // get방식으로 /editBoard/1/2004/update?cp=1 과 같이 4계층과 cp까지

    // /1/2004만 똑같고 앞만 board를 editBoard로

    const url = location.pathname + "/update"; //
    const queryString = location.search; // ?cp=1

    location.href = url + queryString; // 최종 URL: /help/4/394/update?cp=1
    //   /editBoard/1/2004/update?cp=1 과 같이 변함
  });
}

const completionText = document.querySelector("completionText");

if (completionText != null) {
  // <th:block th:if="${board.memberNo == session.loginMember?.memberNo}">가 아니면 랜더링 자체가 안 되기 때문

  completionText.addEventListener("click", () => {
    ///board/1/2004?cp=1 가 현재인데
    // get방식으로 /editBoard/1/2004/update?cp=1 과 같이 4계층과 cp까지

    // /1/2004만 똑같고 앞만 board를 editBoard로

    location.href =
      location.pathname.replace("board", "editBoard") +
      "/update" +
      location.search;

    //   /editBoard/1/2004/update?cp=1 과 같이 변함
  });
}

const qabutton = document.querySelector(".completionToggleBtn");
if (qabutton) {
  const completionText = document.getElementById("completionText");

  qabutton.addEventListener("click", () => {
    if (!confirm("문의 완료 여부를 정말로 수정하시겠습니까?")) {
      alert("취소됨!");
      return;
    }
    const cp = 1;
    const newStatus = qaStatus == "Y" ? "N" : "Y";

    const data = {
      boardNo: boardNo,
      qaStatus: newStatus,
    };

    fetch("/help/updateCompletion", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    })
      .then((resp) => resp.text())
      .then((result) => {
        if (result == 1) {
          qaStatus = newStatus;

          // 화면에 표시되는 텍스트를 바꾸는 로직을 추가. 실제로 바뀌어도 안 바뀌는것처럼 보이던 이유
          completionText.textContent = qaStatus === "Y" ? "문의완료" : "문의중";
        } else {
          alert("오류");
        }
      });
  });
}
