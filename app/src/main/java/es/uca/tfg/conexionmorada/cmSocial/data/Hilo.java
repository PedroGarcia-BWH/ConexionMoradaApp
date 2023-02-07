package es.uca.tfg.conexionmorada.cmSocial.data;

import java.util.Date;

public class Hilo {

    private String id;

    private String autor;


    private String mensaje;

    private Date dateCreation;

    private Date dateElimination;

    private Hilo hiloPadre;

    public Hilo(){}

    public Hilo(String autor, String mensaje, Hilo hiloPadre) {
        this.autor = autor;
        this.mensaje = mensaje;
        this.hiloPadre = hiloPadre;
        this.dateCreation = new Date();
    }

    public Hilo(Hilo hilo) {
        this.autor = hilo.getAutor();
        this.mensaje = hilo.getMensaje();
        this.hiloPadre = hilo.getHiloPadre();
        this.dateCreation = hilo.getDateCreation();
        this.dateElimination = hilo.getDateElimination();
    }

    //getters

    public String getId() {
        return id;
    }

    public String getAutor() {
        return autor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public Date getDateElimination() {
        return dateElimination;
    }

    public Hilo getHiloPadre() {
        return hiloPadre;
    }


    //setters

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDateElimination(Date dateElimination) {
        this.dateElimination = dateElimination;
    }

    public void setHiloPadre(Hilo hiloPadre) {
        this.hiloPadre = hiloPadre;
    }

}
