package br.chatup.tcc.bean;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import br.chatup.tcc.xmpp.XmppManager;

/**
 * Created by jnde on 08/03/2016.
 * test comment
 */
public class User implements Serializable {

    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("properties")
    private LinkedTreeMap<String, Property> properties;

    public User(String username, String password, String name, String email, LinkedTreeMap<String, Property> properties) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.properties = properties;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LinkedTreeMap<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(LinkedTreeMap<String, Property> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (getUsername() != null ? !getUsername().equals(user.getUsername()) : user.getUsername() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(user.getPassword()) : user.getPassword() != null)
            return false;
        if (getName() != null ? !getName().equals(user.getName()) : user.getName() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(user.getEmail()) : user.getEmail() != null)
            return false;
        return getProperties() != null ? getProperties().equals(user.getProperties()) : user.getProperties() == null;

    }

    @Override
    public int hashCode() {
        int result = getUsername() != null ? getUsername().hashCode() : 0;
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getProperties() != null ? getProperties().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", properties=" + properties +
                '}';
    }

    public static class Property implements Serializable {
        @SerializedName("@key")
        private String key;
        @SerializedName("@value")
        private String value;

        public Property(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

}