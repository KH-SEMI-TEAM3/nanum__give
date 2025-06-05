document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("boardWriteFrm");
  const titleInput = form.querySelector("input[name='boardTitle']");

  form.addEventListener("submit", function (e) {
    // summernote html을 textarea value로 강제 복사!
    $("#summernote").val($("#summernote").summernote("code"));

    const contentHtml = $("#summernote").val();
    const contentText = $("<div>").html(contentHtml).text().trim();

    // 1. 제목 미입력
    if (titleInput.value.trim().length === 0) {
      alert("제목을 입력하세요.");
      e.preventDefault();
      return;
    }

    // 2. 제목 글자수 제한
    if (titleInput.value.trim().length > 100) {
      alert("제목은 100자까지 입력 가능합니다.");
      e.preventDefault();
      return;
    }

    // 3. 내용 미입력
    if (contentText.length === 0 || contentHtml === "<p><br></p>") {
      alert("내용을 입력하세요.");
      e.preventDefault();
      return;
    }

    // 4. 내용 글자수 제한
    if (contentText.length > 2000) {
      alert("내용은 2000자까지 입력 가능합니다.");
      e.preventDefault();
      return;
    }

    // 5. 이미지 30MB 제한 검사
    const files = $("input[type='file']")[0]?.files;
    if (files) {
      for (const file of files) {
        if (file.size > 30 * 1024 * 1024) {
          alert("이미지 파일은 30MB를 초과할 수 없습니다.");
          e.preventDefault();
          return;
        }
      }
    }
  });
});
