package liaison.linkit.profile.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfile is a Querydsl query type for Profile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfile extends EntityPathBase<Profile> {

    private static final long serialVersionUID = -419979892L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfile profile = new QProfile("profile");

    public final liaison.linkit.global.QBaseEntity _super = new liaison.linkit.global.QBaseEntity(this);

    public final ListPath<liaison.linkit.profile.domain.awards.Awards, liaison.linkit.profile.domain.awards.QAwards> awardsList = this.<liaison.linkit.profile.domain.awards.Awards, liaison.linkit.profile.domain.awards.QAwards>createList("awardsList", liaison.linkit.profile.domain.awards.Awards.class, liaison.linkit.profile.domain.awards.QAwards.class, PathInits.DIRECT2);

    public final NumberPath<Double> completion = createNumber("completion", Double.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath introduction = createString("introduction");

    public final BooleanPath isAntecedents = createBoolean("isAntecedents");

    public final BooleanPath isAttachUrl = createBoolean("isAttachUrl");

    public final BooleanPath isAwards = createBoolean("isAwards");

    public final BooleanPath isEducation = createBoolean("isEducation");

    public final BooleanPath isIntroduction = createBoolean("isIntroduction");

    public final BooleanPath isJobAndSkill = createBoolean("isJobAndSkill");

    public final BooleanPath isMiniProfile = createBoolean("isMiniProfile");

    public final BooleanPath isProfileJobRole = createBoolean("isProfileJobRole");

    public final BooleanPath isProfileRegion = createBoolean("isProfileRegion");

    public final BooleanPath isProfileSkill = createBoolean("isProfileSkill");

    public final BooleanPath isProfileTeamBuildingField = createBoolean("isProfileTeamBuildingField");

    public final liaison.linkit.member.domain.QMember member;

    public final liaison.linkit.profile.domain.miniProfile.QMiniProfile miniProfile;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    //inherited
    public final EnumPath<liaison.linkit.global.type.StatusType> status = _super.status;

    public QProfile(String variable) {
        this(Profile.class, forVariable(variable), INITS);
    }

    public QProfile(Path<? extends Profile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfile(PathMetadata metadata, PathInits inits) {
        this(Profile.class, metadata, inits);
    }

    public QProfile(Class<? extends Profile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new liaison.linkit.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
        this.miniProfile = inits.isInitialized("miniProfile") ? new liaison.linkit.profile.domain.miniProfile.QMiniProfile(forProperty("miniProfile"), inits.get("miniProfile")) : null;
    }

}

