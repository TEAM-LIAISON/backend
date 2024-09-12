package liaison.linkit.team.dto.response.completion;

import liaison.linkit.team.domain.TeamProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamCompletionResponse {

    // 팀 소개서 완성도 % = teamCompletion
    private final String teamCompletion;

    // 4.5. 팀원 공고
    private final boolean isTeamMemberAnnouncement;

    // 4.6. 활동 방식 및 지역
    private final boolean isActivity;

    // 4.7. 팀 소개
    private final boolean isTeamIntroduction;

    // 4.8. 팀원 소개
    private final boolean isTeamMemberIntroduction;

    // 4.9. 연혁
    private final boolean isHistory;

    // 4.10. 첨부 (url, file) 둘 중 하나라도.
    private final boolean isTeamAttach;

    public static TeamCompletionResponse teamProfileCompletion(final TeamProfile teamProfile) {
        return new TeamCompletionResponse(
                String.format("%.1f", teamProfile.getTeamProfileCompletion()),
                teamProfile.getIsTeamMemberAnnouncement(),
                teamProfile.getIsActivity(),
                teamProfile.getIsTeamIntroduction(),
                teamProfile.getIsTeamMemberIntroduction(),
                teamProfile.getIsHistory(),
                teamProfile.getIsTeamAttach()
        );
    }

}
