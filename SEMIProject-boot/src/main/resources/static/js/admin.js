console.log("✅ admin.js loaded");

function deleteMember() {
  if (!confirm("정말로 해당 회원을 삭제하시겠습니까?")) return;

  const memberNo = document.querySelector(".free-title")?.dataset.memberNo;
  const boardNo = document.querySelector(".free-title")?.dataset.boardNo;
  const cp = new URLSearchParams(location.search).get("cp") || 1;

  if (!memberNo || !boardNo) {
    alert("회원 삭제 실패");
    return;
  }

  location.href = `/admin/free/memberDelete?memberNo=${memberNo}&boardNo=${boardNo}&cp=${cp}`;
}

document.addEventListener("DOMContentLoaded", () => {
  const msg = document.getElementById("alertMessage")?.value;
  if (msg) alert(msg);
});

function deleteBoard() {
  if (!confirm("정말로 이 글을 삭제하시겠습니까?")) return;

  const boardNo = document.querySelector(".free-title")?.dataset.boardNo;
  const cp = new URLSearchParams(location.search).get("cp") || 1;

  if (!boardNo) {
    alert("글 삭제 실패");
    return;
  }

  location.href = `/admin/free/boardDelete?boardNo=${boardNo}&cp=${cp}`;
}
