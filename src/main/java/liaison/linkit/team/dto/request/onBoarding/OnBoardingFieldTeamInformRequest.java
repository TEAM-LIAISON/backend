package liaison.linkit.team.dto.request.onBoarding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OnBoardingFieldTeamInformRequest {
    // 팀명
    private final String teamName;

    // 팀 규모
    private final String sizeType;

    // 분야
    private final String sectorName;
}


