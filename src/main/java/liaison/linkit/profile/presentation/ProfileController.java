package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.member.service.MemberService;
import liaison.linkit.profile.dto.request.IntroductionRequest;
import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
import liaison.linkit.profile.dto.response.ProfileResponse;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.awards.AwardsResponse;
import liaison.linkit.profile.dto.response.completion.CompletionResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import liaison.linkit.profile.service.*;
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
public class ProfileController {

    public final MemberService memberService;
    public final ProfileOnBoardingService profileOnBoardingService;
    public final ProfileService profileService;
    public final MiniProfileService miniProfileService;
    public final CompletionService completionService;
    public final AntecedentsService antecedentsService;
    public final EducationService educationService;
    public final AwardsService awardsService;
    public final ProfileRegionService profileRegionService;

    // 자기소개 생성/수정 메서드
    @PostMapping("/private/introduction")
    @MemberOnly
    public ResponseEntity<Void> createProfileIntroduction(
            @Auth final Accessor accessor,
            @RequestBody @Valid final IntroductionRequest introductionRequest
    ) {
        profileService.validateProfileByMember(accessor.getMemberId());
        profileService.saveIntroduction(accessor.getMemberId(), introductionRequest);
        return ResponseEntity.ok().build();
    }


    // 내 이력서 전체 조회 GET 메서드
    @GetMapping("/private/profile")
    @MemberOnly
    public ResponseEntity<?> getMyProfile(@Auth final Accessor accessor) {
        log.info("내 이력서의 전체 항목 조회 요청 발생");
        try {
            profileService.validateProfileByMember(accessor.getMemberId());

            final ProfileIsValueResponse profileIsValueResponse = profileService.getProfileIsValue(accessor.getMemberId());

            final boolean isPrivateProfileEssential = (profileIsValueResponse.isProfileRegion() && profileIsValueResponse.isMiniProfile());

            if (!isPrivateProfileEssential) {
                log.info("필수 내 이력서 항목이 존재하지 않습니다.");
                return ResponseEntity.ok().body(new ProfileResponse());
            }

            final MiniProfileResponse miniProfileResponse = getMiniProfileResponse(accessor.getMemberId(), profileIsValueResponse.isMiniProfile());
            log.info("miniProfileResponse={}", miniProfileResponse);

            final CompletionResponse completionResponse = getCompletionResponse(accessor.getMemberId());
            final ProfileIntroductionResponse profileIntroductionResponse = getProfileIntroduction(accessor.getMemberId(), profileIsValueResponse.isIntroduction());
            final ProfileRegionResponse profileRegionResponse = getProfileRegionResponse(accessor.getMemberId(), profileIsValueResponse.isProfileRegion());
            final List<AntecedentsResponse> antecedentsResponses = getAntecedentsResponses(accessor.getMemberId(), profileIsValueResponse.isAntecedents());
            final List<EducationResponse> educationResponses = getEducationResponses(accessor.getMemberId(), profileIsValueResponse.isEducation());
            final List<AwardsResponse> awardsResponses = getAwardsResponses(accessor.getMemberId(), profileIsValueResponse.isAwards());

            final ProfileResponse profileResponse = profileService.getProfileResponse(
                    isPrivateProfileEssential,
                    miniProfileResponse,
                    completionResponse,
                    profileIntroductionResponse,
                    profileRegionResponse,
                    antecedentsResponses,
                    educationResponses,
                    awardsResponses
            );

            return ResponseEntity.ok().body(profileResponse);
        } catch (Exception e) {
            log.error("내 이력서 조회 과정에서 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("내 이력서 정보를 불러오는 과정에서 문제가 발생했습니다.");
        }
    }

    private MiniProfileResponse getMiniProfileResponse(
            final Long memberId,
            final boolean isMiniProfile
    ) {
        // 여기서 자꾸 오류 나는
        if (isMiniProfile) {
            miniProfileService.validateMiniProfileByMember(memberId);
            return miniProfileService.getPersonalMiniProfile(memberId);
        } else {
            final String memberName = miniProfileService.getMemberName(memberId);
            return new MiniProfileResponse(memberName);
        }
    }

    private CompletionResponse getCompletionResponse(
            final Long memberId
    ) {
        return completionService.getCompletion(memberId);
    }

    private ProfileIntroductionResponse getProfileIntroduction(
            final Long memberId,
            final boolean isIntroduction
    ) {
        if (isIntroduction) {
            return profileService.getProfileIntroduction(memberId);
        } else {
            return new ProfileIntroductionResponse();
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

    private List<AwardsResponse> getAwardsResponses(
            final Long memberId,
            final boolean isAwards
    ) {
        if (isAwards) {
            awardsService.validateAwardsByMember(memberId);
            return awardsService.getAllAwards(memberId);
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
