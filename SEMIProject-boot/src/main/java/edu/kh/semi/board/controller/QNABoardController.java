package edu.kh.semi.board.controller;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.kh.semi.admin.controller.AdminController;
import edu.kh.semi.board.model.dto.Board;
import edu.kh.semi.board.model.dto.BoardImg;
import edu.kh.semi.board.model.dto.QNABoard;
import edu.kh.semi.board.model.service.QNABoardService;
import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@SessionAttributes({ "loginMember" })
@Controller
@Slf4j
@RequestMapping("help")
public class QNABoardController {

    private final AdminController adminController;

	@Value("${my.board.web-path}")
	private String boardWebPath;

	@Value("${my.board.folder-path}")
	private String boardFolderPath;

	@Autowired
	private QNABoardService service;

    QNABoardController(AdminController adminController) {
        this.adminController = adminController;
    }

	/*
	 * 
	 * @param cp: 현재 조회를 요청한 페이지 번호를 가짐 (없으면 1페이지를 요청한 것과 같다!!)
	 * 
	 * /board/1 /board/2 /board/3 요청 주소에서 쿼리스트링이 아닌 하위 주소를 주소상 변수로서 사용할 수 있는 방법
	 * pathVariable로 매핑한다 이는 정규식이 들어간다
	 * 
	 * 정규식을 이용했기에 /boadrd이하 1레벨 자리에 숫자로 된 요청 주소가 작성되어있을 때에 한해서 이 메서드로 매핑한다
	 */

	// @PathVariable은 "boardCode"에 대한 값을 requestScope에 실어준다 + 매핑까지 해준다.
	@GetMapping("/list")
	public String selectBoardList(HttpSession session,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model
			/* ====================== 추가적으로 키와 쿼리를 얻어옴 ====================== */
			, @RequestParam Map<String, Object> paraMap)
	/* === paramMap안에는{"query" = "짱구", "key"="tc"} 와 같이 검색어 자체와 검색 종류가 들어감 === */

