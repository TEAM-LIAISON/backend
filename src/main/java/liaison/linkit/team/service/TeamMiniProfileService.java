package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.ImageException;
import liaison.linkit.image.domain.ImageFile;
import liaison.linkit.image.domain.S3ImageEvent;
import liaison.linkit.image.infrastructure.S3Uploader;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.miniprofile.IndustrySector;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.miniprofile.TeamScale;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.miniprofile.IndustrySectorRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamScaleRepository;
import liaison.linkit.team.dto.request.miniprofile.TeamMiniProfileCreateRequest;
import liaison.linkit.team.dto.request.onBoarding.OnBoardingFieldTeamInformRequest;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileEarlyOnBoardingResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMiniProfileService {


    private final TeamMiniProfileRepository teamMiniProfileRepository;
    private final TeamProfileRepository teamProfileRepository;
    private final IndustrySectorRepository industrySectorRepository;
    private final TeamScaleRepository teamScaleRepository;

    private final S3Uploader s3Uploader;
    private final ApplicationEventPublisher publisher;

    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_ID));
    }

    private TeamMiniProfile getTeamMiniProfile(final Long teamProfileId) {
        return teamMiniProfileRepository.findByTeamProfileId(teamProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_BY_TEAM_PROFILE_ID));
    }

    // 팀 소개서 유효성 판단
    public void validateTeamMiniProfileByMember(final Long memberId) {
        if (!teamMiniProfileRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID);
        }
    }

    public TeamMiniProfileEarlyOnBoardingResponse getTeamMiniProfileEarlyOnBoarding(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final TeamMiniProfile teamMiniProfile = teamMiniProfileRepository.findByTeamProfileId(teamProfile.getId())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_BY_TEAM_PROFILE_ID));
        return TeamMiniProfileEarlyOnBoardingResponse.personalTeamMiniProfileOnBoarding(teamMiniProfile);
    }

    public void saveOnBoarding(
            final Long memberId,
            final OnBoardingFieldTeamInformRequest onBoardingFieldTeamInformRequest
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        if (teamMiniProfileRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamMiniProfileRepository.deleteByTeamProfileId(teamProfile.getId());
        }

        // IndustrySector 찾기
        final IndustrySector industrySector = industrySectorRepository.findBySectorName(onBoardingFieldTeamInformRequest.getSectorName());

        // TeamScale 찾기
        final TeamScale teamScale = teamScaleRepository.findBySizeType(onBoardingFieldTeamInformRequest.getSizeType());


        final TeamMiniProfile teamMiniProfile = TeamMiniProfile.of(
                teamProfile,
                industrySector,
                teamScale,
                onBoardingFieldTeamInformRequest.getTeamName(),
                null,
                null,
                null,
                null,
                null,
                null
        );

        teamMiniProfileRepository.save(teamMiniProfile);

        teamProfile.updateIsTeamMiniProfile(true);
    }

    public void save(
            final Long memberId,
            final TeamMiniProfileCreateRequest teamMiniProfileCreateRequest,
            final MultipartFile teamMiniProfileImage
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        final String teamMiniProfileImageUrl = saveTeamMiniProfileImage(teamMiniProfileImage);

        final TeamMiniProfile savedTeamMiniProfile = getTeamMiniProfile(teamProfile.getId());

        savedTeamMiniProfile.onBoardingTeamMiniProfile(
                teamMiniProfileCreateRequest.getTeamProfileTitle(),
                teamMiniProfileCreateRequest.getTeamUploadPeriod(),
                teamMiniProfileCreateRequest.isTeamUploadDeadline(),
                teamMiniProfileImageUrl,
                teamMiniProfileCreateRequest.getTeamValue(),
                teamMiniProfileCreateRequest.getTeamDetailInform()
        );
    }

    public String saveTeamMiniProfileImage(
            final MultipartFile teamMiniProfileImage
    ) {
        validateSizeofImage(teamMiniProfileImage);
        final ImageFile imageFile = new ImageFile(teamMiniProfileImage);
        return uploadTeamMiniProfileImage(imageFile);
    }

    private String uploadTeamMiniProfileImage(
            final ImageFile teamMiniProfileImage
    ) {
        try {
            return s3Uploader.uploadMiniProfileImage(teamMiniProfileImage);
        } catch (final ImageException e) {
            publisher.publishEvent(new S3ImageEvent(teamMiniProfileImage.getHashedName()));
            throw e;
        }
    }


    private void validateSizeofImage(
            final MultipartFile teamMiniProfileImage
    ) {
        if (teamMiniProfileImage == null || teamMiniProfileImage.isEmpty()) {
            throw new ImageException(EMPTY_IMAGE);
        }
    }

    public TeamMiniProfileResponse getPersonalTeamMiniProfile(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final TeamMiniProfile teamMiniProfile = getTeamMiniProfile(teamProfile.getId());
        return TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile);
    }



//    private final TeamProfileRepository teamProfileRepository;
//    private final TeamMiniProfileRepository teamMiniProfileRepository;
//
//    public Long validateTeamMiniProfileByMember(final Long memberId) {
//        Long teamProfileId = teamProfileRepository.findByMemberId(memberId).getId();
//        if (!teamMiniProfileRepository.existsByTeamProfileId(teamProfileId)) {
//            throw new AuthException(INVALID_TEAM_MINI_PROFILE_WITH_MEMBER);
//        } else {
//            return teamMiniProfileRepository.findByTeamProfileId(teamProfileId).getId();
//        }
//    }
//
//    public void save(
//            final Long memberId,
//            final TeamMiniProfileCreateRequest teamMiniProfileCreateRequest
//    ) {
//        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);
//
//        final TeamMiniProfile newTeamMiniProfile = TeamMiniProfile.of(
//
//
//                teamMiniProfileCreateRequest.getTeamName(),
//                teamMiniProfileCreateRequest.getTeamOneLineIntroduction(),
//                teamMiniProfileCreateRequest.getTeamLink()
//        );
//
//    }
//
//    @Transactional(readOnly = true)
//    public TeamMiniProfileResponse getTeamMiniProfileDetail(final Long teamMiniProfileId) {
//        final TeamMiniProfile teamMiniProfile = teamMiniProfileRepository.findById(teamMiniProfileId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_ID));
//        return TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile);
//    }


}
