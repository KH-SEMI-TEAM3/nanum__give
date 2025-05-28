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
      const tbody = document.querySelector(".list-table tbody");

      if (data.length === 0) {
        tbody.innerHTML =
          '<tr><td colspan="3">게시글이 존재하지 않습니다.</td></tr>';
      } else {
        tbody.innerHTML = data
          .map(
            (board) => `
          <tr onclick="location.href='/share/detail/${
            board.boardNo
          }'" style="cursor:pointer;">
            <td rowspan="3">${board.boardNo}</td>
            <td rowspan="3">
              <img src="${board.thumbnail}" height="50px">
            </td>
            <td>${board.shareStatus === "Y" ? "나눔 완료" : "나눔 중"}</td>
          </tr>
          <tr onclick="location.href='/share/detail/${
            board.boardNo
          }'" style="cursor:pointer;">
            <td>
              <span>${board.boardTitle}</span>
            </td>
          </tr>
          <tr onclick="location.href='/share/detail/${
            board.boardNo
          }'" style="cursor:pointer;">
            <td>
              작성자: <span>${board.memberNickname}</span> |
              작성일: <span>${board.boardWriteDate}</span> |
              조회수: <span>${board.readCount}</span> |
              찜: <span>${board.jjimCount}</span>
            </td>
          </tr>
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
  const categoryCodeInput = document.getElementById("categoryCode");

  if (categoryCodeInput && categoryCodeInput.value) {
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
});

// 서브 카테고리 변경 이벤트
document.getElementById("sub-category").addEventListener("change", function () {
  const mainCategory = document.getElementById("main-category").value;
  const subCategory = this.value;

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

// 폼 제출 시 카테고리 선택 검증
document.querySelector("form").addEventListener("submit", function (e) {
  const mainCategory = document.getElementById("main-category").value;
  const subCategory = document.getElementById("sub-category").value;
  const categoryCode = document.getElementById("categoryCode");

  console.log("Form Submit - Categories:", { mainCategory, subCategory });
  console.log(
    "Form Submit - Category Code:",
    categoryCode ? categoryCode.value : "not set"
  );

  if (!categoryCode || !categoryCode.value) {
    e.preventDefault();
    alert("카테고리를 선택해주세요.");
    return false;
  }

  // 폼 데이터 확인
  const formData = new FormData(this);
  console.log("Form data before submit:", {
    categoryCode: formData.get("shareBoardCategoryDetailCode"),
    mainCategory: formData.get("mainCategory"),
    subCategory: formData.get("subCategory"),
  });
});
