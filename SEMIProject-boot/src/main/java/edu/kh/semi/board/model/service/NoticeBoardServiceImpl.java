package edu.kh.semi.board.model.service;

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
    public List<Board> searchByKeyword(String query) {
        return mapper.searchByKeyword(query);
    }

	@Override
	public List<Board> selectByMember(int memberNo) {
		// TODO Auto-generated method stub
		return mapper.selectByMember(memberNo);
	}

	@Override
	public Board selectOne(Map<String, Integer> map) {
		// TODO Auto-generated method stub
		return null;
	}
}
