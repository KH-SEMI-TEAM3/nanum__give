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
    "법률·세무·경영 자문",
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
    "법률·세무·경영 자문": 14,
    "의료 및 건강": 15,
    "생활 서비스": 16,
    "번역 및 통역": 17,
  },
};

// 메인 카테고리 코드 매핑
const mainCategoryCodeMap = {
  물건: 0,
  재능: 1,
};

const populateSubCategory = (mainVal, selectedSub = "") => {
  const sub = document.getElementById("sub-category");
  sub.innerHTML = '<option value="">-- 세부 선택 --</option>';
  if (subOptions[mainVal]) {
    subOptions[mainVal].forEach((item) => {
      const opt = document.createElement("option");
      opt.value = item;
      opt.textContent = item;
      sub.appendChild(opt);
    });
    if (selectedSub) sub.value = selectedSub;
  }
};

document
  .getElementById("main-category")
  .addEventListener("change", function () {
    const mainVal = this.value;
    const sub = document.getElementById("sub-category");
    sub.innerHTML = '<option value="">-- 세부 선택 --</option>';
    if (subOptions[mainVal]) {
      subOptions[mainVal].forEach((item) => {
        const opt = document.createElement("option");
        opt.value = item;
        opt.textContent = item;
        sub.appendChild(opt);
      });
    }
    // 소분류 초기화
    const detailInput = document.getElementById("categoryCode");
    if (detailInput) detailInput.value = "";
  });

// 페이지 로드 시 설정
document.addEventListener("DOMContentLoaded", () => {
  const mainCategory = document.getElementById("main-category");
  const subCategory = document.getElementById("sub-category");

  // 게시글 수정
  const detailCodeInput = document.getElementById("categoryCode");
  if (detailCodeInput && detailCodeInput.value) {
    const detailCode = parseInt(detailCodeInput.value);
    const mainVal = detailCode < 10 ? "물건" : "재능";
    mainCategory.value = mainVal;

    subCategory.innerHTML = '<option value="">-- 세부 선택 --</option>';
    subOptions[mainVal].forEach((item) => {
      const opt = document.createElement("option");
      opt.value = item;
      opt.textContent = item;
      subCategory.appendChild(opt);
    });

    for (const [key, value] of Object.entries(categoryCodeMap[mainVal])) {
      if (value === detailCode) {
        subCategory.value = key;
        break;
      }
    }
  }

  // 목록 페이지
  if (selectedMain) {
    mainCategory.value = selectedMain;
    subCategory.innerHTML = '<option value="">-- 세부 선택 --</option>';
    if (subOptions[selectedMain]) {
      subOptions[selectedMain].forEach((sub) => {
        const opt = document.createElement("option");
        opt.value = sub;
        opt.textContent = sub;
        subCategory.appendChild(opt);
      });
    }

    if (selectedSub) {
      subCategory.value = selectedSub;
    }
  }
});

document.getElementById("sub-category").addEventListener("change", function () {
  const main = document.getElementById("main-category").value;
  const sub = this.value;
  const detailInput = document.getElementById("categoryCode");
  if (main && sub && detailInput) {
    detailInput.value = categoryCodeMap[main][sub];
  }
});

// search type/input sealjung yuji
const searchTypeSelect = document.querySelector("select[name='searchType']");
const searchKeywordInput = document.querySelector(
  "input[name='searchKeyword']"
);

if (searchTypeSelect && searchType) {
  searchTypeSelect.value = searchType;
}
if (searchKeywordInput && searchKeyword) {
  searchKeywordInput.value = searchKeyword;
}

console.log("searchType:", searchType);
console.log("searchKeyword:", searchKeyword);

function submitSubCategory() {
  const main = document.getElementById("main-category").value;
  const sub = document.getElementById("sub-category").value;

  const url = new URL(window.location.href);
  url.searchParams.set("mainCategory", main);
  url.searchParams.set("subCategory", sub);
  url.searchParams.set("cp", 1); // 페이지 초기화

  window.location.href = url.toString();
}

