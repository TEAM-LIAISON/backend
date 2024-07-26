package liaison.linkit.matching.domain.repository;

import liaison.linkit.matching.domain.TeamMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamMatchingRepository extends JpaRepository<TeamMatching, Long> {

    @Query("SELECT tm FROM TeamMatching tm WHERE tm.teamMemberAnnouncement.id IN :teamMemberAnnouncementIds AND tm.matchingStatusType = 'REQUESTED'")
    List<TeamMatching> findAllByTeamMemberAnnouncementIds(@Param("teamMemberAnnouncementIds") final List<Long> teamMemberAnnouncementIds);

    @Query("SELECT tm FROM TeamMatching tm WHERE tm.member.id = :memberId AND tm.matchingStatusType = 'REQUESTED'")
    List<TeamMatching> findByMemberIdAndMatchingStatus(@Param("memberId") final Long memberId);

    @Query("SELECT tm FROM TeamMatching tm WHERE tm.matchingStatusType = 'SUCCESSFUL' AND tm.teamMemberAnnouncement.id IN :teamMemberAnnouncementIds")
    List<TeamMatching> findSuccessReceivedMatching(@Param("teamMemberAnnouncementIds") final List<Long> teamMemberAnnouncementIds);

    @Query("SELECT tm FROM TeamMatching tm WHERE tm.matchingStatusType = 'SUCCESSFUL' AND tm.member.id = :memberId")
    List<TeamMatching> findSuccessRequestMatching(@Param("memberId")final Long memberId);


    @Modifying
    @Transactional
    @Query("""
           UPDATE TeamMatching teamMatching
           SET teamMatching.status = 'DELETED'
           WHERE teamMatching.member.id = :memberId
           """)
    void deleteByMemberId(@Param("memberId") final Long memberId);

    boolean existsByMemberId(@Param("memberId") final Long memberId);


    @Query("SELECT COUNT(tm) > 0 FROM TeamMatching tm WHERE tm.teamMemberAnnouncement.id IN :teamMemberAnnouncementIds")
    boolean existsByTeamMemberAnnouncementIds(@Param("teamMemberAnnouncementIds") final List<Long> teamMemberAnnouncementIds);

    @Modifying
    @Transactional
    @Query("""
           UPDATE TeamMatching teamMatching
           SET teamMatching.status = 'DELETED'
           WHERE teamMatching.teamMemberAnnouncement.id IN :teamMemberAnnouncementIds
           """)
    void deleteByTeamMemberAnnouncementIds(@Param("teamMemberAnnouncementIds") final List<Long> teamMemberAnnouncementIds);
}
