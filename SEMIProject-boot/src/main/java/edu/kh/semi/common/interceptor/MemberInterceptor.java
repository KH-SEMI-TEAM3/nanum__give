package edu.kh.semi.common.interceptor;

import edu.kh.semi.member.model.dto.Member;
import edu.kh.semi.member.model.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
@Component
public class MemberInterceptor implements HandlerInterceptor {

    @Autowired
    private MemberService memberService; // 회원 상태 조회용

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        if (session != null) {
            Member loginMember = (Member) session.getAttribute("loginMember");
            //세션에 로그인된 회원 정보가 있다면
            if (loginMember != null) {
                // DB에서 탈퇴여부 조회
                
                String delFl = memberService.getMemberDelFl(loginMember.getMemberNo()); // 현재 접속한 회원의 탈퇴여부를 뽑아온다.
                
                
                if ("Y".equals(delFl)) { // 탈퇴한 회원이 만약 이 서버에 접근을 하려고 한다면? 그런데 서버가 돌아가고 있다면?
                	log.info("[강제 로그아웃] 탈퇴 처리된 회원이 접근 시도 - 회원번호: {}, 아이디: {}", 
                            loginMember.getMemberNo(), loginMember.getMemberId()); // 이 로그를 찍고
                    // 세션 만료(로그아웃)
                    session.invalidate();
                    // 다시 회원가입하든가 하라고 로그인 페이지로 강제 이동 (forcedLogout)
                    response.sendRedirect(request.getContextPath() + "/member/loginPage?forcedLogout=1");
                    return false;
                }
            }
        }
        return true;
    }
}