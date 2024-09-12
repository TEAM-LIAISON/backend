package liaison.linkit.team.dto.response.onBoarding;

import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileEarlyOnBoardingResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OnBoardingFieldTeamInformResponse {
    private final String teamName;
    private final String sectorName;
    private final String sizeType;

    public OnBoardingFieldTeamInformResponse(
            final TeamMiniProfileEarlyOnBoardingResponse teamMiniProfileEarlyOnBoardingResponse
    ) {
        this.teamName = teamMiniProfileEarlyOnBoardingResponse.getTeamName();
        this.sectorName = teamMiniProfileEarlyOnBoardingResponse.getSectorName();
        this.sizeType = teamMiniProfileEarlyOnBoardingResponse.getSizeType();
    }

    public OnBoardingFieldTeamInformResponse() {
        this.teamName = null;
        this.sectorName = null;
        this.sizeType = null;
    }
}
