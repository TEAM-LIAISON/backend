package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.TeamIntroductionCreateRequest;
import liaison.linkit.team.dto.response.*;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.attach.TeamAttachResponse;
import liaison.linkit.team.dto.response.completion.TeamCompletionResponse;
import liaison.linkit.team.dto.response.history.HistoryResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class TeamProfileController {

    final TeamProfileService teamProfileService;
    // 4.1.
    final TeamMiniProfileService teamMiniProfileService;
    // 4.3.
    final TeamCompletionService teamCompletionService;
    // 4.5.
    final TeamMemberAnnouncementService teamMemberAnnouncementService;
    // 4.6.
    final ActivityService activityService;
    // 4.8.
    final TeamMemberIntroductionService teamMemberIntroductionService;
    // 4.9.
    final HistoryService historyService;
    // 4.10.
    final TeamAttachService teamAttachService;

    // 팀 소개서 전체 조회
    @GetMapping("/team/profile")
    @MemberOnly
    public ResponseEntity<?> getTeamProfile(@Auth final Accessor accessor) {
        try {
            log.info("--- 팀 이력서 조회 요청이 들어왔습니다. ---");
            teamProfileService.validateTeamProfileByMember(accessor.getMemberId());
            log.info("--- 팀 이력서가 유효합니다. ---");

            // 팀 소개서에 있는 항목들의 존재 여부 파악
            final TeamProfileIsValueResponse teamProfileIsValueResponse = teamProfileService.getTeamProfileIsValue(accessor.getMemberId());
            log.info("teamProfileIsValueResponse={}", teamProfileIsValueResponse);

            final boolean isTeamProfileEssential = (teamProfileIsValueResponse.isTeamMiniProfile() && teamProfileIsValueResponse.isActivity());

            log.info("isTeamProfileEssential={}", isTeamProfileEssential);

            // 팀 소개서의 필수 구성요소가 충족되지 않았을 경우 응답 처리
            if (!isTeamProfileEssential) {
                // 필수 구성요소가 없는 경우 경고 메시지와 함께 반환
                return ResponseEntity.ok().body(new TeamProfileResponse());
            }

            // 4.1. 팀 미니 프로필
            final TeamMiniProfileResponse teamMiniProfileResponse = getTeamMiniProfileResponse(accessor.getMemberId(), teamProfileIsValueResponse.isTeamMiniProfile());
            log.info("teamMiniProfileResponse={}", teamMiniProfileResponse);

            // 4.3. 프로필 완성도
            final TeamCompletionResponse teamCompletionResponse = getTeamCompletionResponse(accessor.getMemberId());
            log.info("teamCompletionResponse={}", teamCompletionResponse);

            // 4.5. 팀원 공고
            final List<TeamMemberAnnouncementResponse> teamMemberAnnouncementResponse = getTeamMemberAnnouncement(accessor.getMemberId(), teamProfileIsValueResponse.isTeamMemberAnnouncement());
            log.info("teamMemberAnnouncementResponse={}", teamMemberAnnouncementResponse);

            // 4.6. 활동 방식 + 활동 지역/위치
            final ActivityResponse activityResponse = getActivityResponse(accessor.getMemberId(), teamProfileIsValueResponse.isActivity());
            log.info("activityResponse={}", activityResponse);

            // 4.7. 팀 소개
            final TeamProfileIntroductionResponse teamProfileIntroductionResponse = getTeamProfileIntroduction(accessor.getMemberId(), teamProfileIsValueResponse.isTeamIntroduction());
            log.info("teamProfileIntroductionResponse={}", teamProfileIntroductionResponse);

            // 4.8. 팀원 소개
            final List<TeamMemberIntroductionResponse> teamMemberIntroductionResponse = getTeamMemberIntroduction(accessor.getMemberId(), teamProfileIsValueResponse.isTeamMemberIntroduction());
            log.info("teamMemberIntroductionResponse={}", teamMemberIntroductionResponse);

            // 4.9. 연혁
            final List<HistoryResponse> historyResponse = getHistory(accessor.getMemberId(), teamProfileIsValueResponse.isHistory());
            log.info("historyResponse={}", historyResponse);

            // 4.10. 첨부
            final TeamAttachResponse teamAttachResponse = getTeamAttach(accessor.getMemberId(), teamProfileIsValueResponse.isTeamAttachUrl());
            log.info("teamAttachResponse={}", teamAttachResponse);

            final TeamProfileResponse teamProfileResponse = teamProfileService.getTeamProfileResponse(
                    isTeamProfileEssential,
                    teamMiniProfileResponse,
                    teamCompletionResponse,
                    teamMemberAnnouncementResponse,
                    activityResponse,
                    teamProfileIntroductionResponse,
                    teamMemberIntroductionResponse,
                    historyResponse,
                    teamAttachResponse
            );

            return ResponseEntity.ok().body(teamProfileResponse);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("팀 소개서 정보를 불러오는 과정에서 문제가 발생했습니다.");
        }
    }

    // 팀 소개 생성/수정
    @PostMapping("/team/introduction")
    @MemberOnly
    public ResponseEntity<Void> createTeamIntroduction(
            @Auth final Accessor accessor,
            @RequestBody @Valid final TeamIntroductionCreateRequest teamIntroductionCreateRequest
    ) {
        teamProfileService.validateTeamProfileByMember(accessor.getMemberId());
        teamProfileService.saveTeamIntroduction(accessor.getMemberId(), teamIntroductionCreateRequest);
        return ResponseEntity.ok().build();
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
            log.info("isActivity={}", isActivity);
            return activityService.getActivity(memberId);
        } else {
            log.info("isActivity={}", isActivity);
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
