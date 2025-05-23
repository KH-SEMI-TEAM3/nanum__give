package edu.kh.semi.board.model.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.mapper.NoticeEditMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class NoticeEditServiceImpl implements NoticeEditService {

    @Autowired
    private NoticeEditMapper mapper;

    // 게시글 작성
    @Override
    public int boardInsert(Board inputBoard) throws Exception {
    	// 반환 받기 위해 추가 (5.23)
    	int result = mapper.boardInsert(inputBoard);
    	
    	if(result==0) {
    		return 0;
    	}
    	
    	int boardNo = inputBoard.getBoardNo();
    	
    	
    	
        return mapper.boardInsert(inputBoard);
    }

    // 게시글 수정
    @Override
    public int boardUpdate(Board inputBoard) throws Exception {
    	
    	// 게시글 이미지 수정 
    	int result = mapper.boardUpdate(inputBoard);
    	
    	if(result == 0) return 0;
    	
        return mapper.boardUpdate(inputBoard);
    }

    // 게시글 삭제
    @Override
    public int boardDelete(Map<String, Integer> map) {
        return mapper.boardDelete(map);
    }
}
