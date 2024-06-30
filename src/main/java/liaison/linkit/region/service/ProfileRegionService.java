package liaison.linkit.region.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.region.domain.ProfileRegion;
import liaison.linkit.profile.domain.repository.ProfileRegionRepository;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.region.dto.request.ProfileRegionCreateRequest;
import liaison.linkit.region.dto.response.ProfileRegionResponse;
import liaison.linkit.region.domain.Region;
import liaison.linkit.team.domain.repository.activity.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileRegionService {

    final ProfileRegionRepository profileRegionRepository;
    final ProfileRepository profileRepository;
    final RegionRepository regionRepository;

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    private ProfileRegion getProfileRegion(final Long profileId) {
        return profileRegionRepository.findByProfileId(profileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_REGION_BY_PROFILE_ID));
    }

    // 멤버 아이디로 미니 프로필의 유효성을 검증하는 로직
    public void validateProfileRegionByMember(final Long memberId) {
        if (!profileRegionRepository.existsByProfileId(getProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_PROFILE_REGION_BY_MEMBER_ID);
        }
    }

    // 활동 지역 및 위치 생성/수정 메서드
    public void save(final Long memberId, final ProfileRegionCreateRequest profileRegionCreateRequest) {
        try {
            final Profile profile = getProfile(memberId);

            if (profileRegionRepository.existsByProfileId(profile.getId())) {
                profileRegionRepository.deleteByProfileId(profile.getId());
                profile.updateIsProfileRegion(false);
                profile.updateMemberProfileTypeByCompletion();
            }

            final Region region = regionRepository
                    .findRegionByCityNameAndDivisionName(profileRegionCreateRequest.getCityName(), profileRegionCreateRequest.getDivisionName()
            );

            if (region == null) {
                throw new IllegalArgumentException("Region not found for city: " +
                        profileRegionCreateRequest.getCityName() + " and division: " +
                        profileRegionCreateRequest.getDivisionName());
            }

            ProfileRegion newProfileRegion = new ProfileRegion(null, profile, region);

            profileRegionRepository.save(newProfileRegion);

            profile.updateIsProfileRegion(true);
            profile.updateMemberProfileTypeByCompletion();
        } catch (IllegalArgumentException e) {
            throw e;  // or return a custom response or error code
        } catch (Exception e) {
            // Handle unexpected exceptions
            throw new RuntimeException("An unexpected error occurred while saving profile region data", e);
        }
    }

    // 내 이력서에 매핑되어 있는 활동 지역 및 위치 조회
    @Transactional(readOnly = true)
    public ProfileRegionResponse getPersonalProfileRegion(final Long memberId) {
        final Profile profile = getProfile(memberId);
        ProfileRegion profileRegion = getProfileRegion(profile.getId());
        return new ProfileRegionResponse(profileRegion.getRegion().getCityName(), profileRegion.getRegion().getDivisionName());
    }

}
