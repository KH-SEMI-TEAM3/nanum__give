package edu.kh.semi.message.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.semi.board.model.dto.Pagination;
import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.member.model.service.MemberService;
import edu.kh.semi.message.model.dto.Message;
import edu.kh.semi.message.model.service.MessageService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/message")
@Slf4j
public class MessageController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MessageService messageService;


    
    @GetMapping("/inbox")
    public String  inbox(
            HttpSession session,
            @RequestParam(value ="cp", required = false, defaultValue = "1") int cp,
            Model model) {
    	   Member loginMember = (Member) session.getAttribute("loginMember");

           if (loginMember == null) {
               model.addAttribute("errorMessage ", "로그인이 필요합니다!.");
               return "redirect:/member/login";
           }

           int memberNo = loginMember.getMemberNo();

           // 서비스 호출 → Map으로 결과 반환 (pagination, nicknameMessageList)
           Map<String, Object> map = messageService.selectReceivedMessageListPagination(memberNo, cp);

           model.addAttribute("pagination", map.get("pagination"));
           model.addAttribute("nicknameReceivedList", map.get("nicknameReceivedList"));
           return "message/receiveMessageList";
      
    }
   

    
    /** 받은 쪽지 상세 조회
     * @param messageNo
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/inboxDetail/{messageNo:[0-9]+}")
    public String viewMessageinboxDetail(@PathVariable("messageNo") int messageNo, HttpSession session, Model model) {

        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            model.addAttribute("errorMessage", "로그인이  필요합니다.");
            return "redirect:/member/login";
        }

        // 파라미터 묶기
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("messageNo", messageNo);
        paramMap.put("memberNo", loginMember.getMemberNo());

        Message message = messageService.getMessageDetail(paramMap);

        if (message == null) {
            model.addAttribute("errorMessage", "쪽지를 찾을 순 없습니다.");
            return "redirect:/message/outbox";
        }
        
        Member sender = memberService.selectMemberByNo(message.getSenderNo());
        Member receiver = memberService.selectMemberByNo(message.getReceiverNo());
        
        model.addAttribute("senderNickname", sender.getMemberNickname());
        model.addAttribute("receiverNickname", receiver.getMemberNickname());

        model.addAttribute("message", message);
        return "message/receiveMessageDetail"; // 받은 쪽지 상세 템플릿
    }
    
   

    /** 받은쪽지 삭제
     * @param boardNo
     * @param model
     * @param memberNo
     * @param messageNo
     * @param messages
     * @return
     */
    @PostMapping("/inboxDelete/{memberNo}/{messageNo}")
    public String deleteMessageIn ( @RequestParam (required = false, value = "boardNo", defaultValue = "1") int boardNo, 
    		Model model
    		, @PathVariable("memberNo") int memberNo 
    		, @PathVariable("messageNo") int messageNo,
    		@ModelAttribute Message messages,
    		RedirectAttributes ra,
    		HttpSession session
    		){
    	
    	// 현재 로그인한 사용자(받은사람)의 번호 가져오기
        Member loginMember = (Member) session.getAttribute("loginMember");
        int receiverNo = loginMember.getMemberNo();
        
        
    	messages.setBoardNo(boardNo);
    	messages.setMessageNo(messageNo);
    	messages.setReceiverNo(receiverNo);
    	
    	int result = 0;
    	String message = null;
    	
    	
    	
		result = messageService.deleteMessagePageIn(messages);

		
    	if(result==0) {
    		
    		message = "삭제 실패!";
    		ra.addFlashAttribute("message", message); 		

    		  return "redirect:/message/inbox";    	}
    	
    	
    	else {
    		message = "삭제 성공 !";
    		ra.addFlashAttribute("message",message);
    		
        	return "redirect:/message/inbox";

    	}
    	
    	
    }
    
    

    
    
    
    /** 페이지네이션까지 고려한 보낸쪽지함으로 가는 로직
     * @param session
     * @param cp
     * @param model
     * @return
     */
    @GetMapping("/outbox")
    public String outbox(
            HttpSession session,
            @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
            Model model) {

        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            model.addAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        int memberNo = loginMember.getMemberNo();

        // 서비스 호출 → Map으로 결과 반환 (pagination, nicknameMessageList)
        Map<String, Object> map = messageService.selectSentMessageListPagination(memberNo, cp);

        model.addAttribute("pagination", map.get("pagination"));
        model.addAttribute("nicknameSentList", map.get("nicknameSentList"));
        

        return "message/sendMessageList";
    }

  
    
    /** 보낸 쪽지 상세 조회
     *  
     */
    @GetMapping("/outboxDetail/{messageNo:[0-9]+}")
    public String viewMessageOutboxDetail(@PathVariable("messageNo") int messageNo,
                                     HttpSession session,
                                     Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            model.addAttribute("errorMessage", "로그인 이 필요합니다.");
           return "redirect:/member/login";
        }

        // Map으로 파라미터 묶기
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("messageNo", messageNo);
        paramMap.put("memberNo", loginMember.getMemberNo());

        Message message = messageService.getMessageDetail(paramMap);
        if (message == null) {
            model.addAttribute("errorMessage", "쪽지를 찾을 수   없습니다.");
            return "redirect:/message/outbox/"+messageNo;
        }
     // 수신자 닉네임 조회
        Member receiver = memberService.selectMemberByNo(message.getReceiverNo());
        model.addAttribute("message", message);
        model.addAttribute("receiverNickname", receiver.getMemberNickname());
        return "message/sendMessageDetail";    
    }
    
    
    /** 보낸쪽지 삭제
     * @param boardNo
     * @param model
     * @param memberNo
     * @param messageNo
     * @param messages
     * @return
     */
    @PostMapping("/outboxDelete/{memberNo}/{messageNo}")
    public String deleteMessageOut (@RequestParam (required = false, value = "boardNo", defaultValue = "1") int boardNo, Model model
    		, @PathVariable("memberNo") int memberNo , @PathVariable("messageNo") int messageNo, @ModelAttribute Message messages
    		){
    	
   
    	messages.setBoardNo(boardNo);
    	messages.setMessageNo(messageNo);
    	
    	int result = 0;
    	String message= null;
    	
    	
    	
		result = messageService.deleteMessagePageOut(messages);

		
    	if(result==0) {
    		
    		message = "삭제 실패!";
    		model.addAttribute("message", message);
    		
    		return "redirect:/message/outboxDetail";
    	}
    	
    	else {
    		message = "삭제 성공!";
    		model.addAttribute("message", message);
        	return "redirect:/message/outbox";

    	}



    }
    
    
    
    /**
     * 쪽지 작성 폼으로 이동하는 GET 매핑 함수
     * URL: /message/send/{memberNo}
     */
    @GetMapping("/send/{memberNo}")
    public String sendMessagePage(@PathVariable("memberNo") int memberNo,
                                  Model model,
                                  @RequestParam(value = "boardNo", required = false) Integer boardNo,
                                  @RequestParam(value = "boardCode", defaultValue = "1") int boardCode,
                                  @RequestParam(value = "cp", defaultValue = "1") int cp) {

        log.info("sendMessagePage 진입- recipient memberNo={}, boardNo={}, boardCode={}, cp={}", memberNo, boardNo, boardCode, cp);

        Member recipient = memberService.selectMemberByNo(memberNo);
        log.info("selectMemberByNo 조회 결과: {}", recipient);

        if (recipient == null) {
            log.info("sendMessagePage -존재하지 않는 회원(번호={}) 의 요청", memberNo);
            model.addAttribute("errorMessage", "존재하지 않는 회원입니다");
            return "redirect:/";
        }

        model.addAttribute("recipient", recipient);
        model.addAttribute("receiverNo", memberNo); 
        model.addAttribute("boardNo", boardNo);
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("cp", cp);

        log.info("sendMessagePage - 모델에 속성  설정 완료");
        return "message/messagePost"; // templates/message/messagePost.html
    }

    
    /**
     * 쪽지 전송 처리하는 POST 매핑 함수
     * URL: /message/send
     */
    @PostMapping("/send")
    public String sendMessage(@ModelAttribute Message message,
                              HttpSession session,
                              Model model,
                              RedirectAttributes ra,
                            
                              @RequestParam(value = "cp", defaultValue = "1") int cp) {

    	int boardCode =1;
        log.info("sendMessage POST 진입 - message={}, boardCode={}, cp={}", message, boardCode, cp);

        Member loginMember = (Member) session.getAttribute("loginMember");
        log.info("loginMember 세션 조회 결과 {}", loginMember);

        if (loginMember == null) {
            log.info("sendMessage - 로그인 필요");
            model.addAttribute("errorMessage", "로그인이 필요합니다");
            return "redirect:/member/login";
        }

        message.setSenderNo(loginMember.getMemberNo());
        log.info("sendMessage - 설정된 발신자 번호 : {}", message.getSenderNo());

        int result = messageService.sendMessage(message);
        log.info("sendMessage Service 결과: {}", result);

        if (result > 0) {
            String path = "/message/outboxDetail/" + message.getMessageNo();
            log.info("sendMessage 성공 - redirect: {}", path);
            return "redirect:" + path;
        } else {
            log.error("sendMessage 실패 - message={}", message);
            ra.addFlashAttribute("message", "쪽지 전송 실패!.");
            return "redirect:/message/send/" + message.getReceiverNo()
                    + "?boardNo=" + message.getBoardNo()
                    + "&boardCode=" + boardCode
                    + "&cp=" + cp;
        }
    }
}