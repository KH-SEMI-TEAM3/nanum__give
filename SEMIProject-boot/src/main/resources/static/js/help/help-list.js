const insertBtn = document.querySelector("#insertBtn");

// 글쓰기 버튼이 존재할 때

if (insertBtn != null) {
  insertBtn.addEventListener("click", () => {
    /* get방식 요청 보내자 => 글 작성할 수 있는 form이 나타나게끔 이동시킨다
	 "/editBoard/1/insert
	라는 주소로 get으로 보내게 됨
	*/

    const boardCode = 4;
    location.href = `/help/${boardCode}/insert`; // 이거 백틱임 따옴표 아님!
  });
}
