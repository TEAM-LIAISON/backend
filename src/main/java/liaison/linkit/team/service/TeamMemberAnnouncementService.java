package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.domain.repository.teamProfile.TeamProfileRepository;
import liaison.linkit.team.dto.request.announcement.TeamMemberAnnouncementRequest;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.wish.domain.repository.teamWish.TeamWishRepository;
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
public class TeamMemberAnnouncementService {

    private final TeamProfileRepository teamProfileRepository;
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;
    private final TeamWishRepository teamWishRepository;

    // 회원에 대한 팀 소개서 정보를 가져온다. (1개만 저장되어 있음)
    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

    private TeamMemberAnnouncement getTeamMemberAnnouncement(
            final Long teamMemberAnnouncementId
    ) {
        return teamMemberAnnouncementRepository.findById(teamMemberAnnouncementId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_ID));
    }

    public void validateTeamMemberAnnouncement(final Long memberId) {
        if (!teamMemberAnnouncementRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
            throw new AuthException(INVALID_TEAM_MEMBER_ANNOUNCEMENT_WITH_PROFILE);
        }
    }

    @Transactional(readOnly = true)
    public List<TeamMemberAnnouncementResponse> getTeamMemberAnnouncements(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final List<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementRepository.findAllByTeamProfileId(teamProfile.getId());

        return teamMemberAnnouncements.stream()
                .map(this::getTeamMemberAnnouncementResponse)
                .toList();
    }

    private TeamMemberAnnouncementResponse getTeamMemberAnnouncementResponse(
            final TeamMemberAnnouncement teamMemberAnnouncement
    ) {
        final String teamName = teamMemberAnnouncement.getTeamProfile().getTeamMiniProfile().getTeamName();

        return TeamMemberAnnouncementResponse.of(
                teamMemberAnnouncement.getTeamProfile().getTeamMiniProfile().getTeamLogoImageUrl(),
                teamMemberAnnouncement,
                teamName
        );
    }

    // 팀원 공고 저장 메서드
    public Long saveAnnouncement(
            final Long memberId,
            final TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) {
        if (teamMemberAnnouncementRequest.getMainBusiness() == null || teamMemberAnnouncementRequest.getMainBusiness().isEmpty()) {
            throw new BadRequestException(HAVE_TO_INPUT_MAIN_BUSINESS);
        }

        if (teamMemberAnnouncementRequest.getApplicationProcess() == null || teamMemberAnnouncementRequest.getApplicationProcess().isEmpty()) {
            throw new BadRequestException(HAVE_TO_INPUT_APPLICATION_PROCESS);
        }

        final TeamProfile teamProfile = getTeamProfile(memberId);

        if (teamProfile.getIsTeamMemberAnnouncement()) {
            // 메인 객체 우선 저장
            final TeamMemberAnnouncement savedTeamMemberAnnouncement = saveTeamMemberAnnouncement(teamProfile, teamMemberAnnouncementRequest);
            return savedTeamMemberAnnouncement.getId();
        } else {
            // 메인 객체 우선 저장
            final TeamMemberAnnouncement savedTeamMemberAnnouncement = saveTeamMemberAnnouncement(teamProfile, teamMemberAnnouncementRequest);
            teamProfile.updateIsTeamMemberAnnouncement(true);
            teamProfile.updateMemberTeamProfileTypeByCompletion();
            return savedTeamMemberAnnouncement.getId();
        }
    }

    // 팀원 공고 생성/수정
    public void saveAnnouncements(
            final Long memberId,
            final List<TeamMemberAnnouncementRequest> teamMemberAnnouncementRequestList
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        // 팀원 공고 이력이 있다 -> 전체 삭제 -> 재등록 필요
        if (teamMemberAnnouncementRepository.existsByTeamProfileId(teamProfile.getId())) {
            // 전체 삭제 완료
            teamMemberAnnouncementRepository.deleteAllByTeamProfileId(teamProfile.getId());

            // 팀 프로필 업데이트 필요
            // 상태 업데이트 필요

            // 순차적으로 1개씩 저장
            teamMemberAnnouncementRequestList.forEach(request -> {
                saveTeamMemberAnnouncement(teamProfile, request);
            });
        }

        // 팀원 공고가 존재하지 않는다 -> 새로운 생성 필요
        else {
            teamMemberAnnouncementRequestList.forEach(request -> {
                saveTeamMemberAnnouncement(teamProfile, request);
            });
        }
    }

    // 팀원 공고 메인 객체 저장 메서드
    private TeamMemberAnnouncement saveTeamMemberAnnouncement(
            final TeamProfile teamProfile,
            final TeamMemberAnnouncementRequest request
    ) {
        // 저장용 객체 생성
        final TeamMemberAnnouncement newTeamMemberAnnouncement = TeamMemberAnnouncement.of(
                teamProfile,
                request.getMainBusiness(),
                request.getApplicationProcess()
        );
        return teamMemberAnnouncementRepository.save(newTeamMemberAnnouncement);
    }


    // 팀원 공고 1개 삭제 메서드
    public void deleteTeamMemberAnnouncement(
            final Long memberId,
            final Long teamMemberAnnouncementId
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final TeamMemberAnnouncement teamMemberAnnouncement = getTeamMemberAnnouncement(teamMemberAnnouncementId);

        // 누군가가 나의 팀원 공고를 찜했다면
        if (teamWishRepository.existsByTeamMemberAnnouncementId(teamMemberAnnouncement.getId())) {
            // 모든 teamWish 객체를 Usable -> 치환
            teamWishRepository.deleteByTeamMemberAnnouncementId(teamMemberAnnouncement.getId());
        }

        teamMemberAnnouncementRepository.deleteByTeamMemberAnnouncementId(teamMemberAnnouncement.getId());
        log.info("팀원 공고 메인 객체 삭제 완료");

        if (!teamMemberAnnouncementRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamProfile.updateIsTeamMemberAnnouncement(false);
            teamProfile.updateMemberTeamProfileTypeByCompletion();
        }
    }


    public Long updateTeamMemberAnnouncement(
            final Long teamMemberAnnouncementId,
            final TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) {
        if (teamMemberAnnouncementRequest.getMainBusiness() == null || teamMemberAnnouncementRequest.getMainBusiness().isEmpty()) {
            throw new BadRequestException(HAVE_TO_INPUT_MAIN_BUSINESS);
        }

        if (teamMemberAnnouncementRequest.getApplicationProcess() == null || teamMemberAnnouncementRequest.getApplicationProcess().isEmpty()) {
            throw new BadRequestException(HAVE_TO_INPUT_APPLICATION_PROCESS);
        }
        final TeamMemberAnnouncement teamMemberAnnouncement = getTeamMemberAnnouncement(teamMemberAnnouncementId);
        teamMemberAnnouncement.update(teamMemberAnnouncementRequest);
        return teamMemberAnnouncement.getId();
    }
}
