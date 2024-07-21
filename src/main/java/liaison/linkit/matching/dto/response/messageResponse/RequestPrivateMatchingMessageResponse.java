package liaison.linkit.matching.dto.response.messageResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RequestPrivateMatchingMessageResponse {
    // 내 이력서에 보낸 매칭 요청 PK ID
    private final Long privateMatchingId;
    // 수신자 이름
    private final String receiverName;

    private final List<String> jobRoleNames;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 어떤 이력/소개서에 매칭 요청을 보냈는지
    private final boolean isRequestTeamProfile;
}
