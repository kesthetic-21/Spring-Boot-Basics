package springboot.ads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.ads.entities.UserInteraction;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
    long countByAdId(Long adId);
    long countByAdIdAndInteractionType(Long adId, String interactionType);
}
