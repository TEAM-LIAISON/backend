package liaison.linkit.profile.dto.response.isValue;

import liaison.linkit.profile.domain.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileOnBoardingIsValueResponse {
    // 지역 및 위치 항목
    private final boolean isProfileRegion;

    // 역할 및 보유 기술 항목
    private final boolean isJobAndSkill;

    // 경력 항목
    private final boolean isAntecedents;

    // 학력 항목
    private final boolean isEducation;

    // 미니 프로필 항목
    private final boolean isMiniProfile;

    // 온보딩 과정에서 해당 항목들이 저장되어 있는지 여부를 판단하기 위해 구현함.
    public static ProfileOnBoardingIsValueResponse profileOnBoardingIsValue(
            final Profile profile
    ) {
        return new ProfileOnBoardingIsValueResponse(
                profile.getIsProfileSkill(),
                profile.getIsProfileRegion(),
                profile.getIsAntecedents(),
                profile.getIsEducation(),
                profile.getIsMiniProfile()
        );
    }

}
