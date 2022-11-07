package ru.lanolin.quoter.backend.domain;

import lombok.*;
import org.hibernate.Hibernate;
import ru.lanolin.quoter.backend.domain.dto.QuoteEntityDto;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quote_entity")
public class QuoteEntity extends IdentificationClass<QuoteEntityDto>{

	@NotBlank
	@NotEmpty
	@NotNull
	private String text;

	@NotNull
	@OneToOne
	@JoinColumn
	private UserEntity author;

	@NotNull
	@OneToOne
	@JoinColumn
	private QuoteSource source;

	public QuoteEntity(Integer id) {
		super(id);
	}

	public QuoteEntity(Integer id, String text, UserEntity author, QuoteSource source) {
		super(id);
		this.text = text;
		this.author = author;
		this.source = source;
	}

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

	@Override
	public QuoteEntityDto dto() {
		return new QuoteEntityDto(
				this.id, this.text, this.author.dto(), this.source.dto()
		);
	}
}
