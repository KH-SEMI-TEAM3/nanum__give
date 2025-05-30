package edu.kh.semi.search.model.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.ShareBoard;

public interface SearchService {

    Map<String, Object> searchFreeBoard(String searchType, String query, int cp, int limit);
    Map<String, Object> searchShareBoard(String searchType, String query, int cp, int limit);
    Map<String, Object> searchNoticeBoard(String searchType, String query, int cp, int limit);
    Map<String, Object> searchQNABoard(String searchType, String query, int cp, int limit);

}
