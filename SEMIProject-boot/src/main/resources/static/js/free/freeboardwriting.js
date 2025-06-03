document.getElementById("freeImage").addEventListener("change", function (e) {
  const file = e.target.files[0];

  if (file) {
    const reader = new FileReader();

    reader.onload = function (event) {
      const preview = document.getElementById("preview");
      preview.src = event.target.result;
      preview.style.display = "block";
    };

    reader.readAsDataURL(file);
  }
});

document
  .querySelector(".free-submit-btn")
  .addEventListener("click", function (e) {
    const title = document
      .querySelector("input[name='boardTitle']")
      .value.trim();
    const content = document
      .querySelector("textarea[name='boardContent']")
      .value.trim();

    if (title === "") {
      alert("제목을 입력해주세요.");
      document.querySelector("input[name='boardTitle']").focus();
      e.preventDefault(); // 제출 막기
      return;
    }

    if (content === "") {
      alert("내용을 입력해주세요.");
      document.querySelector("textarea[name='boardContent']").focus();
      e.preventDefault(); // 제출 막기
      return;
    }
  });

document
  .querySelector(".free-cancel-btn")
  .addEventListener("click", function (e) {
    e.preventDefault(); // 폼 제출 막기

    const cp = document.querySelector("input[name='cp']")?.value || 1;
    location.href = `/free/list`;
  });
