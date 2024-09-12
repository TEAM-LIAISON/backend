package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ExceptionCode;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final ProfileRepository profileRepository;

    // 프로필 (내 이력서) 1개 조회
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

    // 생성/수정/삭제 포함
    public void saveIntroduction(
            final Long memberId,
            final IntroductionRequest introductionRequest
    ) {
        final Profile profile = getProfileByMember(memberId);
        profile.updateIntroduction(introductionRequest.getIntroduction());
    }

    @Transactional(readOnly = true)
    public ProfileIsValueResponse getProfileIsValue(final Long memberId) {
        final Profile profile = getProfileByMember(memberId);
        return ProfileIsValueResponse.profileIsValue(profile);
    }

    @Transactional(readOnly = true)
    public ProfileIntroductionResponse getProfileIntroduction(final Long memberId) {
        final Profile profile = getProfileByMember(memberId);
        return ProfileIntroductionResponse.profileIntroduction(profile);
    }

    @Transactional(readOnly = true)
    public ProfileIntroductionResponse getBrowseProfileIntroduction(final Long profileId) {
        final Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));
        return ProfileIntroductionResponse.profileIntroduction(profile);
    }

    public ProfileResponse getProfileResponse(
            final boolean isPrivateProfileEssential,
            final MiniProfileResponse miniProfileResponse,
            final CompletionResponse completionResponse,
            final ProfileIntroductionResponse profileIntroductionResponse,
            final ProfileRegionResponse profileRegionResponse,
            final List<AntecedentsResponse> antecedentsResponses,
            final List<EducationResponse> educationResponses,
            final List<AwardsResponse> awardsResponses
    ) {
        return ProfileResponse.profileItems(
                isPrivateProfileEssential,
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                profileRegionResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses
        );
    }
}
