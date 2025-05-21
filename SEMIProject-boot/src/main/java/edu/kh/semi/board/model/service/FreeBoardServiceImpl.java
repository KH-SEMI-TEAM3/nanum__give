package edu.kh.semi.board.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.board.model.mapper.FreeBoardMapper;

@Service
public class FreeBoardServiceImpl implements FreeBoardService {

	@Autowired
	private FreeBoardMapper mapper;
	
	@Override
	public List<Board> getList(Pagination pagination) {
		return mapper.selectFreeList(pagination);
	}
	
	@Override
	public int getListCount() {
		return mapper.selectFreeListCount();
	}
	
	@Override
	public int insertFreeBoard(Board board) {
		return mapper.insertFreeBoard(board);
	}

	@Override
	public Board getFreeBoard(Long boardNo) {
		return mapper.selectFreeBoard(boardNo);
	}
	
	@Override
	public int updateBoard(Board board) {
		return mapper.updateFreeBoard(board);
	}
	

//
//
//    @Override
//    public Board getFreeBoard(Long boardNo) {
//        return mapper.selectFree(boardNo);
//    }
//
//    @Override
//    public int createFreeBoard(Board board) {
//        return mapper.insertFree(board);
//    }
//
//    @Override
//    public int modifyFreeBoard(Board board) {
//        return mapper.updateFree(board);
//    }
//
//    @Override
//    public int removeFreeBoard(Long boardNo) {
//        return mapper.deleteFree(boardNo);
//    }

}