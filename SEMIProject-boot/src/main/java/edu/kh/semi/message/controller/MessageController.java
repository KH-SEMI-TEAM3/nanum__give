package edu.kh.semi.message.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    


   
    /**받은 쪽지함으로 가기
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/inbox")
    public String viewInbox(HttpSession session, Model model) {
        log.info("로그인된 상황에서 진입");
        Member loginMember = (Member) session.getAttribute("loginMember");
        log.info(" loginMember: {}", loginMember);

        if (loginMember == null) {
            log.warn("로그인 필요");
            model.addAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        List<Message> messageList = messageService.selectReceiveMessages(loginMember.getMemberNo());
        log.info("viewInbox - 조회된 받은 쪽지 개수: {}", messageList.size());
        model.addAttribute("messageList", messageList);
        return "message/receiveMessageList";
    }
    
    
    /** 받은 쪽지 상세 조회
     * @param messageNo
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/inboxDetail/{messageNo:[0-9]+}")
    public String viewMessageInboxDetail(@PathVariable("messageNo") int messageNo,
                                     HttpSession session,
                                     Model model
                                    ) {

        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            model.addAttribute("errorMessage", "로그인이 필요합니다.");
           return "redirect:/member/login";
        }

        // Map으로 파라미터 묶기
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("messageNo", messageNo);
        paramMap.put("memberNo", loginMember.getMemberNo());

        Message message = messageService.getMessageDetail(paramMap);

        if (message == null) {
            model.addAttribute("errorMessage", "쪽지를 찾을 수 없습니다.");
            return "redirect:/message/inbox";
        }

        model.addAttribute("message", message);
        return "message/receiveMessageDetail";
    
    }
    
   

    /** 받은쪽지 삭제
     * @param boardNo
     * @param model
     * @param memberNo
     * @param messageNo
     * @param messages
     * @return
     */
    @GetMapping("/inboxDelete/{memberNo}/{messageNo}")
    public String deleteMessageIn ( @RequestParam (required = false, value = "boardNo", defaultValue = "1") int boardNo, Model model
    		, @PathVariable("memberNo") int memberNo , @PathVariable("messageNo") int messageNo, @ModelAttribute Message messages
    		){
    	
   
    	messages.setBoardNo(boardNo);
    	messages.setMessageNo(messageNo);
    	
    	int result = 0;
    	String message= null;
    	
    	
    	
		result = messageService.deleteMessagePage(messages);

		
    	if(result==0) {
    		
    		message = "삭제 실패!";
    		model.addAttribute("message", message);
    		
    		return "redirect:/message/inboxDetail/"+messageNo;
    	}
    	
    	else {
    		message = "삭제 성공!";
    		model.addAttribute("message", message);
        	return "redirect:/message/inbox"+messageNo;

    	}
    	
    	
    }
    
    

    
    
    
    
    
    

 
    /** 보낸 쪽지함으로 이동 시킴
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/outbox")
    public String outbox(HttpSession session, Model model) {

        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            model.addAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        List<Message> sentList = messageService.selectSentMessages(loginMember.getMemberNo());
        model.addAttribute("sentList", sentList);

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
            model.addAttribute("errorMessage", "로그인이 필요합니다.");
           return "redirect:/member/login";
        }

        // Map으로 파라미터 묶기
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("messageNo", messageNo);
        paramMap.put("memberNo", loginMember.getMemberNo());

        Message message = messageService.getMessageDetail(paramMap);

        if (message == null) {
            model.addAttribute("errorMessage", "쪽지를 찾을 수 없습니다.");
            return "redirect:/message/outbox/"+messageNo;
        }

        model.addAttribute("message", message);
        return "message/receiveMessageList/"+messageNo;
    
    }
    
    
    /** 보낸쪽지 삭제
     * @param boardNo
     * @param model
     * @param memberNo
     * @param messageNo
     * @param messages
     * @return
     */
    @GetMapping("/outboxDelete/{memberNo}/{messageNo}")
    public String deleteMessageOut (@RequestParam (required = false, value = "boardNo", defaultValue = "1") int boardNo, Model model
    		, @PathVariable("memberNo") int memberNo , @PathVariable("messageNo") int messageNo, @ModelAttribute Message messages
    		){
    	
   
    	messages.setBoardNo(boardNo);
    	messages.setMessageNo(messageNo);
    	
    	int result = 0;
    	String message= null;
    	
    	
    	
		result = messageService.deleteMessagePage(messages);

		
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
    
    
    
    @GetMapping("/send/{memberNo}")
    /**   쪽지 작성 폼으로 이동하는 get 매핑 함수 
     * @param memberNo
     * @param model
     * @param boardNo
     * @param boardCode
     * @param cp
     * @return
     */
    public String sendMessagePage(@PathVariable int memberNo,
                                  Model model,
                                  @RequestParam(value = "boardNo", required = false) Integer boardNo,
                                  @RequestParam(value = "boardCode", defaultValue = "1") int boardCode,
                                  @RequestParam(value = "cp", defaultValue = "1") int cp) {
        log.info("sendMessagePage 진입 - recipient memberNo={}, boardNo={}, boardCode={}, cp={}", memberNo, boardNo, boardCode, cp);

        Member recipient = memberService.selectMemberByNo(memberNo);
        log.info("selectMemberByNo 조회 결과: {}", recipient);

        if (recipient == null) {
            log.info("sendMessagePage - 존재하지 않는 회원(번호={}) 요청", memberNo);
            model.addAttribute("errorMessage", "존재하지 않는 회원입니다.");
            return "redirect:/";
        }

        model.addAttribute("recipient", recipient);
        model.addAttribute("receiverNo", memberNo);
        model.addAttribute("boardNo", boardNo);
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("cp", cp);
        log.info("sendMessagePage - 모델에 속성 설정 완료");
        return "message/messagePost";
    }

    
    /** 쪽지 전송하는 post 매핑 함수
     * @param message
     * @param session
     * @param model
     * @param ra
     * @param boardCode
     * @param cp
     * @return
     */
    @PostMapping("/send")
    public String sendMessage(@ModelAttribute Message message,
                              HttpSession session,
                              Model model,
                              RedirectAttributes ra,
                              @RequestParam(defaultValue = "1") int boardCode,
                              @RequestParam(defaultValue = "1") int cp) {
        log.info("sendMessage POST 진입 - message={}, boardCode={}, cp={}", message, boardCode, cp);

        Member loginMember = (Member) session.getAttribute("loginMember");
        log.info("loginMember 세션 조회 결과: {}", loginMember);

        if (loginMember == null) {
            log.info("sendMessage - 로그인 필요");
            model.addAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        message.setSenderNo(loginMember.getMemberNo());
        log.info("sendMessage - 설정된 발신자 번호: {}", message.getSenderNo());

        int result = messageService.sendMessage(message);
        log.info("sendMessage Service 결과: {}", result);

        if (result > 0) {
            String path = "/message/inboxDetail/" + message.getMessageNo();
            log.info("sendMessage 성공 - redirect: {}", path);
            return "redirect:" + path;
        } else {
            log.error("sendMessage 실패 - message={}", message);
            ra.addFlashAttribute("message", "쪽지 전송 실패.");
            return "redirect:/message/messagePost/" + message.getReceiverNo();
        }
    }
}