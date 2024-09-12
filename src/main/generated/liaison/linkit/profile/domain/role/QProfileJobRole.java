package liaison.linkit.profile.domain.role;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileJobRole is a Querydsl query type for ProfileJobRole
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileJobRole extends EntityPathBase<ProfileJobRole> {

    private static final long serialVersionUID = 1254699013L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileJobRole profileJobRole = new QProfileJobRole("profileJobRole");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QJobRole jobRole;

    public QProfileJobRole(String variable) {
        this(ProfileJobRole.class, forVariable(variable), INITS);
    }

    public QProfileJobRole(Path<? extends ProfileJobRole> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileJobRole(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileJobRole(PathMetadata metadata, PathInits inits) {
        this(ProfileJobRole.class, metadata, inits);
    }

    public QProfileJobRole(Class<? extends ProfileJobRole> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.jobRole = inits.isInitialized("jobRole") ? new QJobRole(forProperty("jobRole")) : null;
    }

}

