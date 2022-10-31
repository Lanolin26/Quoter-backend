package ru.lanolin.quoter.backend.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.repo.QuoteEntityRepository;
import ru.lanolin.quoter.backend.util.RestApi;

import java.util.Optional;

@Service
public class QuoteEntityService implements RestApi<QuoteEntity, Integer> {

	private final QuoteEntityRepository repo;


	public QuoteEntityService(QuoteEntityRepository repo) {
		this.repo = repo;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public QuoteEntityRepository getRepo() {
		return repo;
	}

	@Override
	public void copyProperties(QuoteEntity entity, QuoteEntity inDbEntity) {
		BeanUtils.copyProperties(entity, inDbEntity, "id");
		inDbEntity.setText(
				Optional.ofNullable(inDbEntity.getText())
						.map(text -> text.replaceAll("&" + "nbsp;", " "))
						.map(text -> text.replaceAll(String.valueOf((char) 160), " "))
						.orElse(null));
	}

}
