package tr.com.beb.boardgame.web.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import tr.com.beb.boardgame.domain.application.commands.RegistrationCommand;

public class RegistrationPayload {

    @NotBlank
    @Size(min = 2, max = 50)
    private String username;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @NotBlank
    @Size(min = 1, max = 30)
    private String surname;

    @NotBlank
    @Size(min = 6, max = 30)
    private String password;

    public RegistrationCommand toCommand() {
        return new RegistrationCommand(username, password, name, surname);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
