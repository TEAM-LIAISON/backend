package liaison.linkit.profile.domain.repository.profile;

import liaison.linkit.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long>, ProfileCustomRepository {

}
