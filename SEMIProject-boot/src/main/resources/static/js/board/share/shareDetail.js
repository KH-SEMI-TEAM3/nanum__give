// 찜 기능 구현
document.addEventListener("DOMContentLoaded", function () {
  const jjimIcon = document.getElementById("jjimIcon");
  const jjimCount = document.getElementById("jjimCount");
  jjimIcon.style.fontVariationSettings = `'FILL' ${jjimCheck}, 'wght' 400, 'GRAD' 0, 'opsz' 24`;
  jjimIcon.style.color = jjimCheck == 1 ? "red" : "black";
  if (jjimIcon) {
    jjimIcon.addEventListener("click", function () {
      // 로그인 상태 체크
      if (loginMemberNo == null) {
        alert("로그인 후 이용해 주세요");
        return;
      }
	  if(isAuthor){
		return;
	  }

      // 요청 데이터 준비
      const obj = {
        memberNo: loginMemberNo,
        boardNo: boardNo,
        jjimCheck: jjimCheck,
      };

      // 찜 INSERT/DELETE 비동기 요청
      fetch("/share/jjim", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(obj),
      })
        .then((resp) => resp.text())
        .then((count) => {
          if (count == -1) {
            console.log("찜 처리 실패");
            return;
          }

          // jjimCheck 값 0 <-> 1 변환
          jjimCheck = jjimCheck == 0 ? 1 : 0;
          jjimIcon.style.fontVariationSettings = `'FILL' ${jjimCheck}, 'wght' 400, 'GRAD' 0, 'opsz' 24`;
          jjimIcon.style.color = jjimCheck == 1 ? "red" : "black";
          // 찜 개수 업데이트
          jjimCount.innerText = count;
        })
        .catch((error) => {
          console.error("찜 처리 중 오류 발생:", error);
        });
    });
  }
});

const updateBtn = document.getElementById("updateBtn");
if (updateBtn) {
  updateBtn.addEventListener("click", () => {
    location.href =
      location.pathname.replace("share", "shareEdit") +
      "/update" +
      location.search;
  });
}

const deleteBtn = document.getElementById("deleteBtn");
if (deleteBtn) {
  deleteBtn.addEventListener("click", () => {
    if (!confirm("삭제하시겠습니까?")) {
      alert("취소됨");
      return;
    }
    location.href =
      location.pathname.replace("share", "shareEdit") +
      "/delete" +
      location.search;
  });
}

const shareStatusBtn = document.getElementById("shareStatus");

if (shareStatusBtn) {
  shareStatusBtn.addEventListener("click", () => {
    if (!isAuthor) {
      alert("작성자만 나눔 상태를 변경할 수 있습니다.");
      return;
    }

    const currentStatus = shareStatusBtn.dataset.status;
    const newStatus = currentStatus === "Y" ? "N" : "Y";
    const confirmMessage =
      newStatus === "Y"
        ? "나눔 완료로 상태를 변경하시겠습니까?"
        : "나눔 중으로 상태를 변경하시겠습니까?";

    if (!confirm(confirmMessage)) return;

    fetch("/share/status", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        boardNo,
        shareStatus: newStatus,
        memberNo: loginMemberNo,
      }),
    })
      .then((res) => res.json())
      .then((result) => {
        if (result > 0) {
          shareStatusBtn.textContent =
            newStatus === "Y" ? "나눔완료" : "나눔중";
          shareStatusBtn.dataset.status = newStatus;

          shareStatusBtn.classList.toggle("btn-primary", newStatus !== "Y");
          shareStatusBtn.classList.toggle("btn-success", newStatus === "Y");
        } else {
          alert("상태 변경 실패");
        }
      })
      .catch((err) => {
        console.error(err);
        alert("상태 변경 중 오류 발생");
      });
  });
}

const adminBoardDeleteBtn = document.getElementById("adminBoardDeleteBtn");
if (adminBoardDeleteBtn) {
  adminBoardDeleteBtn.addEventListener("click", () => {
    if (!confirm("삭제하시겠습니까?")) {
      alert("취소됨");
      return;
    }
    location.href =
      `/admin/${boardNo}/boardDelete` +
      `?cp=${cp}&memberNo=${memberNo}&boardCode=1`;
  });
}

const adminMemberDeleteBtn = document.getElementById("adminMemberDeleteBtn");

if (adminMemberDeleteBtn) {
  adminMemberDeleteBtn.addEventListener("click", () => {
    if (!confirm("해당 글 작성 회원을 정말로 삭제하시겠습니까?")) {
      alert("취소됨");
      return;
    }
    location.href =
      `/admin/memberDelete` +
      `?memberNo=${memberNo}&boardCode=1&boardNo=${boardNo}&cp=${cp}`;
  });
}

const goToListBtn = document.querySelector("#goToListBtn");
goToListBtn.addEventListener("click", () => {
  const query = location.search; // ?cp=1 등
  location.href = "/share/list" + query;
});
