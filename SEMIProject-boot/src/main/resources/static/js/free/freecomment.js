document.addEventListener('DOMContentLoaded', () => {

  const boardNo = document.querySelector('.free-title').dataset.boardNo;
  const commentSection = document.querySelector('.comment-section');
  const commentForm = document.querySelector('.comment-form form');
  const commentTextarea = commentForm?.querySelector('textarea');

  // 댓글 목록 조회
  function loadFreeComments() {
    fetch(`/freeComment?boardNo=${boardNo}`)
      .then(res => res.json())
      .then(list => {
        const section = document.querySelectorAll('.comment-section');
        section.forEach(e => e.remove());

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

          commentForm?.before(commentBox);
        });
      });
  }

  loadFreeComments();

  // 댓글 등록
  commentForm?.addEventListener('submit', e => {
    e.preventDefault();
    const content = commentTextarea.value.trim();

    if (content.length === 0) {
      alert('댓글을 입력하세요.');
      return;
    }

    fetch('/freeComment/insert', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        boardNo,
        commentContent: content,
        parentCommentNo: 0
      })
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

  // 수정 / 삭제 위임 처리
  document.body.addEventListener('click', e => {
    if (e.target.matches('.comment-actions .delete')) {
      e.preventDefault();
      const commentNo = e.target.closest('.comment-actions').dataset.commentNo;

      if (confirm('댓글을 삭제하시겠습니까?')) {
        fetch('/freeComment', {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          body: commentNo
        })
          .then(res => res.text())
          .then(result => {
            if (result > 0) loadFreeComments();
            else alert('삭제 실패');
          });
      }

    } else if (e.target.matches('.comment-actions .update')) {
      e.preventDefault();

      const actions = e.target.closest('.comment-actions');
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
          body: JSON.stringify({
            commentNo,
            commentContent: newContent
          })
        })
          .then(res => res.text())
          .then(result => {
            if (result > 0) {
              loadFreeComments();
            } else {
              alert('수정 실패');
            }
          });
      });
    }
  });

});