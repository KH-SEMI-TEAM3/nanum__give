package edu.kh.semi.board.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.board.model.dto.ShareBoard;
import edu.kh.semi.board.model.service.ShareBoardService;
import edu.kh.semi.member.model.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("share")
public class ShareBoardController {

	@Autowired
	private ShareBoardService service;

	@GetMapping("list")
	public String selectBoardList(
			Model model,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@RequestParam Map<String, Object> paramMap,
			HttpServletRequest req ) {
		/* --------------- 세션 삽입 --------------
		Member loginMember = Member.builder()
				.memberNo(5)
				.memberId("user04")
	            .memberNickname("사용자4")
	            .memberEmail("user04@example.com")
	            .memberDelFl("N")
	            .build();
		req.getSession().setAttribute("loginMember", loginMember);
		 */

		Map<String, Object> map = null;
		int boardCode=1;
		if (paramMap.get("key") == null) {
			map = service.selectBoardList(boardCode, cp);

//		} else {	// 검색인 경우
//			paramMap.put("boardCode", boardCode);
//			map = service.searchList(paramMap, cp);
		}

		
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
//		System.out.println("boardList = " + map.get("boardList"));	// 목록 조회 확인
		
		return "/board/share/shareList";
	}


	@GetMapping("detail/{boardNo:[0-9]+}")
	public String defaultBoardDetail(
			@PathVariable("boardNo") int boardNo,
			Model model, 
			@SessionAttribute(value = "loginMember", required = false) Member loginMember,
			RedirectAttributes ra, 
			HttpServletRequest req,
			HttpServletResponse resp) {
		int boardCode=1;


		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);

		if (loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}

		ShareBoard board = service.selectOne(map);
//		System.out.println(board);		// 목록 상세 조회 확인
		String path = null;
		if(board == null) {
			path = "redirect:/share/list"; // 목록 재요청
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다");
		
		} else {
/* --------------- 쿠키를 이용한 조회 수 증가 -------------------------*/
			// 비회원 또는 로그인한 회원의 글이 아닌 경우 ( == 글쓴이를 뺀 다른 사람)
			if(loginMember == null || 
					loginMember.getMemberNo() != board.getMemberNo()) {
				
				
				// 요청에 담겨있는 모든 쿠키 얻어오기
				Cookie[] cookies = req.getCookies();
				Cookie c = null;
				for(Cookie temp : cookies) {
					// 요청에 담긴 쿠키에 "readBoardNo" 가 존재 할 때
					if(temp.getName().equals("readBoardNo")) {
						c = temp;
						break;
					}
				}
				int result = 0; // 조회수 증가 결과를 저장할 변수
				// "readBoardNo"가 쿠키에 없을 때
				if(c == null) {
					// 새 쿠키 생성("readBoardNo", [게시글번호])
					c = new Cookie("readBoardNo", "["+  boardNo + "]");
					result = service.updateReadCount(boardNo);
				} else {
				// 현재 글을 처음 읽는 경우
					if(c.getValue().indexOf("["+  boardNo + "]") == -1) {
						// 해당 글 번호를 쿠키에 누적 + 서비스 호출
						c.setValue(c.getValue() + "["+  boardNo + "]");
						// [2][30][400][2000][4000][2003]
						result = service.updateReadCount(boardNo);
					}
				}
				// 조회 수 증가 성공 / 조회 성공 시
				if(result > 0) {
					// 먼저 조회된 board의 readCount 값을
					// result 값을 다시 세팅
					board.setReadCount(result);
					// 쿠키 적용 경로 설정
					c.setPath("/");   // "/" 이하 경로 요청 시 쿠키 서버로 전달
					// 쿠키 수명 지정
					// 현재 시간을 얻어오기
					LocalDateTime now = LocalDateTime.now();
					// 다음날 자정  지정
					LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
					// 다음날 자정까지 남은 시간 계산 (초 단위)
					long secondsUntilNextDay = Duration.between(now, nextDayMidnight).getSeconds();
					// 쿠키 수명 설정
					c.setMaxAge((int)secondsUntilNextDay);
					resp.addCookie(c); // 응답 객체를 이용해서 클라이언트에게 전달
				}
			}
/* --------------- 쿠키를 이용한 조회 수 증가 끝 -------------------------*/
			// 조회 결과가 있는 경우
			path = "board/share/shareDetail"; // shareDetail.html 로  forward
			
			// board - 게시글 일반 내용 + imageList + commentList
			model.addAttribute("board", board);
		}	
		return path;
	}
	
	@ResponseBody 
	@PostMapping("jjim")
	public int boardLike(@RequestBody Map<String, Integer> map) {
		return service.boardJJim(map);
	}

}