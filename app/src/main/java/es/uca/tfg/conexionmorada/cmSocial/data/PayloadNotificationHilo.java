package es.uca.tfg.conexionmorada.cmSocial.data;

public class PayloadNotificationHilo {

    //username del usario que ha creado la notificación
    private String username;

    private String idHilo;

    private String autorUuid;

    private String mensajeHilo;

    private String hiloPadreUuid;
    private String mensajeNotificacion;

    private String dateCreationNotificacion;

    public PayloadNotificationHilo(){}

    public PayloadNotificationHilo(String username, String idHilo, String autorUuid, String mensajeHilo, String hiloPadreUuid,
                                   String mensajeNotificacion, String dateCreationNotificacion) {
        this.username = username;
        this.idHilo = idHilo;
        this.autorUuid = autorUuid;
        this.mensajeHilo = mensajeHilo;
        this.hiloPadreUuid = hiloPadreUuid;
        this.mensajeNotificacion = mensajeNotificacion;
        this.dateCreationNotificacion = dateCreationNotificacion;
    }

    //Getters y setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdHilo() {
        return idHilo;
    }

    public void setIdHilo(String idHilo) {
        this.idHilo = idHilo;
    }

    public String getAutorUuid() {
        return autorUuid;
    }

    public void setAutorUuid(String autorUuid) {
        this.autorUuid = autorUuid;
    }

    public String getMensajeHilo() {
        return mensajeHilo;
    }

    public void setMensajeHilo(String mensajeHilo) {
        this.mensajeHilo = mensajeHilo;
    }

    public String getHiloPadreUuid() {
        return hiloPadreUuid;
    }

    public void setHiloPadreUuid(String hiloPadreUuid) {
        this.hiloPadreUuid = hiloPadreUuid;
    }

    public String getMensajeNotificacion() {
        return mensajeNotificacion;
    }

    public void setMensajeNotificacion(String mensajeNotificacion) {
        this.mensajeNotificacion = mensajeNotificacion;
    }

    public String getDateCreationNotificacion() {
        return dateCreationNotificacion;
    }

    public void setDateCreationNotificacion(String dateCreationNotificacion) {
        this.dateCreationNotificacion = dateCreationNotificacion;
    }
}
