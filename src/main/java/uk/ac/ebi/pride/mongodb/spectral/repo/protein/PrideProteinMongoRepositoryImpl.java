package uk.ac.ebi.pride.mongodb.spectral.repo.protein;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import uk.ac.ebi.pride.mongodb.spectral.model.protein.PrideMongoProtein;
import uk.ac.ebi.pride.mongodb.spectral.model.psms.PrideMongoPSM;
import uk.ac.ebi.pride.mongodb.utils.PrideMongoUtils;

import java.util.Collection;
import java.util.List;

public class PrideProteinMongoRepositoryImpl implements PrideProteinMongoRepositoryCustom{

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MongoOperations mongoOperations;

    @Override
    public Page<PrideMongoProtein> filterByAttributes(List<Triple<String, String, String>> filters, Pageable page) {
        Criteria queryCriteria = PrideMongoUtils.buildQuery(filters);
        Query queryMongo = new Query().addCriteria(queryCriteria);
        queryMongo.with(page);
        List<PrideMongoProtein> files =  mongoTemplate.find(queryMongo, PrideMongoProtein.class);
        return PageableExecutionUtils.getPage(files, page, () -> mongoOperations.count(queryMongo, PrideMongoProtein.class));
    }

    @Override
    public List<PrideMongoProtein> findByIdAccessions(Collection<String> accessions, Sort sort) {
        Criteria queryCriteria = PrideMongoUtils.builQueryByAccessions(accessions);
        Query queryMongo = new Query().addCriteria(queryCriteria);
        queryMongo.with(sort);
        return mongoTemplate.find(queryMongo, PrideMongoProtein.class);
    }

}
