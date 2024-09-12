package liaison.linkit.wish.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileKeywordRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import liaison.linkit.search.dto.response.browseAfterLogin.BrowseMiniProfileResponse;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import liaison.linkit.team.domain.repository.teamProfile.TeamProfileRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.domain.repository.miniprofile.teamMiniProfileKeyword.TeamMiniProfileKeywordRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileRepository;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.wish.domain.PrivateWish;
import liaison.linkit.wish.domain.TeamWish;
import liaison.linkit.wish.domain.repository.privateWish.PrivateWishRepository;
import liaison.linkit.wish.domain.repository.teamWish.TeamWishRepository;
import liaison.linkit.wish.domain.type.WishType;
import liaison.linkit.wish.dto.response.WishTeamProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WishService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final MiniProfileRepository miniProfileRepository;
    private final TeamMiniProfileRepository teamMiniProfileRepository;
    private final PrivateWishRepository privateWishRepository;
    private final TeamWishRepository teamWishRepository;
    private final MemberBasicInformRepository memberBasicInformRepository;
    private final MiniProfileKeywordRepository miniProfileKeywordRepository;
    private final TeamMiniProfileKeywordRepository teamMiniProfileKeywordRepository;
    private final TeamProfileRepository teamProfileRepository;
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;

    // 내 이력서 찜하기 최대 개수 판단 메서드
    public void validateMemberMaxPrivateWish(final Long memberId) {
        final Member member = getMember(memberId);
        final int memberPrivateWishCount = member.getPrivateWishCount();
        if (memberPrivateWishCount >= 8) {
            throw new BadRequestException(CANNOT_CREATE_PRIVATE_WISH_BECAUSE_OF_MAX_COUNT);
        }
    }

    // 팀 소개서 찜하기 최대 개수 판단 메서드
    public void validateMemberMaxTeamWish(final Long memberId) {
        final Member member = getMember(memberId);
        final int memberTeamWishCount = member.getTeamWishCount();
        if (memberTeamWishCount >= 8) {
            throw new BadRequestException(CANNOT_CREATE_TEAM_WISH_BECAUSE_OF_MAX_COUNT);
        }
    }

    // 회원 정보를 가져오는 메서드
    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BY_MEMBER_ID));
    }

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_ID));
    }

    private Profile getProfileByMiniProfileId(
            final Long miniProfileId
    ) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));

        return miniProfile.getProfile();
    }

    public void createWishToPrivateProfile(
            final Long memberId,
            final Long miniProfileId
    ) {
        // 내가
        final Member member = getMember(memberId);
        // 상대방의 내 이력서를
        final Profile profile = getProfileByMiniProfileId(miniProfileId);

        if (Objects.equals(getProfile(memberId).getId(), profile.getId())) {
            throw new BadRequestException(NOT_ALLOW_P2P_WISH);
        }

        // 찜한다
        final PrivateWish privateWish = new PrivateWish(
                null,
                member,
                profile,
                WishType.PROFILE
        );

        privateWishRepository.save(privateWish);
        // 내 이력서 찜하기 카운트 + 1
        member.addPrivateWishCount();
    }

    // 찜하기 취소 메서드
    public void cancelWishToPrivateProfile(
            final Long memberId,
            final Long miniProfileId
    ) {
        final Member member = getMember(memberId);
        final Profile profile = getProfileByMiniProfileId(miniProfileId);
        // 내 이력서 찜하기 객체 삭제
        privateWishRepository.deleteByMemberIdAndProfileId(memberId, profile.getId());
        member.subPrivateWishCount();
    }

    // 팀 소개서 찜하기 메서드
    public void createWishToTeamProfile(
            final Long memberId,
            final Long teamMemberAnnouncementId
    ) {
        final Member member = getMember(memberId);

        final TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementRepository.findById(teamMemberAnnouncementId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_ID));

        // 나의 팀 프로필의 ID -> 내가 찜한 팀원 공고의 팀 프로필과 같은지
        if (Objects.equals(getTeamProfile(memberId).getId(), teamMemberAnnouncement.getTeamProfile().getId())) {
            throw new BadRequestException(NOT_ALLOW_P2T_WISH);
        }

        final TeamWish teamWish = new TeamWish(
                null,
                member,
                teamMemberAnnouncement,
                WishType.TEAM_PROFILE
        );

        // DB에 저장
        teamWishRepository.save(teamWish);

        // 팀 소개서 찜하기 카운트 + 1
        member.addTeamWishCount();
    }

    public void cancelWishToTeamProfile(
            final Long memberId,
            final Long teamMemberAnnouncementId
    ) {
        final Member member = getMember(memberId);

        // 삭제하고자 하는 팀 찜하기 객체 조회
        final TeamWish teamWish = teamWishRepository.findByMemberIdAndTeamMemberAnnouncementId(teamMemberAnnouncementId, memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_WISH_BY_TEAM_MEMBER_ANNOUNCEMENT_ID));

        // 바로 삭제
        teamWishRepository.delete(teamWish);

        // 삭제 이후 -1
        member.subTeamWishCount();
    }

    public List<BrowseMiniProfileResponse> getPrivateProfileWishList(final Long memberId) {
        // 주체 객체 조회
        final Member member = getMember(memberId);

        final List<PrivateWish> privateWishList = privateWishRepository.findAllByMemberId(member.getId());
        return privateWishList.stream()
                .map(privateWish -> {
                    // 상대의 미니 프로필 객체를 가져온다.
                    final MiniProfile miniProfile = privateWish.getProfile().getMiniProfile();
                    final List<MiniProfileKeyword> miniProfileKeywords = getMiniProfileKeywords(miniProfile.getId());
                    // 상대 회원의 기본 정보를 가져와야 한다.
                    final MemberBasicInform memberBasicInform = getMemberBasicInform(miniProfile.getProfile().getMember().getId());
                    // privateWish -> 찾아야함 (내가 이 해당 미니 프로필을 찜해뒀는지?)
                    final boolean isPrivateWish = privateWishRepository.findByMemberIdAndProfileId(memberId, miniProfile.getProfile().getId());

                    return BrowseMiniProfileResponse.personalBrowseMiniProfile(
                            miniProfile,
                            miniProfileKeywords,
                            memberBasicInform.getMemberName(),
                            isPrivateWish
                    );
                })
                .toList();
    }


    // 찜한 상대 팀 (팀원 공고)를 찾아야 한다.
    public List<WishTeamProfileResponse> getTeamProfileWishList(final Long memberId) {
        // 찜한 주체 객체 조회
        final Member member = getMember(memberId);
        // 팀 찜 목록 객체 조회
        final List<TeamWish> teamWishList = teamWishRepository.findAllByMemberId(member.getId());
        log.info("teamWishList={}", teamWishList);

        return teamWishList.stream().map(teamWish -> convertToWishTeamProfileResponse(teamWish, member.getId())).toList();
    }

    // 팀 소개서 응답 변환
    private WishTeamProfileResponse convertToWishTeamProfileResponse(final TeamWish teamWish, final Long memberId) {

        // 팀원 공고를 조회한다.
        final TeamMemberAnnouncement teamMemberAnnouncement = teamWish.getTeamMemberAnnouncement();

        final TeamMiniProfile teamMiniProfile = getTeamMiniProfileByTeamProfileId(teamMemberAnnouncement.getTeamProfile().getId());
        final List<TeamMiniProfileKeyword> teamMiniProfileKeyword = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(teamMiniProfile.getId());

        final String teamName = teamMemberAnnouncement.getTeamProfile().getTeamMiniProfile().getTeamName();

        final boolean isTeamSaved = teamWishRepository.findByTeamMemberAnnouncementIdAndMemberId(teamMemberAnnouncement.getId(), memberId);

        return new WishTeamProfileResponse(
                TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile, teamMiniProfileKeyword),
                TeamMemberAnnouncementResponse.afterLogin(teamMiniProfile.getTeamLogoImageUrl(), teamMemberAnnouncement, teamName, isTeamSaved)
        );
    }



    // 회원 기본 정보를 가져오는 메서드
    private MemberBasicInform getMemberBasicInform(final Long memberId) {
        return memberBasicInformRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID));
    }

    private TeamMiniProfile getTeamMiniProfileByTeamProfileId(final Long teamProfileId) {
        return teamMiniProfileRepository.findByTeamProfileId(teamProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_BY_TEAM_PROFILE_ID));
    }

    private List<MiniProfileKeyword> getMiniProfileKeywords(final Long miniProfileId) {
        return miniProfileKeywordRepository.findAllByMiniProfileId(miniProfileId);
    }
}
