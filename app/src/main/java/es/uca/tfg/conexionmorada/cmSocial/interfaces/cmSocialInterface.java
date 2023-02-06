package es.uca.tfg.conexionmorada.cmSocial.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface cmSocialInterface {
    @GET("getSeguidores/{uuid}")
    Call<Integer> getSeguidores(@Path("uuid") String uuid);

    @GET("getSeguidos/{uuid}")
    Call<Integer> getSeguidos(@Path("uuid") String uuid);
}
