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

    @Override
    public int getNoticeListCount() {
        return mapper.getNoticeListCount();
    }

    @Override
    public List<Board> selectNoticeList(Pagination pagination) {
        int limit = pagination.getLimit();
        int offset = (pagination.getCurrentPage() - 1) * limit;
        RowBounds rowBounds = new RowBounds(offset, limit);
        return mapper.selectNoticeList(rowBounds);
    }

    @Override
    public Board selectNoticeDetail(Long boardNo) {
        return mapper.selectNoticeDetail(boardNo);
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

	// return null -> mapper.selectOne으로 수정 
	@Override
	public Board selectOne(Map<String, Integer> map) {
	
		   return mapper.selectOne(map);
	}

}
