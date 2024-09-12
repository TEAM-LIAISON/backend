package liaison.linkit.team.domain.repository.miniprofile;

import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface TeamMiniProfileRepository extends JpaRepository<TeamMiniProfile, Long> {

    boolean existsByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);


    Optional<TeamMiniProfile> findByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

    @Transactional
    @Modifying
    @Query("DELETE FROM TeamMiniProfile teamMiniProfile WHERE teamMiniProfile.teamProfile.id = :teamProfileId")
    void deleteByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);
}
