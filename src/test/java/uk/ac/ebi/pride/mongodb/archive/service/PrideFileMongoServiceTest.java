package uk.ac.ebi.pride.mongodb.archive.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.pride.archive.repo.repos.file.ProjectFile;
import uk.ac.ebi.pride.archive.repo.repos.file.ProjectFileRepository;
import uk.ac.ebi.pride.mongodb.archive.config.ArchiveOracleConfig;
import uk.ac.ebi.pride.mongodb.archive.config.PrideProjectTestConfig;
import uk.ac.ebi.pride.mongodb.archive.model.PrideFile;


import java.util.stream.StreamSupport;


/**
 * @author ypriverol
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PrideProjectTestConfig.class,  ArchiveOracleConfig.class})
public class PrideFileMongoServiceTest {

    @Autowired
    PrideFileMongoService prideFileMongoService;

    @Autowired
    ProjectFileRepository oracleRepository;

    @Test
    public void save() {
        PrideFile file = PrideFile.builder().fileName("Filename.txt").build();
        prideFileMongoService.insert(file);
    }

    @Test
    public void parallelSave() {
        insertFilesSave();
    }


    /**
     * This method helps to read all the projects from PRIDE Archive Oracle Database and
     * move then into MongoDB
     */
    private void insertFilesSave(){

        Iterable<ProjectFile> iterator = oracleRepository.findAll();
        long oracleCount = oracleRepository.count();
        StreamSupport.stream(iterator.spliterator(), true).parallel().forEach( x-> {

            PrideFile file = PrideFile.builder().fileName(x.getFileName()).build();
            prideFileMongoService.insert(file);
        });

        Assert.assertTrue(oracleCount == prideFileMongoService.count());

    }
}