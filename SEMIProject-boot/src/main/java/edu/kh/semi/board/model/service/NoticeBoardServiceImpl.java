package edu.kh.semi.board.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.mapper.NoticeBoardMapper;

@Service
public class NoticeBoardServiceImpl implements NoticeBoardService {

    @Autowired
    private NoticeBoardMapper mapper;

    @Override
    public List<Board> getNoticeList() {
        return mapper.selectNoticeList();
    }

    @Transactional
    @Override
    public Board getNoticeBoard(Long boardNo) {
        mapper.updateReadCount(boardNo); // 조회수 증가
        return mapper.selectNoticeBoard(boardNo);
    }

    @Transactional
    @Override
    public int createNoticeBoard(Board board) {
        return mapper.insertNoticeBoard(board);
    }

    @Transactional
    @Override
    public int modifyNoticeBoard(Board board) {
        return mapper.updateNoticeBoard(board);
    }

    @Transactional
    @Override
    public int removeNoticeBoard(Long boardNo) {
        return mapper.deleteNoticeBoard(boardNo);
    }

    @Override
    public int updateReadCount(Long boardNo) {
        return mapper.updateReadCount(boardNo);
    }
}
