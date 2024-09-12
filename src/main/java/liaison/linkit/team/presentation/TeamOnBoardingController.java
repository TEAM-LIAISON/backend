package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.onBoarding.OnBoardingFieldTeamInformRequest;
import liaison.linkit.team.dto.response.OnBoardingTeamProfileResponse;
import liaison.linkit.team.dto.response.TeamProfileOnBoardingIsValueResponse;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.dto.response.onBoarding.OnBoardingFieldTeamInformResponse;
import liaison.linkit.team.service.ActivityService;
import liaison.linkit.team.service.TeamMiniProfileService;
import liaison.linkit.team.service.TeamOnBoardingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class TeamOnBoardingController {

    final TeamOnBoardingService teamOnBoardingService;
    // 4.1.
    final TeamMiniProfileService teamMiniProfileService;
    // 4.6.
    final ActivityService activityService;

    // 팀 소개서 온보딩 과정에서 첫번째 항목
    @PostMapping("/team/team_building_field/basic_inform")
    @MemberOnly
    public ResponseEntity<Void> createOnBoardingFirst(
            @Auth final Accessor accessor,
            @RequestBody @Valid final OnBoardingFieldTeamInformRequest onBoardingFieldTeamInformRequest
    ) {
        teamMiniProfileService.saveOnBoarding(accessor.getMemberId(), onBoardingFieldTeamInformRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/update/onBoarding/team/mini-profile")
    @MemberOnly
    public ResponseEntity<Void> updateOnBoardingTeamMiniProfile(
            @Auth final Accessor accessor,
            @RequestBody @Valid OnBoardingFieldTeamInformRequest onBoardingFieldTeamInformRequest
    ) {
        log.info("memberId={}의 팀 미니 프로필 수정 요청이 발생하였습니다.", accessor.getMemberId());
        teamMiniProfileService.updateOnBoarding(accessor.getMemberId(), onBoardingFieldTeamInformRequest);
        return ResponseEntity.status(OK).build();
    }

    @GetMapping("/team/onBoarding")
    @MemberOnly
    public ResponseEntity<?> getOnBoardingTeamProfile(@Auth final Accessor accessor) {
        try {
            teamOnBoardingService.validateTeamProfileByMember(accessor.getMemberId());

            final TeamProfileOnBoardingIsValueResponse teamProfileOnBoardingIsValueResponse = teamOnBoardingService.getTeamProfileOnBoardingIsValue(accessor.getMemberId());
            log.info("teamProfileOnBoardingIsValueResponse={}",teamProfileOnBoardingIsValueResponse);

            final OnBoardingFieldTeamInformResponse onBoardingFieldTeamInformResponse = getOnBoardingFieldTeamInformResponse(accessor.getMemberId(), teamProfileOnBoardingIsValueResponse.isTeamMiniProfile());
            log.info("onBoardingFieldTeamInformResponse={}",onBoardingFieldTeamInformResponse);

            final ActivityResponse activityResponse = getActivityResponse(accessor.getMemberId(), teamProfileOnBoardingIsValueResponse.isActivity());
            log.info("activityResponse={}",activityResponse);

            final TeamMiniProfileResponse teamMiniProfileResponse = getTeamMiniProfileResponse(accessor.getMemberId(), teamProfileOnBoardingIsValueResponse.isTeamMiniProfile());
            log.info("teamMiniProfileResponse={}",teamMiniProfileResponse);
            final OnBoardingTeamProfileResponse onBoardingTeamProfileResponse = new OnBoardingTeamProfileResponse(
                    onBoardingFieldTeamInformResponse,
                    activityResponse,
                    teamMiniProfileResponse
            );

            return ResponseEntity.ok().body(onBoardingTeamProfileResponse);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("온보딩 정보를 불러오는 과정에서 문제가 발생했습니다.");
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

    private OnBoardingFieldTeamInformResponse getOnBoardingFieldTeamInformResponse(
            final Long memberId,
            final boolean isTeamMiniProfile
    ) {
        if (isTeamMiniProfile) {
            teamMiniProfileService.validateTeamMiniProfileByMember(memberId);
            return new OnBoardingFieldTeamInformResponse(
                    teamMiniProfileService.getTeamMiniProfileEarlyOnBoarding(memberId)
            );
        } else {
            return new OnBoardingFieldTeamInformResponse();
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

}
