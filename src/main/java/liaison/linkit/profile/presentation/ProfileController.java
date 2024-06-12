package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import liaison.linkit.profile.dto.response.*;
import liaison.linkit.profile.service.*;
import liaison.linkit.region.dto.response.ProfileRegionResponse;
import liaison.linkit.region.service.ProfileRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Slf4j
public class ProfileController {
    public final ProfileService profileService;
    public final MiniProfileService miniProfileService;
    public final CompletionService completionService;
    public final ProfileSkillService profileSkillService;
    public final ProfileTeamBuildingFieldService profileTeamBuildingFieldService;
    public final AntecedentsService antecedentsService;
    public final EducationService educationService;
    public final AwardsService awardsService;
    public final AttachService attachService;
    public final ProfileRegionService profileRegionService;

    // 온보딩 과정 GET 요청 조회 로직 (희망 팀빌딩 분야부터 미니 프로필까지 6개)
    // 얘는 무조건 불러오기만 함. 상태 변경하지 않음.
//    @GetMapping("/onBoarding")
//    @MemberOnly
//    public ResponseEntity<OnBoardingProfileResponse> getOnBoardingProfile(
//            @Auth final Accessor accessor
//    ) {
//
//        log.info("---온보딩 조회 요청이 들어왔습니다---");
//        // 해당 사용자의 내 이력서 PK 조회
//        // 일단 프로필이 제대로 생성되었어야 함.
//        final Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
//        log.info("profileId={}", profileId);
//
//        // boolean 값 가져옴
//        final ProfileOnBoardingIsValueResponse profileOnBoardingIsValueResponse
//                = profileService.getProfileOnBoardingIsValue(profileId);
//
//        // 미니 프로필 응답
//        final MiniProfileResponse miniProfileResponse
//                = getMiniProfileResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isMiniProfile());
//
//        log.info("miniProfileResponse={}", miniProfileResponse);
//
//        // 1. 희망 팀빌딩 분야
//        final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse
//                = getProfileTeamBuildingResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isProfileTeamBuildingField());
//        log.info("profileTeamBuildingFieldResponse={}", profileTeamBuildingFieldResponse);
//
//        // 2. 희망하는 역할 및 기술
//        final ProfileSkillResponse profileSkillResponse
//                = getProfileSkillResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isProfileSkill());
//        log.info("profileSkillResponse={}", profileSkillResponse);
//
//        // 3. 지역 및 위치 정보
//        final ProfileRegionResponse profileRegionResponse
//                = getProfileRegionResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isProfileRegion());
//        log.info("profileRegionResponse={}", profileRegionResponse);
//
//        // 4. 학교 정보
//        final List<EducationResponse> educationResponses
//                = getEducationResponses(accessor.getMemberId(), profileOnBoardingIsValueResponse.isEducation());
//        log.info("educationResponses={}", educationResponses);
//
//        // 5. 이력 정보
//        final List<AntecedentsResponse> antecedentsResponses
//                = getAntecedentsResponses(accessor.getMemberId(), profileOnBoardingIsValueResponse.isAntecedents());
//        log.info("antecedentsResponses={}", antecedentsResponses);
//
//        final OnBoardingProfileResponse onBoardingProfileResponse = profileService.getOnBoardingProfile(
//                profileTeamBuildingFieldResponse,
//                profileSkillResponse,
//                profileRegionResponse,
//                educationResponses,
//                antecedentsResponses,
//                miniProfileResponse
//        );
//
//        return ResponseEntity.ok().body(onBoardingProfileResponse);
//    }

    // 온보딩 GET 요청 처리 메서드
    @GetMapping("/onBoarding")
    @MemberOnly
    public ResponseEntity<?> getOnBoardingProfile(@Auth final Accessor accessor) {
        try {
            log.info("---온보딩 조회 요청이 들어왔습니다---");
            profileService.validateProfileByMember(accessor.getMemberId());

            final ProfileOnBoardingIsValueResponse profileOnBoardingIsValueResponse
                    = profileService.getProfileOnBoardingIsValue(accessor.getMemberId());

            final MiniProfileResponse miniProfileResponse
                    = getMiniProfileResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isMiniProfile());

            log.info("miniProfileResponse={}", miniProfileResponse);

            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse
                    = getProfileTeamBuildingResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isProfileTeamBuildingField());
            log.info("profileTeamBuildingFieldResponse={}", profileTeamBuildingFieldResponse);

