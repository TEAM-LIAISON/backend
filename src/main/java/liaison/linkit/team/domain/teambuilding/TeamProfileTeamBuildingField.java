package liaison.linkit.team.domain.teambuilding;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
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
public class TeamProfileTeamBuildingField {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_profile_team_building_field_id")
    private Long id;

    // 팀빌딩 분야 매핑
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_building_field_id")
    private TeamBuildingField teamBuildingField;

    public static TeamProfileTeamBuildingField of(
            final TeamBuildingField teamBuildingField
    ) {
        return new TeamProfileTeamBuildingField(
                null,
                teamBuildingField
        );
    }
}
