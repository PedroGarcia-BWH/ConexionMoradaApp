package es.uca.tfg.conexionmorada.cmSocial.data;

public class PayloadHilo extends Hilo {

    private Integer likes;
    private Integer dislikes;

    private Boolean liked;

    private Boolean disliked;

    public PayloadHilo(Hilo hilo, Integer likes, Integer dislikes, Boolean liked, Boolean disliked) {
        super(hilo);
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
}
