package springboot.ads.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springboot.ads.entities.Ad;
import springboot.ads.entities.User;
import springboot.ads.entities.UserInteraction;
import springboot.ads.repository.AdRepository;
import springboot.ads.repository.UserInteractionRepository;
import springboot.ads.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class AdAnalyticsService {

    @Autowired
    private UserInteractionRepository userInteractionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    public void trackInteraction(User user, Ad ad) {
        ad = adRepository.findById(ad.getId())
                .orElseThrow(() -> new IllegalArgumentException("Ad not found"));

        ad.setClicks(ad.getClicks() + 1);
        adRepository.save(ad);

        UserInteraction interaction = new UserInteraction();
        interaction.setAdId(ad.getId());
        interaction.setUserId(user.getId());
        interaction.setInteractionType("click");
        interaction.setTimestamp(LocalDateTime.now());
        userInteractionRepository.save(interaction);
    }

    public double getCTR(Long ad) {
        if (ad == null) {
            throw new IllegalArgumentException("Ad ID cannot be null");
        }

        long totalImpressions = userInteractionRepository.countByAdId(ad);
        long totalClicks = userInteractionRepository.countByAdIdAndInteractionType(ad, "click");

        return totalImpressions > 0 ? (double) totalClicks / totalImpressions : 0.0;
    }
}
