package liaison.linkit.profile.domain.repository.miniProfile;

import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MiniProfileRepositoryCustom {
    boolean existsByProfileId(final Long profileId);

    Optional<MiniProfile> findByProfileId(@Param("profileId") final Long profileId);

    void deleteByProfileId(@Param("profileId") final Long profileId);

    Slice<MiniProfile> searchAll(
            Long lastIndex,
            Pageable pageable,
            String cityName,
            String divisionName
    );
}
