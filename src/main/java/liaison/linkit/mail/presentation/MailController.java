package liaison.linkit.mail.presentation;

import liaison.linkit.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    // 1. 내 이력서 -> 내 이력서로 매칭 요청 보낸 경우
    @PostMapping("/private/private")
    public ResponseEntity<?> sendPrivateToPrivateMatchingRequest() throws Exception {
        mailService.mailRequestPrivateToPrivate(
                "kwondm7@naver.com",
                "권동민",
                "주서영",
                LocalDateTime.now(),
                "요청 메시지입니다."
        );
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
