package ru.lanolin.quoter.backend.domain;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class IdentificationClass<ID extends Number, DTO> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected ID id;

	public abstract DTO dto();

}
