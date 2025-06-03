package edu.kh.semi.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.Board;

public interface NoticeEditService {
    
    // 게시글 작성
    int boardInsert(Board inputBoard, List<MultipartFile> images) throws Exception;

    // 게시글 수정
    int boardUpdate(Board inputBoard,  List<MultipartFile> images) throws Exception;

    // 게시글 삭제
    int boardDelete(Map<String, Integer> map);
}
