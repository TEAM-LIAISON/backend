package liaison.linkit.team.domain.teambuilding;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamProfileTeamBuildingField is a Querydsl query type for TeamProfileTeamBuildingField
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamProfileTeamBuildingField extends EntityPathBase<TeamProfileTeamBuildingField> {

    private static final long serialVersionUID = -1713509767L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamProfileTeamBuildingField teamProfileTeamBuildingField = new QTeamProfileTeamBuildingField("teamProfileTeamBuildingField");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final liaison.linkit.profile.domain.teambuilding.QTeamBuildingField teamBuildingField;

    public QTeamProfileTeamBuildingField(String variable) {
        this(TeamProfileTeamBuildingField.class, forVariable(variable), INITS);
    }

    public QTeamProfileTeamBuildingField(Path<? extends TeamProfileTeamBuildingField> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamProfileTeamBuildingField(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamProfileTeamBuildingField(PathMetadata metadata, PathInits inits) {
        this(TeamProfileTeamBuildingField.class, metadata, inits);
    }

    public QTeamProfileTeamBuildingField(Class<? extends TeamProfileTeamBuildingField> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.teamBuildingField = inits.isInitialized("teamBuildingField") ? new liaison.linkit.profile.domain.teambuilding.QTeamBuildingField(forProperty("teamBuildingField")) : null;
    }

}

