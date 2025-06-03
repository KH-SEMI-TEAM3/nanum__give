// =======================
// comment.js
// =======================
// (전역 변수들은 HTML 상단 <script>에서 이미 선언되어 있다고 가정합니다.)
//   const boardNo = ...;
//   const loginMemberNo = ...;
//   const loginMemberAuthority = ...;
//   const userDefaultIamge = ...;

// 댓글 목록 조회 함수 (수정된 부분만 표시합니다)
const selectCommentList = () => {
  fetch("/helpcomment?boardNo=" + boardNo)
    .then((resp) => resp.json())
    .then((commentList) => {
      const ul = document.querySelector("#commentList");
      ul.innerHTML = "";

      for (let comment of commentList) {
        const li = document.createElement("li");
        li.classList.add("comment-row");

        // 자식 댓글이면 들여쓰기용 클래스 추가
        if (comment.parentCommentNo != 0) li.classList.add("child-comment");

        // -------------------
        // 1) 탈퇴된 회원이면, “탈퇴된 회원입니다” 출력만
        // -------------------
        if (comment.memberDelFl === "Y") {
          li.innerText = "삭제된 댓글입니다";
          ul.append(li);
          continue;
        }

        // -------------------
        // 2) 회원은 남아있지만 댓글만 삭제된 경우
        // -------------------
        if (comment.commentDelFl === "Y") {
          li.innerText = "삭제된 댓글입니다";
          ul.append(li);
          continue;
        }

        // -------------------
        // 3) 정상 댓글 렌더링
        // -------------------

        // (작성자 프로필 + 닉네임 + 작성시간)
        const writer = document.createElement("p");
        writer.classList.add("comment-writer");

        const img = document.createElement("img");
        img.src = comment.memberImg || userDefaultIamge;

        const name = document.createElement("span");
        name.innerText = comment.memberNickname;

        const date = document.createElement("span");
        date.classList.add("comment-date");
        date.innerText = comment.commentWriteDate;

        writer.append(img, name, date);

        // (댓글 본문)
        const content = document.createElement("p");
        content.classList.add("comment-content");
        content.innerText = comment.commentContent;

        // -------------------
        // 4) “관리자 전용 버튼 영역”
        //    - 로그인한 사용자가 관리자일 때( authority === 0 )
        //    - 그리고 “댓글 작성자와 로그인한 멤버No가 다를 때”에만 버튼을 보여줌
        // -------------------
        if (loginMemberAuthority === 0 && comment.memberNo != loginMemberNo) {
          const adminArea = document.createElement("div");
          adminArea.classList.add("admin-btn-area");

          // 4-1) “관리자 댓글 삭제”
          const adminDeleteBtn = document.createElement("button");
          adminDeleteBtn.innerText = "관리자 댓글 삭제";
          adminDeleteBtn.setAttribute(
            "onclick",
            `adminDeleteComment(${comment.commentNo})`
          );
          adminArea.append(adminDeleteBtn);

          // 4-2) “관리자 댓글 작성자 삭제”
          const adminDeleteCommentMember = document.createElement("button");
          adminDeleteCommentMember.innerText = "관리자 댓글 작성자 삭제";
          adminDeleteCommentMember.setAttribute(
            "onclick",
            `adminDeleteCommentMember(${comment.memberNo})`
          );
          adminArea.append(adminDeleteCommentMember);

          // li 맨 앞(댓글 전체 앞)에 관리자 버튼 영역을 붙여준다
          li.prepend(adminArea);
        }

        // -------------------
        // 5) “답글 / 수정 / 삭제 버튼 영역”
        // -------------------
        const btnArea = document.createElement("div");
        btnArea.classList.add("comment-btn-area");

        // (답글 버튼) : 원글 작성자이거나 관리자만 가능
        if (comment.parentCommentNo === 0) {
          if (loginMemberNo === boardWriterNo || loginMemberAuthority === 0) {
            const replyBtn = document.createElement("button");
            replyBtn.innerText = "답글";
            replyBtn.setAttribute(
              "onclick",
              `showInsertComment(${comment.commentNo}, this)`
            );
            btnArea.append(replyBtn);
          }
        }

        // (수정/삭제 버튼) : 본인이 쓴 댓글만
        if (loginMemberNo && loginMemberNo === comment.memberNo) {
          const updateBtn = document.createElement("button");
          updateBtn.innerText = "수정";
          updateBtn.setAttribute(
            "onclick",
            `showUpdateComment(${comment.commentNo}, this)`
          );

          const deleteBtn = document.createElement("button");
          deleteBtn.innerText = "삭제";
          deleteBtn.setAttribute(
            "onclick",
            `deleteComment(${comment.commentNo})`
          );

          btnArea.append(updateBtn, deleteBtn);
        }

        // li에 “작성자 정보 + 내용 + 버튼영역”을 붙인다
        li.append(writer, content, btnArea);
        ul.append(li);
      }
    })
    .catch((err) => {
      console.error("selectCommentList 에러:", err);
    });
};

// 최초 호출
selectCommentList();

// ==================================
// (아래는 기존에 작성된 다른 함수들: 댓글 등록, 답글/수정/삭제, 관리자 삭제 등)
// ==================================

