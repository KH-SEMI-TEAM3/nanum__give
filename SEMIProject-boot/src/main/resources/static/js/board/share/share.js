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

// 메인 카테고리 코드 매핑
const mainCategoryCodeMap = {
  물건: 0,
  재능: 1,
};


document.getElementById("categoryCode").addEventListener("change", function () {
  const mainVal = this.value;
  const sub = document.getElementById("categoryDetailCode");
  sub.innerHTML = '<option value="">-- 세부 선택 --</option>';
  if (subOptions[mainVal]) {
    subOptions[mainVal].forEach(item => {
      const opt = document.createElement("option");
      opt.value = item;
      opt.textContent = item;
      sub.appendChild(opt);
    });
  }
  // 소분류 초기화
  const detailInput = document.getElementById("categoryDetailCodeInput");
  if (detailInput) detailInput.value = "";
});

// 페이지 로드 시 설정
document.addEventListener("DOMContentLoaded", () => {
  const mainCategory = document.getElementById("categoryCode");
  const subCategory = document.getElementById("categoryDetailCode");
  const detailCodeInput = document.getElementById("categoryDetailCodeInput");

  if (detailCodeInput && detailCodeInput.value) {
    const detailCode = parseInt(detailCodeInput.value);
    const mainVal = detailCode < 10 ? "물건" : "재능";
    mainCategory.value = mainVal;

    subCategory.innerHTML = '<option value="">-- 세부 선택 --</option>';
    subOptions[mainVal].forEach(item => {
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
});

// 검색 버튼 이벤트
document.getElementById("category-search-btn").addEventListener("click", () => {
  const mainCategory = document.getElementById("categoryCode").value;
  const subCategory = document.getElementById("categoryDetailCode").value;

  const url = new URL(window.location.href);
  url.searchParams.set("cp", "1");

  if (subCategory) {
    const code = categoryCodeMap[mainCategory][subCategory];
    url.searchParams.set("categoryDetailCode", code);
    url.searchParams.delete("categoryCode");
  } else if (mainCategory) {
    const mainCode = mainCategoryCodeMap[mainCategory];
    url.searchParams.set("categoryCode", mainCode);
    url.searchParams.delete("categoryDetailCode");
  } else {
    url.searchParams.delete("categoryCode");
    url.searchParams.delete("categoryDetailCode");
  }
  window.location.href = url.toString();
});