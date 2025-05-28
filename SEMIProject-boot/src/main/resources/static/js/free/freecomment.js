document.addEventListener("DOMContentLoaded", () => {
  const editMode = document.body.getAttribute("data-edit-mode") === "true";

  if (editMode) {
    console.log("🛑 수정 모드 - 댓글 JS 작동 중지");
    document.querySelector(".comment-section")?.remove();
    document.querySelector(".comment-form")?.remove();
    return;
  }

  console.log("✅ 일반 모드 - 댓글 JS 작동 시작");

  const boardNo = document.querySelector(".free-title")?.dataset.boardNo;
  const commentListArea = document.querySelector(".comment-section");
  const commentForm = document.querySelector(".comment-form form");
  const commentTextarea = commentForm?.querySelector("textarea");

  if (!boardNo || !commentListArea || !commentForm || !commentTextarea) {
    console.warn("❗댓글 관련 요소 누락. 초기화 중단");
    return;
  }

  // 댓글 목록 조회
  function loadFreeComments() {
    fetch(`/freeComment?boardNo=${boardNo}`)
      .then((res) => res.json())
      .then((list) => {
        commentListArea.innerHTML = "";
        list.forEach((comment) => {
          const commentBox = document.createElement("div");
          commentBox.className = "comment-box";
          if (comment.level > 1) {
            commentBox.classList.add("child-comment"); // 대댓글 클래스 추가
          }

          commentBox.innerHTML = `
  <div class="comment-header">
    <div class="comment-writer">
      <img src="${comment.memberImg || "/images/user.png"}" class="comment-img">
      <span>${comment.memberNickname}</span>
      <span>${comment.commentWriteDate}</span>
    </div>
    <div class="comment-actions" data-comment-no="${comment.commentNo}">
      <a href="#" class="update">수정</a>
      <a href="#" class="delete">삭제</a>
    </div>
  </div>
  <div class="comment-content">${comment.commentContent}</div>
  <button class="reply-btn" data-parent-no="${comment.commentNo}">답글</button>

  <!-- ✅ 대댓글 입력 영역 추가 -->
  <div class="reply-form" style="display:none;">
    <textarea class="reply-content" placeholder="답글을 입력하세요."></textarea>
    <button type="button" class="reply-submit-btn"
      data-parent-no="${comment.commentNo}" data-board-no="${boardNo}">
      등록
    </button>
  </div>
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

    // 삭제 버튼 처리
    if (target.matches(".delete")) {
      e.preventDefault();
      const commentNo = target.closest(".comment-actions").dataset.commentNo;

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

      const actions = target.closest(".comment-actions");
      const commentNo = actions.dataset.commentNo;
      const contentDiv = actions.parentElement.nextElementSibling;
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
        textarea.replaceWith(contentDiv);
        actions.innerHTML = `
        <a href="#" class="update">수정</a>
        <a href="#" class="delete">삭제</a>
      `;
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
    }
  });
});

function goEditMode() {
  const boardNo = document.querySelector(".free-title")?.dataset.boardNo;
  location.href = `/free/view/${boardNo}?edit=true`;
}