	{

		

		Member loginMember = (Member) session.getAttribute("loginMember");

		log.info("QRNA용 selectBoardList, loginMember = {}", session.getAttribute("loginMember"));

		if (loginMember != null) {
			model.addAttribute("loginMember", loginMember);
		}

		int boardCode = 4;
		log.debug("paraMap.get(\"key\") = '{}'", paraMap.get("key"));
		log.debug("paraMap 전체 내용:{}", paraMap);
		
		log.info("[GET] /board/{}", boardCode);
		log.debug(" 현재 페이지(cp): {}", cp);
		log.debug(" 파라미터(paraMap): {}", paraMap);

		// 조회 서비스 호출 후 결과를 맵으로 반환

		Map<String, Object> map = null;

		/* ====================== 검색이 아닐 때 ====================== */

		/* ========= 검색이 아니라면 paramMap은 {}라는 빈 맵 상태 ================ */

		if (paraMap.get("key") == null) {
			log.info("검색이 아닌 그냥 게시글 목록 조회 요청");

			/*
			 * 조건에 따라 어떤 서비스의 메서드를 호출할지 가름. 다만 반환되는 것을 Map으로
			 * 
			 * 맨 밑에서 하는 검색인 경우와 검색이 아닌 경우를 따진다 board ?key=t & query = 1930; => key는 검색어에
			 * 해당하며 t또는 c 또는 tc 또는 w로 key가 설정될 수 있다
			 * 
			 * 검색 역시 게시판의 목록 조회와 똑같으므로 맵으로 넘어온다
			 * 
			 * 게시글 목록 조회 서비스 호출하기
			 */

			map = service.selectQNABoardList(boardCode, cp);
			// 어떤 게시판 종류인지, 어떤 페이지를 요청했는지가 인자로 들어감

			log.debug("map의 내용 { }", map);

		}

		else {

			/* ====================== 검색일 때 ====================== */

			log.info("검색 기반 게시글 목록 요청!");

			// 검색이 아닐 때는 서비스단으로 넘겨줄 때 boardCode, cp만 넘겨줬었음 paramMap까지 넘겨줘야 하니까 애초에 paramMap에
			// boardCode를 넣어버려

			// boardCode를 paramMap에 추가
			paraMap.put("boardCode", boardCode);
			// paraMap = {"query"="짱구", "key"="tc", "boardCode"=1 }

			// cp는 따로 보내도 된다. 페이지네이션은 유지되어야 하기때문
			// cp로 검색서비스에서 페이지네이션을 만든다.

			// 검색 서비스 호출
			log.debug(" 검색 결과 목록: {}", map);
			map = service.searchQNAList(paraMap, cp);
			// selectBoardList(boardCode, cp);가 아니라
			// searchList(paraMap,cp)

		}

		log.info("QRNA용 selectBoardList, loginMember = {}", session.getAttribute("loginMember"));

		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));

		return "board/help/help-list";
		// src/main/resources/templates/board/help-list.html
	}
	
	
	

	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}")
	public String boardDetail(@PathVariable("boardNo") int boardNo, Model model,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes ra,
			HttpServletRequest req /* 쿠키 얻어오려고 */
			, HttpServletResponse resp /* 새로운 쿠키를 구워 클라이언트로 보낼 때 */) {
		int boardCode = 4;
		// 게시글 상세 조회 서비스 호출

		/*
		 * SQL문까지 boardCode boardNo를 전달해야 하나만 저장할 수 있다. 따라서 맵으로 묶으면 된다
		 */

		Map<String, Integer> map = new HashMap<>();

		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);

		// 로그인 상태일 때 한정으로만 memberNo를 꺼내서 추가하자

		if (loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}

		QNABoard QNAboard = service.selectOne(map); // 게시글 상세조회를 하려는데 board를 받아야해?
		// 한 행의 데이터가 보드로 담겨야하기 때문

		String path = null;

		if (QNAboard == null) {

			path = "redirect:/help/help-list";
			// 해당 게시판의 목록으로 재요청 가령 자유게시판 목록으로
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다!");
			return path;

		}

		else {

			/* ====================== 조회수 증가 처리 (쿠키 기반) ====================== */

			// 로그인하지 않았거나, 로그인했더라도 작성자가 아닌 경우만 조회수 증가
			if (loginMember == null || loginMember.getMemberNo() != QNAboard.getMemberNo()) {

				// 요청에 포함된 모든 쿠키 가져오기
				Cookie[] cookies = req.getCookies();

				// 게시글 조회 여부를 기록하는 쿠키 변수
				Cookie c = null;

				// 쿠키 배열에서 "readBoardNo"라는 이름의 쿠키를 찾기
				if (cookies != null) {
					for (Cookie temp : cookies) {
						if (temp.getName().equals("readBoardNo")) {
							c = temp;
							break; // 찾으면 더 이상 순회하지 않음
						}
					}
				}

				int result = 0; // 실제로 DB에 조회수를 반영한 결과

				// "readBoardNo" 쿠키가 없는 경우 = 처음 읽는 글
				if (c == null) {
					// 쿠키를 새로 생성하고, 현재 게시글 번호만 기록
					c = new Cookie("readBoardNo", "[" + boardNo + "]");

					// 조회수 1 증가
					result = service.updateReadCount(boardNo);

				} else {
					// "readBoardNo" 쿠키는 있지만, 현재 글 번호가 없으면?
					if (c.getValue().indexOf("[" + boardNo + "]") == -1) {
						// 이전에 읽지 않았던 글 → 쿠키에 번호 추가하고 조회수 증가

						/*
						 * indexOf(): 쿠키 값에는 [2][5][30] 처럼 이미 조회한 글 번호들이 문자열로 저장되어 있음 : 이 중에 현재 글
						 * 번호([boardNo])가 포함되어 있는지 검사 indexOf("[1982]") == -1 => 포함되어 있지 않음 => 처음 보는 글임
						 * indexOf(...) >= 0 => 이미 본 글 => 조회수 증가하지 않음
						 */
						c.setValue(c.getValue() + "[" + boardNo + "]");
						result = service.updateReadCount(boardNo);
					}
				}

				// 이후 result > 0일 경우 → 실제 쿠키 설정 및 클라이언트로 전송 등 수행

				// 조회수 증가 처리 후, board 객체의 조회수 필드도 업데이트

				if (result > 0) {

					/* ---------- 조회수 반영 ---------- */
					// DB에서 READ_COUNT를 1 증가시켰으므로,
					// 메모리에 이미 올라와 있는 board 객체에도 동일한 값을 반영해 준다.
					QNAboard.setReadCount(result);

					/* ---------- 쿠키 설정 ---------- */
					// (1) 쿠키 전송 범위 : 사이트 전역 “/”
					// → 이후 모든 요청에 이 쿠키가 자동 포함되어 다시 조회수 중복 증가를 막음
					c.setPath("/");

					// (2) 쿠키 만료 시각 : **내일 자정(00:00)**
					// → 같은 날에는 조회수가 한 번만 올라가고,
					// 자정이 지나면 쿠키가 사라져 새로운 날에 다시 1회 증가 가능
					LocalDateTime now = LocalDateTime.now();

					// 다음날 자정까지를 쿠키의 만료 시간으로 지정하자

					LocalDateTime nextDatMidNigh = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
					// 다음날 자정 0시 0분 0초

					// 현재시간부터 다음날 자정까지의 남은 시간을 계산하여 초단위로 표현

					// duration.between

					long seconds = Duration.between(now, nextDatMidNigh).getSeconds(); // 차이를 초로 환산

					// 쿠키수명 설정 시 넣어 주자

					c.setMaxAge((int) seconds);

					resp.addCookie(c);
					// 응답객체가 이미 있으므로 바로 클라이언트에 쿠키 전달 가능
 
				}
 
			} 
            
			/*
			 * 쿠키를 이용한 조회수 증가 끝
			 */
  
			path = "board/help/help-detail";

			String content = QNAboard.getBoardContent();
			content = content.replaceAll("\\[img:(.+?)\\]", "<img src=\"$1\"/>");
			QNAboard.setBoardContent(content);
			// 게시글의 일반 내용과 imageList + commentList
			model.addAttribute("board", QNAboard);
			log.info("boardDetail() -> service.selectOne() 결과: {}", QNAboard);

			model.addAttribute("loginMember", loginMember);

			// 조회된 이미지 목록이 있을 경우
    
			if (!QNAboard.getImageList().isEmpty()) {
				BoardImg thumbnail = null;
				// imageList의 0번 인덱스 == 가장 빠른 순서.
				// 실제 DB에서 IMG_ORDER가 작은것부터 나오게 됨
				// 만약 이미지 목록의 첫번째 행의 image order가 0인경우라면 썸네일이라는 뜻

				if (QNAboard.getImageList().get(0).getImgOrder() == 0) {
					// 썸네일의 조건

					thumbnail = QNAboard.getImageList().get(0);
				}

				model.addAttribute("thumbnail", thumbnail);
				model.addAttribute("start", thumbnail != null ? 1 : 0);
				// start라는 값은 썸네일이 있다면 1을 저장, 없으면 0을 저장
			}
			log.info("최종 model에 담긴 board: {}", model.getAttribute("board"));

			return path;
		}

	}

	// -------------------------이상은 리스트, 상세조회 /// 이하는 삽입, 수정,
	// 삭제----------------------------------------------------//

	/**
	 * 작성 페이지로 이동
	 * 
	 * @param boardCode
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/insert")
	public String BoardInsert(@PathVariable("boardCode") int boardCode) {

		boardCode = 4;
		return "board/help/help-write";
	}

	/**
	 * 게시글 작성
	 * 
	 * @param boardCode   어떤 게시판에 작성될 글일지 구분
	 * @param intputBoard 입력된 값들 중 제목과 내용만 세팅되어 있음. 즉 커멘드 객체 상태
	 * @param loginMember 현재 로그인한 회원의 번호를 얻어오는 용도
	 * @param images      제출된 파일타입의 input태그가 전달하는 중인 데이터
	 * @param ra
	 * @return
	 */
	@PostMapping("{boardCode:[0-9]+}/insert")
	public String BoardInsert(@PathVariable("boardCode") int boardCode, @ModelAttribute QNABoard inputBoard,
			@SessionAttribute("loginMember") Member loginMember, @RequestParam("images") List<MultipartFile> images,
			RedirectAttributes ra) throws Exception {

		// 1) boardDTO에는 memberNo boardCode의 필드가 있으니 @ModelAttribute Board intputBoard에
		// 다 넣어버리지?
		//
		inputBoard.setBoardCode(boardCode);
		inputBoard.setMemberNo((loginMember.getMemberNo()));
		// inputBoard는 총 네 가지 필드를 가지게 된다. boardTitle, boardContent, boardCode, member

		// 2) 서비스 메서드 호출 후 결과 반환 페이지? 성공 시에는 상세 조회를 요청할 수 있도록
		// 현재 삽입된 게시글의 번호를 반환받기

		int boardNo = service.boardInsert(inputBoard, images);

		// 3. 서비스 결과에 따른 메시지 작성 및 리다이렉트 경로 지정

		String path = null;
		String message = null;

		if (boardNo > 0) {
			message = "게시글이 잘 작성되었습니다";
			// 게시글 잘 작성
			path = "/help/" + boardCode + "/" + boardNo;
		}

		else {
			path = "/help" + boardCode + "insert";
			// editBoard/1/insert
			message = "게시글 작성 실패";
		}

		ra.addFlashAttribute("message", message);
		return "redirect:" + path;

	}

	@PostMapping("/uploadImage")
	@ResponseBody
	public String uploadImage(@RequestParam("image") MultipartFile image) throws Exception {

		// 1. 저장할 폴더 확인
		File dir = new File(boardFolderPath);
		if (!dir.exists())
			dir.mkdirs();

		// 2. 파일명 생성
		String originalName = image.getOriginalFilename();
		// 확장자 분리
		String ext = originalName.substring(originalName.lastIndexOf("."));

		// UUID 16자리 + 확장자
		String rename = UUID.randomUUID().toString().replace("-", "").substring(0, 16) + ext;
		// 3. 파일 저장
		File dest = new File(boardFolderPath, rename);
		image.transferTo(dest);

		// 4. 반환할 웹의 경로와 삽입할 대상
		return boardWebPath + rename;
	}
 
	/**
	 * 게시글 삭제 로직
	 * 
	 * @param boardNo
	 * @param cp
	 * @param ra
	 * @param logMember
	 * @return
	 */
	@RequestMapping(value = "{boardCode:[0-9]+}/{boardNo:[0-9]+}/delete", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String boardDelete(@PathVariable("boardNo") int boardNo,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp, RedirectAttributes ra,
			@SessionAttribute("loginMember") Member logMember) {

		Map<String, Integer> map = new HashMap<>();
		// 전부 value에 해당하는 실제 값이 int형이어서
		int boardCode = 4;
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		map.put("memberNo", logMember.getMemberNo());

		int result = service.boardDelete(map);

		log.info("코드는 4로 고정인데! boardCode = {}", boardCode);
		log.info("몇번글? boardNo {}", boardNo);
		log.info("회원번호는? memberNo {}", logMember.getMemberNo());

		String path = null;
		String message = null;

		if (result > 0) {
			message = "삭제되었습니다";
			path = String.format("/help/list?cp=%d", cp);
			// /help/1?cp=7
		}

		else {

			message = "삭제 실패!";
			path = String.format("help/list?cp=%d", cp);
			// /help/1/2000?cp=7

		}

		ra.addFlashAttribute("message", message);

		return "redirect:" + path;
	}

	/**
	 * 게시글 디테일에서 수정화면으로 전환하는 컨트롤러 메서드
	 * 
	 * @param boardCode: 게시판 종류 번호
	 * @param boardNo:   게시글 번호
	 * @param logMember: 현재 로그인한 회원 객체 => 본인이 아니면 리다이렉트
	 * @param model
	 * @param ra
	 * @return
	 */
	@GetMapping("/4/{boardNo:[0-9]+}/update")
	public String boardUpdateForm(@PathVariable("boardNo") int boardNo,
			@SessionAttribute("loginMember") Member logMember, Model model, RedirectAttributes ra,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp) {

		// 수정 화면에 출력할 제목 내용 이미지까지 조회
		// 게시글 상세조회를 컨트롤러 서비스 매퍼 다 만들어뒀을 듯
		// selectOne() => 재활용 (제목 내용 이미지리스트 댓글리스트)
 
		Map<String, Integer> map = new HashMap<>();

		map.put("boardCode", 4);
		map.put("boardNo", boardNo);

		// BoardSerice.selectOne(map)호출
		// QNABoard가 반환 될듯

		QNABoard board = service.selectOne(map);
  
		log.info("일단 수정 이동 페이지로 들어옴");
		String message = null;
		String path = null;
 
		if (board == null) {
			message = "해당 게시글이 존재하지 않습니다!";
			// 저러한 boardCode에 저런 boardNo가 없다
			path = "redirect:board/help/list"; // 메인 페이지로 리다이렉트
			ra.addFlashAttribute("message", message);
			// 리턴할 때 이렇게 안 하고 여기서 처리하는 이유는
			// redirect와 get이 모두 존재하기 때문
		}

		else if (board.getMemberNo() != logMember.getMemberNo()) {
			message = "자신이 작성한 글만 수정 가능합니다!";
			path = String.format("redirect:/board/help/4/%d?cp=%d", boardNo, cp); // 상세조회 페이지로 리다이렉트
			ra.addFlashAttribute("message", message);
		}

		else {
			path = "board/help/help-update";
			// src/main/resources/templates/help/help-update.html로 forwarding

			model.addAttribute("board", board);
		}

		return path;
	}

	/**
	 * 실제 수정을 적용하는 메서드
	 * 
	 * @param boardNo
	 * @param logMember
	 * @param model
	 * @param ra
	 * @param cp
	 * @return
	 */
	@PostMapping("/4/{boardNo:[0-9]+}/update")
	public String boardUpdate(@PathVariable("boardNo") int boardNo, QNABoard qnaBoard,
			@SessionAttribute("loginMember") Member logMember, Model model, RedirectAttributes ra,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@SessionAttribute("loginMember") Member loginMember) {

		log.info("컨트롤러까지 들어옴");
		int boardCode = 4;

		// 1. 커맨드 객체(inputBoard)에 boardCode, boardNo, memberNo 세팅
		qnaBoard.setBoardCode(boardCode);
		qnaBoard.setBoardNo(boardNo);
		qnaBoard.setMemberNo(loginMember.getMemberNo());
		// inputBoard -> (제목, 내용, boardCode, boardNo, memberNo)

		// 2. 게시글 수정 서비스 호출 후 결과 반환 받기
		int result = service.boardUpdate(qnaBoard);

		// 3. 서비스 결과에 따라 응답 제어
		String message = null;
		String path = null;

		if (result > 0) {
			message = "게시글이 수정 되었습니다";
			path = String.format("/help/%d/%d?cp=%d", boardCode, boardNo, cp);
		} else {
			message = "수정 실패";
			path = "update"; // GET (수정 화면 전환) 리다이렉트하는 상대경로
		}

		ra.addFlashAttribute("message", message);

		return "redirect:" + path;
	}



	// 굳이 비동기로 해야겠다면

	/** 비동기식 문의상태 바꾸기
	 * @param paramMap
	 * @param loginMember
	 * @return
	 */
	@PostMapping("updateCompletion")
	@ResponseBody
	public int updateCompletionStatus(@RequestBody Map<String, Object> paramMap,
			@SessionAttribute(value = "loginMember", required = false) Member loginMember) {
		if (loginMember == null)
			return 0;

		return service.updateCompletion(paramMap);
	}
	
	
	
	
	@GetMapping("qaSearch")
	public String qaSearch(@RequestParam (value = "qaStatus", required = false) String qaStatus, 	
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,   
			Model model) {

		// 조회 서비스 호출 후 결과를 맵으로 반환adminController
		int boardCode=4;
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("qaStatus", qaStatus);
		map.put("cp", cp);
		map.put("boardCode", boardCode);



		/* ====================== 검색이 아닐 때 ====================== */

			// cp는 따로 보내도 된다. 페이지네이션은 유지되어야 하기때문
			// cp로 검색서비스에서 페이지네이션을 만든다.

			// 검색 서비스 호출
			map = service.qaSearch(map, cp);


		


		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));

		return "board/help/help-list";
		// src/main/resources/templates/board/help-list.html
	}
	
	
	

}
