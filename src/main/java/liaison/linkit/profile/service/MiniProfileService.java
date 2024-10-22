package liaison.linkit.profile.service;

import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.image.infrastructure.S3Uploader;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.profile.domain.repository.jobRole.ProfileJobRoleRepository;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.implement.MiniProfileQueryAdapter;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileRequestDTO;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO;
import liaison.linkit.scrap.domain.repository.privateScrap.PrivateScrapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MiniProfileService {


    private final ProfileQueryAdapter profileQueryAdapter;
    private final MiniProfileQueryAdapter miniProfileQueryAdapter;
    private final RegionQueryAdapter regionQueryAdapter;

    private final ProfileRepository profileRepository;
    private final MemberBasicInformRepository memberBasicInformRepository;
    private final ProfileJobRoleRepository profileJobRoleRepository;

    private final S3Uploader s3Uploader;
    private final ApplicationEventPublisher publisher;

    private final PrivateScrapRepository privateScrapRepository;

    // 미니 프로필을 조회한다
    @Transactional(readOnly = true)
    public MiniProfileResponseDTO.MiniProfileDetail getMiniProfileDetail(final Long memberId) {
        return miniProfileQueryAdapter.getMiniProfileDetail(memberId);
    }


    // 미니 프로필을 저장한다
    public MiniProfileResponseDTO.SaveMiniProfile saveMiniProfile(
            final Long memberId,
            final MultipartFile profileImage,
            final MiniProfileRequestDTO.SaveMiniProfileRequest saveMiniProfileRequest
    ) {
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        // 프로필 사진을 업데이트한다

        // 포지션을 업데이트한다

        // 활동 지역을 업데이트한다
        final Region region = regionQueryAdapter.findByCityNameAndDivisionName(saveMiniProfileRequest.getCityName(), saveMiniProfileRequest.getDivisionName());
        profile.setRegion(region);

        // 현재 상태를 업데이트한다

        // 프로필 공개 여부를 업데이트한다
        return null;
    }
}
