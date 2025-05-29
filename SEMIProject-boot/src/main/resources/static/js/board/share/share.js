const subOptions = {
  물건: [
    "디지털 기기",
    "생활 가전",
    "의류/잡화",
    "스포츠",
    "취미/게임/음반",
    "도서",
    "기타",
  ],
  재능: [
    "교육 및 멘토링",
    "예술 및 문화",
    "디자인 및 콘텐츠",
    "IT 및 기술",
    "법률-세무-경영 자문",
    "의료 및 건강",
    "생활 서비스",
    "번역 및 통역",
  ],
};

// 카테고리 코드 매핑
const categoryCodeMap = {
  물건: {
    "디지털 기기": 0,
    "생활 가전": 1,
    "의류/잡화": 2,
    스포츠: 3,
    "취미/게임/음반": 4,
    도서: 5,
    기타: 6,
  },
  재능: {
    "교육 및 멘토링": 10,
    "예술 및 문화": 11,
    "디자인 및 콘텐츠": 12,
    "IT 및 기술": 13,
    "법률-세무-경영 자문": 14,
    "의료 및 건강": 15,
    "생활 서비스": 16,
    "번역 및 통역": 17,
  },
};

// 카테고리 필터링 함수
function filterByCategory() {
  const mainCategory = document.getElementById("main-category").value;
  const subCategory = document.getElementById("sub-category").value;
  const loadingIndicator = document.getElementById("loading-indicator");
  // 변경: list-table tbody 대신 list-wrapper 사용
  const listWrapper = document.querySelector(".list-wrapper");

  // 로딩 표시
  loadingIndicator.style.display = "block";

  // AJAX 요청 URL 구성
  let url = `/share/list/filter?mainCategory=${encodeURIComponent(
    mainCategory
  )}`;
  if (subCategory) {
    url += `&subCategory=${encodeURIComponent(subCategory)}`;
  }

  // AJAX 요청
  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      // 변경: listWrapper의 내용을 비웁니다.
      listWrapper.innerHTML = ''; 

      if (data.length === 0) {
        // 변경: 빈 목록 메시지를 list-wrapper에 직접 추가
        listWrapper.innerHTML = '<div class="empty-message">게시글이 존재하지 않습니다.</div>';
      } else {
        // 변경: 데이터 맵핑 시 board-item div 구조를 사용
        listWrapper.innerHTML = data
          .map(
            (board) => `
            <div class="board-item" onclick="location.href='/share/detail/${board.boardNo}'">
              <div class="board-thumbnail">
                <img src="${board.thumbnail}" alt="썸네일">
              </div>
              <div class="board-content">
                <div class="board-status-line">
                  <div class="board-status ${board.shareStatus === 'Y' ? 'completed' : ''}">
                    ${board.shareStatus === "Y" ? "나눔완료" : "나눔중"}
                  </div>
                </div>
                <div class="board-title-line">
                  <div class="board-title">${board.boardTitle}</div>
                </div>
                <div class="board-meta">
                  <span>카테고리: <span>${board.shareBoardCategoryCode}</span></span>
                  <span>세부카테고리: <span>${board.shareBoardCategoryDetailCode}</span></span>
                  <span>작성자: <span>${board.memberNickname}</span></span>
                  <span>작성일: <span>${board.boardWriteDate}</span></span>
                  <span>조회수: <span>${board.readCount}</span></span>
                  <span>찜: <span>${board.jjimCount}</span></span>
                </div>
              </div>
            </div>
          `
          )
          .join("");
      }
    })
    .catch((error) => {
      console.error("Error fetching filtered results:", error);
      alert("게시글을 불러오는 중 오류가 발생했습니다.");
    })
    .finally(() => {
      // 로딩 표시 제거
      loadingIndicator.style.display = "none";
    });
}

// 메인 카테고리 변경 이벤트
document
  .getElementById("main-category")
  .addEventListener("change", function () {
    const mainVal = this.value;
    const sub = document.getElementById("sub-category");
    sub.innerHTML = '<option value="">-- 세부 선택 --</option>'; // 초기화

    if (subOptions[mainVal]) {
      subOptions[mainVal].forEach((item) => {
        const opt = document.createElement("option");
        opt.value = item;
        opt.textContent = item;
        sub.appendChild(opt);
      });
    }

    // 메인 카테고리 필터링 실행
    filterByCategory();
  });

