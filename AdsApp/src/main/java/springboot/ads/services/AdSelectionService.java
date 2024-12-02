package springboot.ads.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springboot.ads.entities.Ad;
import springboot.ads.entities.User;
import springboot.ads.repository.AdRepository;
import springboot.ads.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AdSelectionService {

    private static final Logger logger = LoggerFactory.getLogger(AdSelectionService.class);

    @Autowired
    private AdAnalyticsService adAnalyticsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Transactional
    public Ad selectAd(List<Ad> ads) {
        if (ads == null || ads.isEmpty()) {
            throw new IllegalArgumentException("Ad list cannot be null or empty");
        }

        Ad selectedAd = null;
        double maxBetaSample = Double.NEGATIVE_INFINITY;

        for (Ad ad : ads) {

            double betaSample = sampleBeta(ad.getClicks() + 1, ad.getImpressions() - ad.getClicks() + 1);

            if (betaSample > maxBetaSample) {
                maxBetaSample = betaSample;
                selectedAd = ad;
            }
        }

        if (selectedAd != null) {
            List<User> users = userRepository.findByPreferences(selectedAd.getCategory());

            logger.info("Ad selected: " + selectedAd.getId());

            for (User user : users) {
                adAnalyticsService.trackInteraction(user, selectedAd);
            }

            selectedAd.setImpressions(selectedAd.getImpressions() + 1);
            adRepository.save(selectedAd);
        }

        return selectedAd;
    }

    private double sampleBeta(int alpha, int beta) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        double sample = random.nextDouble();

        return (Math.pow(sample, 1.0/alpha))/(Math.pow(sample, 1.0/alpha) + Math.pow(sample, 1.0/beta));
    }

    public Ad updateAd(Long id){
        if (adRepository.existsById(id)) {

            Ad ad = adRepository.findById(id).get();

            ad.setId(id);
            return adRepository.save(ad);
        }
        return null;
    }
}