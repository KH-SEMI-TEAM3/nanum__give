package edu.kh.semi.board.model.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.BoardImg;
import edu.kh.semi.board.model.dto.Comment;
import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.board.model.mapper.CommentMapper;
import edu.kh.semi.board.model.mapper.FreeBoardMapper;
import edu.kh.semi.common.util.Utility;

@Service
public class FreeBoardServiceImpl implements FreeBoardService {

	@Autowired
	private FreeBoardMapper mapper;
	
	@Autowired
	private CommentMapper commentMapper;
	
	@Value("${my.board.folder-path}")
	private String folderPath;

	@Value("${my.board.web-path}")
	private String webPath;

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
		int result = mapper.insertFreeBoard(board);

		// 여기서는 이미지 저장은 하지 않음
	    return result > 0 ? board.getBoardNo() : 0;
	}

	@Override
	public Board getFreeBoard(int boardNo) {
		return mapper.selectFreeBoard(boardNo);
	}

	@Override
	public void updateReadCount(int boardNo) {
		mapper.updateReadCount(boardNo);
	}
	
	@Override
	public int updateBoard(Board board, MultipartFile boardImage) {
	    // 게시글 내용 수정
	    int result = mapper.updateFreeBoard(board);

	    //이미지가 비어있지 않으면 → 기존 이미지 삭제 + 새 이미지 저장
	    if (boardImage != null && !boardImage.isEmpty()) {

	        // 2-1. 기존 이미지 삭제
	        mapper.deleteBoardImage(board.getBoardNo());

	        // 2-2. 파일 이름 처리
	        String originalName = boardImage.getOriginalFilename();
	        String renamed = Utility.fileRename(originalName);  // 파일명 중복 방지

	        // 2-3. 저장 경로
	        String imgPath = webPath;  // 웹 접근 경로
	        String savePath = folderPath; // 실제 저장 경로
	        
	        File directory = new File(folderPath); 
	        if (!directory.exists()) {
	            directory.mkdirs();
	        }
	        // 2-4. 실제 파일 저장
	        try {
	        	boardImage.transferTo(new File(folderPath + renamed));
	        } catch (Exception e) {
	            e.printStackTrace();
	            return 0; // 실패 시
	        }

	        // 2-5. BoardImg 객체 생성 및 삽입
	        BoardImg image = BoardImg.builder()
	            .imgPath(imgPath)
	            .imgOriginalName(originalName)
	            .imgRename(renamed)
	            .imgOrder(0)
	            .boardNo(board.getBoardNo())
	            .build();

	        // 이미지 INSERT
	        mapper.insertBoardImage(image);
	    }

	    // 3. 모든 처리 완료 후 성공 여부 리턴
	    return result;
	}

	// 검색 기능 추가 김동준 2025-05-22
	@Override
	public Map<String, Object> searchByKeyword(String query, int cp) {
	    int listCount = mapper.countSearch(query);
	    Pagination pagination = new Pagination(cp, listCount);

	    int limit = pagination.getLimit();
	    int offset = (cp - 1) * limit;
	    RowBounds rowBounds = new RowBounds(offset, limit);

	    List<Board> boardList = mapper.searchByKeyword(query, rowBounds);

	    Map<String, Object> map = new HashMap<>();
	    map.put("pagination", pagination);
	    map.put("boardList", boardList);

	    return map;
	}

	// 내가 쓴 글 김동준 2025-05-22
	@Override
	public List<Board> selectByMember(int memberNo) {
		return mapper.selectByMember(memberNo);
	}
	

	@Override
	public int deleteBoard(int boardNo) {
		return mapper.deleteBoard(boardNo);
	}
	
	@Override
	public List<Comment> getCommentList(int boardNo) {
		return commentMapper.select(boardNo); 
	}



}