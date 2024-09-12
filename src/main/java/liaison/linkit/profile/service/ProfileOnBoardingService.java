package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileOnBoardingIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.onBoarding.OnBoardingProfileResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileOnBoardingService {

    private final ProfileRepository profileRepository;

    // 내 이력서 조회
    private Profile getProfileByMember(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 멤버 아이디로 내 이력서 유효성을 검증하는 로직
    public void validateProfileByMember(final Long memberId) {
        if (!profileRepository.existsByMemberId(memberId)) {
            throw new AuthException(ExceptionCode.INVALID_PROFILE_WITH_MEMBER);
        }
    }

    @Transactional(readOnly = true)
    public ProfileOnBoardingIsValueResponse getProfileOnBoardingIsValue(final Long memberId) {
        final Profile profile = getProfileByMember(memberId);
        return ProfileOnBoardingIsValueResponse.profileOnBoardingIsValue(profile);
    }

    public OnBoardingProfileResponse getOnBoardingProfile(
            // 2. 지역 및 위치 정보
            final ProfileRegionResponse profileRegionResponse,
            // 4. 학교 정보
            final List<EducationResponse> educationResponses,
            // 5. 이력 정보
            final List<AntecedentsResponse> antecedentsResponses,
            // 6. 미니 프로필 정보
            final MiniProfileResponse miniProfileResponse
    ) {
        return OnBoardingProfileResponse.onBoardingProfileItems(
                profileRegionResponse,
                educationResponses,
                antecedentsResponses,
                miniProfileResponse
        );
    }
}
