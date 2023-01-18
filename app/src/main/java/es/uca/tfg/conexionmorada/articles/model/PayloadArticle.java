package es.uca.tfg.conexionmorada.articles.model;

import java.util.List;

public class PayloadArticle {
    private String status;
    private List<String> preferences;
    private Integer numberArticles;

    public PayloadArticle() {}

    public PayloadArticle(String status, List<String> preferences, Integer numberArticles) {
        this.status = status;
        this.preferences = preferences;
        this.numberArticles = numberArticles;
    }

    //getters
    public String getStatus() {
        return status;
    }
    public List<String> getPreferences() {
        return preferences;
    }
    public Integer getNumberArticles() {
        return numberArticles;
    }

    //setters
    public void setStatus(String status) {
        this.status = status;
    }
    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }
    public void setNumberArticles(Integer numberArticles) {
        this.numberArticles = numberArticles;
    }
}
