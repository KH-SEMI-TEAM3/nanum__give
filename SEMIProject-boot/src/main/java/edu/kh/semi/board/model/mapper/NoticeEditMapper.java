package edu.kh.semi.board.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.BoardImg;

@Mapper
public interface NoticeEditMapper {

    /** 게시글 작성 */
    int boardInsert(Board inputBoard);

    /** 게시글 수정 */
    int boardUpdate(Board inputBoard);

    /** 게시글 삭제 */
    int boardDelete(Map<String, Integer> map);
    
    int insertBoardImage(BoardImg img);

    int deleteBoardImages(int boardNo);

}
