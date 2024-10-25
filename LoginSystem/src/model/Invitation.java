package model;

import java.util.List;

public class Invitation {
    private List<String> roles;

    public Invitation(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getRoles() {
        return roles;
    }
}
