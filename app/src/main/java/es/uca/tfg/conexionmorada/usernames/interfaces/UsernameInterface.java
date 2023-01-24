package es.uca.tfg.conexionmorada.usernames.interfaces;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UsernameInterface {

    @POST("addUsername")
    Call<Boolean> addUsername(@Body String username);

    @GET("getUsername/{username}")
    Call<Boolean> existUsername(@Path("username") String username);
}
