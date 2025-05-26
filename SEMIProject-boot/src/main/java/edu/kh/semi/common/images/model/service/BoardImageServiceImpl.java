package edu.kh.semi.common.images.model.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.BoardImg;
import edu.kh.semi.common.images.model.mapper.BoardImageMapper;
import edu.kh.semi.common.util.Utility;
import jakarta.servlet.http.HttpSession;

@Service
public class BoardImageServiceImpl implements BoardImageService {
	
	@Value("${my.board.folder-path}") // = C:/images/board/
	private String folderPath;

	@Autowired
	private BoardImageMapper mapper;
	
	
	@Override
	public void save(BoardImg img) {
		mapper.insertImage(img);
	}

	@Override
	public void saveBoardImage(Board board, MultipartFile image, String webPath, HttpSession session) {
		try {
	        // 1. 저장 폴더가 없다면 생성
	        File directory = new File(folderPath);
	        if (!directory.exists()) directory.mkdirs();

	        // 2. 파일명 변경
	        String originalName = image.getOriginalFilename();
	        String renamed = Utility.fileRename(originalName);

	        // 3. 실제 파일 저장
	        File dest = new File(folderPath, renamed);
	        image.transferTo(dest);

	        // 4. DB 저장용 객체 생성
	        BoardImg boardImg = BoardImg.builder()
	            .imgOriginalName(originalName)
	            .imgRename(renamed)
	            .imgPath(webPath) // 웹에서 접근 가능한 경로 (/images/board/)
	            .boardNo(board.getBoardNo())
	            .imgOrder(0)
	            .build();

	        mapper.insertImage(boardImg);

	        // 5. 화면에서 사용할 썸네일 경로 지정
	        board.setThumbnail(webPath + renamed);
	        
	        System.out.println("[DEBUG] 저장 완료 → " + folderPath + renamed);

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("게시판 이미지 저장 중 예외 발생");
	    }
	}
}
