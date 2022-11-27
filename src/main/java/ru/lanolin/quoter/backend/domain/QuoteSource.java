package ru.lanolin.quoter.backend.domain;

import lombok.*;
import org.hibernate.Hibernate;
import ru.lanolin.quoter.backend.domain.dto.QuoteSourceDto;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "quote_source_entity")
public class QuoteSource extends IdentificationClass<Integer, QuoteSourceDto>{

	@NotBlank
	@NotEmpty
	@NotNull
	private String sourceName;

	@NotNull
	@OneToOne
	@JoinColumn
	private QuoteSourceType type;

	public QuoteSource(Integer id) {
		super(id);
	}

	public QuoteSource(Integer id, String sourceName, QuoteSourceType type) {
		super(id);
		this.sourceName = sourceName;
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
			return false;
		QuoteSource that = (QuoteSource) o;
		return id != null && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public QuoteSourceDto dto() {
		return new QuoteSourceDto(id, sourceName, Optional.ofNullable(type).map(QuoteSourceType::dto).orElse(null));
	}
}
