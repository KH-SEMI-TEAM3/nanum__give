package edu.kh.semi.common.images;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.semi.board.model.dto.BoardImg;
import edu.kh.semi.common.images.model.service.BoardImageService;
import edu.kh.semi.common.util.Utility;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/image")
public class BoardImageController {

	@Autowired
	private BoardImageService imageService;
	
	@PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> uploadImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("boardNo") int boardNo,
            HttpSession session) throws IOException {

        Map<String, Object> response = new HashMap<>();

        if (image.isEmpty()) {
            response.put("result", false);
            response.put("message", "이미지 없음");
            return response;
        }

        // 저장 경로 및 이름 생성
        String rename = Utility.fileRename(image.getOriginalFilename());
        String webPath = "/images/board/";
        String filePath = session.getServletContext().getRealPath(webPath);

        File dest = new File(filePath, rename);
        image.transferTo(dest);

        // DB 저장용 객체 생성
        BoardImg img = new BoardImg();
        img.setBoardNo(boardNo);
        img.setImgOriginalName(image.getOriginalFilename());
        img.setImgRename(rename);
        img.setImgPath(webPath);

        imageService.save(img);

        response.put("result", true);
        response.put("path", webPath + rename);
        return response;
    }
	
}
