// comment.js

// 전역 변수는 HTML 상단에 선언되어 있어야 합니다.
// const boardNo = ...;
// const loginMemberNo = ...;
// const userDefaultIamge = ...;

// 댓글 목록 조회 함수
const selectCommentList = () => {
  fetch("/help-comment?boardNo=" + boardNo)
    .then((resp) => resp.json())
    .then((commentList) => {
      const ul = document.querySelector("#commentList");
      ul.innerHTML = "";

      for (let comment of commentList) {
        const li = document.createElement("li");
        li.classList.add("comment-row");

        if (comment.parentCommentNo != 0) li.classList.add("child-comment");

        if (comment.commentDelFl === "Y") {
          li.innerText = "삭제된 댓글 입니다";
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

          writer.append(img, name, date);

          const content = document.createElement("p");
          content.classList.add("comment-content");
          content.innerText = comment.commentContent;

          const btnArea = document.createElement("div");
          btnArea.classList.add("comment-btn-area");

          const replyBtn = document.createElement("button");
          replyBtn.innerText = "답글";
          replyBtn.setAttribute(
            "onclick",
            `showInsertComment(${comment.commentNo}, this)`
          );
          btnArea.append(replyBtn);

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

// 댓글 등록
const commentContent = document.querySelector("#commentContent");
const addComment = document.querySelector("#addComment");

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
  console.log("전송될 댓글 데이터:", data);

  fetch("/help-comment", {
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

// 페이지 진입 시 댓글 목록 자동 조회
selectCommentList();
