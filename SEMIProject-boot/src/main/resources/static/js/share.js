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
    " IT 및 기술",
    "법률-세무-경영 자문",
    "의료 및 건강",
    "생활 서비스",
    "번역 및 통역",
  ],
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
