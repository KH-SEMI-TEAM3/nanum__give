document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.getElementById("searchQuery");

  searchInput.addEventListener("focus", function () {
    this.setAttribute("data-placeholder", this.getAttribute("placeholder"));
    this.setAttribute("placeholder", "");
  });

  searchInput.addEventListener("blur", function () {
    if (this.value === "") {
      this.setAttribute("placeholder", this.getAttribute("data-placeholder"));
    }
  });
});

document.addEventListener("DOMContentLoaded", function () {
  const searchForm = document.getElementById("search");
  const searchInput = document.getElementById("searchQuery");

  // 포커스 시 placeholder 제거, blur 시 복원
  searchInput.addEventListener("focus", function () {
    this.setAttribute("data-placeholder", this.getAttribute("placeholder"));
    this.setAttribute("placeholder", "");
  });

  searchInput.addEventListener("blur", function () {
    if (this.value.trim() === "") {
      this.setAttribute("placeholder", this.getAttribute("data-placeholder"));
    }
  });

  // 검색 제출 시 빈 문자열 검사
  searchForm.addEventListener("submit", function (e) {
    if (searchInput.value.trim() === "") {
      alert("검색어를 입력해주세요.");
      e.preventDefault(); // 제출 중단
    }
  });
});