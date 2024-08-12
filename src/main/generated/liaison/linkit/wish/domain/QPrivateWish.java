package liaison.linkit.wish.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPrivateWish is a Querydsl query type for PrivateWish
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPrivateWish extends EntityPathBase<PrivateWish> {

    private static final long serialVersionUID = -712733729L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPrivateWish privateWish = new QPrivateWish("privateWish");

    public final liaison.linkit.global.QBaseEntity _super = new liaison.linkit.global.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.member.domain.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final liaison.linkit.profile.domain.QProfile profile;

    //inherited
    public final EnumPath<liaison.linkit.global.type.StatusType> status = _super.status;

    public final EnumPath<liaison.linkit.wish.domain.type.WishType> wishType = createEnum("wishType", liaison.linkit.wish.domain.type.WishType.class);

    public QPrivateWish(String variable) {
        this(PrivateWish.class, forVariable(variable), INITS);
    }

    public QPrivateWish(Path<? extends PrivateWish> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPrivateWish(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPrivateWish(PathMetadata metadata, PathInits inits) {
        this(PrivateWish.class, metadata, inits);
    }

    public QPrivateWish(Class<? extends PrivateWish> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new liaison.linkit.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.profile = inits.isInitialized("profile") ? new liaison.linkit.profile.domain.QProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

