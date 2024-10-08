package liaison.linkit.team.domain.announcement;

import jakarta.persistence.*;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.dto.request.announcement.TeamMemberAnnouncementRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class TeamMemberAnnouncement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_member_announcement_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_profile_id")
    private TeamProfile teamProfile;

    // 2. 주요 업무
    @Column(name = "main_business")
    private String mainBusiness;

    // 4. 지원 절차
    @Column(name = "application_process")
    private String applicationProcess;

    public static TeamMemberAnnouncement of(
            final TeamProfile teamProfile,
            final String mainBusiness,
            final String applicationProcess
    ) {
        return new TeamMemberAnnouncement(
                null,
                teamProfile,
                mainBusiness,
                applicationProcess
        );
    }

    public void update(
            final TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) {
        this.mainBusiness = teamMemberAnnouncementRequest.getMainBusiness();
        this.applicationProcess = teamMemberAnnouncementRequest.getApplicationProcess();
    }
}
