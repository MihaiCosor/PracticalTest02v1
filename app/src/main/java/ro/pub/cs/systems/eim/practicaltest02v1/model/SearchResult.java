package ro.pub.cs.systems.eim.practicaltest02v1.model;

import java.util.ArrayList;

public class SearchResult {
    private final ArrayList<String> results;

    public SearchResult() {
        this.results = new ArrayList<>();
    }

    public void addResult(String result) {
        results.add(result);
    }

    public ArrayList<String> getResults() {
        return results;
    }
}
