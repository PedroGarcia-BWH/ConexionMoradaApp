package es.uca.tfg.conexionmorada.articles.interfaces;

import java.util.List;

import es.uca.tfg.conexionmorada.articles.model.Article;
import es.uca.tfg.conexionmorada.articles.model.PayloadArticle;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CRUDInterface {
    @POST("lastArticles")
    Call<List<Article>> lastArticles(@Body PayloadArticle payloadArticle);
}
