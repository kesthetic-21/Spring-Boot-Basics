package springboot.searchEngineAlgo.models;

import java.util.List;

public class SearchRequest {

    private List<String> documents;
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }
}