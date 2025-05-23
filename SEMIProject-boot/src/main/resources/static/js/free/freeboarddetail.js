document.addEventListener('DOMContentLoaded', () => {

	const modifyBtn = document.querySelector('.btn.modify');
	const titleEl = document.querySelector('.free-title');
	const contentEl = document.querySelector('.free-content');
	const boardNo = titleEl.dataset.boardNo;

	const memberNo = titleEl.dataset.memberNo;

	let originalTitle = '';
	let originalContent = '';

	modifyBtn.addEventListener('click', () => {

		if (modifyBtn.textContent === '수정') {
			originalTitle = titleEl.textContent;
			originalContent = contentEl.textContent;

			const thumbnailArea = document.getElementById('thumbnail-area');

			const fileInput = document.createElement('input');
			fileInput.type = 'file';
			fileInput.name = 'boardImage';
			fileInput.accept = 'image/*';
			fileInput.className = 'edit-image';
			fileInput.style = 'margin-top: 10px;';

			thumbnailArea.appendChild(fileInput);

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

			// 🔥 여기에 FormData 방식으로 교체
			const formData = new FormData();
			formData.append("boardNo", boardNo);
			formData.append("boardTitle", newTitle);
			formData.append("boardContent", newContent);
			formData.append("memberNo", memberNo);

			const imageInput = document.querySelector(".edit-image");
			if (imageInput && imageInput.files.length > 0) {
				formData.append("boardImage", imageInput.files[0]);
			}

			fetch('/free/update', {
				method: 'POST',
				body: formData
			})
				.then(res => res.json())
				.then(result => {
					if (result.success) {
						alert("수정되었습니다.");
						location.reload();
					} else {
						alert("수정 실패: " + result.message);
					}
				})
				.catch(err => {
					console.error(err);
					alert("오류 발생");
				});
		}
	});

});

function confirmDelete(boardNo) {
	if (confirm("정말 삭제하시겠습니까?")) {
		location.href = `/free/delete/${boardNo}`;
	}
}