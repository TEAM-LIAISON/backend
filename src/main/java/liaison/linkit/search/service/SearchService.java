package liaison.linkit.search.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileKeywordRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.search.dto.response.SearchTeamProfileResponse;
import liaison.linkit.search.dto.response.browseAfterLogin.BrowseMiniProfileResponse;
import liaison.linkit.search.dto.response.browseAfterLogin.SearchBrowseTeamProfileResponse;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileRepository;
import liaison.linkit.team.domain.repository.miniprofile.teamMiniProfileKeyword.TeamMiniProfileKeywordRepository;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.wish.domain.repository.privateWish.PrivateWishRepository;
import liaison.linkit.wish.domain.repository.teamWish.TeamWishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SearchService {
    private final MiniProfileRepository miniProfileRepository;
    private final MemberBasicInformRepository memberBasicInformRepository;
    private final MiniProfileKeywordRepository miniProfileKeywordRepository;
    private final TeamMiniProfileRepository teamMiniProfileRepository;
    private final TeamMiniProfileKeywordRepository teamMiniProfileKeywordRepository;
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;
    private final PrivateWishRepository privateWishRepository;
    private final TeamWishRepository teamWishRepository;

    // 개인 미니 프로필 (페이지) 조회
    @Transactional(readOnly = true)
    public Page<MiniProfileResponse> findPrivateMiniProfile(
            final Pageable pageable,
            final String cityName,
            String divisionName
    ) {

        // 미니 프로필 이력서에서 페이지네이션으로 조회
        final Page<MiniProfile> miniProfiles = miniProfileRepository.findAll(
                cityName,
                divisionName,
                pageable
        );

        log.info("miniProfiles.getNumberOfElements={}", miniProfiles.getNumberOfElements());
        return miniProfiles.map(this::convertToMiniProfileResponse);
    }

    @Transactional(readOnly = true)
    public Page<BrowseMiniProfileResponse> findPrivateMiniProfileLogin(
            final Long memberId,
            final Pageable pageable,
            final String cityName,
            String divisionName
    ) {
        // 미니 프로필 이력서에서 페이지네이션으로 조회
        final Page<MiniProfile> miniProfiles = miniProfileRepository.findAll(
                cityName,
                divisionName,
                pageable
        );

        return miniProfiles.map(miniProfile -> convertToBrowseMiniProfileResponse(miniProfile, memberId));
    }


    // 팀원 공고, 팀 미니 프로필 응답 (페이지) 조회
    @Transactional(readOnly = true)
    public Page<SearchTeamProfileResponse> findTeamMemberAnnouncementsWithTeamMiniProfile(
            final Pageable pageable,
            // 팀 소개서에 해당
            final String cityName,
            // 팀 소개서에 해당
            String divisionName,
            // 팀 소개서에 해당
            final List<String> activityTagName
    ) {

        final Long activityTagCount = (activityTagName != null) ? (long) activityTagName.size() : null;


        // 해당되는 모든 팀원 공고를 조회한다.
        Page<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementRepository.findAllByOrderByCreatedDateDesc(
                cityName,
                divisionName,
                activityTagName,
                activityTagCount,
                pageable
        );

        return teamMemberAnnouncements.map(this::convertToSearchTeamProfileResponse);
    }

    // 팀원 공고, 팀 미니 프로필 응답 (페이지) 조회
    @Transactional(readOnly = true)
    public Page<SearchBrowseTeamProfileResponse> findTeamMemberAnnouncementsWithTeamMiniProfileAfterLogin(
            final Long memberId,
            final Pageable pageable,
            final String cityName,
            String divisionName,
            final List<String> activityTagName
    ) {
        final Long activityTagCount = (activityTagName != null) ? (long) activityTagName.size() : null;

        // 해당되는 모든 팀원 공고를 조회한다.
        Page<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementRepository.findAllByOrderByCreatedDateDesc(
                cityName,
                divisionName,
                activityTagName,
                activityTagCount,
                pageable
        );
        return teamMemberAnnouncements.map(teamMemberAnnouncement -> convertToSearchTeamProfileResponseAfterLogin(teamMemberAnnouncement, memberId));
    }


    private SearchTeamProfileResponse convertToSearchTeamProfileResponse(final TeamMemberAnnouncement teamMemberAnnouncement) {
        // 각각의 개별 팀원 공고를 찾아냈다.
        final TeamMiniProfile teamMiniProfile = getTeamMiniProfileByTeamProfileId(teamMemberAnnouncement.getTeamProfile().getId());
        final List<TeamMiniProfileKeyword> teamMiniProfileKeyword = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(teamMiniProfile.getId());
        final String teamName = teamMemberAnnouncement.getTeamProfile().getTeamMiniProfile().getTeamName();


        return new SearchTeamProfileResponse(
                TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile, teamMiniProfileKeyword),
                TeamMemberAnnouncementResponse.of(
                        teamMiniProfile.getTeamLogoImageUrl(), teamMemberAnnouncement, teamName)
        );
    }

    private SearchBrowseTeamProfileResponse convertToSearchTeamProfileResponseAfterLogin(final TeamMemberAnnouncement teamMemberAnnouncement, final Long memberId) {
        final TeamMiniProfile teamMiniProfile = getTeamMiniProfileByTeamProfileId(teamMemberAnnouncement.getTeamProfile().getId());
        final List<TeamMiniProfileKeyword> teamMiniProfileKeyword = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(teamMiniProfile.getId());
        final String teamName = teamMemberAnnouncement.getTeamProfile().getTeamMiniProfile().getTeamName();
        final boolean isTeamSaved = teamWishRepository.findByTeamMemberAnnouncementIdAndMemberId(teamMemberAnnouncement.getId(), memberId);
        log.info("isTeamSaved={}", isTeamSaved);

        return new SearchBrowseTeamProfileResponse(
                TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile, teamMiniProfileKeyword),
                TeamMemberAnnouncementResponse.afterLogin(teamMiniProfile.getTeamLogoImageUrl(), teamMemberAnnouncement, teamName, isTeamSaved)
        );
    }

    private TeamMiniProfile getTeamMiniProfileByTeamProfileId(final Long teamProfileId) {
        return teamMiniProfileRepository.findByTeamProfileId(teamProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_BY_TEAM_PROFILE_ID));
    }

    // 미니 프로필 객체를 응답 DTO 변환
    private MiniProfileResponse convertToMiniProfileResponse(final MiniProfile miniProfile) {
        final String memberName = getMemberNameByMiniProfile(miniProfile.getId());

        List<String> myKeywordNames = miniProfileKeywordRepository.findAllByMiniProfileId(miniProfile.getId()).stream()
                .map(MiniProfileKeyword::getMyKeywordNames)
                .collect(Collectors.toList());

        return new MiniProfileResponse(
                miniProfile.getId(),
                miniProfile.getProfileTitle(),
                miniProfile.getMiniProfileImg(),
                miniProfile.isActivate(),
                myKeywordNames,
                memberName,
                false
        );
    }

    private BrowseMiniProfileResponse convertToBrowseMiniProfileResponse(final MiniProfile miniProfile, final Long memberId) {
        final String memberName = getMemberNameByMiniProfile(miniProfile.getId());
        List<String> myKeywordNames = miniProfileKeywordRepository.findAllByMiniProfileId(miniProfile.getId()).stream()
                .map(MiniProfileKeyword::getMyKeywordNames)
                .toList();

        // privateWish -> 찾아야함 (내가 이 해당 미니 프로필을 찜해뒀는지?)
        final boolean isPrivateWish = privateWishRepository.findByMemberIdAndProfileId(memberId, miniProfile.getProfile().getId());

        return new BrowseMiniProfileResponse(
                miniProfile.getId(),
                miniProfile.getProfileTitle(),
                miniProfile.getMiniProfileImg(),
                miniProfile.isActivate(),
                myKeywordNames,
                memberName,
                isPrivateWish
        );
    }

    @Transactional(readOnly = true)
    public String getMemberNameByMiniProfile(final Long miniProfileId) {
        final Profile profile = getProfileIdByMiniProfile(miniProfileId);
        // 오류 터짐

        final Member member = profile.getMember();
        final MemberBasicInform memberBasicInform = getMemberBasicInform(member.getId());
        return memberBasicInform.getMemberName();
    }

    private Profile getProfileIdByMiniProfile(final Long miniProfileId) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));
        return miniProfile.getProfile();
    }

    // 회원 기본 정보를 가져오는 메서드
    private MemberBasicInform getMemberBasicInform(final Long memberId) {


        return memberBasicInformRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID));
    }

    @Transactional(readOnly = true)
    public Slice<MiniProfileResponse> searchPrivateMiniProfile(
            final Long lastIndex,
            final Pageable pageable,
            final String cityName,
            final String divisionName
    ) {
        // MiniProfile 조회
        final Slice<MiniProfile> miniProfiles = miniProfileRepository.searchAll(
                lastIndex,
                pageable,
                cityName,
                divisionName
        );

        // MiniProfile을 BrowseMiniProfileResponse로 변환
        return miniProfiles.map(this::convertToMiniProfileResponse);
    }

}
