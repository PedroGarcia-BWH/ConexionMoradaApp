package es.uca.tfg.conexionmorada.usernames.interfaces;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UsernameInterface {

    @POST("addUsername")
    Call<Boolean> addUsername(@Body String username, @Body String uuid);

    @GET("getUsername/{username}")
    Call<Boolean> existUsername(@Path("username") String username);

    @DELETE("deleteUsername/{username}")
    Call<Boolean> deleteUsername(@Path("username") String username);
}
