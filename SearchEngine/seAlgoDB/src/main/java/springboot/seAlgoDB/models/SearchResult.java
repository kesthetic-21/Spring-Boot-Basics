package springboot.seAlgoDB.models;

import java.util.List;

public class SearchResult {

    private String document;
    private double score;
    private List<String> usernames;
    private int count;

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public SearchResult(String document, double score) {
        this.document = document;
        this.score = score;
    }

    public String getSkill() {
        return document;
    }

    public void setSkill(String document) {
        this.document = document;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
