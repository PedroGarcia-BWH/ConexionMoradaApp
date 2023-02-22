package es.uca.tfg.conexionmorada.usernames.data;

public class PayloadUsername {
    public String uuid;

    public String username;

    public String token;

    public PayloadUsername(String uuid, String username, String token) {
        this.uuid = uuid;
        this.username = username;
        this.token = token;
    }
}
