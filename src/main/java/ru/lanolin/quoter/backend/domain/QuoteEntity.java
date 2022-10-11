package ru.lanolin.quoter.backend.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "quotes_entity")
public class QuoteEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String text;
	@OneToOne
	private UserEntity author;
	//	@OneToOne
	//	private UserEntity lastEditor;
	@OneToOne
	private QuoterFromTypeEntity fromType;
	private String fromName;

	@ElementCollection(targetClass = UserRoles.class)
	@ToString.Exclude
	@Enumerated(EnumType.STRING)
	@Column(name = "roles")
	private Set<UserRoles> roles = new java.util.LinkedHashSet<>();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
			return false;
		QuoteEntity that = (QuoteEntity) o;
		return id != null && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
