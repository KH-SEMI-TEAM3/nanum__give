package edu.kh.semi.board.controller;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.board.model.dto.ShareBoard;
import edu.kh.semi.board.model.service.ShareBoardEditService;
import edu.kh.semi.board.model.service.ShareBoardService;
import edu.kh.semi.common.util.Utility;
import edu.kh.semi.member.model.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("shareEdit")
@PropertySource("classpath:/config.properties")
public class ShareBoardEditController {


	@Value("${my.board.web-path}")
	private String webPath;
	@Value("${my.board.folder-path}")
	private String folderPath;
	
	@Autowired
	private ShareBoardEditService editService;
    @Autowired
    private ShareBoardService service;

	@GetMapping("insert")
	public String insertBoard(
			@SessionAttribute(value = "loginMember", required = false) Member loginMember,
			Model model) {
		if(loginMember == null) {
			return "redirect:/member/loginPage";
		}
		return "/board/share/shareWrite";
	}

    /** 게시글 작성 
	 * @param boardCode : 어떤 게시판에 작성할 글인지 구분 (1/2/3..)
	 * @param inputBoard : 입력된 값(제목, 내용) 세팅되어있음 (커맨드 객체)
	 * @param loginMember : 로그인한 회원 번호를 얻어오는 용도(세션에 등록되어있음)
	 * @param images : 제출된 file 타입 input태그가 전달한 데이터들 (이미지 파일..)
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달
	 * @return
	 * @throws Exception 
     * @author 원기찬
	 */
	@PostMapping("insert")
	public String boardInsert(
                        @ModelAttribute ShareBoard inputBoard, 
                        @SessionAttribute("loginMember") Member loginMember,
                        RedirectAttributes ra ) throws Exception {
		
		int boardCode=1;

		inputBoard.setBoardCode(boardCode);
		inputBoard.setMemberNo(loginMember.getMemberNo());
        
        // 디버깅을 위한 로그 추가
        System.out.println("Received ShareBoard: " + inputBoard);
        System.out.println("Category Detail Code: " + inputBoard.getShareBoardCategoryDetailCode());
        
        // 카테고리 코드가 0이면 기본값 설정
        if(inputBoard.getShareBoardCategoryDetailCode() == 0) {
            inputBoard.setShareBoardCategoryDetailCode(6); // 기타로 설정
        }

		int boardNo = editService.boardInsert(inputBoard);
				
		String path = null;
		String message = null;
		
		if(boardNo > 0) {
			path = "/share/detail/" + boardNo; 
			message = "게시글이 작성 되었습니다!";
		} else {
			path = "insert";     //    /shareEdit/insert  
			message = "게시글 작성 실패";
		}
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}

	/** 이미지 서버에 업로드
	 * @param imageFile
	 * @param request
	 * @return
	 * @throws IOException
	 * @author 원기찬
	 */
	@PostMapping("UploadImage")
	@ResponseBody
	public String uploadImage(
                @RequestParam("image") MultipartFile imageFile, 
                HttpServletRequest request)
			throws IOException {

		File dir = new File(folderPath); // 파일 저장할 디렉토리 객체 생성
		if (!dir.exists())
			dir.mkdirs(); // 폴드 없으면 생성

		// 저장할 파일명을 UUID로 생성하여 중복 방지 + 원래 파일 이름을 붙임
		String fileName = Utility.fileRename(imageFile.getOriginalFilename());

		// 최종적으로 저장할 파일 경로 객체 생성
		File dest = new File(folderPath, fileName);

		// 업로드된 이미지 파일을 지정한 경로로 저장
		imageFile.transferTo(dest);

		// 클라이언트(브라우저)에 돌려줄 이미지 URL 경로 반환
		return webPath + fileName;
	}
	

