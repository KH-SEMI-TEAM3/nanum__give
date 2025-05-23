package edu.kh.semi.board.model.service;

import java.util.Map;

import edu.kh.semi.board.model.dto.Board;

public interface NoticeEditService {
    
    // 게시글 작성
    int boardInsert(Board inputBoard) throws Exception;

    // 게시글 수정
    int boardUpdate(Board inputBoard) throws Exception;

    // 게시글 삭제
    int boardDelete(Map<String, Integer> map);
}
