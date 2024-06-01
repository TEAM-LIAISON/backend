package liaison.linkit.team.domain.repository.activity;

import liaison.linkit.team.domain.activity.ActivityMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ActivityMethodRepository extends JpaRepository<ActivityMethod, Long> {

    boolean existsByTeamProfileId(final Long teamProfileId);

    @Modifying
    @Transactional  // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM ActivityMethod activityMethod WHERE activityMethod.teamProfile.id = :teamProfileId")
    void deleteAllByTeamProfileId(@Param("teamProfileId")final Long teamProfileId);

    @Query("SELECT ActivityMethod FROM ActivityMethod activityMethod WHERE activityMethod.teamProfile.id = :teamProfileId")
    List<ActivityMethod> findAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);
}
