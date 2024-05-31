package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.domain.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileOnBoardingIsValueResponse {
    // 희망 팀빌딩 분야 항목
    private final boolean isProfileTeamBuildingField;

    // 기술 항목
    private final boolean isProfileSkill;

    // 지역 및 위치 항목
    private final boolean isProfileRegion;

    // 이력 항목
    private final boolean isAntecedents;

    // 교육 항목
    private final boolean isEducation;

    // 미니 프로필 항목
    private final boolean isMiniProfile;

    // 온보딩 과정에서 해당 항목들이 저장되어 있는지 여부를 판단하기 위해 구현함.
    public static ProfileOnBoardingIsValueResponse profileOnBoardingIsValue(
            final Profile profile
    ) {
        return new ProfileOnBoardingIsValueResponse(
                profile.getIsProfileTeamBuildingField(),
                profile.getIsProfileSkill(),
                profile.getIsProfileRegion(),
                profile.getIsAntecedents(),
                profile.getIsEducation(),
                profile.getIsMiniProfile()
        );
    }

}
