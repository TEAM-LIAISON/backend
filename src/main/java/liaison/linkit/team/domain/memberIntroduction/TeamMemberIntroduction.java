package liaison.linkit.team.domain.memberIntroduction;

import jakarta.persistence.*;
import liaison.linkit.team.domain.TeamProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class TeamMemberIntroduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "team_profile_id")
    private TeamProfile teamProfile;

    @Column(nullable = false)
    private String teamMemberName;

    @Column(nullable = false)
    private String teamMemberRole;

    @Column(nullable = false)
    private String teamMemberIntroductionText;

    public static TeamMemberIntroduction of(
            final TeamProfile teamProfile,
            final String teamMemberName,
            final String teamMemberRole,
            final String teamMemberIntroductionText
    ) {
        return new TeamMemberIntroduction(
                null,
                teamProfile,
                teamMemberName,
                teamMemberRole,
                teamMemberIntroductionText
        );
    }
}
