package liaison.linkit.profile.domain.repository.miniProfile;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.QProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.QMiniProfile;
import liaison.linkit.profile.domain.region.QProfileRegion;
import liaison.linkit.profile.domain.region.QRegion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class MiniProfileRepositoryCustomImpl implements MiniProfileRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QMiniProfile miniProfile = QMiniProfile.miniProfile;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(miniProfile)
                .where(miniProfile.profile.id.eq(profileId))
                .fetchFirst(); // fetchFirst()는 존재 여부를 확인하기 위해 사용됩니다.

        return count != null;
    }

    @Override
    public Optional<MiniProfile> findByProfileId(final Long profileId) {
        QMiniProfile miniProfile = QMiniProfile.miniProfile;

        MiniProfile result = jpaQueryFactory
                .selectFrom(miniProfile)
                .where(miniProfile.profile.id.eq(profileId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public void deleteByProfileId(final Long profileId) {
        QMiniProfile miniProfile = QMiniProfile.miniProfile;

        jpaQueryFactory
                .delete(miniProfile)
                .where(miniProfile.profile.id.eq(profileId))
                .execute();
    }

    @Override
    public Slice<MiniProfile> searchAll(
            Long lastIndex,
            Pageable pageable,
            String cityName,
            String divisionName) {

        QMiniProfile mp = QMiniProfile.miniProfile;
        QProfile p = QProfile.profile;
        QProfileRegion pr = QProfileRegion.profileRegion;
        QRegion r = QRegion.region;

        // 기본 쿼리 생성
        JPAQuery<MiniProfile> query = jpaQueryFactory.selectFrom(mp)
                .join(mp.profile, p)
                .leftJoin(pr).on(p.id.eq(pr.profile.id))
                .leftJoin(r).on(pr.region.id.eq(r.id))
                .where(
                        mp.isActivate.isTrue(),
                        lastIndex != null ? mp.id.lt(lastIndex) : null, // lastIndex를 사용하여 페이지네이션 처리
                        isCityNameMatch(r, cityName),
                        isDivisionNameMatch(r, divisionName)
                )
                .orderBy(mp.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1); // Slice를 위한 limit + 1 설정

        List<MiniProfile> miniProfiles = query.fetch();

        boolean hasNext = miniProfiles.size() > pageable.getPageSize();

        if (hasNext) {
            miniProfiles.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(miniProfiles, pageable, hasNext);
    }

    private BooleanExpression isCityNameMatch(QRegion r, String cityName) {
        if (cityName == null) {
            return null;
        }
        return r.cityName.eq(cityName);
    }

    private BooleanExpression isDivisionNameMatch(QRegion r, String divisionName) {
        if (divisionName == null) {
            return null;
        }
        return r.divisionName.eq(divisionName);
    }

}
