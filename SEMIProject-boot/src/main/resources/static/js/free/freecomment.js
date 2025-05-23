document.addEventListener('DOMContentLoaded', () => {
  const editMode = document.body.getAttribute("data-edit-mode") === "true";

  if (editMode) {
    console.log("🛑 수정 모드 - 댓글 JS 작동 중지");

    // DOM에 남아 있을 수 있는 댓글 요소 강제 제거
    document.querySelector('.comment-section')?.remove();
    document.querySelector('.comment-form')?.remove();
    
    return; // 댓글 관련 로직 실행 중단
  }

  console.log("✅ 일반 모드 - 댓글 JS 작동 시작");

  // 댓글 기능 요소들
  const boardNo = document.querySelector('.free-title')?.dataset.boardNo;
  const commentListArea = document.querySelector('.comment-section');
  const commentForm = document.querySelector('.comment-form form');
  const commentTextarea = commentForm?.querySelector('textarea');

  if (!boardNo || !commentListArea || !commentForm || !commentTextarea) {
    console.warn("❗댓글 관련 요소 누락. 초기화 중단");
    return;
  }

  // 댓글 목록 조회
  function loadFreeComments() {
    fetch(`/freeComment?boardNo=${boardNo}`)
      .then(res => res.json())
      .then(list => {
        commentListArea.innerHTML = '';
        list.forEach(comment => {
          const commentBox = document.createElement('div');
          commentBox.className = 'comment-box';
          commentBox.innerHTML = `
            <div class="comment-header">
              <div class="comment-writer">
                <img src="${comment.profileImg || '/images/default-profile.png'}" class="comment-img">
                <span>${comment.memberNickname}</span>
                <span>${comment.commentWriteDate}</span>
              </div>
              <div class="comment-actions" data-comment-no="${comment.commentNo}">
                <a href="#" class="update">수정</a>
                <a href="#" class="delete">삭제</a>
              </div>
            </div>
            <div class="comment-content">${comment.commentContent}</div>
          `;
          commentListArea.appendChild(commentBox);
        });
      });
  }

  loadFreeComments();

  // 댓글 등록
  commentForm.addEventListener('submit', e => {
    e.preventDefault();
    const content = commentTextarea.value.trim();

    if (content.length === 0) {
      alert('댓글을 입력하세요.');
      return;
    }

    fetch('/freeComment/insert', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ boardNo, commentContent: content, parentCommentNo: 0 })
    })
      .then(res => res.text())
      .then(result => {
        if (result > 0) {
          commentTextarea.value = '';
          loadFreeComments();
        } else {
          alert('댓글 등록 실패');
        }
      });
  });

  // 댓글 수정/삭제
  commentListArea.addEventListener('click', e => {
    const target = e.target;

    if (target.matches('.delete')) {
      e.preventDefault();
      const commentNo = target.closest('.comment-actions').dataset.commentNo;

      if (confirm('댓글을 삭제하시겠습니까?')) {
        fetch('/freeComment', {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(parseInt(commentNo))
        })
          .then(res => res.text())
          .then(result => {
            if (result > 0) {
              loadFreeComments();
            } else {
              alert('댓글 삭제 실패');
            }
          });
      }

    } else if (target.matches('.update')) {
      e.preventDefault();

      const actions = target.closest('.comment-actions');
      const commentNo = actions.dataset.commentNo;
      const contentDiv = actions.parentElement.nextElementSibling;
      const originalContent = contentDiv.textContent;

      const textarea = document.createElement('textarea');
      textarea.className = 'edit-area';
      textarea.value = originalContent;
      contentDiv.replaceWith(textarea);

      const saveBtn = document.createElement('a');
      saveBtn.href = '#';
      saveBtn.textContent = '저장';
      saveBtn.className = 'save';

      const cancelBtn = document.createElement('a');
      cancelBtn.href = '#';
      cancelBtn.textContent = '취소';
      cancelBtn.className = 'cancel';

      actions.innerHTML = '';
      actions.append(saveBtn, cancelBtn);

      cancelBtn.addEventListener('click', e => {
        e.preventDefault();
        textarea.replaceWith(contentDiv);
        actions.innerHTML = `
          <a href="#" class="update">수정</a>
          <a href="#" class="delete">삭제</a>
        `;
      });

      saveBtn.addEventListener('click', e => {
        e.preventDefault();
        const newContent = textarea.value.trim();

        if (newContent.length === 0) {
          alert('내용을 입력하세요.');
          return;
        }

        fetch('/freeComment', {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ commentNo: parseInt(commentNo), commentContent: newContent })
        })
          .then(res => res.text())
          .then(result => {
            if (result > 0) {
              loadFreeComments();
            } else {
              alert('댓글 수정 실패');
            }
          });
      });
    }
  });
});

function goEditMode() {
  const boardNo = document.querySelector('.free-title')?.dataset.boardNo;
  location.href = `/free/view/${boardNo}?edit=true`;
}