package liaison.linkit.profile.domain;

import jakarta.persistence.*;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.type.ProfileType;
import liaison.linkit.profile.domain.awards.Awards;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@SQLRestriction("status = 'USABLE'")
@Slf4j
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    // 3.1. 미니 프로필
    @OneToOne(mappedBy = "profile", cascade = ALL)
    private MiniProfile miniProfile;

    // 3.10. 수상
    @OneToMany(mappedBy = "profile", cascade = REMOVE)
    private List<Awards> awardsList = new ArrayList<>();

    // 3.3. 프로필 완성도
    // 전체 프로필 완성도 값 (%) - 소수점 가능 (double 자료형)
    @Column(nullable = false)
    private double completion;

    // 3.4. 자기소개
    @Column(name = "introduction", length = 300)
    private String introduction;

    // 3.4. 자기소개 기입 여부
    @Column(nullable = false)
    private boolean isIntroduction;

    // 3.5. 직무/역할 및 보유 기술 기입 여부
    @Column(nullable = false)
    private boolean isJobAndSkill;

    // 3.5.1. 직무/역할 기입 여부
    @Column(nullable = false)
    private boolean isProfileJobRole;

    // 3.5.2. 보유 기술 기입 여부
    @Column(nullable = false)
    private boolean isProfileSkill;

    // 3.6. 희망 팀빌딩 분야 기입 여부
    @Column(nullable = false)
    private boolean isProfileTeamBuildingField;

    // 3.7. 활동 지역 및 위치 기입 여부
    @Column(nullable = false)
    private boolean isProfileRegion;

    // 3.8. 이력 기입 여부
    @Column(nullable = false)
    private boolean isAntecedents;

    // 3.9. 학력 기입 여부
    @Column(nullable = false)
    private boolean isEducation;

    // 3.10. 수상 기입 여부
    @Column(nullable = false)
    private boolean isAwards;

//    // 3.11. 첨부 기입 여부
//    @Column(nullable = false)
//    private boolean isAttach;

    // 3.11. 첨부 링크(URL) 기입 여부
    @Column(nullable = false)
    private boolean isAttachUrl;

