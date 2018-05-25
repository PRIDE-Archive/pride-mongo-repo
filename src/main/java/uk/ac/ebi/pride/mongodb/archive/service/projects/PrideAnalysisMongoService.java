package uk.ac.ebi.pride.mongodb.archive.service.projects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import uk.ac.ebi.pride.mongodb.archive.model.PrideArchiveField;
import uk.ac.ebi.pride.mongodb.archive.model.projects.MongoPrideAnalysis;
import uk.ac.ebi.pride.mongodb.archive.repo.projects.PrideAnalysisMongoRepository;
import uk.ac.ebi.pride.mongodb.utils.PrideMongoUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author ypriverol
 *
 */
public class PrideAnalysisMongoService {

    /** Logger use to query and filter the data **/
    private static final Logger LOGGER = LoggerFactory.getLogger(PrideProjectMongoService.class);

    final PrideAnalysisMongoRepository repository;

    @Autowired
    private MongoOperations mongo;

    @Autowired
    public PrideAnalysisMongoService(PrideAnalysisMongoRepository repository) {
        this.repository = repository;
    }

    /**
     * Insert is allowing using to create a Accession for the File and insert the actual File into MongoDB.
     * @param prideAnalysis {@link MongoPrideAnalysis}
     * @return MongoPrideAnalysis
     */
    public MongoPrideAnalysis insert(MongoPrideAnalysis prideAnalysis) {
        NumberFormat formatter = new DecimalFormat("000000");
        if (prideAnalysis.getAccession() == null) {
            String accession = "PXDA" + formatter.format(PrideMongoUtils.getNextSizedSequence(mongo, PrideArchiveField.PRIDE_FILE_COLLECTION_NAME, 1));
            prideAnalysis.setAccession(accession);
            prideAnalysis = repository.save(prideAnalysis);
            LOGGER.debug("A new project has been saved into MongoDB database with Accession -- " + prideAnalysis.getAccession());
        } else
            LOGGER.error("A project with similar accession has been found in the MongoDB database, please use update function -- " + prideAnalysis.getAccession());
        return prideAnalysis;
    }




}
