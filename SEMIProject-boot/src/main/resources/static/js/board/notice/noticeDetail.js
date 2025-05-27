// ---------- 게시글 수정 버튼 ------------------

const updateBtn = document.querySelector("#updateBtn");

if (updateBtn != null) {
  // 수정 버튼 존재 시

  updateBtn.addEventListener("click", () => {
    // get 방식
    // 현재 : /notice/481/update?cp=1
    // 목표 : /noticeEdit/2001/update?cp=1
    location.href =
      location.pathname.replace("notice", "noticeEdit") +
      "/update" +
      location.search;
  });
}

// 삭제(GET)
const deleteBtn = document.querySelector("#deleteBtn");

if (deleteBtn != null) {
  deleteBtn.addEventListener("click", () => {
    if (!confirm("삭제 하시겠습니까?")) {
      alert("취소됨");
      return;
    }

    const url = location.pathname.replace("board", "editBoard") + "/delete";
    // 현재 : /board/1/2004?cp=1
    // 목표 : /editBoard/1/2004/delete?cp=1

    const queryString = location.search; // ?cp=1
    location.href = url + queryString;

    // -> /editBoard/1/2004/delete?cp=1
  });
}

// 삭제(POST)
const deleteBtn2 = document.querySelector("#deleteBtn2");

if (deleteBtn2 != null) {
  deleteBtn2.addEventListener("click", () => {
    if (!confirm("삭제 하시겠습니까?")) {
      alert("취소됨");
      return;
    }

    const url = location.pathname.replace("board", "editBoard") + "/delete";
    // 목표 : /editBoard/1/2004/delete

    // JS에서 동기식으로 Post 요청 보내는법
    // -> form 태그 생성
    const form = document.createElement("form");
    form.action = url;
    form.method = "POST";

    // cp 값을 저장할 input 생성
    const input = document.createElement("input");
    input.type = "hidden";
    input.name = "cp";

    // 쿼리스트링에서 원하는 파라미터 얻어오기
    const params = new URLSearchParams(location.search);
    // ?cp=1
    const cp = params.get("cp"); // 1
    input.value = cp;

    form.append(input);

    // 화면에 form 태그를 추가한 후 제출하기
    document.querySelector("body").append(form);
    form.submit();
  });
}
