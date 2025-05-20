package edu.kh.semi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class}) // Spring Security에서 기본 제공하는 로그인 페이지를 이용 안하겠다!
public class SemiProjectBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SemiProjectBootApplication.class, args);
	}

}
