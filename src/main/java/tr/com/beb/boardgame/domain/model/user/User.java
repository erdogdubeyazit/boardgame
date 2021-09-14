package tr.com.beb.boardgame.domain.model.user;

public class User {

    private UserId userId;
    private UserRoles role;

    private String username;
    private String password;

    private String name;
    private String surname;

    public User(UserId userId, UserRoles role, String username, String password, String name, String surname) {
        this.userId = userId;
        this.role = role;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public UserRoles getRole() {
        return role;
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

}
