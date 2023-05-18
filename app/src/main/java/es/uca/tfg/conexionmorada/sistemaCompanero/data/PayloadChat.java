package es.uca.tfg.conexionmorada.sistemaCompanero.data;

import javax.xml.transform.sax.SAXResult;

public class PayloadChat {
    private String id;
    private String lastMensaje;
    private String fecha;
    private String uuidUser;
    private String nNotificaciones;


    public PayloadChat() {
    }

    public PayloadChat(String id, String lastMensaje, String fecha, String uuidUser, String nNotificaciones) {
        this.id = id;
        this.lastMensaje = lastMensaje;
        this.fecha = fecha;
        this.uuidUser = uuidUser;
        this.nNotificaciones = nNotificaciones;
    }

    public String getId() {
        return id;
    }

    public String getLastMensaje() {
        return lastMensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public String getUuidUser() {
        return uuidUser;
    }

    public String getnNotificaciones() {
        return nNotificaciones;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLastMensaje(String lastMensaje) {
        this.lastMensaje = lastMensaje;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setUuidUser(String uuidUser) {
        this.uuidUser = uuidUser;
    }

    public void setnNotificaciones(String nNotificaciones) {
        this.nNotificaciones = nNotificaciones;
    }
}
