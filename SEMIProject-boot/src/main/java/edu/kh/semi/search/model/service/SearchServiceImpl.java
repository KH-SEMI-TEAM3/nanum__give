package edu.kh.semi.search.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.board.model.dto.ShareBoard;
import edu.kh.semi.search.model.mapper.SearchMapper;

@Service
public class SearchServiceImpl implements SearchService {
	@Autowired
	public SearchMapper mapper;

    @Override
    public Map<String, Object> searchFreeBoard(String searchType, String query, int cp, int limit) {
        int offset = (cp - 1) * limit;
        RowBounds rowBounds = new RowBounds(offset, limit);

        // 전체 결과 개수 조회
        int listCount = mapper.getFreeBoardSearchCount(searchType, query);

        // Pagination 객체 생성
        Pagination pagination = new Pagination(cp, listCount, limit, 10); // (현재페이지, 전체수, 한페이지당개수, 보여질페이지수)

        List<Board> boardList = mapper.searchFreeBoard(searchType, query, rowBounds);

        Map<String, Object> result = new HashMap<>();
        result.put("boardList", boardList);
        result.put("pagination", pagination);
        return result;
    }

    public Map<String, Object> searchShareBoard(String searchType, String query, int cp, int limit) {
        int offset = (cp - 1) * limit;
        RowBounds rowBounds = new RowBounds(offset, limit);

        // 전체 결과 개수 조회
        int listCount = mapper.getShareBoardSearchCount(searchType, query);

        // Pagination 객체 생성
        Pagination pagination = new Pagination(cp, listCount, limit, 10); // (현재페이지, 전체수, 한페이지당개수, 보여질페이지수)

        List<Board> boardList = mapper.searchShareBoard(searchType, query, rowBounds);

        Map<String, Object> result = new HashMap<>();
        result.put("boardList", boardList);
        result.put("pagination", pagination);
        return result;
    }

    @Override
    public Map<String, Object> searchNoticeBoard(String searchType, String query, int cp, int limit) {
        int offset = (cp - 1) * limit;
        RowBounds rowBounds = new RowBounds(offset, limit);

        // 전체 결과 개수 조회
        int listCount = mapper.getNoticeBoardSearchCount(searchType, query);

        // Pagination 객체 생성
        Pagination pagination = new Pagination(cp, listCount, limit, 10); // (현재페이지, 전체수, 한페이지당개수, 보여질페이지수)

        List<Board> boardList = mapper.searchNoticeBoard(searchType, query, rowBounds);

        Map<String, Object> result = new HashMap<>();
        result.put("boardList", boardList);
        result.put("pagination", pagination);
        return result;
    }

    @Override
    public Map<String, Object> searchQNABoard(String searchType, String query, int cp, int limit) {
        int offset = (cp - 1) * limit;
        RowBounds rowBounds = new RowBounds(offset, limit);

        // 전체 결과 개수 조회
        int listCount = mapper.getQNABoardSearchCount(searchType, query);

        // Pagination 객체 생성
        Pagination pagination = new Pagination(cp, listCount, limit, 10); // (현재페이지, 전체수, 한페이지당개수, 보여질페이지수)

        List<Board> boardList = mapper.searchQNABoard(searchType, query, rowBounds);

        Map<String, Object> result = new HashMap<>();
        result.put("boardList", boardList);
        result.put("pagination", pagination);
        return result;
    }
}