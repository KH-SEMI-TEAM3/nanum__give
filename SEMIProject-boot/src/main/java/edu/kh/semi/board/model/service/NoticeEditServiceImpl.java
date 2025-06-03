package edu.kh.semi.board.model.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.BoardImg;
import edu.kh.semi.board.model.mapper.NoticeEditMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class NoticeEditServiceImpl implements NoticeEditService {
	

    // config.properties에서 경로 값 주입
    @Value("${my.board.folder-path}")
    private String folderPath;

    @Value("${my.board.web-path}")
    private String webPath;


    @Autowired
    private NoticeEditMapper mapper;

    // 게시글 작성
    @Override
    public int boardInsert(Board inputBoard, List<MultipartFile> images) throws Exception {
    	// 반환 받기 위해 추가 (5.23)
    	int result = mapper.boardInsert(inputBoard);
    
    	
    	if(result==0) return 0;

        // 이미지 저장
        if (!images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                if (file.isEmpty()) continue;

                // 고유 파일명 생성
                String rename = System.currentTimeMillis() + "_" + i;

                // 실제 파일 저장
                file.transferTo(new File(folderPath + "/" + rename));

                BoardImg img = BoardImg.builder()
                    .imgPath(webPath)
                    .imgOriginalName(file.getOriginalFilename())
                    .imgRename(rename)
                    .imgOrder(i)
                    .boardNo(inputBoard.getBoardNo())
                    .build();

                mapper.insertBoardImage(img);
            }
        }

    	
    	//int boardNo = inputBoard.getBoardNo();
        //return mapper.boardInsert(inputBoard);
    	
    	return inputBoard.getBoardNo();
    	 
    }

    // 게시글 수정
    @Override
    public int boardUpdate(Board inputBoard, List<MultipartFile> images) throws Exception {
    	
    	
    	// 게시글 이미지 수정 
    	int result = mapper.boardUpdate(inputBoard);
    	
    	if(result == 0) return 0;

    	   // 기존 이미지 삭제
        mapper.deleteBoardImages(inputBoard.getBoardNo());
        

        if (!images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                if (file.isEmpty()) continue;

                String rename = System.currentTimeMillis() + "_" + i;
                file.transferTo(new File(folderPath + "/" + rename));

                BoardImg img = BoardImg.builder()
                    .imgPath(webPath)
                    .imgOriginalName(file.getOriginalFilename())
                    .imgRename(rename)
                    .imgOrder(i)
                    .boardNo(inputBoard.getBoardNo())
                    .build();

                mapper.insertBoardImage(img);
             }
    	
       
    }
        return result;
        
    }

    // 게시글 삭제
    @Override
    public int boardDelete(Map<String, Integer> map) {
        return mapper.boardDelete(map);
    }
}
