package es.uca.tfg.conexionmorada.cmSocial.data;

public class PayloadNotificationPersona {

    private String username;

    private String mensaje;

    private String dateCreation;

    public PayloadNotificationPersona(){}

    public PayloadNotificationPersona(String username, String mensaje, String dateCreation) {
        this.username = username;
        this.mensaje = mensaje;
        this.dateCreation = dateCreation;
    }

    //Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }
}