// 페이지 로드 시 기존 카테고리 값 설정
document.addEventListener("DOMContentLoaded", function () {
  const mainCategory = document.getElementById("main-category");
  const subCategory = document.getElementById("sub-category");
  // categoryCodeInput이 HTML에 없다면 이 부분은 필요 없을 수 있습니다.
  // 만약 categoryCodeInput이 없다면, 이 부분을 삭제하거나 HTML에 추가해야 합니다.
  const categoryCodeInput = document.getElementById("categoryCode"); 

  if (categoryCodeInput && categoryCodeInput.value) { // categoryCodeInput이 존재하고 값이 있을 때만 실행
    const categoryCode = parseInt(categoryCodeInput.value);
    const mainVal = categoryCode < 10 ? "물건" : "재능";

    // 메인 카테고리 설정
    mainCategory.value = mainVal;

    // 서브 카테고리 옵션 생성
    const sub = document.getElementById("sub-category");
    sub.innerHTML = '<option value="">-- 세부 선택 --</option>';

    if (subOptions[mainVal]) {
      subOptions[mainVal].forEach((item) => {
        const opt = document.createElement("option");
        opt.value = item;
        opt.textContent = item;
        sub.appendChild(opt);
      });

      // 서브 카테고리 값 설정
      for (const [key, value] of Object.entries(categoryCodeMap[mainVal])) {
        if (value === categoryCode) {
          subCategory.value = key;
          break;
        }
      }
    }
  }

  // 페이지 로드 시 초기 필터링 실행 (기존 HTML에서 Thymealf로 렌더링된 초기 boardList를 덮어씀)
  // 아니면, 서버에서 초기 데이터를 로드하게 하고 이 부분은 제거해도 됩니다.
  // 만약 이 함수가 초기 데이터를 로드하지 않는다면, 이 호출을 제거하세요.
  // filterByCategory(); // 필요하다면 이 줄의 주석을 해제합니다.
});

// 서브 카테고리 변경 이벤트
document.getElementById("sub-category").addEventListener("change", function () {
  const mainCategory = document.getElementById("main-category").value;
  const subCategory = this.value;

  // 이 로직은 `insert`나 `update` 폼에서 `categoryCode` input을 사용할 때 유효합니다.
  // 현재 목록 페이지에서는 이 값이 필터링에 직접 사용되지 않을 가능성이 높습니다.
  // `filterByCategory`에서 `mainCategory`와 `subCategory` 문자열을 바로 사용하고 있으므로,
  // 이 `categoryCodeInput` 로직은 필요 없을 수 있습니다.
  // HTML에 `<input type="hidden" id="categoryCode" name="categoryCode">`가 없다면 오류가 발생합니다.
  if (mainCategory && subCategory) {
    const categoryCode = categoryCodeMap[mainCategory][subCategory];
    const categoryCodeInput = document.getElementById("categoryCode");
    if (categoryCodeInput) {
      categoryCodeInput.value = categoryCode;
    }
  }

  // 서브 카테고리 필터링 실행
  filterByCategory();
});

// 폼 제출 시 카테고리 선택 검증 (이 부분은 나눔 글쓰기/수정 페이지의 폼에 해당될 가능성이 높습니다.)
// 현재 나눔 게시판 목록 페이지에는 전체 폼이 없으므로 이 이벤트 리스너는 동작하지 않을 수 있습니다.
// 만약 이 코드가 목록 페이지의 '검색 폼'을 대상으로 한다면 해당 폼에 맞는 셀렉터를 사용해야 합니다.
const shareListForm = document.querySelector(".search-area form"); // 목록 페이지의 검색 폼에 <form> 태그가 있다고 가정
if (shareListForm) {
    shareListForm.addEventListener("submit", function (e) {
        // 이 부분은 카테고리 필터링이 아닌, 검색 폼에 대한 로직일 것입니다.
        // 현재 폼에는 categoryCode가 직접적으로 없는 것으로 보입니다.
        // 이 로직이 정말 필요한지 검토하거나, 검색 폼의 기능에 맞게 수정해야 합니다.
        // 현재로서는 `e.preventDefault()`로 폼 제출을 막는 것이 아니라,
        // 검색 기능을 호출하는 것이 적절할 것입니다.

        // 예시: 검색어 유효성 검사 (기존 카테고리 검증 로직은 주석 처리 또는 삭제)
        const searchKeywordInput = this.querySelector('input[name="searchKeyword"]');
        if (searchKeywordInput && searchKeywordInput.value.trim() === '') {
            e.preventDefault();
            alert("검색어를 입력해주세요.");
            return false;
        }
        // 기본 폼 제출 동작 (서버로 검색 요청)
    });
}
