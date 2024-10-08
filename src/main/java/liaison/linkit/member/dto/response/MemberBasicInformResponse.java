package liaison.linkit.member.dto.response;

import liaison.linkit.member.domain.MemberBasicInform;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberBasicInformResponse {

    private final Long id;
    private final String memberName;
    private final String contact;
    private final String email;
    private final boolean marketingAgree;

    public static MemberBasicInformResponse personalMemberBasicInform(final MemberBasicInform memberBasicInform) {
        return new MemberBasicInformResponse(
                memberBasicInform.getId(),
                memberBasicInform.getMemberName(),
                memberBasicInform.getContact(),
                memberBasicInform.getMember().getEmail(),
                memberBasicInform.isMarketingAgree()
        );
    }
}
