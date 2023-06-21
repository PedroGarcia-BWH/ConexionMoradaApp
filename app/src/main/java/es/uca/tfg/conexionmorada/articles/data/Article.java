package es.uca.tfg.conexionmorada.articles.data;

import java.util.Date;

public class Article {
    private String Id;

    private String title;

    private String description;

    private String body;

    private String urlFrontPage;

    private Date creationDate;

    private Date eliminationDate;

    private String Category;

    public Article(){}

    public Article(String title, String description, String body, String urlFrontPage) {
        this.title = title;
        this.description = description;
        this.body = body;
        this.urlFrontPage = urlFrontPage;
        this.creationDate = new Date();
    }

    //getters
    public String getId() {
        return Id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getBody() {
        return body;
    }
    public String getUrlFrontPage() {
        return urlFrontPage;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public Date getEliminationDate() {
        return eliminationDate;
    }

    public String getCategory() {
        return Category;
    }
    //setters
    public void setId(String id) {
        Id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public void setUrlFrontPage(String urlFrontPage) {
        this.urlFrontPage = urlFrontPage;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public void setEliminationDate(Date eliminationDate) {
        this.eliminationDate = eliminationDate;
    }

    public void setCategory(String category) {
        Category = category;
    }

    @Override
    public String toString() {
        return "Article [Id=" + Id + ", title=" + title + ", description=" + description + ", body=" + body
                + ", urlFrontPage=" + urlFrontPage + ", creationDate=" + creationDate + ", eliminationDate="
                + eliminationDate + ", Category=" + Category + "]";
    }
}
