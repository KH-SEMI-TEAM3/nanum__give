document.addEventListener("DOMContentLoaded", function () {
  const searchForm = document.getElementById("search");
  const searchInput = document.getElementById("searchQuery");
  const defaultPlaceholder = "검색어를 입력해주세요.";

  // placeholder 초기 설정
  searchInput.placeholder = defaultPlaceholder;

  // 포커스 시 placeholder 제거
  searchInput.addEventListener("focus", function () {
    this.placeholder = "";
  });

  // 포커스 해제 시 값이 비어있으면 placeholder 복원
  searchInput.addEventListener("blur", function () {
    if (this.value.trim() === "") {
      this.placeholder = defaultPlaceholder;
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
