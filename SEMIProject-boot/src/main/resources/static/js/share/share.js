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

// 서브 카테고리 변경 시 카테고리 코드 설정
document.getElementById("sub-category").addEventListener("change", function () {
  const mainCategory = document.getElementById("main-category").value;
  const subCategory = this.value;

  console.log("Selected categories:", { mainCategory, subCategory });

  if (mainCategory && subCategory) {
    const categoryCode = categoryCodeMap[mainCategory][subCategory];
    console.log("Category code from map:", categoryCode);

    // 기존 hidden input에 값 설정
    const categoryCodeInput = document.getElementById("categoryCode");
    if (categoryCodeInput) {
      categoryCodeInput.value = categoryCode;
      console.log("Category Code Set:", categoryCode);
    }
  }
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

const mainCategory = document.getElementById("main-category");

mainCategory.onchange = () => {
  const selected = mainCategory.value;

  if (selected) {
    location.href = `/share/list?key=mainCategory&query=${selected}`;
  } else {
    location.href = "/board/3";
  }
};

const url = new URLSearchParams(window.location.search);
const selectedQuery = url.get("query"); // 물건, 재능 중 하나

if (selectedQuery) {
  const mainSelect = document.getElementById("main-category");
  mainSelect.value = selectedQuery;
}
