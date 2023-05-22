package es.uca.tfg.conexionmorada.sistemaCompanero.interfaces;

import java.util.List;

import es.uca.tfg.conexionmorada.cmSocial.data.PayloadSeguidores;
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadChat;
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadMensaje;
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadPuntoCompanero;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SistemaCompaneroInterface {

    @GET("/puntoCompanero/all")
    Call<List<PayloadPuntoCompanero>> getAllPointsActive();

    @POST("/puntoCompanero/add")
    Call<Void> addPuntoCompanero(@Body PayloadPuntoCompanero payloadPuntoCompanero);

    @PUT("/puntoCompanero/accept/{id}/{uuid}")
    Call<Boolean> aceptPuntoCompanero(@Path("id") String id, @Path("uuid") String uuid);

    @GET("/puntoCompanero/uuid/{uuid}")
    Call<List<PayloadPuntoCompanero>> getAllPointsByUuid(@Path("uuid") String uuid);

    @GET("/puntoCompanero/{id}")
    Call<PayloadPuntoCompanero> getPointById(@Path("id") String id);

    @GET("/chats/{id}")
    Call<List<PayloadChat>> getAllChatsByUuid(@Path("id") String id);

    @GET("/chat/{id}")
    Call<List<PayloadMensaje>> getAllMensajesByChatId(@Path("id") String id);

    @PUT("/check/mensajes/chat/{id}/user/{uuid}")
    Call<Void> checkMensajesChat(@Path("id") String id, @Path("uuid") String uuid);

    @POST("/mensaje")
    Call<Boolean> addMensaje(@Body PayloadMensaje payloadMensaje);
}
