package liaison.linkit.wish.domain.repository.privateWish;

import liaison.linkit.wish.domain.PrivateWish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateWishRepository extends JpaRepository<PrivateWish, Long>, PrivateWishRepositoryCustom {

}
