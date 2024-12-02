package springboot.seAlgoDB.models;

import java.util.List;

public class SearchRequest {

    private List<String> skills;
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> documents) {
        this.skills = documents;
    }
}