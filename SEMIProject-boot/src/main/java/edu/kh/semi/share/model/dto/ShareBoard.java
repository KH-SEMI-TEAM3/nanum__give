package edu.kh.semi.share.model.dto;

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
    private int shareBoardCategoryDetailCode;
    
    // 추가 필드
    private String memberNickname;
    private String shareBoardCategoryDetailName;
    private String shareBoardCategoryName;
    
    // 댓글 수, 찜 수
    private int commentCount;
    private int jjimCount;
}
