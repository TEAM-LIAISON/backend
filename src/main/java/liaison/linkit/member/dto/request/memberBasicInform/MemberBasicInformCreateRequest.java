package liaison.linkit.member.dto.request.memberBasicInform;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MemberBasicInformCreateRequest {

    @NotNull(message = "성함을 입력해주세요")
    private final String memberName;

    @NotNull(message = "연락처를 입력해주세요")
    private final String contact;

    @NotNull(message = "마케팅 정보 수신 동의를 체크해주세요")
    private final boolean marketingAgree;

    public MemberBasicInformCreateRequest(
            final String memberName,
            final String contact,
            boolean marketingAgree
    ) {
        this.memberName = memberName;
        this.contact = contact;
        this.marketingAgree = marketingAgree;
    }
}
