package edu.kh.semi.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.board.model.mapper.NoticeBoardMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class NoticeBoardServiceImpl implements NoticeBoardService {

	
    @Autowired
    private NoticeBoardMapper mapper;

	
	// 게시판 지정 페이지 목록 조회 서비스 
	@Override
	public Map<String, Object> selectNoticeList(int boardCode , int cp) {
		
		
		int listCount = mapper.getNoticeListCount(boardCode);
		
		Pagination pagination = new Pagination(cp, listCount);
		
		// 3. 
		int limit = pagination.getLimit(); // 10개씩 조회
		int offset = (cp - 1) * limit ; 
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		
		List<Board> boardList =mapper.selectNoticeList(boardCode, rowBounds);
		
		// 4. 목록 조회 결과 
		Map<String, Object> map =new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		
		// 5. 결과 반환 
		return map; 
		}

	// ---------------------------------------------------------------------------
	// 검색 서비스 
	public Map<String, Object> noticeSearchList(Map<String, Object> paramMap , int cp){
		
		// 1.
		int listCount =mapper.getnoticeSearchCount(paramMap);
		
		// 2.
		Pagination pagination =new Pagination(cp,listCount);
		
		// 3.
		int limit = pagination.getLimit();
		int offset = (cp-1) * limit ;
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Board> boardList = mapper.selectNoticeSearchList(paramMap, rowBounds);
		
		// 4.
		Map<String, Object> map = new HashMap<>();
		
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		
		return map;
		
		
		
	}
	
	
	
	
	// ---------------------------------------------------------------------------
	
	// 상세조회 
    @Override
    public Board selectNoticeDetail(Long boardNo) {
        return mapper.selectNoticeDetail(boardNo);
    }
    
	// return null -> mapper.selectOne으로 수정 
	@Override
	public Board selectOne(Map<String, Integer> map) {
	
		   return mapper.selectOne(map);
	}

    @Override
    public int updateReadCount(Long boardNo) {
        return mapper.updateReadCount(boardNo);
    }

    @Override
    public Map<String, Object> searchByKeyword(String query, int cp) {
        int listCount = mapper.getSearchCount(query);

        Pagination pagination = new Pagination(cp, listCount);
        int limit = pagination.getLimit();
        int offset = (cp - 1) * limit;

        RowBounds rowBounds = new RowBounds(offset, limit);
        List<Board> boardList = mapper.searchByKeyword(query, rowBounds);

        Map<String, Object> map = new HashMap<>();
        map.put("boardList", boardList);
        map.put("pagination", pagination);
        return map;
    }
	@Override
	public List<Board> selectByMember(int memberNo) {
	
		return mapper.selectByMember(memberNo);
	}

	@Override
	public int getNoticeListCount() {
		// TODO Auto-generated method stub
		return 0;
	}



}
