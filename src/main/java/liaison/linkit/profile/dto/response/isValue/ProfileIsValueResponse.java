package liaison.linkit.profile.dto.response.isValue;

import liaison.linkit.profile.domain.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileIsValueResponse {

    // 자기소개 항목
    private final boolean isIntroduction;

    // 지역 및 위치 항목
    private final boolean isProfileRegion;

    // 이력 항목
    private final boolean isAntecedents;

    // 학력 항목
    private final boolean isEducation;

    // 수상 항목
    private final boolean isAwards;

    // 첨부 항목
    private final boolean isAttachUrl;

    // 미니 프로필 항목
    private final boolean isMiniProfile;

    public static ProfileIsValueResponse profileIsValue(final Profile profile) {
        return new ProfileIsValueResponse(
                profile.getIsIntroduction(),
                profile.getIsProfileRegion(),
                profile.getIsAntecedents(),
                profile.getIsEducation(),
                profile.getIsAwards(),
                profile.getIsAttachUrl(),
                profile.getIsMiniProfile()
        );
    }
}
