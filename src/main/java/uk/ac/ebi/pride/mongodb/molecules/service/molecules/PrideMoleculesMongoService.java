package uk.ac.ebi.pride.mongodb.molecules.service.molecules;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.pride.mongodb.molecules.model.peptide.PrideMongoPeptideEvidence;
import uk.ac.ebi.pride.mongodb.molecules.model.protein.PrideMongoProteinEvidence;
import uk.ac.ebi.pride.mongodb.molecules.repo.peptide.PridePeptideEvidenceMongoRepository;
import uk.ac.ebi.pride.mongodb.molecules.repo.protein.PrideProteinMongoRepository;
import uk.ac.ebi.pride.mongodb.utils.PrideMongoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Service
@Slf4j
public class PrideMoleculesMongoService {

    final PrideProteinMongoRepository proteinMongoRepository;

    final PridePeptideEvidenceMongoRepository peptideMongoRepository;

    @Autowired
    public PrideMoleculesMongoService(PrideProteinMongoRepository proteinRepository, PridePeptideEvidenceMongoRepository peptideMongoRepository) {
        this.proteinMongoRepository = proteinRepository;
        this.peptideMongoRepository = peptideMongoRepository;
    }

    /**
     * This function is to find all the {@link PrideMongoProteinEvidence}
     * @param projectAccession Project Accession
     * @param page Page Number of Proteins to be retrieve .
     * @return List of {@link PrideMongoProteinEvidence}
     */
    public Page<PrideMongoProteinEvidence> findProteinsByProjectAccession(String projectAccession, Pageable page){

        List<Triple<String, String, String>> filters = PrideMongoUtils.parseFilterParameters("projectAccession=in=" + projectAccession);
        Page<PrideMongoProteinEvidence> proteins =  proteinMongoRepository.filterByAttributes(filters, page);
        log.debug("The number of Proteins for the Project Accession -- " + projectAccession + " and Page -- " + page.getPageNumber() + " is -- " + proteins.getTotalElements());
        return proteins;
    }

    /**
     * Save an specific Protein in MongoDB
     * @param protein {@link PrideMongoProteinEvidence}
     */
    public void saveProteinEvidences(PrideMongoProteinEvidence protein){
        Optional<PrideMongoProteinEvidence> currentProtein = proteinMongoRepository.findByAccessionAndAssayAccession(protein.getReportedAccession(), protein.getAssayAccession());
        if(currentProtein.isPresent()){
            protein.setId((ObjectId) currentProtein.get().getId());
            log.debug("The protein will be updated -- Assay: " + protein.getAssayAccession() + " Protein: " + protein.getReportedAccession());
        }else{
            log.debug("New protein will be added to the database -- Assay: " + protein.getAssayAccession() + " Protein: " + protein.getReportedAccession());
        }
        proteinMongoRepository.save(protein);
    }

    /**
     * Delete all Protein evidences from the database
     */
    public void deleteAllProteinEvidences() {
        proteinMongoRepository.deleteAll();
    }

    /**
     * Search Proteins by specific properties in the filter Query.
     * @param filterQuery Query properties
     * @param page Page to be retrieved
     * @return Page containing the {@link PrideMongoProteinEvidence}.
     */
    public Page<PrideMongoProteinEvidence> searchProteins(String filterQuery, Pageable page) {
        List<Triple<String, String, String>> filters = PrideMongoUtils.parseFilterParameters(filterQuery);
        return proteinMongoRepository.filterByAttributes(filters, page);

    }

    /**
     * Find all {@link PrideMongoProteinEvidence}. This method should be executed using the Pagination.
     * @return List of {@link PrideMongoProteinEvidence}
     * @param page a {@link PageRequest}
     */
    public Page<PrideMongoProteinEvidence> findAllProteinEvidences(PageRequest page) {
        return proteinMongoRepository.findAll(page);
    }


    /**
     * This functions allows to find all the Peptides for an specific project Accession
     * @param projectAccession Project Accession
     * @param page Page to be retrieve
     * @return List of Peptides
     */
    public Page<PrideMongoPeptideEvidence> findPeptideEvidencesByProjectAccession(String projectAccession, Pageable page){
        List<Triple<String, String, String>> filters = PrideMongoUtils.parseFilterParameters("projectAccession=in=" + projectAccession);
        Page<PrideMongoPeptideEvidence> psms =  peptideMongoRepository.filterByAttributes(filters, page);
        log.debug("The number of PSMs for the Project Accession -- " + projectAccession + " -- "+ psms.getTotalElements());
        return psms;
    }

