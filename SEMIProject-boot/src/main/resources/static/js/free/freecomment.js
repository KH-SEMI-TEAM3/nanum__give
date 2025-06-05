document.addEventListener("DOMContentLoaded", () => {
  const editMode =
    document.body.getAttribute("data-edit-mode")?.toLowerCase() === "true";

  console.log(" editMode:", editMode);

  const isAdmin =
    document.body.getAttribute("data-admin")?.toLowerCase() === "true";
  console.log(" isAdmin:", isAdmin);

  const memberNoAttr = document.body.getAttribute("data-member-no");
  const loginMemberNo =
    memberNoAttr === "" || memberNoAttr === null
      ? null
      : parseInt(memberNoAttr);

  if (editMode) {
    console.log("수정 모드 - 댓글 JS 작동 중지");
    document.querySelector(".comment-section")?.remove();
    document.querySelector(".comment-form")?.remove();
    return;
  }

  console.log("✅ 일반 모드 - 댓글 JS 작동 시작");

  const boardNo = document.querySelector(".free-title")?.dataset.boardNo;
  const commentListArea = document.querySelector(".comment-section");
  const commentForm = document.querySelector(".comment-form form");
  const commentTextarea = commentForm?.querySelector("textarea");

  if (!boardNo || !commentListArea) return;

  if (commentForm && commentTextarea) {
    commentForm.addEventListener("submit", (e) => {
      // 등록 로직
    });
  }

  function loadFreeComments() {
    fetch(`/freeComment?boardNo=${boardNo}`)
      .then((res) => res.json())
      .then((list) => {
        commentListArea.innerHTML = "";

        list.forEach((comment) => {
          const commentBox = document.createElement("div");
          commentBox.className = "comment-box";
          if (comment.level > 1) commentBox.classList.add("child-comment");

          commentBox.setAttribute("data-member-no", comment.memberNo);

          const isDeletedMember = comment.memberDelFl === "Y";
          const isDeletedComment = comment.commentDelFl?.toUpperCase() === "Y";

          let actionButtons = "";
          const commentWriterNo = parseInt(comment.memberNo);

          if (!isDeletedComment) {
            if (loginMemberNo !== null && loginMemberNo === commentWriterNo) {
              // 본인 댓글
              actionButtons = `
      <div class="comment-actions1" data-comment-no="${comment.commentNo}">
      <a href="#" class="reply-btn" data-parent-no="${comment.commentNo}">답글</a>
        <a href="#" class="update">수정</a>
        <a href="#" class="delete">삭제</a>
        
      </div>`;
            } else if (isAdmin) {
              // 관리자 & 타인 댓글
              actionButtons = `
      <div class="comment-actions" data-comment-no="${comment.commentNo}">
        <div class="admin-actions">
          <a href="#" class="admin-delete">관리자 댓글 삭제</a>
          <a href="#" class="admin-kick">회원 삭제</a>
        </div>
        <div class="user-actions">
          <a href="#" class="reply-btn" data-parent-no="${comment.commentNo}">답글</a>
        </div>
      </div>`;
            } else {
              // 일반 사용자 (본인 댓글 아님)
              actionButtons = `
      <div class="comment-actions1" data-comment-no="${comment.commentNo}">
        <a href="#" class="reply-btn" data-parent-no="${comment.commentNo}">답글</a>
      </div>`;
            }
          }

          commentBox.innerHTML = `
  <div class="comment-header">
    <div class="comment-writer">
      <img src="${comment.memberImg || "/images/user.png"}" class="comment-img">
      <span>${isDeletedMember ? "탈퇴한 회원" : comment.memberNickname}</span>
    </div>
    ${actionButtons}
    ${
      isDeletedComment
        ? ""
        : `<span class="comment-date">${comment.commentWriteDate}</span>`
    }
  </div>

  <div class="comment-content">
    ${
      isDeletedComment
        ? "삭제된 댓글입니다."
        : isDeletedMember
        ? "<em class='deleted-member-comment'>삭제된 회원의 댓글입니다.</em>"
        : comment.commentContent
    }
  </div>

  ${
    isDeletedComment || isDeletedMember
      ? ""
      : `<div class="reply-form" style="display:none;">
          <textarea class="reply-content" placeholder="답글을 입력하세요."></textarea>
          <button type="button" class="reply-submit-btn"
            data-parent-no="${comment.commentNo}" data-board-no="${boardNo}">
            등록
          </button>
          <button type="button" class="reply-cancel-btn">취소</button>
        </div>`
  }
`;

          commentListArea.appendChild(commentBox);
        });
      });
  }

  loadFreeComments();

  // 댓글 등록
  commentForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const content = commentTextarea.value.trim();

    if (content.length === 0) {
      alert("댓글을 입력하세요.");
      return;
    }

    fetch("/freeComment/insert", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        boardNo,
        commentContent: content,
        parentCommentNo: 0,
      }),
    })
      .then((res) => res.text())
      .then((result) => {
        if (result > 0) {
          commentTextarea.value = "";
          loadFreeComments();
        } else {
          alert("댓글 등록 실패");
        }
      });
  });

  commentListArea.addEventListener("click", (e) => {
    const target = e.target;

    if (target.matches(".admin-kick")) {
      e.preventDefault();

      const commentBox = target.closest(".comment-box");
      const memberNo = commentBox?.dataset.memberNo;

      if (!memberNo) {
        alert("회원 번호를 찾을 수 없습니다.");
        return;
      }

      if (confirm("해당 회원을 탈퇴 처리하시겠습니까?")) {
        const boardNo = document.querySelector(".free-title")?.dataset.boardNo;
        const cp = new URLSearchParams(location.search).get("cp") || 1;

        location.href = `/admin/free/memberDelete?memberNo=${memberNo}&boardNo=${boardNo}&cp=${cp}`;
      }

      return;
    }

    //관리자 댓글 삭제 처리
    if (target.matches(".admin-delete")) {
      e.preventDefault();
      const commentNo = target.closest(".comment-actions, .comment-actions1")
        .dataset.commentNo;

      if (confirm("댓글을 삭제하시겠습니까?")) {
        fetch("/freeComment", {
          method: "DELETE",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(parseInt(commentNo)),
        })
          .then((res) => res.text())
          .then((result) => {
            if (result > 0) {
              alert("삭제가 완료되었습니다.");
              loadFreeComments();
            } else {
              alert("댓글 삭제 실패");
            }
          });
      }

      return; // 아래 조건들 실행 방지
    }
    // 삭제 버튼 처리
    if (target.matches(".delete")) {
      e.preventDefault();
      const commentNo = target.closest(".comment-actions , .comment-actions1")
        .dataset.commentNo;

      if (confirm("댓글을 삭제하시겠습니까?")) {
        fetch("/freeComment", {
          method: "DELETE",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(parseInt(commentNo)),
        })
          .then((res) => res.text())
          .then((result) => {
            if (result > 0) {
              loadFreeComments();
            } else {
              alert("댓글 삭제 실패");
            }
          });
      }

      // 수정 버튼 처리
    } else if (target.matches(".update")) {
      e.preventDefault();

      const actions = target.closest(".comment-actions , .comment-actions1");
      const commentNo = actions.dataset.commentNo;
      const contentDiv = actions.parentElement.nextElementSibling;

      const commentBox = target.closest(".comment-box");
      const replyForm = commentBox?.querySelector(".reply-form");
      if (replyForm) replyForm.style.display = "none";

      const replyBtn = commentBox?.querySelector(".reply-btn");
      if (replyBtn) replyBtn.style.display = "none";

      const originalContent = contentDiv.textContent;

      const textarea = document.createElement("textarea");
      textarea.className = "edit-area";
      textarea.value = originalContent;
      contentDiv.replaceWith(textarea);

      const saveBtn = document.createElement("a");
      saveBtn.href = "#";
      saveBtn.textContent = "저장";
      saveBtn.className = "save";

      const cancelBtn = document.createElement("a");
      cancelBtn.href = "#";
      cancelBtn.textContent = "취소";
      cancelBtn.className = "cancel";

      actions.innerHTML = "";
      actions.append(saveBtn, cancelBtn);

      cancelBtn.addEventListener("click", (e) => {
        e.preventDefault();
        loadFreeComments();
      });

      saveBtn.addEventListener("click", (e) => {
        e.preventDefault();
        const newContent = textarea.value.trim();

        if (newContent.length === 0) {
          alert("내용을 입력하세요.");
          return;
        }

        fetch("/freeComment", {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            commentNo: parseInt(commentNo),
            commentContent: newContent,
          }),
        })
          .then((res) => res.text())
          .then((result) => {
            if (result > 0) {
              loadFreeComments();
            } else {
              alert("댓글 수정 실패");
            }
          });
      });

      // ✅ 대댓글 (답글) 버튼 처리
    } else if (target.matches(".reply-btn")) {
      e.preventDefault();

      const commentBox = target.closest(".comment-box");

      if (!commentBox) {
        console.warn("❗ comment-box를 찾을 수 없습니다.");
        return;
      }

      const replyForm = commentBox.querySelector(".reply-form");

      if (!replyForm) {
        console.warn("❗ reply-form을 찾을 수 없습니다.");
        return;
      }

      replyForm.style.display = "block";
    }

    // ✅ 대댓글 등록 버튼 처리
    else if (target.matches(".reply-submit-btn")) {
      e.preventDefault();

      const parentCommentNo = target.dataset.parentNo;
      const boardNo = target.dataset.boardNo;
      const replyContent = target
        .closest(".reply-form")
        .querySelector(".reply-content")
        .value.trim();

      if (replyContent.length === 0) {
        alert("답글 내용을 입력하세요.");
        return;
      }

      fetch("/freeComment/insert", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          boardNo: boardNo,
          commentContent: replyContent,
          parentCommentNo: parentCommentNo,
        }),
      })
        .then((res) => res.text())
        .then((result) => {
          if (result > 0) {
            loadFreeComments();
          } else {
            alert("답글 등록 실패");
          }
        });
    } else if (target.matches(".reply-cancel-btn")) {
      e.preventDefault();

      const replyForm = target.closest(".reply-form");
      if (replyForm) replyForm.style.display = "none";

      const commentBox = target.closest(".comment-box");
      const replyBtn = commentBox?.querySelector(".reply-btn");
      if (replyBtn) replyBtn.style.display = "inline-block";
    }
  });
});

function goEditMode() {
  const boardNo = document.querySelector(".free-title")?.dataset.boardNo;
  location.href = `/free/view/${boardNo}?edit=true`;
}
