package ru.lanolin.quoter.backend.domain;

import lombok.*;
import org.hibernate.Hibernate;
import ru.lanolin.quoter.backend.domain.dto.QuoteSourceTypeDto;

import javax.persistence.Entity;
import javax.persistence.Table;
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
@Table(name = "quote_source_type_entity")
public class QuoteSourceType extends IdentificationClass<Integer, QuoteSourceTypeDto>{

	@NotBlank
	@NotEmpty
	@NotNull
	private String type;

	public QuoteSourceType(Integer id) {
		super(id);
	}

	public QuoteSourceType(Integer id, String type) {
		super(id);
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
			return false;
		QuoteSourceType that = (QuoteSourceType) o;
		return id != null && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public QuoteSourceTypeDto dto() {
		return new QuoteSourceTypeDto(id, type);
	}
}
