package edu.kh.semi.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.board.model.dto.Board;

@Mapper
public interface NoticeBoardMapper {

    List<Board> selectNoticeList();

    Board selectNoticeBoard(Long boardNo);

    int insertNoticeBoard(Board board);

    int updateNoticeBoard(Board board);

    int deleteNoticeBoard(Long boardNo);

    int updateReadCount(Long boardNo);
}
