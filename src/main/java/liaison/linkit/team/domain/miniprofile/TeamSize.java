package liaison.linkit.team.domain.miniprofile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TeamSize {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "size_type")
    private String sizeType;
}
