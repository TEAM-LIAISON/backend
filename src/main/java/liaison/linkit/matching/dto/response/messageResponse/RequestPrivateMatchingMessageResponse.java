package liaison.linkit.matching.dto.response.messageResponse;

import liaison.linkit.matching.domain.type.SenderType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestPrivateMatchingMessageResponse {
    // 내 이력서 또는 팀 소개서에 매칭 요청 온 PK ID
    private final Long requestMatchingId;
    // 수신자 이름
    private final String receiverName;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 발신자가 누구인지
    private final SenderType senderType;
    // 어떤 이력/소개서에 매칭 요청을 보냈는지
    private final boolean isRequestTeamProfile;
}
