package liaison.linkit.profile.dto.response.onBoarding;

import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OnBoardingProfileResponse {

    // 모든 온보딩 정보가 전달되는 핵심 비즈니스 로직 변수
    private final ProfileRegionResponse profileRegionResponse;
    private final List<EducationResponse> educationResponses;
    private final List<AntecedentsResponse> antecedentsResponses;
    private final MiniProfileResponse miniProfileResponse;

    public static OnBoardingProfileResponse onBoardingProfileItems(
            final ProfileRegionResponse profileRegionResponse,
            final List<EducationResponse> educationResponses,
            final List<AntecedentsResponse> antecedentsResponses,
            final MiniProfileResponse miniProfileResponse
    ) {
        return new OnBoardingProfileResponse(
                profileRegionResponse,
                educationResponses,
                antecedentsResponses,
                miniProfileResponse
        );
    }

}