            final ProfileSkillResponse profileSkillResponse
                    = getProfileSkillResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isProfileSkill());
            log.info("profileSkillResponse={}", profileSkillResponse);

            final ProfileRegionResponse profileRegionResponse
                    = getProfileRegionResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isProfileRegion());
            log.info("profileRegionResponse={}", profileRegionResponse);

            final List<EducationResponse> educationResponses
                    = getEducationResponses(accessor.getMemberId(), profileOnBoardingIsValueResponse.isEducation());
            log.info("educationResponses={}", educationResponses);

            final List<AntecedentsResponse> antecedentsResponses
                    = getAntecedentsResponses(accessor.getMemberId(), profileOnBoardingIsValueResponse.isAntecedents());
            log.info("antecedentsResponses={}", antecedentsResponses);

            final OnBoardingProfileResponse onBoardingProfileResponse = profileService.getOnBoardingProfile(
                    profileTeamBuildingFieldResponse,
                    profileSkillResponse,
                    profileRegionResponse,
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


//    // Default 나의 역량 생성 메서드
//    @PostMapping("/default")
//    @MemberOnly
//    public ResponseEntity<Void> createDefaultProfile(
//            @Auth final Accessor accessor,
//            @RequestBody @Valid final DefaultProfileCreateRequest defaultProfileCreateRequest
//    ) {
//        profileService.saveDefault(accessor.getMemberId(), defaultProfileCreateRequest);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/introduction")
    @MemberOnly
    public ResponseEntity<ProfileIntroductionResponse> getProfileIntroduction(
            @Auth final Accessor accessor
    ) {
        profileService.validateProfileByMember(accessor.getMemberId());
        final ProfileIntroductionResponse profileIntroductionResponse = profileService.getProfileIntroduction(accessor.getMemberId());
        return ResponseEntity.ok().body(profileIntroductionResponse);
    }

    @PatchMapping("/introduction")
    @MemberOnly
    public ResponseEntity<Void> updateProfileIntroduction(
            @Auth final Accessor accessor,
            @RequestBody @Valid final ProfileUpdateRequest updateRequest
    ) {
        profileService.validateProfileByMember(accessor.getMemberId());
        profileService.update(accessor.getMemberId(), updateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/introduction")
    @MemberOnly
    public ResponseEntity<Void> deleteProfileIntroduction(
            @Auth final Accessor accessor
    ) {
        profileService.validateProfileByMember(accessor.getMemberId());
        profileService.deleteIntroduction(accessor.getMemberId());
        return ResponseEntity.noContent().build();
    }

//    // 마이페이지 컨트롤러
//    @GetMapping
//    @MemberOnly
//    public ResponseEntity<ProfileResponse> getProfile(
//            @Auth final Accessor accessor
//    ) {
//        // 내 이력서의 유효성 판단
//        profileService.validateProfileByMember(accessor.getMemberId());
//        // 내 이력서의 미니 프로필의 유효성 판단
//        miniProfileService.validateMiniProfileByMember(accessor.getMemberId());
//
//        // 1. 미니 프로필
//        final MiniProfileResponse miniProfileResponse = miniProfileService.getPersonalMiniProfile(accessor.getMemberId());
//
//        // 2. 프로필 완성도
//        final CompletionResponse completionResponse = completionService.getCompletion(accessor.getMemberId());
//
//        // 3. 자기소개
//        final ProfileIntroductionResponse profileIntroductionResponse = profileService.getProfileIntroduction(accessor.getMemberId());
//
//        // 4. 보유기술
//        final ProfileSkillResponse profileSkillResponse = profileSkillService.getAllProfileSkills(accessor.getMemberId());
//
//        // 5. 희망 팀빌딩 분야
//        final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse = profileTeamBuildingFieldService.getAllProfileTeamBuildings(accessor.getMemberId());
//
//        // 6. 이력
//        final List<AntecedentsResponse> antecedentsResponses = antecedentsService.getAllAntecedents(accessor.getMemberId());
//
//        // 7. 학력
//        final List<EducationResponse> educationResponses = educationService.getAllEducations(accessor.getMemberId());
//
//        // 8. 수상
//        final List<AwardsResponse> awardsResponses = awardsService.getAllAwards(accessor.getMemberId());
//
//        // 9. 첨부
//        final AttachResponse attachResponse = attachService.getAttachList(accessor.getMemberId());
//
//        final ProfileResponse profileResponse = profileService.getProfile(
//                miniProfileResponse,
//                completionResponse,
//                profileIntroductionResponse,
//                profileSkillResponse,
//                profileTeamBuildingFieldResponse,
//                antecedentsResponses,
//                educationResponses,
//                awardsResponses,
//                attachResponse
//        );
//
//        return ResponseEntity.ok().body(profileResponse);
//    }



    private MiniProfileResponse getMiniProfileResponse(
            final Long memberId,
            final boolean isMiniProfile
    ) {
        if (isMiniProfile) {
            miniProfileService.validateMiniProfileByMember(memberId);
            return miniProfileService.getPersonalMiniProfile(memberId);
        } else {
            return new MiniProfileResponse();
        }
    }
    private ProfileTeamBuildingFieldResponse getProfileTeamBuildingResponse(
            final Long memberId,
            final boolean isProfileTeamBuildingField
    ) {
        if (isProfileTeamBuildingField) {
            profileTeamBuildingFieldService.validateProfileTeamBuildingFieldByMember(memberId);
            return profileTeamBuildingFieldService.getAllProfileTeamBuildings(memberId);
        } else {
            return new ProfileTeamBuildingFieldResponse();
        }
    }

    private ProfileSkillResponse getProfileSkillResponse(
            final Long memberId,
            final boolean isProfileSkill
    ) {
        if (isProfileSkill) {
            profileSkillService.validateProfileSkillByMember(memberId);
            return profileSkillService.getAllProfileSkills(memberId);
        } else {
            return null;
        }
    }

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