//    // 3.11. 첨부 파일(File) 기입 여부
//    @Column(nullable = false)
//    private boolean isAttachFile;

    // 3.1. 미니 프로필 기입 여부
    @Column(nullable = false)
    private boolean isMiniProfile;

    public Profile(
            final Long id,
            final Member member,
            final double completion
    ) {
        this.id = id;
        this.member = member;
        this.completion = completion;
        this.introduction = null;
        this.isIntroduction = false;
        this.isJobAndSkill = false;
        this.isProfileJobRole = false;
        this.isProfileSkill = false;
        this.isProfileTeamBuildingField = false;
        this.isProfileRegion = false;
        this.isAntecedents = false;
        this.isEducation = false;
        this.isAwards = false;
//        this.isAttach = false;
        this.isAttachUrl = false;
//        this.isAttachFile = false;
        this.isMiniProfile = false;
    }

    public Profile(
            final Member member,
            final int completion
    ) {
        this(null, member, completion);
    }

    // 기본 입력 항목 (희망 역할 및 기술 / 활동 지역 및 위치 / 희망 팀빌딩 분야 / 이력 / 학력 ) -> 각 10% 총합 50%
    // 자기소개 30%
    // 수상 10%
    // 첨부 10%

    // 디폴트 항목 관리 메서드
    public void addPerfectionDefault() {this.completion += 10.0;}
    public void cancelPerfectionDefault() {this.completion -= 10.0;}

    // 수상 및 첨부 관리 메서드
    public void addPerfectionTen() {this.completion += 10.0;}
    public void cancelPerfectionTen() {this.completion -= 10.0;}

    // 자기소개 관리 메서드
    public void addPerfectionThirty() { this.completion += 30.0; }
    public void cancelPerfectionThirty() {this.completion -= 30.0;}

    // 3.1. 미니 프로필 업데이트
    public void updateIsMiniProfile(final Boolean isMiniProfile) {
        this.isMiniProfile = isMiniProfile;
    }

    // 3.4. 자기소개 업데이트
    // introduction ""인 경우 -> 삭제로 간주한다.
    public void updateIntroduction(final String introduction) {
        if (!Objects.equals(introduction, "")) {            // 삭제 요청이 아닌 수정/생성인 경우
            if (this.introduction != null) {                    // 기존에 데이터가 있는 경우 (수정)
                this.introduction = introduction;
            } else {                                            // 기존에 데이터가 없는 경우 (생성)
                this.introduction = introduction;
                updateIsIntroduction(true);
                addPerfectionThirty();
                updateMemberProfileTypeByCompletion();
            }
        } else {                              // 삭제 요청인 경우
            deleteIntroduction();
            updateIsIntroduction(false);
            cancelPerfectionThirty();
            updateMemberProfileTypeByCompletion();
        }
    }

    // 3.4. 자기소개 업데이트
    public void updateIsIntroduction(final boolean isIntroduction) {
        this.isIntroduction = isIntroduction;
    }

    // 3.5.1. 내 이력서 직무/역할
    public void updateIsProfileJobRole(final boolean isProfileJobRole) {
        log.info("isProfileJobRole={}", isProfileJobRole);
        this.isProfileJobRole = isProfileJobRole;

    }

    // 3.6. 희망 팀빌딩 분야 업데이트
    public void updateIsProfileTeamBuildingField(final boolean isProfileTeamBuildingField) {
        log.info("isProfileTeamBuildingField={}", isProfileTeamBuildingField);
        this.isProfileTeamBuildingField = isProfileTeamBuildingField;
        if (isProfileTeamBuildingField) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    // 3.7. 활동 지역 및 위치 업데이트
    public void updateIsProfileRegion(final boolean isProfileRegion) {
        log.info("isProfileRegion={}", isProfileRegion);
        this.isProfileRegion = isProfileRegion;
        if (isProfileRegion) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    // 3.8. 이력 업데이트
    public void updateIsAntecedents(final boolean isAntecedents) {
        log.info("isAntecedents={}", isAntecedents);
        this.isAntecedents = isAntecedents;
        log.info("this.Antecedents={}", this.isAntecedents);
        if (isAntecedents) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    // 3.9. 학력 업데이트
    public void updateIsEducation(final boolean isEducation) {
        log.info("isEducation={}", isEducation);
        this.isEducation = isEducation;
        if (isEducation) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    // 3.10. 수상 업데이트
    public void updateIsAwards(final boolean isAwards) {
        log.info("isAwards={}", isAwards);
        this.isAwards = isAwards;
        if (isAwards) {
            addPerfectionTen();
        } else {
            cancelPerfectionTen();
        }
    }

    // 3.11. 첨부 업데이트
//    private void updateIsAttach(final boolean isAttachUrl, final boolean isAttachFile) {
//        // attachUrl, attachFile 2개 중에 하나라도 참이면 isAttach는 참이다.
//        if (this.isAttach != (isAttachUrl || isAttachFile)) {
//            this.isAttach = !this.isAttach;
//            if (this.isAttach) {
//                addPerfectionTen();
//            } else {
//                cancelPerfectionTen();
//            }
//        }
//    }

    // 3.11.1 첨부 링크 업데이트
    public void updateIsAttachUrl(final boolean isAttachUrl) {
        this.isAttachUrl = isAttachUrl;
        if (this.isAttachUrl) {
            addPerfectionTen();
        } else {
            cancelPerfectionTen();
        }
    }
    // 3.11.2 첨부 파일 업데이트
//    public void updateIsAttachFile(final boolean isAttachFile) {
//        this.isAttachFile = isAttachFile;
//        updateIsAttach(this.isAttachUrl, isAttachFile);
//    }





    // 내 이력서의 완성도 % 기반으로 접근 권한 관리 메서드
    public void updateMemberProfileTypeByCompletion() {
        final double presentCompletion = this.getCompletion();
        final ProfileType profileType = this.getMember().getProfileType();

        if (presentCompletion >= 0 && presentCompletion < 50) {
            if (ProfileType.NO_PERMISSION.equals(profileType)) {
                return;
            } else {
                // 해당 상태를 변경해줘야함.
                this.getMember().setProfileType(ProfileType.NO_PERMISSION);
            }
        } else if (presentCompletion >= 50 && presentCompletion < 80) {
            if (ProfileType.ALLOW_BROWSE.equals(profileType)) {
                return;
            } else {
                this.getMember().setProfileType(ProfileType.ALLOW_BROWSE);
            }
        } else {
            if (ProfileType.ALLOW_MATCHING.equals(profileType)) {
                return;
            } else {
                this.getMember().setProfileType(ProfileType.ALLOW_MATCHING);
            }
        }
    }

    // 내 이력서의 각 항목들 기입 여부 조회 개별 메서드
    public boolean getIsMiniProfile() { return isMiniProfile; }
    public boolean getIsIntroduction() {
        return isIntroduction;
    }
    public boolean getIsProfileSkill() {
        return isProfileSkill;
    }

    public boolean getIsJobAndSkill() {
        return isJobAndSkill;
    }
    public boolean getIsProfileTeamBuildingField() {
        return isProfileTeamBuildingField;
    }
    public boolean getIsProfileRegion() {
        return isProfileRegion;
    }
    public boolean getIsAntecedents() {return isAntecedents;}
    public boolean getIsEducation() {
        return isEducation;
    }
    public boolean getIsAwards() {
        return isAwards;
    }
    public boolean getIsAttachUrl() {
        return isAttachUrl;
    }


    // 3.4. 자기소개 초기화 및 삭제 메서드
    public void deleteIntroduction() {this.introduction = null;}

    public boolean getExistDefaultPrivateProfile() {
        if (this.isProfileTeamBuildingField && this.isProfileRegion && this.isMiniProfile) {
            return true;
        } else return false;
    }


}
