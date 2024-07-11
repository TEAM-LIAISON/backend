package liaison.linkit.matching.domain.repository;

import liaison.linkit.matching.domain.PrivateMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrivateMatchingRepository extends JpaRepository<PrivateMatching, Long> {

    @Query("SELECT pm FROM PrivateMatching pm WHERE pm.profile.id = :profileId")
    List<PrivateMatching> findByProfileId(@Param("profileId") final Long profileId);

    @Query("SELECT pm FROM PrivateMatching pm WHERE pm.member.id = :memberId")
    List<PrivateMatching> findByMemberId(@Param("memberId") final Long memberId);

    @Query("SELECT pm FROM PrivateMatching pm WHERE pm.matchingStatus = 'SUCCESSFUL' AND pm.profile.id = :profileId")
    List<PrivateMatching> findSuccessReceivedMatching(@Param("profileId") final Long profileId);

    @Query("SELECT pm FROM PrivateMatching pm WHERE pm.matchingStatus = 'SUCCESSFUL' AND pm.member.id = :memberId")
    List<PrivateMatching> findSuccessRequestMatching(@Param("memberId") final Long memberId);
}
