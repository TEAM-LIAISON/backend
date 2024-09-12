package liaison.linkit.profile.dto.response.browse;

import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.awards.AwardsResponse;
import liaison.linkit.profile.dto.response.completion.CompletionResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BrowsePrivateProfileResponse {

    private final Long profileId;
    private final MiniProfileResponse miniProfileResponse;
    private final CompletionResponse completionResponse;
    private final ProfileIntroductionResponse profileIntroductionResponse;
    private final ProfileRegionResponse profileRegionResponse;
    private final List<AntecedentsResponse> antecedentsResponse;
    private final List<EducationResponse> educationResponse;
    private final List<AwardsResponse> awardsResponse;

    public static BrowsePrivateProfileResponse privateProfile(
            final Long profileId,
            final MiniProfileResponse miniProfileResponse,
            final CompletionResponse completionResponse,
            final ProfileIntroductionResponse profileIntroductionResponse,
            final ProfileRegionResponse profileRegionResponse,
            final List<AntecedentsResponse> antecedentsResponses,
            final List<EducationResponse> educationResponses,
            final List<AwardsResponse> awardsResponses
    ) {
        return new BrowsePrivateProfileResponse(
                profileId,
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                profileRegionResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses
        );
    }
}
