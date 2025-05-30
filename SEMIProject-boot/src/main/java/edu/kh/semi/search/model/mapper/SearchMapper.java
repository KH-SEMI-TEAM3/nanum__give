package edu.kh.semi.search.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.ShareBoard;
@Mapper
public interface SearchMapper {

	int getFreeBoardSearchCount(@Param("searchType") String searchType,
            @Param("query") String query);

List<Board> searchFreeBoard(@Param("searchType") String searchType,
            @Param("query") String query,
            RowBounds rowBounds);

int getShareBoardSearchCount(@Param("searchType") String searchType,
              @Param("query") String query);

List<Board> searchShareBoard(@Param("searchType") String searchType,
              @Param("query") String query,
              RowBounds rowBounds);

int getNoticeBoardSearchCount(@Param("searchType") String searchType,
               @Param("query") String query);

List<Board> searchNoticeBoard(@Param("searchType") String searchType,
               @Param("query") String query,
               RowBounds rowBounds);

int getQNABoardSearchCount(@Param("searchType") String searchType,
            @Param("query") String query);

List<Board> searchQNABoard(@Param("searchType") String searchType,
            @Param("query") String query,
            RowBounds rowBounds);






}
