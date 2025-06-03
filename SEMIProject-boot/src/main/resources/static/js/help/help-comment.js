const selectCommentList = () => {
  fetch("/helpcomment?boardNo=" + boardNo)
    .then((resp) => resp.json())
    .then((commentList) => {
      const ul = document.querySelector("#commentList");
      ul.innerHTML = "";

      for (let comment of commentList) {
        const li = document.createElement("li");
        li.classList.add("comment-row");

        if (comment.parentCommentNo != 0) li.classList.add("child-comment");

        // 1) 탈퇴된 회원이면 닉네임도 내용도 모두 숨기고 메시지 출력
        if (comment.memberDelFl === "Y") {
          li.innerText = "삭제된 댓글입니다";
          ul.append(li);
          continue;
        }

        // 2) 회원은 살아있지만 댓글만 삭제된 경우
        if (comment.commentDelFl === "Y") {
          li.innerText = "삭제된 댓글 입니다";
          ul.append(li);
          continue;
        }

        // 3) 둘 다 아니라면 정상 댓글 렌더링
        const writer = document.createElement("p");
        writer.classList.add("comment-writer");

        const img = document.createElement("img");
        img.src = comment.memberImg || userDefaultIamge;

        const name = document.createElement("span");
        name.innerText = comment.memberNickname;

        const date = document.createElement("span");
        date.classList.add("comment-date");
        date.innerText = comment.commentWriteDate;

        writer.append(img, name, date);

        const content = document.createElement("p");
        content.classList.add("comment-content");
        content.innerText = comment.commentContent;

        // 관리자 전용 버튼 영역 (생략된 로직 그대로)
        if (loginMemberAuthority === 0 && comment.memberNo != loginMemberNo) {
          const adminArea = document.createElement("div");
          adminArea.classList.add("admin-btn-area");

          const adminDeleteBtn = document.createElement("button");
          adminDeleteBtn.innerText = "관리자 댓글 삭제";
          adminDeleteBtn.setAttribute(
            "onclick",
            `adminDeleteComment(${comment.commentNo})`
          );
          adminArea.append(adminDeleteBtn);

          const adminDeleteCommentMember = document.createElement("button");
          adminDeleteCommentMember.innerText = "관리자 댓글 작성자 삭제";
          adminDeleteCommentMember.setAttribute(
            "onclick",
            `adminDeleteCommentMember(${comment.memberNo})`
          );
          adminArea.append(adminDeleteCommentMember);

          li.prepend(adminArea);
        }

        const btnArea = document.createElement("div");
        btnArea.classList.add("comment-btn-area");

        if (comment.parentCommentNo === 0) {
          if (loginMemberNo == boardWriterNo || loginMemberAuthority == 0) {
            const replyBtn = document.createElement("button");
            replyBtn.innerText = "답글";
            replyBtn.setAttribute(
              "onclick",
              `showInsertComment(${comment.commentNo}, this)`
            );
            btnArea.append(replyBtn);
          }
        }

        if (loginMemberNo && loginMemberNo == comment.memberNo) {
          const updateBtn = document.createElement("button");
          updateBtn.innerText = "수정";
          updateBtn.setAttribute(
            "onclick",
            `showUpdateComment(${comment.commentNo}, this)`
          );

          const deleteBtn = document.createElement("button");
          deleteBtn.innerText = "삭제";
          deleteBtn.setAttribute(
            "onclick",
            `deleteComment(${comment.commentNo})`
          );

          btnArea.append(updateBtn, deleteBtn);
        }

        li.append(writer, content, btnArea);
        ul.append(li);
      }
    });
};

// 최초 호출
selectCommentList();
