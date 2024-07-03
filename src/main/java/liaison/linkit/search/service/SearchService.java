package liaison.linkit.search.service;

import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileKeywordRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileKeywordRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileRepository;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SearchService {

    final MiniProfileRepository miniProfileRepository;
    final MiniProfileKeywordRepository miniProfileKeywordRepository;
    final TeamMiniProfileRepository teamMiniProfileRepository;
    final TeamMiniProfileKeywordRepository teamMiniProfileKeywordRepository;


    @Transactional(readOnly = true)
    public Page<MiniProfileResponse> findPrivateMiniProfile(
            final Pageable pageable,
            final String teamBuildingFieldName,
            final String jobRoleName,
            final String skillName,
            final String cityName,
            String divisionName
    ) {

        if ("전체".equals(divisionName)) {
            divisionName = null;
        }

        Page<MiniProfile> miniProfiles = miniProfileRepository.findAllByOrderByCreatedDateDesc(
                teamBuildingFieldName,
                jobRoleName,
                skillName,
                cityName,
                divisionName,
                pageable
        );

        log.info("miniProfiles={}", miniProfiles);
        return miniProfiles.map(this::convertToMiniProfileResponse);
    }

    @Transactional(readOnly = true)
    public Page<TeamMiniProfileResponse> findTeamMiniProfile(
            final Pageable pageable,
            final String teamBuildingFieldName,
            final String jobRoleName,
            final String skillName,
            final String cityName,
            String divisionName,
            final String activityTagName
    ) {
        Page<TeamMiniProfile> teamMiniProfiles = teamMiniProfileRepository.findAllByOrderByCreatedDateDesc(
                teamBuildingFieldName,
                jobRoleName,
                skillName,
                cityName,
                divisionName,
                activityTagName,
                pageable
        );
        return teamMiniProfiles.map(this::convertToTeamMiniProfileResponse);
    }

    private TeamMiniProfileResponse convertToTeamMiniProfileResponse(final TeamMiniProfile teamMiniProfile) {
        List<String> teamKeywordNames = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(teamMiniProfile.getId()).stream()
                .map(TeamMiniProfileKeyword::getTeamKeywordNames)
                .toList();

        return new TeamMiniProfileResponse(
                teamMiniProfile.getId(),
                teamMiniProfile.getIndustrySector().getSectorName(),
                teamMiniProfile.getTeamScale().getSizeType(),
                teamMiniProfile.getTeamName(),
                teamMiniProfile.getTeamProfileTitle(),
                teamMiniProfile.getTeamUploadPeriod(),
                teamMiniProfile.getTeamUploadDeadline(),
                teamMiniProfile.getTeamLogoImageUrl(),
                teamKeywordNames
        );
    }

    private MiniProfileResponse convertToMiniProfileResponse(final MiniProfile miniProfile) {
        List<String> myKeywordNames = miniProfileKeywordRepository.findAllByMiniProfileId(miniProfile.getId()).stream()
                .map(MiniProfileKeyword::getMyKeywordNames)
                .collect(Collectors.toList());
        return new MiniProfileResponse(
                miniProfile.getId(),
                miniProfile.getProfileTitle(),
                miniProfile.getUploadPeriod(),
                miniProfile.isUploadDeadline(),
                miniProfile.getMiniProfileImg(),
                miniProfile.getMyValue(),
                myKeywordNames
        );
    }
}