// 댓글 등록
const commentContent = document.querySelector("#commentContent");
const addComment = document.querySelector("#addComment");
if (addComment != null) {
  addComment.addEventListener("click", () => {
    const content = commentContent.value;

    if (!loginMemberNo) {
      alert("로그인 후 이용해주세요");
      return;
    }

    if (content.trim().length === 0) {
      alert("내용을 작성해주세요");
      commentContent.focus();
      return;
    }

    const data = {
      commentContent: content,
      boardNo: boardNo,
      memberNo: loginMemberNo,
    };

    fetch("/helpcomment", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    })
      .then((resp) => resp.text())
      .then((result) => {
        if (result > 0) {
          alert("댓글이 등록되었습니다");
          commentContent.value = "";
          selectCommentList();
        } else {
          alert("댓글 등록 실패");
        }
      });
  });
}

// 답글 표시 / 등록 / 취소
const showInsertComment = (parentCommentNo, btn) => {
  if (!loginMemberNo) {
    alert("로그인 후 이용하세요.");
    return;
  }

  // 이미 열려있는 답글 작성란이 있을 경우 묻고 닫음
  const temp = document.getElementsByClassName("commentInsertContent");
  if (temp.length > 0) {
    if (
      confirm("다른 답글을 작성 중입니다. 현재 댓글에 답글을 작성하시겠습니까?")
    ) {
      temp[0].nextElementSibling.remove();
      temp[0].remove();
    } else {
      return;
    }
  }

  // textarea + 버튼 영역을 동적으로 생성
  const textarea = document.createElement("textarea");
  textarea.classList.add("commentInsertContent");
  btn.parentElement.after(textarea);

  const commentBtnArea = document.createElement("div");
  commentBtnArea.classList.add("comment-btn-area");
  const insertBtn = document.createElement("button");
  insertBtn.innerText = "등록";
  insertBtn.setAttribute(
    "onclick",
    `insertChildComment(${parentCommentNo}, this)`
  );

  const cancelBtn = document.createElement("button");
  cancelBtn.innerText = "취소";
  cancelBtn.setAttribute("onclick", "insertCancel(this)");

  commentBtnArea.append(insertBtn, cancelBtn);
  textarea.after(commentBtnArea);
};

const insertCancel = (cancelBtn) => {
  cancelBtn.parentElement.previousElementSibling.remove();
  cancelBtn.parentElement.remove();
};

const insertChildComment = (parentCommentNo, btn) => {
  const textarea = btn.parentElement.previousElementSibling;
  if (textarea.value.trim().length == 0) {
    alert("내용 작성 후 등록 버튼을 클릭해주세요!");
    textarea.focus();
    return;
  }

  const data = {
    commentContent: textarea.value,
    memberNo: loginMemberNo,
    boardNo: boardNo,
    parentCommentNo: parentCommentNo,
  };

  fetch("/helpcomment", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result > 0) {
        alert("답글이 등록 되었습니다!");
        selectCommentList();
      } else {
        alert("답글 등록 실패");
      }
    });
};

// 수정
let beforeCommentRow;

const showUpdateComment = (commentNo, btn) => {
  const row = btn.closest("li");
  beforeCommentRow = row.cloneNode(true);
  const prevContent = row.querySelector(".comment-content").innerText;

  row.innerHTML = "";

  const textarea = document.createElement("textarea");
  textarea.classList.add("update-textarea");
  textarea.value = prevContent;

  const btnArea = document.createElement("div");
  btnArea.classList.add("comment-btn-area");

  const updateBtn = document.createElement("button");
  updateBtn.innerText = "수정";
  updateBtn.setAttribute("onclick", `updateComment(${commentNo}, this)`);

  const cancelBtn = document.createElement("button");
  cancelBtn.innerText = "취소";
  cancelBtn.setAttribute("onclick", "updateCancel(this)");

  btnArea.append(updateBtn, cancelBtn);
  row.append(textarea, btnArea);
};

const updateCancel = (btn) => {
  const row = btn.closest("li");
  row.after(beforeCommentRow);
  row.remove();
};

const updateComment = (commentNo, btn) => {
  const content = btn.parentElement.previousElementSibling.value;
  if (content.trim().length === 0) {
    alert("내용을 입력해주세요.");
    return;
  }

  const data = {
    commentNo: commentNo,
    commentContent: content,
  };

  fetch("/helpcomment", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result > 0) {
        alert("수정 완료");
        selectCommentList();
      } else {
        alert("수정 실패");
      }
    });
};

// 삭제
const deleteComment = (commentNo) => {
  if (!confirm("정말 삭제하시겠습니까?")) return;

  fetch("/helpcomment", {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: commentNo,
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result > 0) {
        alert("삭제 완료");
        selectCommentList();
      } else {
        alert("삭제 실패");
      }
    });
};

// 관리자 댓글 삭제
const adminDeleteComment = (commentNo) => {
  if (!confirm("관리자 권한으로 이 댓글을 삭제하시겠습니까?")) return;

  fetch("/admin/3/commentDelete", {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: commentNo,
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result > 0) {
        alert("관리자 댓글 삭제 완료");
        selectCommentList();
      } else {
        alert("삭제 실패");
      }
    });
};

// 관리자 댓글 작성자(회원) 삭제
const adminDeleteCommentMember = (memberNo) => {
  if (!confirm("관리자 권한으로 이 댓글을 단 회원을 정말 탈퇴시겠습니까?"))
    return;

  fetch(`/admin/3/deleteCommentMemeber?memberNo=${memberNo}`, {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: memberNo,
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result > 0) {
        alert("댓글 단 회원 삭제 완료");
        selectCommentList();
      } else {
        alert("삭제 실패");
      }
    });
};
