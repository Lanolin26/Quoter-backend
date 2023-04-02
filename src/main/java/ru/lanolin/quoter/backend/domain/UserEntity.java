package ru.lanolin.quoter.backend.domain;

import lombok.*;
import org.hibernate.Hibernate;
import ru.lanolin.quoter.backend.domain.dto.UserEntityDto;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_entity")
public class UserEntity extends IdentificationClass<Integer, UserEntityDto> {

    @NotBlank
    @NotEmpty
    @NotNull
    private String login;

    @NotBlank
    @NotEmpty
    @NotNull
    private String name;

    @Size(min = 6)
    @NotBlank
    @NotEmpty
    @NotNull
    private String password;

    private String img;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UserRoles> roles = new HashSet<>();

    public UserEntity(Integer id) {
        super(id);
    }

    public UserEntity(Integer id, String login, String name, String password, String img, Set<UserRoles> roles) {
        super(id);
        this.login = login;
        this.name = name;
        this.password = password;
        this.img = img;
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        UserEntity that = (UserEntity) o;
        return id != null && Objects.equals(id, that.id)
                && login != null && Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public UserEntityDto dto() {
        return new UserEntityDto(id, login, name, password, img, roles);
    }
}
