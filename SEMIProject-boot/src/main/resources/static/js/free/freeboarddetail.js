// HTML 로딩이 완료된 후 실행
document.addEventListener("DOMContentLoaded", () => {
  // 수정 버튼 요소 찾기
  const modifyBtn = document.querySelector(".btn.modify");

  // 내용 요소는 고정됨 (초기: .free-content, 수정 시: .edit-content로 교체됨)
  const contentEl =
    document.querySelector(".free-content") ||
    document.querySelector(".edit-content");

  // 필수 요소 확인
  if (!contentEl || !modifyBtn) {
    console.warn("필수 요소가 없습니다.");
    return;
  }

  // 최초 제목/내용 텍스트 백업 (수정 취소 시 복원용)
  let originalTitle = document.querySelector(".free-title")?.textContent || "";
  let originalContent = contentEl.textContent || contentEl.value;

  // 수정 버튼 클릭 시
  modifyBtn.addEventListener("click", () => {
    // 📌 매 클릭 시마다 제목 요소를 다시 찾고 boardNo, memberNo를 읽는다
    const titleEl =
      document.querySelector(".free-title") ||
      document.querySelector(".edit-title");

    if (!titleEl) {
      alert("제목 요소가 없습니다.");
      return;
    }

    const boardNo = titleEl.getAttribute("data-board-no");
    const memberNo = titleEl.getAttribute("data-member-no");

    console.log("[📦 저장 요청 시 boardNo]", boardNo);

    if (!boardNo || boardNo === "undefined") {
      alert("게시글 번호가 유효하지 않습니다.");
      return;
    }

    // ✅ 수정 모드 진입
    if (modifyBtn.textContent === "수정") {
      const thumbnailArea = document.getElementById("thumbnail-area");

      // 이미지 업로드 input 추가
      const fileInput = document.createElement("input");
      fileInput.type = "file";
      fileInput.name = "boardImage";
      fileInput.accept = "image/*";
      fileInput.className = "edit-image";
      fileInput.style = "margin-top: 10px;";
      thumbnailArea.appendChild(fileInput);

      // 제목 → input으로 교체
      const input = document.createElement("input");
      input.type = "text";
      input.value = originalTitle;
      input.className = "edit-title";
      input.setAttribute("data-board-no", boardNo);
      input.setAttribute("data-member-no", memberNo);
      input.style = "width: 100%; font-size: 1.5rem;";
      titleEl.replaceWith(input);

      // 본문 → textarea로 교체
      const textarea = document.createElement("textarea");
      textarea.className = "edit-content";
      textarea.value = originalContent;
      textarea.style = "width: 100%; height: 300px; font-size: 1rem;";
      contentEl.replaceWith(textarea);

      // 버튼 텍스트 전환
      modifyBtn.textContent = "저장";

      // 취소 버튼 생성
      const cancelBtn = document.createElement("button");
      cancelBtn.className = "btn cancel";
      cancelBtn.textContent = "취소";
      cancelBtn.style.marginLeft = "8px";
      modifyBtn.after(cancelBtn);

      // 취소 버튼 클릭 시 복원
      cancelBtn.addEventListener("click", () => {
        const restoredTitle = document.createElement("h1");
        restoredTitle.className = "free-title";
        restoredTitle.textContent = originalTitle;
        restoredTitle.setAttribute("data-board-no", boardNo);
        restoredTitle.setAttribute("data-member-no", memberNo);

        const restoredContent = document.createElement("div");
        restoredContent.className = "free-content";
        restoredContent.textContent = originalContent;

        document.querySelector(".edit-title").replaceWith(restoredTitle);
        document.querySelector(".edit-content").replaceWith(restoredContent);

        modifyBtn.textContent = "수정";
        cancelBtn.remove(); // 버튼 제거
      });
    }
    // ✅ 저장 동작
    else {
      const newTitle = document.querySelector(".edit-title")?.value.trim();
      const newContent = document.querySelector(".edit-content")?.value.trim();

      if (!newTitle || !newContent) {
        alert("제목과 내용을 모두 입력하세요.");
        return;
      }

      const formData = new FormData();
      formData.append("boardNo", boardNo);
      formData.append("boardTitle", newTitle);
      formData.append("boardContent", newContent);
      formData.append("memberNo", memberNo);

      // 이미지 첨부가 있을 경우에만 추가
      const imageInput = document.querySelector(".edit-image");
      if (imageInput && imageInput.files.length > 0) {
        formData.append("boardImage", imageInput.files[0]);
      }

      // 비동기 요청
      fetch("/free/update", {
        method: "POST",
        body: formData,
      })
        .then((res) => res.json())
        .then((result) => {
          if (result.success) {
            alert("수정되었습니다.");
            location.href = `/free/view/${boardNo}`;
          } else {
            alert("수정 실패: " + result.message);
          }
        })
        .catch((err) => {
          console.error("수정 요청 실패", err);
          alert("서버 오류가 발생했습니다. 다시 시도해주세요.");
        });
    }
  });
});

// 삭제 버튼 클릭 시 확인 → 삭제 이동
function confirmDelete(boardNo) {
  if (confirm("정말 삭제하시겠습니까?")) {
    location.href = `/free/delete/${boardNo}`;
  }
}
