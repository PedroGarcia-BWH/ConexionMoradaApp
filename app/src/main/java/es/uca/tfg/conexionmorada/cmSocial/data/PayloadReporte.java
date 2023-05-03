package es.uca.tfg.conexionmorada.cmSocial.data;

public class PayloadReporte {

    private String reportadoUuid;

    private String reportadorUuid;

    private String motivo;

    private String descripcion;

    private String mensajeUuid;

    public PayloadReporte(){}

    public PayloadReporte(String reportado_uuid, String reportador_uuid, String motivo, String descripcion, String mensaje_uuid) {
        this.reportadoUuid = reportado_uuid;
        this.reportadorUuid = reportador_uuid;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.mensajeUuid = mensaje_uuid;
    }

    //getters

    public String getReportadoUuid() {
        return reportadoUuid;
    }

    public String getReportadorUuid() {
        return reportadorUuid;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getMensajeUuid() {
        return mensajeUuid;
    }

    //setters

    public void setReportadoUuid(String reportado_uuid) {
        this.reportadoUuid = reportado_uuid;
    }

    public void setReportadorUuid(String reportador_uuid) {
        this.reportadorUuid = reportador_uuid;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setMensajeUuid(String mensaje_uuid) {
        this.mensajeUuid = mensaje_uuid;
    }
}