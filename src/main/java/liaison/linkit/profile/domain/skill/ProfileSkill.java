package liaison.linkit.profile.domain.skill;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ProfileSkill {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "profile_skill_id")
    private Long id;

    // 역할 및 기술 저장된 skill
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;
}
