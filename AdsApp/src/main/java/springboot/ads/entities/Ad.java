package springboot.ads.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ads")
@Data
@NoArgsConstructor
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String category;
    private double weight;
    private String targetDemographics;
    private int impressions = 0;
    private int clicks = 0;

    @OneToMany(mappedBy = "adId")
    private List<UserInteraction> interactions;
}