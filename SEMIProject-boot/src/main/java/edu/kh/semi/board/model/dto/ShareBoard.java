package edu.kh.semi.board.model.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class ShareBoard {
    private int boardNo;
    private String boardTitle;
    private String boardContent;
    private String boardWriteDate;
    private String boardUpdateDate;
    private int readCount;
    private String boardDelFl;
    private int memberNo;
    private int boardCode;
    private String shareStatus;
    private int shareBoardCategoryCode;
    private int shareBoardCategoryDetailCode;
    
    // 추가 필드
    private String memberNickname;
    private String shareBoardCategoryName;
    private String shareBoardCategoryDetailName;
    
    // 댓글 수, 찜 수
    private int commentCount;
    private int jjimCheck;
    private int jjimCount;
    
    //썸네일
    private String thumbnail;

    private List<Comment> commentList;
}
