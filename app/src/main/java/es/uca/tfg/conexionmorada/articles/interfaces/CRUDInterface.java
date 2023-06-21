package es.uca.tfg.conexionmorada.articles.interfaces;

import java.util.List;

import es.uca.tfg.conexionmorada.articles.data.Article;
import es.uca.tfg.conexionmorada.articles.data.PayloadArticle;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CRUDInterface {
    @POST("lastArticles")
    Call<List<Article>> lastArticles(@Body PayloadArticle payloadArticle);

    @GET("/query/{query}/{numberArticles}/{comunidad}/{city}")
    Call<List<Article>> query(@Path("query") String query, @Path("numberArticles") int numberArticles,
                              @Path("comunidad") String comunidad, @Path("city") String city);
}
