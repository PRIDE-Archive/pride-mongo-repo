package uk.ac.ebi.pride.mongodb.archive.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.pride.mongodb.archive.config.PrideProjectTestConfig;
import uk.ac.ebi.pride.mongodb.archive.model.PrideProject;

import static org.junit.Assert.*;

/**
 * @author ypriverol
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PrideProjectTestConfig.class})
public class PrideProjectMongoServiceTest {

    @Autowired
    PrideProjectMongoService prideProjectService;

    @Test
    public void save() {

        /** Save Project using only an accession in the dataset **/
        PrideProject project = PrideProject.builder().accession("PXD000001").build();
        prideProjectService.save(project);
    }
}