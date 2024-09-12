package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamMemberAnnouncementRepository extends JpaRepository<TeamMemberAnnouncement, Long> {

    boolean existsByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

    @Query("SELECT teamMemberAnnouncement FROM TeamMemberAnnouncement teamMemberAnnouncement WHERE teamMemberAnnouncement.teamProfile.id = :teamProfileId AND teamMemberAnnouncement.status = 'USABLE'")
    List<TeamMemberAnnouncement> findAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

    @Query("SELECT teamMemberAnnouncement FROM TeamMemberAnnouncement teamMemberAnnouncement WHERE teamMemberAnnouncement.teamProfile.id = :teamProfileId AND (teamMemberAnnouncement.status = 'USABLE' OR teamMemberAnnouncement.status = 'DELETED')")
    List<TeamMemberAnnouncement> findAllByTeamProfileIdUsableAndDeleted(@Param("teamProfileId") final Long teamProfileId);

    // 사용하지 않음
    @Modifying
    @Transactional
    // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM TeamMemberAnnouncement teamMemberAnnouncement WHERE teamMemberAnnouncement.teamProfile.id = :teamProfileId")
    void deleteAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

    @Query("""
          SELECT DISTINCT tma FROM TeamMemberAnnouncement tma
          JOIN tma.teamProfile tp
            
         LEFT JOIN ActivityRegion ar ON tp.id = ar.teamProfile.id
         LEFT JOIN Region r ON ar.region.id = r.id
         
         LEFT JOIN ActivityMethod am ON tp.id = am.teamProfile.id
         LEFT JOIN ActivityMethodTag amt ON am.activityMethodTag.id = amt.id
         
         LEFT JOIN TeamMiniProfile tmp ON tp.id = tmp.teamProfile.id
         
         WHERE (:cityName IS NULL OR r.cityName = :cityName)
         AND (:divisionName IS NULL OR r.divisionName = :divisionName)
         AND (:activityTagName IS NULL OR
              (SELECT COUNT(amt2.activityTagName) FROM ActivityMethodTag amt2
               JOIN ActivityMethod am2 ON amt2.id = am2.activityMethodTag.id
               WHERE am2.teamProfile.id = tp.id AND amt2.activityTagName IN :activityTagName) = :activityTagCount)
         AND (tmp.isTeamActivate = true)
         AND (tma.status = 'USABLE')
         
         ORDER BY tma.createdAt DESC
         """)
    Page<TeamMemberAnnouncement> findAllByOrderByCreatedDateDesc(
            @Param("cityName") final String cityName,
            @Param("divisionName") final String divisionName,
            @Param("activityTagName") final List<String> activityTagName,
            @Param("activityTagCount") final Long activityTagCount,
            final Pageable pageable
    );


    @Modifying
    @Transactional
    @Query("""
           UPDATE TeamMemberAnnouncement teamMemberAnnouncement
           SET teamMemberAnnouncement.status = 'DELETED'
           WHERE teamMemberAnnouncement.id = :teamMemberAnnouncementId
           """)
    void deleteByTeamMemberAnnouncementId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);
}
