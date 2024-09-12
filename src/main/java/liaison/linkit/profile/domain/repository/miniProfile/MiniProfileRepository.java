package liaison.linkit.profile.domain.repository.miniProfile;

import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MiniProfileRepository extends JpaRepository<MiniProfile, Long>, MiniProfileRepositoryCustom {

    @Query(value = """
           SELECT mp FROM MiniProfile mp
           JOIN mp.profile p

           LEFT JOIN ProfileRegion pr ON p.id = pr.profile.id
           LEFT JOIN Region r ON pr.region.id = r.id

           AND (:cityName IS NULL OR r.cityName = :cityName)
           AND (:divisionName IS NULL OR r.divisionName = :divisionName)
           AND (mp.isActivate = true)

           ORDER BY mp.createdDate DESC
           """)
    Page<MiniProfile> findAll(
            @Param("cityName") final String cityName,
            @Param("divisionName") final String divisionName,
            final Pageable pageable
    );
}