	/** 게시글 수정 화면 전환
	 * @param boardCode : 게시판 종류
	 * @param boardNo   : 게시글 번호
	 * @param loginMember : 로그인한 회원이 작성한 글이 맞는지 검사하는 용도
	 * @param model : 포워드 시     request scope로 값 전달하는 용도
	 * @param ra    : 리다이렉트 시 request scope로 값 전달하는 용도
	 * @return
     * @author 원기찬
	 */
	@GetMapping("detail/{boardNo:[0-9]+}/update")
	public String boardUpdate(
							@PathVariable("boardNo") int boardNo,
							@SessionAttribute("loginMember") Member loginMember,
							Model model,
							RedirectAttributes ra) {
		int boardCode = 1;
		// 수정 화면에 출력할 기존의 제목/내용/이미지 조회
		// -> 게시글 상세 조회
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		// selectOne(map) 호출
		ShareBoard board = service.selectOne(map);
		System.out.println(board);
		String message = null;
		String path = null;
		
		
		if(board == null) {
			message = "해당 게시글이 존재하지 않습니다";
			path = "redirect:/";  // 메인페이지로 리다이렉트
			
			ra.addFlashAttribute("message", message);
		
		} else if(board.getMemberNo() != loginMember.getMemberNo()) {
			message = "자신이 작성한 글만 수정할 수 있습니다!";
			path = String.format("redirect:/board/%d/%d", boardCode, boardNo);
			ra.addFlashAttribute("message", message);
		} else {
			
			path = "board/share/shareWrite";
			model.addAttribute("board", board);
		}
		
		return path;
	}
	
	
	
	/** 게시글 수정
	 * @param boardCode        : 게시판 종류
	 * @param boardNo          : 수정할 게시글 번호
	 * @param inputBoard       : 커맨드 객체(제목, 내용)
	 * @param loginMember      : 로그인한 회원 번호 이용 (로그인 == 작성자)
	 * @param images           : 제출된 input type="file"  모든 요소
	 * @param ra               : redirect 시 request scope로 값 전달
	 * @param deleteOrderList  : 삭제된 이미지 순서가 기록된 문자열 (1,2,3)
	 * @param cp      		   : 수정 성공 시 이전 파라미터 유지
	 * @return
	 */
	@PostMapping("detail/{boardNo:[0-9]+}/update")
	public String boardUpdate(
					@PathVariable("boardNo") int boardNo,
					@ModelAttribute ShareBoard inputBoard,
					@SessionAttribute("loginMember") Member loginMember,
					RedirectAttributes ra,
					@RequestParam(value="cp", required = false, defaultValue = "1") int cp		
			) throws Exception {
		int boardCode = 1;

		// 1. 커맨드 객체(inputBoard)에 boardCode, boardNo, memberNo 세팅
		inputBoard.setBoardCode(boardCode);
		inputBoard.setBoardNo(boardNo);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		// inputBoard -> (제목, 내용, boardCode, boardNo, memberNo)
		
		// 2. 게시글 수정 서비스 호출 후 결과 반환 받기
		int result = editService.boardUpdate(inputBoard);
		
		// 3. 서비스 결과에 따라 응답 제어
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "게시글이 수정 되었습니다";
			path = String.format("/share/detail/%d?cp=%d", boardNo, cp);			
		} else {
			message = "수정 실패";
			path = "update";   // GET (수정 화면 전환) 리다이렉트하는 상대경로
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
	
	
	/** 게시글 삭제
	 * @param boardCode     : 게시판 종류 번호
	 * @param boardNo  		: 게시글 번호
	 * @param cp			: 삭제 시 게시글 목록으로 리다이렉트 할 때 사용할 페이지 번호
	 * @param loginMember   : 현재 로그인한 회원 번호 사용 예정
	 * @param ra			: 리다이렉트 시 request scope로 값 전달용 
	 * @return
	 */
	@GetMapping("detail/{boardNo:[0-9]+}/delete")
	public String boardDelete(
			@PathVariable("boardNo") int boardNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			RedirectAttributes ra,
			@SessionAttribute("loginMember") Member loginMember) {
		int boardCode = 1;
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		map.put("memberNo", loginMember.getMemberNo());
		
		int result = editService.boardDelete(map);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			path = String.format("/share/list?cp=%d", cp);
								// /board/1?cp=7
			message = "삭제 되었습니다!";
			
		} else {
//			path = String.format("/board/%d/%d?cp=%d", boardCode, boardNo, cp);
			path = String.format("/share/detail/%d?", boardNo);
								// /board/1/1997?cp=7
			message = "삭제 실패";
		}
		
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:" + path;
	}
}