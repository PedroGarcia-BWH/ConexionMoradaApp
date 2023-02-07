package es.uca.tfg.conexionmorada.cmSocial.interfaces;

import java.util.List;

import es.uca.tfg.conexionmorada.cmSocial.data.Hilo;
import es.uca.tfg.conexionmorada.cmSocial.data.PayloadHilo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface cmSocialInterface {
    @GET("get/Seguidores/{uuid}")
    Call<Integer> getSeguidores(@Path("uuid") String uuid);

    @GET("get/Seguidos/{uuid}")
    Call<Integer> getSeguidos(@Path("uuid") String uuid);

    @POST("/add/UserUuid")
    Call<Boolean> addUserUuid(String uuid);

    @GET("/get/lastHilos/{uuid}")
    Call<List<PayloadHilo>> getLastHilos(@Path("uuid") String uuid);

    @GET("/get/lastHilosSeguidos/{uuid}")
    Call<List<PayloadHilo>> getLastHilosSeguidos(@Path("uuid") String uuid);

    @POST("/add/hilo")
    Call addHilo(PayloadHilo hilo);

    @POST("/delete/hilo")
    Call deleteHilo(PayloadHilo hilo);

    @POST("/add/like")
    Call addLike(Hilo hilo, String uuid);

    @POST("/add/dislike")
    Call addDislike(Hilo hilo, String uuid);

    @POST("/delete/like")
    Call deleteLike(Hilo hilo, String uuid);

    @POST("/delete/dislike")
    Call deleteDislike(Hilo hilo, String uuid);

}
