// 전역 변수는 HTML 상단에 선언되어 있어야 합니다.
// const boardNo = ...;
// const loginMemberNo = ...;
// const userDefaultIamge = ...;

// 댓글 목록 조회 함수
const selectCommentList = () => {
  fetch("/noticecomment?boardNo=" + boardNo)
    .then((resp) => resp.json())
    .then((commentList) => {
      const ul = document.querySelector("#commentList");
      ul.innerHTML = "";

      for (let comment of commentList) {
        const li = document.createElement("li");
        li.classList.add("comment-row");

        if (comment.parentCommentNo != 0) li.classList.add("child-comment");

        if (comment.commentDelFl === "Y") {
          const writer = document.createElement("p");
          writer.classList.add("comment-writer");

          const img = document.createElement("img");
          img.src = comment.memberImg || userDefaultIamge;

          const name = document.createElement("span");
          name.innerText = comment.memberNickname || "알 수 없음";

          writer.append(img, name);
          li.append(writer);

          const deletedMsg = document.createElement("p");
          deletedMsg.classList.add("deleted-comment");
          deletedMsg.innerText = "삭제된 댓글입니다.";
          li.append(deletedMsg);
        } else {
          const writer = document.createElement("p");
          writer.classList.add("comment-writer");

          const img = document.createElement("img");
          img.src = comment.memberImg || userDefaultIamge;

          const name = document.createElement("span");
          name.innerText = comment.memberNickname;

          const date = document.createElement("span");
          date.classList.add("comment-date");
          date.innerText = comment.commentWriteDate;

          let adminArea = null;

          if (loginMemberAuthority === 0 && comment.memberNo != loginMemberNo) {
            adminArea = document.createElement("div");
            adminArea.classList.add("admin-btn-area");

            const adminDeleteBtn = document.createElement("button");
            adminDeleteBtn.innerText = "관리자 댓글 삭제";
            adminDeleteBtn.classList.add("admin-comment-btn");
            adminDeleteBtn.setAttribute(
              "onclick",
              `adminDeleteComment(${comment.commentNo})`
            );
            adminArea.append(adminDeleteBtn);

            const adminDeleteCommentMember = document.createElement("button");
            adminDeleteCommentMember.classList.add("admin-member-btn");
            adminDeleteCommentMember.innerText = "관리자 댓글 작성자 삭제";
            adminDeleteCommentMember.setAttribute(
              "onclick",
              `adminDeleteCommentMember(${comment.memberNo})`
            );
            adminArea.append(adminDeleteCommentMember);
          }

          writer.append(img, name);
          if (adminArea) writer.append(adminArea);
          writer.append(date);

          const content = document.createElement("p");
          content.classList.add("comment-content");
          content.innerText = comment.commentContent;

          const btnArea = document.createElement("div");
          btnArea.classList.add("comment-btn-area");

          if (comment.parentCommentNo === 0) {
            const replyBtn = document.createElement("button");
            replyBtn.innerText = "답글";
            replyBtn.setAttribute(
              "onclick",
              `showInsertComment(${comment.commentNo}, this)`
            );
            btnArea.append(replyBtn);
          }

          if (loginMemberNo && loginMemberNo == comment.memberNo) {
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

          li.append(writer, content, btnArea);
        }

        ul.append(li);
      }
    });
};

selectCommentList();

const commentContent = document.querySelector("#commentContent");
const addComment = document.querySelector("#addComment");

addComment.addEventListener("click", () => {
  const content = commentContent.value;

  if (loginMemberNo == null) {
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

  fetch("/noticecomment", {
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

const showInsertComment = (parentCommentNo, btn) => {
  if (loginMemberNo == null) {
    alert("로그인 후 이용하세요.");
    return;
  }

  const temp = document.getElementsByClassName("commentInsertContent");
  if (temp.length > 0) {
    if (
      confirm(
        "다른 답글을 작성 중입니다. 현재 댓글에 답글을 작성 하시겠습니까?"
      )
    ) {
      temp[0].nextElementSibling.remove();
      temp[0].remove();
    } else {
      return;
    }
  }

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

  fetch("/noticecomment", {
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

  fetch("/noticecomment", {
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

const deleteComment = (commentNo) => {
  if (!confirm("정말 삭제하시겠습니까?")) return;

  fetch("/noticecomment", {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(commentNo),
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

const adminDeleteComment = (commentNo) => {
  if (!confirm("관리자 권한으로 이 댓글을 삭제하시겠습니까?")) return;

  fetch("/admin/3/commentDelete", {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(commentNo),
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

const adminDeleteCommentMember = (memberNo) => {
  if (!confirm("관리자 권한으로 이 댓글을 단 회원을 정말 탈퇴시겠습니까?"))
    return;

  fetch(`/admin/3/deleteCommentMemeber?memberNo=${memberNo}`, {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(memberNo),
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
