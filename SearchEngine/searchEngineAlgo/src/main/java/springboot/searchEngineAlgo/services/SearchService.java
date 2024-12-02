package springboot.searchEngineAlgo.services;

import org.springframework.stereotype.Service;
import springboot.searchEngineAlgo.models.SearchResult;

import java.util.*;

@Service
public class SearchService {

    public List<SearchResult> performSearch(List<String> documents, String query) {

        // The Term-Document Matrix (relationship between terms or words and the document)
        Map<String, int[]> termDocMatrix = buildTermDocumentMatrix(documents, query);

        // Calculate Cosine Similarity for Each Document
        int numDocs = documents.size();
        int[] queryVector = termDocMatrix.values().stream().mapToInt(arr -> arr[numDocs]).toArray();

        Map<String, Double> scores = new HashMap<>();
        for (int itr = 0; itr < numDocs; itr++) {
            int finalItr = itr;
            int[] docVector = termDocMatrix.values().stream().mapToInt(arr -> arr[finalItr]).toArray();
            double similarity = cosineSimilarity(docVector, queryVector);
            scores.put(documents.get(itr), similarity);
        }

        // Similarity in docs is prioritized in descending order
        List<SearchResult> results = new ArrayList<>();
        scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> results.add(new SearchResult(entry.getKey(), entry.getValue())));

        return results;
    }

    private Map<String, int[]> buildTermDocumentMatrix(List<String> documents, String query) {
        Set<String> uniqueTerms = new HashSet<>();
        documents.forEach(doc -> uniqueTerms.addAll(Arrays.asList(doc.toLowerCase().split(" "))));
        uniqueTerms.addAll(Arrays.asList(query.toLowerCase().split(" ")));

        Map<String, int[]> termDocMatrix = new HashMap<>();
        int docCount = documents.size();
        for (String term : uniqueTerms) {
            int[] frequencies = new int[docCount + 1];
            for (int itr = 0; itr < docCount; itr++) {
                frequencies[itr] = countOccurrences(term, documents.get(itr));
            }
            frequencies[docCount] = countOccurrences(term, query);
            termDocMatrix.put(term, frequencies);
        }

        return termDocMatrix;
    }

    private int countOccurrences(String term, String document) {
        return (int) Arrays.stream(document.toLowerCase().split(" "))
                .filter(word -> word.equals(term))
                .count();
    }

    private double cosineSimilarity(int[] docVector, int[] queryVector) {
        int dotProduct = 0;
        double magnitudeA = 0;
        double magnitudeB = 0;

        for (int itr = 0; itr < docVector.length; itr++) {
            dotProduct += docVector[itr] * queryVector[itr];
            magnitudeA += Math.pow(docVector[itr], 2);
            magnitudeB += Math.pow(queryVector[itr], 2);
        }

        magnitudeA = Math.sqrt(magnitudeA);
        magnitudeB = Math.sqrt(magnitudeB);

        return (magnitudeA == 0 || magnitudeB == 0) ? 0 : dotProduct / (magnitudeA * magnitudeB);
    }
}