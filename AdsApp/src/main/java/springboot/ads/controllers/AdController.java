package springboot.ads.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.ads.entities.Ad;
import springboot.ads.entities.User;
import springboot.ads.repository.AdRepository;
import springboot.ads.services.AdAnalyticsService;
import springboot.ads.services.AdSelectionService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ads")
public class AdController {

    @Autowired
    private final AdRepository adRepository;

    @Autowired
    private final AdSelectionService adSelectionService;

    @Autowired
    private AdAnalyticsService adAnalyticsService;

    public AdController(AdRepository adRepository, AdSelectionService adSelectionService) {
        this.adRepository = adRepository;
        this.adSelectionService = adSelectionService;
    }

    @PostMapping("/createAd")
    public ResponseEntity<Ad> createAd(@RequestBody Ad ad) {
        Ad createdAd = adRepository.save(ad);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAd);
    }

    @GetMapping("/fetchAd")
    public Ad fetchAd(@RequestParam String category) {
        List<Ad> ads = adRepository.findByCategory(category);

        if (ads.isEmpty()) {
            throw new IllegalArgumentException("No ads available for category: " + category);
        }

        return adSelectionService.selectAd(ads);
    }

    @PutMapping("/updateAd/{id}")
    public Ad updateAd(@PathVariable Long id) {
        Optional<Ad> ads = adRepository.findById(id);

        if (ads.isEmpty()) {
            throw new IllegalArgumentException("No ads available for id: " + id);
        }

        return adSelectionService.updateAd(id);
    }

    @DeleteMapping("/deleteAd/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id) {
        adRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/trackClick")
    public ResponseEntity<Void> trackClick(@RequestBody User user, @RequestBody Ad ad) {
        adAnalyticsService.trackInteraction(user, ad);
        return ResponseEntity.ok().build();
    }

}