package es.uca.tfg.conexionmorada.cmSocial.data;

public class PayloadHilo {

    private String autorUuid;

    private String mensaje;

    private String hiloPadreUuid;

    //private Date dateCreation;
    private Integer likes;
    private Integer dislikes;

    private Boolean liked;

    private Boolean disliked;

    public PayloadHilo() {}

    public PayloadHilo(String autorUuid, String mensaje, String hiloPadreUuid, Integer likes, Integer dislikes, Boolean liked, Boolean disliked) {
        this.autorUuid = autorUuid;
        this.mensaje = mensaje;
        this.hiloPadreUuid = hiloPadreUuid;
        this.likes = likes;
        this.dislikes = dislikes;
        this.liked = liked;
        this.disliked = disliked;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Boolean getDisliked() {
        return disliked;
    }

    public void setDisliked(Boolean disliked) {
        this.disliked = disliked;
    }

    public String getAutorUuid() {
        return autorUuid;
    }

    public void setAutorUuid(String autor_uuid) {
        this.autorUuid = autor_uuid;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getHiloPadreUuid() {
        return hiloPadreUuid;
    }

    public void setHiloPadreUuid(String hiloPadre_uuid) {
        this.hiloPadreUuid = hiloPadre_uuid;
    }

}

