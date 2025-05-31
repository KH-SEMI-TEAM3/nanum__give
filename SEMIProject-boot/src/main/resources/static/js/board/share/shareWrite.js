$(document).ready(function () {
  $("#summernote").summernote({
    height: 300,
    lang: "ko-KR",
    toolbar: [
      ["style", ["bold", "italic", "underline", "clear"]],
      ["font", ["strikethrough", "superscript", "subscript"]],
      ["fontname", ["fontname"]],
      ["fontsize", ["fontsize"]],
      ["color", ["color"]],
      ["para", ["ul", "ol", "paragraph"]],
      ["table", ["table"]],
      ["insert", ["picture"]],
      ["view", ["fullscreen", "codeview", "help"]],
    ],
    callbacks: {
      onImageUpload: function (files) {
        for (let i = 0; i < files.length; i++) {
          defaultUploadImage(files[i], this);
        }
      },
    },
  });
});

// 이미지 서버에 업로드
function defaultUploadImage(file, editor) {
  const formData = new FormData();
  formData.append("image", file);

  fetch("/shareEdit/UploadImage", {
    method: "POST",
    body: formData,
  })
    .then((res) => res.text())
    .then((imageUrl) => {
      $(editor).summernote("insertImage", imageUrl, function ($image) {
        $image.css("max-width", "500px");
      });
    })
    .catch((err) => {
      console.error("이미지 업로드 실패", err);
      alert("이미지 업로드 중 오류 발생");
    });
}

// content / image 검사
$("form").on("submit", function (e) {
  var content = $("#summernote").summernote("isEmpty")
    ? ""
    : $("#summernote").summernote("code").trim();

  if (!content || content === "<p><br></p>") {
    alert("내용을 입력해주세요.");
    e.preventDefault();
    return;
  }

  var imgCount = (content.match(/<img /g) || []).length;
  if (imgCount < 1) {
    alert("이미지는 최소 1개 이상 첨부해주세요.");
    e.preventDefault();
  }
});
