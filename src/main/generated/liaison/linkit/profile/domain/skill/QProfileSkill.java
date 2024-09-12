package liaison.linkit.profile.domain.skill;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileSkill is a Querydsl query type for ProfileSkill
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileSkill extends EntityPathBase<ProfileSkill> {

    private static final long serialVersionUID = -1206278430L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileSkill profileSkill = new QProfileSkill("profileSkill");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSkill skill;

    public QProfileSkill(String variable) {
        this(ProfileSkill.class, forVariable(variable), INITS);
    }

    public QProfileSkill(Path<? extends ProfileSkill> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileSkill(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileSkill(PathMetadata metadata, PathInits inits) {
        this(ProfileSkill.class, metadata, inits);
    }

    public QProfileSkill(Class<? extends ProfileSkill> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.skill = inits.isInitialized("skill") ? new QSkill(forProperty("skill")) : null;
    }

}

