package liaison.linkit.profile.presentation;

import static liaison.linkit.global.exception.ExceptionCode.HAVE_TO_INPUT_BOTH_JOB_AND_SKILL;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import jakarta.validation.Valid;
import java.util.List;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.business.MemberService;
import liaison.linkit.profile.dto.request.onBoarding.OnBoardingPersonalJobAndSkillCreateRequest;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileOnBoardingIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.onBoarding.JobAndSkillResponse;
import liaison.linkit.profile.dto.response.onBoarding.OnBoardingProfileResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
import liaison.linkit.profile.service.AntecedentsService;
import liaison.linkit.profile.service.EducationService;
import liaison.linkit.profile.service.MiniProfileService;
import liaison.linkit.profile.service.ProfileOnBoardingService;
import liaison.linkit.profile.service.ProfileRegionService;
import liaison.linkit.profile.service.TeamBuildingFieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class ProfileOnBoardingController {

    public final MemberService memberService;
    public final ProfileOnBoardingService profileOnBoardingService;
    public final MiniProfileService miniProfileService;
    public final TeamBuildingFieldService teamBuildingFieldService;
    public final ProfileRegionService profileRegionService;
    public final EducationService educationService;
    public final AntecedentsService antecedentsService;
    
    // 1.5.4 희망 역할 및 보유 기술 생성/수정
    @PostMapping("/private/job/skill")
    @MemberOnly
    public ResponseEntity<Void> createOnBoardingPersonalJobAndSkill(
            @Auth final Accessor accessor,
            @RequestBody @Valid final OnBoardingPersonalJobAndSkillCreateRequest createRequest
    ) throws BadRequestException {
        // 2개 중에 하나라도 null인 경우
        if (createRequest.getSkillNames().isEmpty() || createRequest.getJobRoleNames().isEmpty()) {
            log.info("createRequest 포함된 값에 Null이 존재합니다.");
            throw new BadRequestException(HAVE_TO_INPUT_BOTH_JOB_AND_SKILL);
        } else {
            // 2개 다 입력이 들어온 경우
            profileOnBoardingService.savePersonalJobAndRole(accessor.getMemberId(), createRequest.getJobRoleNames());
            profileOnBoardingService.savePersonalSkill(accessor.getMemberId(), createRequest.getSkillNames());
            // 2개 모두 저장 완료, true로 저장 완료함
            profileOnBoardingService.updateMemberProfileType(accessor.getMemberId());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 온보딩 항목 전체 조회
    @GetMapping("/private/onBoarding")
    @MemberOnly
    public ResponseEntity<?> getOnBoardingProfile(@Auth final Accessor accessor) {
        log.info("내 이력서의 온보딩 정보 항목 조회 요청 발생");
        try {
            profileOnBoardingService.validateProfileByMember(accessor.getMemberId());

            final ProfileOnBoardingIsValueResponse profileOnBoardingIsValueResponse = profileOnBoardingService.getProfileOnBoardingIsValue(
                    accessor.getMemberId());
            final MiniProfileResponse miniProfileResponse = getMiniProfileResponse(accessor.getMemberId(),
                    profileOnBoardingIsValueResponse.isMiniProfile());
            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse = getProfileTeamBuildingResponse(
                    accessor.getMemberId(), profileOnBoardingIsValueResponse.isProfileTeamBuildingField());
            final ProfileRegionResponse profileRegionResponse = getProfileRegionResponse(accessor.getMemberId(),
                    profileOnBoardingIsValueResponse.isProfileRegion());
            final JobAndSkillResponse jobAndSkillResponse = getJobAndSkillResponse(accessor.getMemberId(),
                    profileOnBoardingIsValueResponse.isJobAndSkill());
            final List<EducationResponse> educationResponses = getEducationResponses(accessor.getMemberId(),
                    profileOnBoardingIsValueResponse.isEducation());
            final List<AntecedentsResponse> antecedentsResponses = getAntecedentsResponses(accessor.getMemberId(),
                    profileOnBoardingIsValueResponse.isAntecedents());

            final OnBoardingProfileResponse onBoardingProfileResponse = profileOnBoardingService.getOnBoardingProfile(
                    profileTeamBuildingFieldResponse,
                    profileRegionResponse,
                    jobAndSkillResponse,
                    educationResponses,
                    antecedentsResponses,
                    miniProfileResponse
            );

            return ResponseEntity.ok().body(onBoardingProfileResponse);
        } catch (Exception e) {
            log.error("온보딩 조회 과정에서 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("온보딩 정보를 불러오는 과정에서 문제가 발생했습니다.");
        }
    }

    private MiniProfileResponse getMiniProfileResponse(
            final Long memberId,
            final boolean isMiniProfile
    ) {
        if (isMiniProfile) {
            miniProfileService.validateMiniProfileByMember(memberId);
            return miniProfileService.getPersonalMiniProfile(memberId);
        } else {
            final String memberName = miniProfileService.getMemberName(memberId);
            final List<String> jobRoleNames = miniProfileService.getJobRoleNames(memberId);
            return new MiniProfileResponse(memberName, jobRoleNames);
        }
    }

    // 1.5.2. 희망 팀빌딩 분야 조회
    private ProfileTeamBuildingFieldResponse getProfileTeamBuildingResponse(
            final Long memberId,
            final boolean isProfileTeamBuildingField
    ) {
        if (isProfileTeamBuildingField) {
            teamBuildingFieldService.validateProfileTeamBuildingFieldByMember(memberId);
            return teamBuildingFieldService.getAllProfileTeamBuildingFields(memberId);
        } else {
            return new ProfileTeamBuildingFieldResponse();
        }
    }

    // 1.5.3. 활동 지역 조회
    private ProfileRegionResponse getProfileRegionResponse(
            final Long memberId,
            final boolean isProfileRegion
    ) {
        if (isProfileRegion) {
            profileRegionService.validateProfileRegionByMember(memberId);
            return profileRegionService.getPersonalProfileRegion(memberId);
        } else {
            return null;
        }
    }

    // 1.5.4. 역할 및 보유 기술 조회
    private JobAndSkillResponse getJobAndSkillResponse(
            final Long memberId,
            final boolean isJobAndSkill
    ) {
        if (isJobAndSkill) {
            profileOnBoardingService.validateProfileByMember(memberId);
            return profileOnBoardingService.getJobAndSkill(memberId);
        } else {
            return new JobAndSkillResponse();
        }
    }

    // 1.5.6. 학력 조회
    private List<EducationResponse> getEducationResponses(
            final Long memberId,
            final boolean isEducation
    ) {
        if (isEducation) {
            educationService.validateEducationByMember(memberId);
            return educationService.getAllEducations(memberId);
        } else {
            return null;
        }
    }

    // 1.5.7. 경력 조회
    private List<AntecedentsResponse> getAntecedentsResponses(
            final Long memberId,
            final boolean isAntecedents
    ) {
        if (isAntecedents) {
            antecedentsService.validateAntecedentsByMember(memberId);
            return antecedentsService.getAllAntecedents(memberId);
        } else {
            return null;
        }
    }
}
