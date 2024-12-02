package springboot.searchEngineAlgo.models;

public class SearchResult {

    private String document;
    private double score;

    public SearchResult(String document, double score) {
        this.document = document;
        this.score = score;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
