document.addEventListener('DOMContentLoaded', () => {

  const modifyBtn = document.querySelector('.btn.modify');
  const titleEl = document.querySelector('.free-title');
  const contentEl = document.querySelector('.free-content');
  const boardNo = titleEl.dataset.boardNo;

  let originalTitle = '';
  let originalContent = '';

  modifyBtn.addEventListener('click', () => {

    if (modifyBtn.textContent === '수정') {
      originalTitle = titleEl.textContent;
      originalContent = contentEl.textContent;

      const input = document.createElement('input');
      input.type = 'text';
      input.value = originalTitle;
      input.className = 'edit-title';
      input.style = 'width: 100%; font-size: 1.5rem;';
      titleEl.replaceWith(input);

      const textarea = document.createElement('textarea');
      textarea.className = 'edit-content';
      textarea.value = originalContent;
      textarea.style = 'width: 100%; height: 300px; font-size: 1rem;';
      contentEl.replaceWith(textarea);

      modifyBtn.textContent = '저장';

      const cancelBtn = document.createElement('button');
      cancelBtn.className = 'btn cancel';
      cancelBtn.textContent = '취소';
      cancelBtn.style.marginLeft = '8px';
      modifyBtn.after(cancelBtn);

      cancelBtn.addEventListener('click', () => {
        const restoredTitle = document.createElement('h1');
        restoredTitle.className = 'free-title';
        restoredTitle.textContent = originalTitle;
        restoredTitle.setAttribute('data-board-no', boardNo);

        const restoredContent = document.createElement('div');
        restoredContent.className = 'free-content';
        restoredContent.textContent = originalContent;

        document.querySelector('.edit-title').replaceWith(restoredTitle);
        document.querySelector('.edit-content').replaceWith(restoredContent);

        modifyBtn.textContent = '수정';
        cancelBtn.remove();
      });

    } else {
      const newTitle = document.querySelector('.edit-title').value.trim();
      const newContent = document.querySelector('.edit-content').value.trim();

      if (newTitle === '' || newContent === '') {
        alert('제목과 내용을 모두 입력하세요.');
        return;
      }

      fetch('/freeBoard/update', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          boardNo: boardNo,
          boardTitle: newTitle,
          boardContent: newContent
        })
      })
        .then(res => res.json())
        .then(result => {
          if (result.success) {
            alert('수정되었습니다.');
            location.reload();
          } else {
            alert('수정 실패');
          }
        })
        .catch(err => {
          console.error(err);
          alert('오류 발생');
        });
    }
  });

});