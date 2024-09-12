package liaison.linkit.profile.dto.response.completion;

import liaison.linkit.profile.domain.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompletionResponse {
    // 프로필 완성도 % = completion
    private final String completion;

    // 상태도 던져줘야하나?
    // -> 아직 미확정

    // 기입여부 boolean 값들 던져줘야 함. && 고려해야하는 부분: default 한번에 던져줄지.

    // 자기소개 기입여부
    private final boolean isIntroduction;

    // 보유기술 기입여부 -> Default
    private final boolean isProfileSkill;

    // 활동 지역 및 위치 기입여부
    private final boolean isProfileRegion;

    // 이력 분야 기입여부
    private final boolean isAntecedents;

    // 학력 분야 기입여부 -> Default
    private final boolean isEducation;

    // 수상 분야 기입여부
    private final boolean isAwards;

    // 첨부 분야 기입여부
    private final boolean isAttach;


    public static CompletionResponse profileCompletion(final Profile profile) {
        return new CompletionResponse(
                String.format("%.1f", profile.getCompletion()),
                profile.getIsIntroduction(),
                profile.getIsProfileSkill(),
                profile.getIsProfileRegion(),
                profile.getIsAntecedents(),
                profile.getIsEducation(),
                profile.getIsAwards(),
                profile.getIsAttachUrl()
        );
    }

}
