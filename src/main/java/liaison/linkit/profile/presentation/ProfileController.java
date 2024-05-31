package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.DefaultProfileCreateRequest;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import liaison.linkit.profile.dto.response.*;
import liaison.linkit.profile.dto.response.Attach.AttachResponse;
import liaison.linkit.profile.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/onBoarding")
    @MemberOnly
    public ResponseEntity<OnBoardingProfileResponse> getOnBoardingProfile(
            @Auth final Accessor accessor
    ) {
        // 해당 사용자의 내 이력서 PK 조회
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());

        final Long miniProfileId = miniProfileService.validateMiniProfileByMember(accessor.getMemberId());
        log.info("miniProfileId={}", miniProfileId);
        // 해당 PK를 통해서 각 테이블들의 정보를 조회

        // 1. 희망 팀빌딩 분야
        final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse = profileTeamBuildingFieldService.getAllProfileTeamBuildings(accessor.getMemberId());
        log.info("profileTeamBuildingFieldResponse={}", profileTeamBuildingFieldResponse);

        // 2. 희망하는 역할
        final ProfileSkillResponse profileSkillResponse = profileSkillService.getAllProfileSkills(accessor.getMemberId());

        // 3. 지역 및 위치 정보
        final ProfileRegionResponse profileRegionResponse = profileRegionService.getProfileRegion(accessor.getMemberId());

        // 4. 학교 정보
        final List<EducationResponse> educationResponses = educationService.getAllEducations(accessor.getMemberId());

        // 5. 이력 정보
        final List<AntecedentsResponse> antecedentsResponses = antecedentsService.getAllAntecedents(accessor.getMemberId());

        // 6. 미니 프로필 정보
        final MiniProfileResponse miniProfileResponse = miniProfileService.getMiniProfileDetail(miniProfileId);

        final OnBoardingProfileResponse onBoardingProfileResponse = profileService.getOnBoardingProfile(
                profileTeamBuildingFieldResponse,
                profileSkillResponse,
                profileRegionResponse,
                educationResponses,
                antecedentsResponses,
                miniProfileResponse
        );

        return ResponseEntity.ok().body(onBoardingProfileResponse);
    }


    // Default 나의 역량 생성 메서드
    @PostMapping("/default")
    @MemberOnly
    public ResponseEntity<Void> createDefaultProfile(
            @Auth final Accessor accessor,
            @RequestBody @Valid final DefaultProfileCreateRequest defaultProfileCreateRequest
    ) {
        profileService.saveDefault(accessor.getMemberId(), defaultProfileCreateRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/introduction")
    @MemberOnly
    public ResponseEntity<ProfileIntroductionResponse> getProfileIntroduction(
            @Auth final Accessor accessor
    ) {
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        final ProfileIntroductionResponse profileIntroductionResponse = profileService.getProfileIntroduction(profileId);
        return ResponseEntity.ok().body(profileIntroductionResponse);
    }

    @PatchMapping("/introduction")
    @MemberOnly
    public ResponseEntity<Void> updateProfileIntroduction(
            @Auth final Accessor accessor,
            @RequestBody @Valid final ProfileUpdateRequest updateRequest
    ) {
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        profileService.update(profileId, updateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @MemberOnly
    public ResponseEntity<Void> deleteProfileIntroduction(
            @Auth final Accessor accessor
    ) {
        Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        profileService.deleteIntroduction(profileId);
        return ResponseEntity.noContent().build();
    }

    // 마이페이지 컨트롤러
    @GetMapping
    @MemberOnly
    public ResponseEntity<ProfileResponse> getProfile(
            @Auth final Accessor accessor
    ) {
        // ProfileResponse에 [3. 내 이력서]를 모두 담아야 한다.
        final Long profileId = profileService.validateProfileByMember(accessor.getMemberId());
        final Long miniProfileId = miniProfileService.validateMiniProfileByMember(accessor.getMemberId());
        // 미니 프로필 -> 프로필 완성도 -> 자기소개 -> 보유기술 -> 희망 팀빌딩 분야
        // -> 이력 -> 학력 -> 수상 -> 첨부 순으로 기입 필요

        // 1. 미니 프로필
        final MiniProfileResponse miniProfileResponse = miniProfileService.getMiniProfileDetail(miniProfileId);

        // 2. 프로필 완성도
        final CompletionResponse completionResponse = completionService.getCompletion(profileId);

        // 3. 자기소개
        final ProfileIntroductionResponse profileIntroductionResponse = profileService.getProfileIntroduction(profileId);

        // 4. 보유기술
        final ProfileSkillResponse profileSkillResponse = profileSkillService.getAllProfileSkills(accessor.getMemberId());

        // 5. 희망 팀빌딩 분야
        final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse = profileTeamBuildingFieldService.getAllProfileTeamBuildings(accessor.getMemberId());

        // 6. 이력
        final List<AntecedentsResponse> antecedentsResponses = antecedentsService.getAllAntecedents(accessor.getMemberId());

        // 7. 학력
        final List<EducationResponse> educationResponses = educationService.getAllEducations(accessor.getMemberId());

        // 8. 수상
        final List<AwardsResponse> awardsResponses = awardsService.getAllAwards(accessor.getMemberId());

        // 9. 첨부
        final AttachResponse attachResponse = attachService.getAttachList(accessor.getMemberId());

        final ProfileResponse profileResponse = profileService.getProfile(
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                profileSkillResponse,
                profileTeamBuildingFieldResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses,
                attachResponse
        );

        return ResponseEntity.ok().body(profileResponse);
    }
}
