package springboot.seAlgoDB.services;

import org.springframework.stereotype.Service;
import springboot.seAlgoDB.models.SearchResult;

import java.util.*;

@Service
public class SearchService {

    public List<SearchResult> performSearch(List<String> skills, List<String> usernames, String query) {

        Map<String, int[]> termDocMatrix = buildTermDocumentMatrix(skills, query);

        int numDocs = skills.size();
        int[] queryVector = termDocMatrix.values().stream().mapToInt(arr -> arr[numDocs]).toArray();

        Map<String, Double> scores = new HashMap<>();
        for (int itr = 0; itr < numDocs; itr++) {
            int finalItr = itr;
            int[] skillVector = termDocMatrix.values().stream().mapToInt(arr -> arr[finalItr]).toArray();
            double similarity = cosineSimilarity(skillVector, queryVector);
            scores.put(skills.get(itr), similarity);
        }

        List<SearchResult> results = new ArrayList<>();
        scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    String skill = entry.getKey();
                    double score = entry.getValue();

                    long users = 0;

                    List<String> matchingUsernames = new ArrayList<>();
                    for (int i = 0; i < skills.size(); i++) {
                        if (skills.get(i).toLowerCase().contains(skill.toLowerCase())) {
                            matchingUsernames.add(usernames.get(i));
                            users++;
                        }
                    }

                    System.out.println("Query" + query);
                    System.out.println("Skill" + skill);

                    int queryCount = Arrays.stream(query.toLowerCase().split(" "))
                            .mapToInt(term -> countOccurrences(term, skill))
                            .sum();

                    SearchResult result = new SearchResult(skill, score);
                    result.setUsernames(matchingUsernames);
                    result.setCount(queryCount);

                    results.add(result);
                });

        return results;
    }

    private Map<String, int[]> buildTermDocumentMatrix(List<String> skills, String query) {
        Set<String> uniqueTerms = new HashSet<>();
        skills.forEach(doc -> uniqueTerms.addAll(Arrays.asList(doc.toLowerCase().split(" "))));
        uniqueTerms.addAll(Arrays.asList(query.toLowerCase().split(" ")));

        Map<String, int[]> termDocMatrix = new HashMap<>();
        int skillCount = skills.size();
        for (String term : uniqueTerms) {
            int[] frequencies = new int[skillCount + 1];
            for (int itr = 0; itr < skillCount; itr++) {
                frequencies[itr] = countOccurrences(term, skills.get(itr));
            }
            frequencies[skillCount] = countOccurrences(term, query);
            termDocMatrix.put(term, frequencies);
        }

        return termDocMatrix;
    }

    public int countOccurrences(String term, String document) {

        String lowerDoc = document.toLowerCase();
        String lowerTerm = term.toLowerCase();

        int count = 0, index = 0;
        while ((index = lowerDoc.indexOf(lowerTerm, index)) != -1) {
            count++;
            index += lowerTerm.length();
        }

        return count;
    }

    private double cosineSimilarity(int[] skillVector, int[] queryVector) {
        int dotProduct = 0;
        double magnitudeA = 0;
        double magnitudeB = 0;

        for (int itr = 0; itr < skillVector.length; itr++) {
            dotProduct += skillVector[itr] * queryVector[itr];
            magnitudeA += Math.pow(skillVector[itr], 2);
            magnitudeB += Math.pow(queryVector[itr], 2);
        }

        magnitudeA = Math.sqrt(magnitudeA);
        magnitudeB = Math.sqrt(magnitudeB);

        return (magnitudeA == 0 || magnitudeB == 0) ? 0 : dotProduct / (magnitudeA * magnitudeB);
    }
}