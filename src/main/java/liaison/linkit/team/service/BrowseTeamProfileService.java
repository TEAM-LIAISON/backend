package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.repository.teamProfile.TeamProfileRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileRepository;
import liaison.linkit.team.dto.response.*;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.attach.TeamAttachResponse;
import liaison.linkit.team.dto.response.browse.BrowseTeamProfileResponse;
import liaison.linkit.team.dto.response.completion.TeamCompletionResponse;
import liaison.linkit.team.dto.response.history.HistoryResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BrowseTeamProfileService {

    private final ProfileRepository profileRepository;
    private final TeamProfileRepository teamProfileRepository;
    private final TeamMiniProfileRepository teamMiniProfileRepository;

    // 팀 미니 프로필로 해당 팀 소개서의 유효성 판단
    public void validateTeamProfileByTeamMiniProfile(final Long teamMiniProfileId) throws AuthException {
        if (!teamMiniProfileRepository.existsById(teamMiniProfileId)) {
            throw new AuthException(INVALID_TEAM_MINI_PROFILE);
        }
    }

    public Long getTargetTeamProfileByTeamMiniProfileId(final Long teamMiniProfileId) {
        final TeamProfile teamProfile = getTeamProfileByMiniProfileId(teamMiniProfileId);
        return teamProfile.getId();
    }

    private TeamProfile getTeamProfileByMiniProfileId(Long teamMiniProfileId) {
        final TeamMiniProfile teamMiniProfile = teamMiniProfileRepository.findById(teamMiniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_ID));
        return teamMiniProfile.getTeamProfile();
    }

    final TeamProfile getTeamProfile(final Long browseTargetTeamProfileId) {
        return teamProfileRepository.findById(browseTargetTeamProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_ID));
    }

    public TeamProfileIsValueResponse getTeamProfileIsValue(final Long browseTargetTeamProfileId) {
        final TeamProfile teamProfile = getTeamProfile(browseTargetTeamProfileId);
        return TeamProfileIsValueResponse.teamProfileIsValue(teamProfile);
    }


    public BrowseTeamProfileResponse getBrowseTeamProfileResponse(
            final Long profileId,
            final TeamMiniProfileResponse teamMiniProfileResponse,
            final TeamCompletionResponse teamCompletionResponse,
            final List<TeamMemberAnnouncementResponse> teamMemberAnnouncementResponse,
            final ActivityResponse activityResponse,
            final TeamProfileIntroductionResponse teamProfileIntroductionResponse,
            final List<TeamMemberIntroductionResponse> teamMemberIntroductionResponse,
            final List<HistoryResponse> historyResponse,
            final TeamAttachResponse teamAttachResponse
    ) {
        return BrowseTeamProfileResponse.teamProfile(
                profileId,
                teamMiniProfileResponse,
                teamCompletionResponse,
                teamMemberAnnouncementResponse,
                activityResponse,
                teamProfileIntroductionResponse,
                teamMemberIntroductionResponse,
                historyResponse,
                teamAttachResponse
        );
    }

    public boolean checkBrowseAuthority(final Long memberId) {
        final Profile profile = profileRepository.findByMemberId(memberId).orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));
        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_ID));

        if (profile.getCompletion() < 50 && teamProfile.getTeamProfileCompletion() < 50) {
            return true;
        } else {
            return false;
        }
    }
}

