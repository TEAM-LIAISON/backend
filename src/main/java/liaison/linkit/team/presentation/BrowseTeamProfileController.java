package liaison.linkit.team.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
import liaison.linkit.team.dto.response.TeamMemberIntroductionResponse;
import liaison.linkit.team.dto.response.TeamProfileIntroductionResponse;
import liaison.linkit.team.dto.response.TeamProfileIsValueResponse;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.attach.TeamAttachResponse;
import liaison.linkit.team.dto.response.browse.BrowseTeamProfileResponse;
import liaison.linkit.team.dto.response.completion.TeamCompletionResponse;
import liaison.linkit.team.dto.response.history.HistoryResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class BrowseTeamProfileController {

    public final BrowseTeamProfileService browseTeamProfileService;
    public final TeamProfileService teamProfileService;

    // 4.1.
    public final TeamMiniProfileService teamMiniProfileService;
    // 4.3.
    public final TeamCompletionService teamCompletionService;
    // 4.5.
    public final TeamMemberAnnouncementService teamMemberAnnouncementService;
    // 4.6.
    public final ActivityService activityService;
    // 4.8.
    public final TeamMemberIntroductionService teamMemberIntroductionService;
    // 4.9.
    public final HistoryService historyService;
    // 4.10.
    public final TeamAttachService teamAttachService;

    @GetMapping("/browse/team/profile/{teamMiniProfileId}")
    @MemberOnly
    public ResponseEntity<?> getBrowseTeamProfile(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMiniProfileId
    ) {
        if (browseTeamProfileService.checkBrowseAuthority(accessor.getMemberId())) {
            throw new BadRequestException(ExceptionCode.NOT_ALLOW_BROWSE);
        }

        log.info("teamMiniProfileId={}에 대한 팀 소개서 열람 요청이 발생했습니다.", teamMiniProfileId);
        try {
            log.info("타겟 팀 소개서 열람 유효성 검사 로직");
            browseTeamProfileService.validateTeamProfileByTeamMiniProfile(teamMiniProfileId);

            // 2. 열람하고자 하는 회원의 ID를 가져온다.
            final Long browseTargetTeamProfileId = browseTeamProfileService.getTargetTeamProfileByTeamMiniProfileId(teamMiniProfileId);
            log.info("browseTargetTeamProfileId={}", browseTargetTeamProfileId);

            // 팀 소개서에 있는 항목들의 존재 여부 파악
            final TeamProfileIsValueResponse teamProfileIsValueResponse = browseTeamProfileService.getTeamProfileIsValue(browseTargetTeamProfileId);

            // 4.1. 팀 미니 프로필
            final TeamMiniProfileResponse teamMiniProfileResponse = getTeamMiniProfileResponse(browseTargetTeamProfileId, teamProfileIsValueResponse.isTeamMiniProfile());
            log.info("teamMiniProfileResponse={}", teamMiniProfileResponse);

            // 4.3. 프로필 완성도
            final TeamCompletionResponse teamCompletionResponse = getTeamCompletionResponse(browseTargetTeamProfileId);
            log.info("teamCompletionResponse={}", teamCompletionResponse);

            // 4.5. 팀원 공고
            final List<TeamMemberAnnouncementResponse> teamMemberAnnouncementResponse = getTeamMemberAnnouncement(browseTargetTeamProfileId, teamProfileIsValueResponse.isTeamMemberAnnouncement());
            log.info("teamMemberAnnouncementResponse={}", teamMemberAnnouncementResponse);

            // 4.6. 활동 방식 + 활동 지역/위치
            final ActivityResponse activityResponse = getActivityResponse(browseTargetTeamProfileId, teamProfileIsValueResponse.isActivity());
            log.info("activityResponse={}", activityResponse);

            // 4.7. 팀 소개
            final TeamProfileIntroductionResponse teamProfileIntroductionResponse = getTeamProfileIntroduction(browseTargetTeamProfileId, teamProfileIsValueResponse.isTeamIntroduction());
            log.info("teamProfileIntroductionResponse={}", teamProfileIntroductionResponse);

            // 4.8. 팀원 소개
            final List<TeamMemberIntroductionResponse> teamMemberIntroductionResponse = getTeamMemberIntroduction(browseTargetTeamProfileId, teamProfileIsValueResponse.isTeamMemberIntroduction());
            log.info("teamMemberIntroductionResponse={}", teamMemberIntroductionResponse);

            // 4.9. 연혁
            final List<HistoryResponse> historyResponse = getHistory(browseTargetTeamProfileId, teamProfileIsValueResponse.isHistory());
            log.info("historyResponse={}", historyResponse);

            // 4.10. 첨부
            final TeamAttachResponse teamAttachResponse = getTeamAttach(browseTargetTeamProfileId, teamProfileIsValueResponse.isTeamAttachUrl());
            log.info("teamAttachResponse={}", teamAttachResponse);

            final BrowseTeamProfileResponse browseTeamProfileResponse = browseTeamProfileService.getBrowseTeamProfileResponse(
                    browseTargetTeamProfileId,
                    teamMiniProfileResponse,
                    teamCompletionResponse,
                    teamMemberAnnouncementResponse,
                    activityResponse,
                    teamProfileIntroductionResponse,
                    teamMemberIntroductionResponse,
                    historyResponse,
                    teamAttachResponse
            );

            return ResponseEntity.ok().body(browseTeamProfileResponse);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("팀 소개서를 조회하는 과정에서 문제가 발생했습니다.");
        }
    }


    // 4.3. 프로필 완성도
    private TeamCompletionResponse getTeamCompletionResponse(
            final Long memberId
    ) {
        return teamCompletionService.getTeamCompletion(memberId);
    }

    // 4.5. 팀원 공고
    private List<TeamMemberAnnouncementResponse> getTeamMemberAnnouncement(
            final Long memberId,
            final boolean isTeamMemberAnnouncement
    ) {
        if (isTeamMemberAnnouncement) {
            return teamMemberAnnouncementService.getTeamMemberAnnouncements(memberId);
        } else {
            return null;
        }
    }
    // 4.6. 활동 방식 + 활동 지역 및 위치
    private ActivityResponse getActivityResponse(
            final Long memberId,
            final boolean isActivity
    ) {
        log.info("getActivityResponse() 메서드 실행 여부");
        if (isActivity) {
            return activityService.getActivity(memberId);
        } else {
            return new ActivityResponse();
        }
    }

    // 4.7. 팀 소개
    private TeamProfileIntroductionResponse getTeamProfileIntroduction(
            final Long memberId,
            final boolean isTeamIntroduction
    ) {
        if (isTeamIntroduction) {
            return teamProfileService.getTeamIntroduction(memberId);
        } else {
            return new TeamProfileIntroductionResponse();
        }
    }

    // 4.8. 팀원 소개
    private List<TeamMemberIntroductionResponse> getTeamMemberIntroduction(
            final Long memberId,
            final boolean isTeamMemberIntroduction
    ) {
        if (isTeamMemberIntroduction) {
            return teamMemberIntroductionService.getAllTeamMemberIntroduction(memberId);
        } else {
            return null;
        }
    }

    // 4.9. 연혁
    private List<HistoryResponse> getHistory(
            final Long memberId,
            final boolean isHistory
    ) {
        if (isHistory) {
            return historyService.getAllHistories(memberId);
        } else {
            return null;
        }
    }

    // 4.10 첨부
    private TeamAttachResponse getTeamAttach(
            final Long memberId,
            final boolean isTeamAttach
    ) {
        if (isTeamAttach) {
            return teamAttachService.getTeamAttachList(memberId);
        } else {
            return new TeamAttachResponse();
        }
    }

    private TeamMiniProfileResponse getTeamMiniProfileResponse(
            final Long memberId,
            final boolean isTeamMiniProfile
    ) {
        if (isTeamMiniProfile) {
            teamMiniProfileService.validateTeamMiniProfileByMember(memberId);
            return teamMiniProfileService.getPersonalTeamMiniProfile(memberId);
        } else {
            final String teamName = teamMiniProfileService.getTeamName(memberId);
            return new TeamMiniProfileResponse(teamName);
        }
    }
}
