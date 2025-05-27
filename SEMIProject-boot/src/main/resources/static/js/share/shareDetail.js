// 찜 기능 구현
document.addEventListener("DOMContentLoaded", function () {
  const jjimIcon = document.getElementById("jjimIcon");
  const jjimCount = document.getElementById("jjimCount");
  jjimIcon.style.fontVariationSettings = `'FILL' ${jjimCheck}, 'wght' 400, 'GRAD' 0, 'opsz' 24`;
  jjimIcon.style.color = jjimCheck == 1 ? "red" : "black";
  if (jjimIcon) {
    jjimIcon.addEventListener("click", function () {
      // 로그인 상태 체크
      if (loginMemberNo == null) {
        alert("로그인 후 이용해 주세요");
        return;
      }

      // 요청 데이터 준비
      const obj = {
        memberNo: loginMemberNo,
        boardNo: boardNo,
        jjimCheck: jjimCheck,
      };

      // 찜 INSERT/DELETE 비동기 요청
      fetch("/share/jjim", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(obj),
      })
        .then((resp) => resp.text())
        .then((count) => {
          if (count == -1) {
            console.log("찜 처리 실패");
            return;
          }

          // jjimCheck 값 0 <-> 1 변환
          jjimCheck = jjimCheck == 0 ? 1 : 0;
          jjimIcon.style.fontVariationSettings = `'FILL' ${jjimCheck}, 'wght' 400, 'GRAD' 0, 'opsz' 24`;
          jjimIcon.style.color = jjimCheck == 1 ? "red" : "black";
          // 찜 개수 업데이트
          jjimCount.innerText = count;
        })
        .catch((error) => {
          console.error("찜 처리 중 오류 발생:", error);
        });
    });
  }
});
