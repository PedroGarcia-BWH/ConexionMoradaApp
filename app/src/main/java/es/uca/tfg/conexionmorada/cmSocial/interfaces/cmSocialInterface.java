package es.uca.tfg.conexionmorada.cmSocial.interfaces;

import java.util.List;

import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface cmSocialInterface {
    @GET("get/Seguidores/{uuid}")
    Call<Integer> getSeguidores(@Path("uuid") String uuid);

    @GET("get/Seguidos/{uuid}")
    Call<Integer> getSeguidos(@Path("uuid") String uuid);

    @GET("/get/lastHilos/{uuid}")
    Call<List<PayloadHilo>> getLastHilos(@Path("uuid") String uuid);

    @GET("/get/lastHilosSeguidos/{uuid}")
    Call<List<PayloadHilo>> getLastHilosSeguidos(@Path("uuid") String uuid);

    @POST("/add/hilo")
    Call<Void> addHilo(@Body PayloadHilo payloadHilo);

    @POST("/delete/hilo")
    Call<Void> deleteHilo(@Body PayloadHilo hilo);

    @POST("/add/like")
    Call addLike(@Body PayloadHilo hilo, @Body String uuid);

    @POST("/add/dislike")
    Call addDislike(PayloadHilo hilo, String uuid);

    @POST("/delete/like")
    Call deleteLike(PayloadHilo hilo, String uuid);

    @POST("/delete/dislike")
    Call deleteDislike(PayloadHilo hilo, String uuid);

    @GET("/add/userApp/{uuid}")
    Call<Void> addUserApp(@Path("uuid") String uuid);

    @GET("/search/hilos/{search}")
    Call<List<PayloadHilo>> searchHilos(@Path("search") String search);

    @GET("/search/usuarios/{search}")
    Call<List<String>> searchUsuarios(@Path("search") String search);

}
