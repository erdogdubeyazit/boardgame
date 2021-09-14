package tr.com.beb.boardgame.domain.application.commands;

import org.springframework.util.Assert;

public class RegistrationCommand {

    private String username;
    private String password;
    private String name;
    private String surname;

    public RegistrationCommand(String username, String password, String name, String surname) {
        Assert.hasText(username, "Parameter `username` must not be empty");
        Assert.hasText(password, "Parameter `password` must not be empty");
        Assert.hasText(name, "Parameter `name` must not be empty");
        Assert.hasText(surname, "Parameter `surname` must not be empty");

        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public String toString() {
        return "RegistrationCommand [name=" + name + ", password=" + password + ", surname=" + surname + ", username="
                + username + "]";
    }

}
