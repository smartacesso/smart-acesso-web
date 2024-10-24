package br.com.smartacesso.pedestre.model.to;

/**
 * Created by gustavo on 27/06/17.
 */

public class SendService {

    private String token;
    private Long   user;
    private Object object;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }
}
