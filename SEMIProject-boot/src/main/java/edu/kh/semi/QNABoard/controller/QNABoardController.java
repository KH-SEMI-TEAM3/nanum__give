package edu.kh.semi.QNABoard.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.kh.semi.QNABoard.model.servcie.QNABoardService;
import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.member.model.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
@SessionAttributes({"loginMember"})
@Controller
@Slf4j
@RequestMapping("help")
public class QNABoardController {
	
	
	@Autowired
	private QNABoardService service;
	
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
	public String selectBoardList(HttpSession session, @RequestParam(value = "cp", required = false, defaultValue = "1") int cp, Model model
	/* ====================== 추가적으로 키와 쿼리를 얻어옴 ====================== */
	,@RequestParam Map<String, Object> paraMap)
	/* === paramMap안에는{"query" = "짱구", "key"="tc"} 와 같이 검색어 자체와 검색 종류가 들어감 === */

	{

		Member loginMember = (Member) session.getAttribute("loginMember");
		
		log.info("QRNA용 selectBoardList, loginMember = {}", session.getAttribute("loginMember"));

		
		if(loginMember!=null) {
			model.addAttribute("loginMember",loginMember);
		}
		
		int boardCode =4;
	    log.info("[GET] /board/{}", boardCode);
	    log.debug(" 현재 페이지(cp): {}", cp);
	    log.debug(" 파라미터(paraMap): {}", paraMap);
	    
		// 조회 서비스 호출 후 결과를 맵으로 반환

		Map<String, Object> map = null;
		
		
		/* ====================== 검색이 아닐 때  ====================== */

		
		/* ========= 검색이 아니라면 paramMap은 {}라는 빈 맵 상태 ================ */

		if(paraMap.get("key") == null){
			log.info("검색이 아닌 그냥 게시글 목록 조회 요청");
			

			/* 조건에 따라 어떤 서비스의 메서드를 호출할지 가름.
			 다만 반환되는 것을 Map으로

			 맨 밑에서 하는 검색인 경우와 검색이 아닌 경우를 따진다
			 board ?key=t & query = 1930; => key는 검색어에 해당하며 t또는 c 또는 tc 또는 w로 key가 설정될 수
			 있다

			 검색 역시 게시판의 목록 조회와 똑같으므로 맵으로 넘어온다

			 게시글 목록 조회 서비스 호출하기*/

			map = service.selectQNABoardList(boardCode, cp);
			// 어떤 게시판 종류인지, 어떤 페이지를 요청했는지가 인자로 들어감
			
			
		}
		
		
		
		
		else {
			
			/* ====================== 검색일 때 ====================== */
			
			log.info("검색 기반 게시글 목록 요청");

			// 검색이 아닐 때는 서비스단으로 넘겨줄 때 boardCode, cp만 넘겨줬었음 paramMap까지 넘겨줘야 하니까 애초에 paramMap에 boardCode를 넣어버려
			
			// boardCode를 paramMap에 추가
			paraMap.put("boardCode", boardCode);
			//paraMap =  {"query"="짱구", "key"="tc", "boardCode"=1 }
			
			// cp는 따로 보내도 된다. 페이지네이션은 유지되어야 하기때문
			// cp로 검색서비스에서 페이지네이션을 만든다.
			
			// 검색 서비스 호출
			log.debug(" 검색 결과 목록: {}", map);
			map = service.searchQNAList(paraMap,cp);
			//selectBoardList(boardCode, cp);가 아니라
			//searchList(paraMap,cp)
			
			
			
			
		}

		
		 log.info("QRNA용 selectBoardList, loginMember = {}", session.getAttribute("loginMember"));

		
		

		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));

		return "board/help/help-list";
		// src/main/resources/templates/board/help-list.html
	}
	
	
	
	
	
	
	 
}
