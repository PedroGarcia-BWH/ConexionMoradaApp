package es.uca.tfg.conexionmorada.sistemaCompanero.data;

public class PayloadPuntoCompanero {
    private String id;

    private String uuidSolicitante;

    private String uuidAceptante;

    private String markerOrigenLatitud;

    private String markerOrigenLongitud;

    private String markerOrigenTitulo;

    private String markerDestinoLatitud;

    private String markerDestinoLongitud;

    private String markerDestinoTitulo;

    private String dateCreated;

    private String dateEliminated;

    private String dateEvento;

    public PayloadPuntoCompanero() {}

    public  PayloadPuntoCompanero(String id, String uuidSolicitante, String uuidAceptante, String markerOrigenLatitud,
                                  String markerOrigenLongitud, String markerOrigenTitulo, String markerDestinoLatitud, String markerDestinoLongitud,
                                  String markerDestinoTitulo, String dateCreated, String dateEliminated, String dateEvento) {
        this.id = id;
        this.uuidSolicitante = uuidSolicitante;
        this.uuidAceptante = uuidAceptante;
        this.markerOrigenLatitud = markerOrigenLatitud;
        this.markerOrigenLongitud = markerOrigenLongitud;
        this.markerOrigenTitulo = markerOrigenTitulo;
        this.markerDestinoLatitud = markerDestinoLatitud;
        this.markerDestinoLongitud = markerDestinoLongitud;
        this.markerDestinoTitulo = markerDestinoTitulo;
        this.dateCreated = dateCreated;
        this.dateEliminated = dateEliminated;
        this.dateEvento = dateEvento;
    }

    public String getId() {
        return id;
    }

    public String getUuidSolicitante() {
        return uuidSolicitante;
    }

    public String getUuidAceptante() {
        return uuidAceptante;
    }

    public String getMarkerOrigenLatitud() {
        return markerOrigenLatitud;
    }

    public String getMarkerOrigenLongitud() {
        return markerOrigenLongitud;
    }

    public String getMarkerOrigenTitulo() {
        return markerOrigenTitulo;
    }

    public String getMarkerDestinoLatitud() {
        return markerDestinoLatitud;
    }

    public String getMarkerDestinoLongitud() {
        return markerDestinoLongitud;
    }

    public String getMarkerDestinoTitulo() {
        return markerDestinoTitulo;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getDateEliminated() {
        return dateEliminated;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUuidSolicitante(String uuidSolicitante) {
        this.uuidSolicitante = uuidSolicitante;
    }

    public void setUuidAceptante(String uuidAceptante) {
        this.uuidAceptante = uuidAceptante;
    }

    public void setMarkerOrigenLatitud(String markerOrigenLatitud) {
        this.markerOrigenLatitud = markerOrigenLatitud;
    }

    public void setMarkerOrigenLongitud(String markerOrigenLongitud) {
        this.markerOrigenLongitud = markerOrigenLongitud;
    }

    public void setMarkerOrigenTitulo(String markerOrigenTitulo) {
        this.markerOrigenTitulo = markerOrigenTitulo;
    }

    public void setMarkerDestinoLatitud(String markerDestinoLatitud) {
        this.markerDestinoLatitud = markerDestinoLatitud;
    }

    public void setMarkerDestinoLongitud(String markerDestinoLongitud) {
        this.markerDestinoLongitud = markerDestinoLongitud;
    }

    public void setMarkerDestinoTitulo(String markerDestinoTitulo) {
        this.markerDestinoTitulo = markerDestinoTitulo;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setDateEliminated(String dateEliminated) {
        this.dateEliminated = dateEliminated;
    }

    public String getDateEvento() {
        return dateEvento;
    }

    public void setDateEvento(String dateEvento) {
        this.dateEvento = dateEvento;
    }

}

