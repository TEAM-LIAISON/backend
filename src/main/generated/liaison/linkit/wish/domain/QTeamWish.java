package liaison.linkit.wish.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamWish is a Querydsl query type for TeamWish
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamWish extends EntityPathBase<TeamWish> {

    private static final long serialVersionUID = -563800657L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamWish teamWish = new QTeamWish("teamWish");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.member.domain.QMember member;

    public final liaison.linkit.team.domain.QTeam team;

    public QTeamWish(String variable) {
        this(TeamWish.class, forVariable(variable), INITS);
    }

    public QTeamWish(Path<? extends TeamWish> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamWish(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamWish(PathMetadata metadata, PathInits inits) {
        this(TeamWish.class, metadata, inits);
    }

    public QTeamWish(Class<? extends TeamWish> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new liaison.linkit.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.team = inits.isInitialized("team") ? new liaison.linkit.team.domain.QTeam(forProperty("team"), inits.get("team")) : null;
    }

}

