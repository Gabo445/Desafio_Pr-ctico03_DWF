package sv.edu.udb.dto;

import lombok.Data;
import sv.edu.udb.model.User;

@Data
public class UserDto {
    private Integer idUser;
    private String username;
    private String firstname;
    private String lastname;
    private Integer age;

    public UserDto(User user) {
        this.idUser = user.getIdUser();
        this.username = user.getUsername();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.age = user.getAge();
    }
}
