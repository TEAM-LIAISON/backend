package liaison.linkit.team.dto.response.browse;

import liaison.linkit.team.dto.response.TeamMemberIntroductionResponse;
import liaison.linkit.team.dto.response.TeamProfileIntroductionResponse;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.attach.TeamAttachResponse;
import liaison.linkit.team.dto.response.completion.TeamCompletionResponse;
import liaison.linkit.team.dto.response.history.HistoryResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BrowseTeamProfileResponse {

    private final Long teamProfileId;
    private final TeamMiniProfileResponse teamMiniProfileResponse;
    private final TeamCompletionResponse teamCompletionResponse;
    private final List<TeamMemberAnnouncementResponse> teamMemberAnnouncementResponses;
    private final ActivityResponse activityResponse;
    private final TeamProfileIntroductionResponse teamProfileIntroductionResponse;
    private final List<TeamMemberIntroductionResponse> teamMemberIntroductionResponses;
    private final List<HistoryResponse> historyResponses;
    private final TeamAttachResponse teamAttachResponse;

    public static BrowseTeamProfileResponse teamProfile(
            final Long teamProfileId,
            final TeamMiniProfileResponse teamMiniProfileResponse,
            final TeamCompletionResponse teamCompletionResponse,
            final List<TeamMemberAnnouncementResponse> teamMemberAnnouncementResponses,
            final ActivityResponse activityResponse,
            final TeamProfileIntroductionResponse teamProfileIntroductionResponse,
            final List<TeamMemberIntroductionResponse> teamMemberIntroductionResponses,
            final List<HistoryResponse> historyResponses,
            final TeamAttachResponse teamAttachResponse
    ) {
        return new BrowseTeamProfileResponse(
                teamProfileId,
                teamMiniProfileResponse,
                teamCompletionResponse,
                teamMemberAnnouncementResponses,
                activityResponse,
                teamProfileIntroductionResponse,
                teamMemberIntroductionResponses,
                historyResponses,
                teamAttachResponse
        );
    }
}