document.querySelectorAll("#boardTitle").forEach((el) => {
  const maxLength = 30;
  if (el.textContent.length > maxLength) {
    el.textContent = el.textContent.slice(0, maxLength) + "...";
  }
});

/*
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
    "법률·세무·경영 자문",
    "의료 및 건강",
    "생활 서비스",
    "번역 및 통역",
  ],
};

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
    "법률·세무·경영 자문": 14,
    "의료 및 건강": 15,
    "생활 서비스": 16,
    "번역 및 통역": 17,
  },
};

const mainCategoryCodeMap = {
  물건: 0,
  재능: 1,
};

// ✅ 공통 함수
const populateSubCategory = (mainVal, selectedSub = "") => {
  const sub = document.getElementById("sub-category");
  sub.innerHTML = '<option value="">-- 세부 선택 --</option>';
  if (subOptions[mainVal]) {
    subOptions[mainVal].forEach((item) => {
      const opt = document.createElement("option");
      opt.value = item;
      opt.textContent = item;
      sub.appendChild(opt);
    });
    if (selectedSub) sub.value = selectedSub;
  }
};

// ✅ 메인 카테고리 변경 시
document.getElementById("main-category").addEventListener("change", function () {
  populateSubCategory(this.value);
  const detailInput = document.getElementById("categoryCode");
  if (detailInput) detailInput.value = "";
});

// ✅ 페이지 로드시 설정
document.addEventListener("DOMContentLoaded", () => {
  const mainCategory = document.getElementById("main-category");
  const subCategory = document.getElementById("sub-category");
  const detailCodeInput = document.getElementById("categoryCode");

  // 게시글 수정
  if (detailCodeInput && detailCodeInput.value) {
    const detailCode = parseInt(detailCodeInput.value);
    const mainVal = detailCode < 10 ? "물건" : "재능";
    mainCategory.value = mainVal;
    populateSubCategory(mainVal);

    for (const [key, value] of Object.entries(categoryCodeMap[mainVal])) {
      if (value === detailCode) {
        subCategory.value = key;
        break;
      }
    }
  }

  // 목록 페이지
  if (typeof selectedMain !== "undefined") {
    mainCategory.value = selectedMain;
    populateSubCategory(selectedMain, selectedSub);
  }
});

// ✅ 소분류 선택 시 코드 설정
document.getElementById("sub-category").addEventListener("change", function () {
  const main = document.getElementById("main-category").value;
  const sub = this.value;
  const detailInput = document.getElementById("categoryCode");
  if (main && sub && detailInput) {
    detailInput.value = categoryCodeMap[main][sub];
  }
});

// ✅ 검색 필터 유지
const searchTypeSelect = document.querySelector("select[name='searchType']");
const searchKeywordInput = document.querySelector("input[name='searchKeyword']");

if (searchTypeSelect && typeof searchType !== "undefined") {
  searchTypeSelect.value = searchType;
}
if (searchKeywordInput && typeof searchKeyword !== "undefined") {
  searchKeywordInput.value = searchKeyword;
}

console.log("searchType:", searchType);
console.log("searchKeyword:", searchKeyword);

// ✅ 카테고리 선택 후 검색
function submitSubCategory() {
  const main = document.getElementById("main-category").value;
  const sub = document.getElementById("sub-category").value;

  const url = new URL(window.location.href);
  url.searchParams.set("mainCategory", main);
  url.searchParams.set("subCategory", sub);
  url.searchParams.set("cp", 1);

  window.location.href = url.toString();
}

// ✅ 게시글 제목 자르기
document.querySelectorAll("#boardTitle").forEach((el) => {
  const maxLength = 30;
  if (el.textContent.length > maxLength) {
    el.textContent = el.textContent.slice(0, maxLength) + "...";
  }
});

*/
