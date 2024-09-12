package liaison.linkit.matching.dto.response.messageResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReceivedTeamMatchingMessageResponse {
    private final Long receivedMatchingId;
    private final String senderName;
    private final String requestMessage;
    private final boolean isReceivedTeamProfile;
}
