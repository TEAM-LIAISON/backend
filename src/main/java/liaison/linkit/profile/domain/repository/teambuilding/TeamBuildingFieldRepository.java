package liaison.linkit.profile.domain.repository.teambuilding;

import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamBuildingFieldRepository extends JpaRepository<TeamBuildingField, Long> {

    @Query("""
           SELECT t
           FROM TeamBuildingField t
           WHERE t.teamBuildingFieldName in :teamBuildingFieldNames
           """)
    List<TeamBuildingField> findTeamBuildingFieldsByFieldNames(@Param("teamBuildingFieldNames") final List<String> teamBuildingFieldNames);
}
