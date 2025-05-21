package edu.kh.semi.board.model.mapper;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.annotations.Mapper;

import edu.kh.semi.board.model.dto.Board;

@Mapper
public interface NoticeBoardMapper {

    int getNoticeListCount();

    List<Board> selectNoticeList(RowBounds rowBounds);

    Board selectNoticeDetail(Long boardNo);

    int updateReadCount(Long boardNo);
}
