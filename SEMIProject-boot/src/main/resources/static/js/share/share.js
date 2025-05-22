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

// 서브 카테고리 변경 시 카테고리 코드 설정
document.getElementById("sub-category").addEventListener("change", function () {
  const mainCategory = document.getElementById("main-category").value;
  const subCategory = this.value;

  if (mainCategory && subCategory) {
    const categoryCode = categoryCodeMap[mainCategory][subCategory];
    // hidden input에 카테고리 코드 설정
    const categoryCodeInput = document.createElement("input");
    categoryCodeInput.type = "hidden";
    categoryCodeInput.name = "shareBoardCategoryDetailCode";
    categoryCodeInput.value = categoryCode;

    // 기존 hidden input이 있다면 제거
    const existingInput = document.querySelector(
      'input[name="shareBoardCategoryDetailCode"]'
    );
    if (existingInput) {
      existingInput.remove();
    }

    // form에 hidden input 추가
    document.querySelector("form").appendChild(categoryCodeInput);
  }
});

// 폼 제출 시 카테고리 선택 검증
document.querySelector("form").addEventListener("submit", function (e) {
  const categoryCode = document.querySelector(
    'input[name="shareBoardCategoryDetailCode"]'
  );
  if (!categoryCode || !categoryCode.value) {
    e.preventDefault();
    alert("카테고리를 선택해주세요.");
    return false;
  }
});
