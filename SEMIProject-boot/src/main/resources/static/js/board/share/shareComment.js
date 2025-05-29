// 관리자가 댓글단사람을 탈퇴시킨다거나 댓글 자체를 삭제하는 로직은 이 안에 있다. 버튼 불러오기부터 해서 클릭 시 삭제되는것도 다 있다.
// 나머지 관리자기능 둘(글삭 글작성자삭)은 shareDetail.html 내에서 함수를 정의해서 했다 (파일 내 통일성을 위함)

// 댓글 목록 조회 함수
const selectCommentList = () => {
  fetch("/shareComment?boardNo=" + boardNo)
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

          if (loginMemberAuthority !== 0) {
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

          // 관리자 버튼도 추가하려고 시도
          if (loginMemberAuthority === 0) {
            const adminDeleteBtn = document.createElement("button");
            adminDeleteBtn.innerText = "관리자 댓글 삭제";
            adminDeleteBtn.setAttribute(
              "onclick",
              `adminDeleteComment(${comment.commentNo})`
            );
            btnArea.append(adminDeleteBtn);
          }

          // 관리자 댓글 회원삭제도 추가하려고 시도
          if (loginMemberAuthority === 0) {
            const adminDeleteCommentMember = document.createElement("button");
            adminDeleteCommentMember.innerText = "관리자 댓글 작성자 삭제";
            adminDeleteCommentMember.setAttribute(
              "onclick",
              `adminDeleteCommentMember(${comment.memberNo})`
            );
            btnArea.append(adminDeleteCommentMember);
          }

          li.append(writer, content, btnArea);
        }

        ul.append(li);
      }
    });
};
selectCommentList();
// 댓글 등록
const commentContent = document.querySelector("#commentContent");
const addComment = document.querySelector("#addComment");

if (addComment) {
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

    fetch("/shareComment", {
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

const showInsertComment = (parentCommentNo, btn) => {
  // ** 답글 작성 textarea가 한 개만 열릴 수 있도록 만들기 **
  const temp = document.getElementsByClassName("commentInsertContent");

  if (temp.length > 0) {
    // 답글 작성 textara가 이미 화면에 존재하는 경우
    if (
      confirm(
        "다른 답글을 작성 중입니다. 현재 댓글에 답글을 작성 하시겠습니까?"
      )
    ) {
      temp[0].nextElementSibling.remove(); // 버튼 영역부터 삭제
      temp[0].remove(); // textara 삭제 (기준점은 마지막에 삭제해야 된다!)
    } else {
      return; // 함수를 종료시켜 답글이 생성되지 않게함.
    }
  }

  // 답글을 작성할 textarea 요소 생성
  const textarea = document.createElement("textarea");
  textarea.classList.add("commentInsertContent");

  // 답글 버튼의 부모의 뒤쪽에 textarea 추가
  // after(요소) : 뒤쪽에 추가
  btn.parentElement.after(textarea);

  // 답글 버튼 영역 + 등록/취소 버튼 생성 및 추가
  const commentBtnArea = document.createElement("div");

  commentBtnArea.classList.add("comment-btn-area");
  const insertBtn = document.createElement("button");

  insertBtn.innerText = "등록";
  // 매개변수에 +문자열+ 작성 시 Number타입으로 형변환하라
  // Number(parentCommentNo)  == +parentCommentNo+
  insertBtn.setAttribute(
    "onclick",
    "insertChildComment(" + parentCommentNo + ", this)"
  );

  const cancelBtn = document.createElement("button");
  cancelBtn.innerText = "취소";
  cancelBtn.setAttribute("onclick", "insertCancel(this)");

  // 답글 버튼 영역의 자식으로 등록/취소 버튼 추가
  commentBtnArea.append(insertBtn, cancelBtn);

  // 답글 버튼 영역을 화면에 추가된 textarea 뒤쪽에 추가
  textarea.after(commentBtnArea);
};
// ---------------------------------------

/** 답글 (자식 댓글) 작성 취소
 * @param {*} cancelBtn : 취소 버튼
 */
const insertCancel = (cancelBtn) => {
  // 취소 버튼 부모의 이전 요소(textarea) 삭제
  cancelBtn.parentElement.previousElementSibling.remove();
  // 취소 버튼이 존재하는 버튼영역 삭제
  cancelBtn.parentElement.remove();
};

// 답글 (자식 댓글) 등록
const insertChildComment = (parentCommentNo, btn) => {
  // 답글 내용이 작성된 textarea 요소
  const textarea = btn.parentElement.previousElementSibling;

  // 유효성 검사
  if (textarea.value.trim().length == 0) {
    alert("내용 작성 후 등록 버튼을 클릭해주세요!");
    textarea.focus();
    return;
  }

  // ajax를 이용해 전달할 데이터
  const data = {
    commentContent: textarea.value, // 작성한 글
    memberNo: loginMemberNo, // 누가 작성했는가?
    boardNo: boardNo, // 어느 게시글에 달린 댓글인가?
    parentCommentNo: parentCommentNo, // 어느 댓글에 달리는 답글인가?
  };

  fetch("/shareComment", {
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

  fetch("/shareComment", {
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

//  삭제
const deleteComment = (commentNo) => {
  if (!confirm("정말 삭제하시겠습니까?")) return;

  fetch("/shareComment", {
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

const adminDeleteComment = (commentNo) => {
  if (!confirm("관리자 권한으로 이 댓글을 삭제하시겠습니까?")) return;

  fetch("/admin/1/commentDelete", {
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

const adminDeleteCommentMember = (memberNo) => {
  if (!confirm("관리자 권한으로 이 댓글을 단 회원을 정말 탈퇴시겠습니까?"))
    return;

  fetch(`/admin/1/deleteCommentMemeber?memberNo=${memberNo}`, {
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
