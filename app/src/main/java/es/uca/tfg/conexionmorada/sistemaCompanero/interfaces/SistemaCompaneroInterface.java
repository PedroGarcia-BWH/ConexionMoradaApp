package es.uca.tfg.conexionmorada.sistemaCompanero.interfaces;

import java.util.List;

import es.uca.tfg.conexionmorada.cmSocial.data.PayloadSeguidores;
import es.uca.tfg.conexionmorada.sistemaCompanero.data.PayloadPuntoCompanero;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SistemaCompaneroInterface {

    @GET("/puntoCompanero/all")
    Call<List<PayloadPuntoCompanero>> getAllPointsActive();

    @POST("/puntoCompanero/add")
    Call<Void> addPuntoCompanero(@Body PayloadPuntoCompanero payloadPuntoCompanero);
}
