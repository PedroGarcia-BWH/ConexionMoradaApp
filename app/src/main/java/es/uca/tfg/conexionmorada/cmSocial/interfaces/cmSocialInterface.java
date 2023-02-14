package es.uca.tfg.conexionmorada.cmSocial.interfaces;

import java.util.List;

import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo;
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadSeguidores;
import es.uca.tfg.conexionmorada.usernames.data.PayloadUsername;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface cmSocialInterface {
    @GET("/get/Seguidores/{uuid}")
    Call<Integer> getSeguidores(@Path("uuid") String uuid);

    @GET("/get/Seguidos/{uuid}")
    Call<Integer> getSeguidos(@Path("uuid") String uuid);

    @POST("/add/Seguidor")
    Call<Void> addSeguidor(@Body PayloadSeguidores payloadSeguidores);

    @DELETE("/delete/Seguidor/{uuidSeguidor}/{uuidSeguido}")
    Call<Void> deleteSeguidor(@Path("uuidSeguidor") String uuidSeguidor, @Path("uuidSeguido") String uuidSeguido);

    @GET("/get/SeguidorExist/{uuidSeguidor}/{uuidSeguido}")
    Call<Boolean> getSeguidorExist(@Path("uuidSeguidor") String uuidSeguidor, @Path("uuidSeguido") String uuidSeguido);

    @GET("/get/lastHilos/{uuid}")
    Call<List<PayloadHilo>> getLastHilos(@Path("uuid") String uuid);

    @GET("/get/lastHilosSeguidos/{uuid}")
    Call<List<PayloadHilo>> getLastHilosSeguidos(@Path("uuid") String uuid);

    @POST("/add/hilo")
    Call<Void> addHilo(@Body PayloadHilo payloadHilo);

    @POST("/delete/hilo")
    Call<Void> deleteHilo(@Body PayloadHilo hilo);

    @POST("/add/like")
    Call<Void> addLike(@Body PayloadHilo hilo);

    @POST("/add/dislike")
    Call<Void> addDislike(@Body PayloadHilo hilo);

    @DELETE("/delete/like/{idHilo}/{uuid}")
    Call<Void> deleteLike(@Path("idHilo") String idHilo, @Path("uuid") String uuid);

    @DELETE("/delete/dislike/{idHilo}/{uuid}")
    Call<Void> deleteDislike(@Path("idHilo") String idHilo, @Path("uuid") String uuid);

    @GET("/get/like/{idHilo}/{uuid}")
    Call<Boolean> getLike(@Path("idHilo") String idHilo, @Path("uuid") String uuid);

    @GET("/get/dislike/{idHilo}/{uuid}")
    Call<Boolean> getDislike(@Path("idHilo") String idHilo, @Path("uuid") String uuid);

    @GET("/add/userApp/{uuid}")
    Call<Void> addUserApp(@Path("uuid") String uuid);

    @GET("/search/hilos/{search}/{uuid}")
    Call<List<PayloadHilo>> searchHilos(@Path("search") String search, @Path("uuid") String uuid);

    @GET("/search/usuarios/{search}")
    Call<List<PayloadUsername>> searchUsuarios(@Path("search") String search);

    @GET("/get/hilos/{uuid}")
    Call<List<PayloadHilo>> getHilosUser(@Path("uuid") String uuid);

    @GET("get/respuesta/{idHilo}/{uuid}")
    Call<List<PayloadHilo>> getRespuestas(@Path("idHilo") String idHilo, @Path("uuid") String uuid);
}
