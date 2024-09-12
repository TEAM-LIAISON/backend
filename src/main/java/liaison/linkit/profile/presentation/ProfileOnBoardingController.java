package liaison.linkit.profile.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.member.service.MemberService;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileOnBoardingIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.onBoarding.OnBoardingProfileResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import liaison.linkit.profile.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class ProfileOnBoardingController {

    public final MemberService memberService;
    public final ProfileOnBoardingService profileOnBoardingService;
    public final MiniProfileService miniProfileService;
    public final ProfileRegionService profileRegionService;
    public final EducationService educationService;
    public final AntecedentsService antecedentsService;

    // 온보딩 항목 전체 조회
    @GetMapping("/private/onBoarding")
    @MemberOnly
    public ResponseEntity<?> getOnBoardingProfile(@Auth final Accessor accessor) {
        log.info("내 이력서의 온보딩 정보 항목 조회 요청 발생");
        try {
            profileOnBoardingService.validateProfileByMember(accessor.getMemberId());

            final ProfileOnBoardingIsValueResponse profileOnBoardingIsValueResponse = profileOnBoardingService.getProfileOnBoardingIsValue(accessor.getMemberId());
            final MiniProfileResponse miniProfileResponse = getMiniProfileResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isMiniProfile());
            final ProfileRegionResponse profileRegionResponse = getProfileRegionResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isProfileRegion());
            final List<EducationResponse> educationResponses = getEducationResponses(accessor.getMemberId(), profileOnBoardingIsValueResponse.isEducation());
            final List<AntecedentsResponse> antecedentsResponses = getAntecedentsResponses(accessor.getMemberId(), profileOnBoardingIsValueResponse.isAntecedents());

            final OnBoardingProfileResponse onBoardingProfileResponse = profileOnBoardingService.getOnBoardingProfile(
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

    private MiniProfileResponse getMiniProfileResponse(
            final Long memberId,
            final boolean isMiniProfile
    ) {
        if (isMiniProfile) {
            miniProfileService.validateMiniProfileByMember(memberId);
            return miniProfileService.getPersonalMiniProfile(memberId);
        } else {
            final String memberName = miniProfileService.getMemberName(memberId);
            return new MiniProfileResponse(memberName);
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
