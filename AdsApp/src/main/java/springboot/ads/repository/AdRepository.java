package springboot.ads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.ads.entities.Ad;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByCategory(String category);
    List<Ad> findAdsByTargetDemographics(String targetDemographics);
}