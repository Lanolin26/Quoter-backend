package ru.lanolin.quoter.backend.controllers.rest.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.lanolin.quoter.backend.domain.QuoteSource;
import ru.lanolin.quoter.backend.domain.QuoteSourceType;
import ru.lanolin.quoter.backend.domain.view.QuoteEntityInfo;
import ru.lanolin.quoter.backend.service.QuoteEntityService;
import ru.lanolin.quoter.backend.service.QuoteSourceService;
import ru.lanolin.quoter.backend.service.QuoteSourceTypeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/search/v2")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor(onConstructor_ = { @Autowired } )
public class SearchController {

    private final QuoteEntityService quoteEntityService;
    private final QuoteSourceService quoteSourceService;
    private final QuoteSourceTypeService quoteSourceTypeService;

    @PostMapping(value = "quote-entity")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'GUEST', 'ANON')")
    public List<QuoteEntityInfo> searchQuoteEntity(@RequestBody String search) {
        return quoteEntityService.search(search);
    }

    @PostMapping("quote-source")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'GUEST', 'ANON')")
    public Optional<QuoteSource> searchQuoteSourceEntity(@RequestBody String search) {
        return quoteSourceService.search(search);
    }

    @PostMapping("quote-source-type")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EDITOR', 'GUEST', 'ANON')")
    public Optional<QuoteSourceType> searchQuoteSourceTypeEntity(@RequestBody String search) {
        return quoteSourceTypeService.search(search);
    }

}
