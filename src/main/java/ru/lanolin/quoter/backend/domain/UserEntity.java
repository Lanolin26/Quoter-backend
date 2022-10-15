package ru.lanolin.quoter.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.Length;

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
@Entity
@Table(name = "user_entity")
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

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
	@ElementCollection
	private Set<UserRoles> roles = new HashSet<>();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
			return false;
		UserEntity that = (UserEntity) o;
		return id != null && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
