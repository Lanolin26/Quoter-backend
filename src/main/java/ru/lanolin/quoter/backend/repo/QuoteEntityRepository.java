package ru.lanolin.quoter.backend.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.lanolin.quoter.backend.domain.QuoteEntity;
import ru.lanolin.quoter.backend.domain.view.QuoteEntityIdsInfo;
import ru.lanolin.quoter.backend.domain.view.QuoteEntityInfo;

import java.util.List;
import java.util.Optional;

public interface QuoteEntityRepository extends JpaRepository<QuoteEntity, Integer> {

    @Query("""
                select
                    qe.id as id,
                    qe.text as text,
                    ue.login as authorLogin,
                    ue.name as authorName,
                    qse.sourceName as sourceName,
                    qste.type as sourceType
                from QuoteEntity qe
                join UserEntity ue on ue.id = qe.author.id
                join QuoteSource qse on qse.id = qe.source.id
                join QuoteSourceType qste on qste.id = qe.source.type.id
                where qe.id = ?1
            """)
    Optional<QuoteEntityInfo> getQuoteEntityInfoById(Integer id);

    @Query("""
                select
                    qe.id as id,
                    qe.text as text,
                    ue.login as authorLogin,
                    ue.name as authorName,
                    qse.sourceName as sourceName,
                    qste.type as sourceType
                from QuoteEntity qe
                join UserEntity ue on ue.id = qe.author.id
                join QuoteSource qse on qse.id = qe.source.id
                join QuoteSourceType qste on qste.id = qe.source.type.id
            """)
    Page<QuoteEntityInfo> getQuoteEntityInfo(Pageable page);

    @Query("""
                select
                    qe.id as id,
                    qe.text as text,
                    ue.login as authorLogin,
                    ue.name as authorName,
                    qse.sourceName as sourceName,
                    qste.type as sourceType
                from QuoteEntity qe
                join UserEntity ue on ue.id = qe.author.id
                join QuoteSource qse on qse.id = qe.source.id
                join QuoteSourceType qste on qste.id = qe.source.type.id
            """)
    List<QuoteEntityInfo> getQuoteEntityInfo();


    @Query("""
            select qe.id        as id,
                   qe.author.id as authorId,
                   qe.source.id as sourceId,
                   qse.type.id  as sourceTypeId,
                   qe.text as text
            from QuoteEntity qe
                join QuoteSource qse on qse.id = qe.source.id
            where qe.id = ?1
            """)
    Optional<QuoteEntityIdsInfo> getQuoteEntityIdsInfoById(Integer id);

    @Query("""
            select qe.id        as id,
                   qe.author.id as authorId,
                   qe.source.id as sourceId,
                   qse.type.id  as sourceTypeId,
                   qe.text as text
            from QuoteEntity qe
                join QuoteSource qse on qse.id = qe.source.id
            """)
    Page<QuoteEntityIdsInfo> getQuoteEntityIdsInfo(Pageable page);

    @Query("""
            select qe.id        as id,
                   qe.author.id as authorId,
                   qe.source.id as sourceId,
                   qse.type.id  as sourceTypeId,
                   qe.text as text
            from QuoteEntity qe
                join QuoteSource qse on qse.id = qe.source.id
            """)
    List<QuoteEntityIdsInfo> getQuoteEntityIdsInfo();
}