package uk.ac.ebi.pride.mongodb.molecules.repo.psm;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uk.ac.ebi.pride.mongodb.molecules.model.peptide.PrideMongoPeptideEvidence;
import uk.ac.ebi.pride.mongodb.molecules.model.psm.PrideMongoPsmSummaryEvidence;

import java.util.Collection;
import java.util.List;

/**
 * @author ypriverol
 */
public interface PridePsmSummaryEvidenceMongoRepositoryCustom {

    /**
     * Filter By Attributes
     * @param filters Attributes in {@link Triple} structure
     * @param page Pageable
     * @return List of {@link uk.ac.ebi.pride.mongodb.molecules.model.psm.PrideMongoPsmSummaryEvidence}
     */
    Page<PrideMongoPsmSummaryEvidence> filterByAttributes(List<Triple<String, String, String>> filters, Pageable page) ;


    Page<PrideMongoPsmSummaryEvidence> findPsmSummaryEvidencesByUsis(List<String> usis, Pageable page);
}