const contentHTML = $("#summernote").summernote("code");
if (contentHTML.length > 2000) {
  alert("내용은 2000자 이내로 작성해주세요.");
  e.preventDefault();
}