    /**
     * This functions allows to find all the PSMs for an specific project Accession
     * @param analysisAccession Analysis Accession
     * @param page Page to be retrieve
     * @return List of PSMs
     */
    public Page<PrideMongoPeptideEvidence> findPeptideEvidencesByAssayAccession(String analysisAccession, Pageable page){
        List<Triple<String, String, String>> filters = PrideMongoUtils.parseFilterParameters("analysisAccession=in=" + analysisAccession);
        Page<PrideMongoPeptideEvidence> psms = peptideMongoRepository.filterByAttributes(filters, page);
        log.debug("The number of PSMs for the Analysis Accession -- " + analysisAccession + " -- "+ psms.getTotalElements());
        return psms;
    }

//    /**
//     * Finds a PSM by an Accession .
//     *
//     * @param accession Accession
//     * @return a PSM corresponding to the provided ID.
//     */
//    public PrideMongoPeptideEvidence findByAccession(String accession) {
//        return psmMongoRepository.findByAccession(accession).orElse(null);
//    }


    /**
     * Counts how many PSMs are for a project accession.
     *
     * @param projectAccession the project accession to search for
     * @return the number of PSMs corresponding to the provided project accession
     */
    public long countByProjectAccession(String projectAccession) {
        return findPeptideEvidencesByProjectAccession(projectAccession, PageRequest.of(0,10)).getTotalElements();
    }

    /**
     * Counts how many PSMs are for a assay accession and assay accession.
     *
     * @param assayAccession the assay accession to search for
     * @return the number of PSMs corresponding to the provided project accession
     */
    public long countByAssayAccession(String assayAccession) {
        return findPeptideEvidencesByProjectAccession(assayAccession, PageRequest.of(0,10)).getTotalElements();
    }

    /**
     * Save an specific PSM in MongoDB
     * @param peptideEvidence {@link PrideMongoPeptideEvidence}
     */
    public void savePeptideEvidence(PrideMongoPeptideEvidence peptideEvidence){
        Optional<PrideMongoPeptideEvidence> currentPeptide = peptideMongoRepository
                .findPeptideByProteinAndAssayAccession(peptideEvidence.getProteinAccession(),
                        peptideEvidence.getAssayAccession(),
                        peptideEvidence.getPeptideAccession());
        if(currentPeptide.isPresent())
            peptideEvidence.setId(currentPeptide.get().getId());

         peptideMongoRepository.save(peptideEvidence);
    }

    /**
     * Delete all the PSMs in the Mongo Database
     */
    public void deleteAllPeptideEvidences() {
        peptideMongoRepository.deleteAll();
    }


    public Optional<PrideMongoProteinEvidence> getProteinEvidence(String reportedAccession, String projectAccession, String assayAccession) {
        return proteinMongoRepository.findByAccessionAndAssayAccessionAndProjectAccession(reportedAccession,
                assayAccession, projectAccession);
    }

    public Page<PrideMongoPeptideEvidence> findPeptideEvidencesByProteinEvidence(String proteinAccession,
                                                                                 String projectAccession,
                                                                                 String assayAccession, PageRequest page) {

        Page<PrideMongoPeptideEvidence> peptides = peptideMongoRepository.findPeptideEvidenceByProteinEvidence(projectAccession,assayAccession,proteinAccession, page);

        return peptides;
    }

    /**
     * Return one {@link PrideMongoProteinEvidence} protein evidence for an specific project and assay.
     * @param projectAccession Protect Accession
     * @param assayAccession Assay Accession
     * @param reportedProtein Reported protein accession
     * @return
     */
    public Optional<PrideMongoProteinEvidence> findProteinsEvidence(String projectAccession, String assayAccession,
                                                                    String reportedProtein) {
        return proteinMongoRepository.findByAccessionAndAssayAccessionAndProjectAccession(reportedProtein, assayAccession, projectAccession);
    }

    public Page<PrideMongoPeptideEvidence> findPeptideEvidences(String projectAccession, String assayAccession, String peptideSequence, String reportedProtein,
           PageRequest page){

        StringJoiner filter = new StringJoiner(",");
        if(projectAccession != null && !projectAccession.isEmpty())
            filter.add("projectAccession=in=" + projectAccession);
        if(assayAccession != null && !assayAccession.isEmpty())
            filter.add("assayAccession=in=" + assayAccession);
        if(peptideSequence != null && !peptideSequence.isEmpty())
            filter.add("peptideSequence=in=" + peptideSequence);
        if(reportedProtein != null && !reportedProtein.isEmpty())
            filter.add("proteinAccession=in=" + reportedProtein);

        List<Triple<String, String, String>> filters = PrideMongoUtils.parseFilterParameters(filter.toString());
        return peptideMongoRepository.filterByAttributes(filters, page);

    }

    public Optional<PrideMongoPeptideEvidence> findPeptideEvidence(String projectAccession, String assayAccession,
                                                                   String reportedProtein, String peptideAccession) {
        return peptideMongoRepository.findPeptideByProteinAndAssayAccession(reportedProtein, assayAccession, peptideAccession);
    }
}