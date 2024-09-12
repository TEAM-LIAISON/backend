package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.repository.antecedents.AntecedentsRepository;
import liaison.linkit.profile.domain.repository.education.DegreeRepository;
import liaison.linkit.profile.domain.repository.education.EducationRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.domain.repository.teambuilding.ProfileTeamBuildingFieldRepository;
import liaison.linkit.profile.domain.repository.teambuilding.TeamBuildingFieldRepository;
import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.awards.AwardsResponse;
import liaison.linkit.profile.dto.response.browse.BrowsePrivateProfileResponse;
import liaison.linkit.profile.dto.response.completion.CompletionResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.repository.teamProfile.TeamProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BrowsePrivateProfileService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final TeamProfileRepository teamProfileRepository;
    private final MiniProfileRepository miniProfileRepository;
    private final ProfileTeamBuildingFieldRepository profileTeamBuildingFieldRepository;
    private final TeamBuildingFieldRepository teamBuildingFieldRepository;
    private final AntecedentsRepository antecedentsRepository;
    private final EducationRepository educationRepository;
    private final DegreeRepository degreeRepository;

    // 미니 프로필로 해당 내 이력서의 유효성 판단
    public void validatePrivateProfileByMiniProfile(final Long miniProfileId) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId).orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));
        if (!profileRepository.existsById(miniProfile.getProfile().getId())) {
            throw new AuthException(INVALID_MINI_PROFILE_WITH_MEMBER);
        }
    }

    // 미니 프로필 ID로 해당 내 이력서 ID 조회
    public Long getTargetPrivateProfileIdByMiniProfileId(final Long miniProfileId) {
        final Profile profile = getProfileByMiniProfileId(miniProfileId);
        log.info("열람하고자 하는 내 이력서의 ID는 profileI={}입니다.", profile.getId());
        return profile.getId();
    }

    // 미니 프로필로 해당 내 이력서 조회
    private Profile getProfileByMiniProfileId(final Long miniProfileId) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));
        return miniProfile.getProfile();
    }

    // 프로필 (내 이력서) 1개 조회
    private Profile getProfile(final Long browseTargetPrivateProfileId) {
        return profileRepository.findById(browseTargetPrivateProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));
    }

    @Transactional(readOnly = true)
    public ProfileIsValueResponse getProfileIsValue(final Long browseTargetPrivateProfileId) {
        final Profile profile = getProfile(browseTargetPrivateProfileId);
        return ProfileIsValueResponse.profileIsValue(profile);
    }

    public BrowsePrivateProfileResponse getProfileResponse(
            final Long profileId,
            final MiniProfileResponse miniProfileResponse,
            final CompletionResponse completionResponse,
            final ProfileIntroductionResponse profileIntroductionResponse,
            final ProfileRegionResponse profileRegionResponse,
            final List<AntecedentsResponse> antecedentsResponses,
            final List<EducationResponse> educationResponses,
            final List<AwardsResponse> awardsResponses
    ) {
        return BrowsePrivateProfileResponse.privateProfile(
                profileId,
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                profileRegionResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses
        );
    }

    public boolean checkBrowseAuthority(final Long memberId) {
        final Profile profile = profileRepository.findByMemberId(memberId).orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));
        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_ID));

        if (profile.getCompletion() < 50 && teamProfile.getTeamProfileCompletion() < 50) {
            return true;
        } else {
            return false;
        }
    }
}
