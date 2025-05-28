const reverseCategoryCodeMap = {
  0: "디지털 기기",
  1: "생활 가전",
  2: "의류/잡화",
  3: "스포츠",
  4: "취미/게임/음반",
  5: "도서",
  6: "기타",
  10: "교육 및 멘토링",
  11: "예술 및 문화",
  12: "디자인 및 콘텐츠",
  13: "IT 및 기술",
  14: "법률-세무-경영 자문",
  15: "의료 및 건강",
  16: "생활 서비스",
  17: "번역 및 통역",
};

function getCategoryName(code) {
  return reverseCategoryCodeMap[parseInt(code)] || "기타";
}

document.querySelectorAll("[data-category-code]").forEach((el) => {
  const code = el.getAttribute("data-category-code");
  const name = getCategoryName(code);
  el.textContent = name;
});
