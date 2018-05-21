package uk.ac.ebi.pride.mongodb.archive.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.archive.dataprovider.identification.ModificationProvider;
import uk.ac.ebi.pride.archive.dataprovider.param.CvParamProvider;
import uk.ac.ebi.pride.archive.dataprovider.param.DefaultCvParam;
import uk.ac.ebi.pride.archive.dataprovider.utils.Tuple;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;
import uk.ac.ebi.pride.mongodb.archive.model.PrideMongoPSM;

import java.io.File;
import java.util.*;

/** Tests building PSMs from mzTab files. */
public class MongoPsmMzTabBuilderTest {

  private static final String PROJECT_1_ACCESSION = "PXD000581";
  private static final String PROJECT_2_ACCESSION = "TST000121";
  private static final String PROJECT_1_ASSAY_1 = "32411";
  private static final String PROJECT_1_ASSAY_2 = "32416";
  private static final String PROJECT_1_ASSAY_3 = "32417";
  private static final String PROJECT_2_ASSAY_1 = "00001";
  private static final String MOD_ACCESSION = "MOD:01214";
  private static final String MOD_NAME = "iodoacetamide - site C";
  private static final String FILE_PRE = ";PRIDE_Exp_Complete_Ac_";
  private static final String FILE_POST = ".xml;spectrum=";
  private static final int NUM_ASSAYS = 3;
  private static Logger logger = LoggerFactory.getLogger(MongoPsmMzTabBuilderTest.class);
  private static MZTabFile mzTabFileP1A1;
  private static MZTabFile mzTabFileP1A2;
  private static MZTabFile mzTabFileP1A3;
  private static MZTabFile mzTabFileP2A1;

  /**
   * Reads in mzTab files.
   *
   * @throws Exception problems reading mzTab files
   */
  @BeforeClass
  public static void initialise() throws Exception {

    mzTabFileP1A1 =
        new MZTabFileParser(
                new File(MongoPsmMzTabBuilderTest.class.getClassLoader().getResource("submissions/2014/01/PXD000581/generated/PRIDE_Exp_Complete_Ac_32411.mztab").toURI()),System.out)
            .getMZTabFile();
    mzTabFileP1A2 =
        new MZTabFileParser(
                new File(MongoPsmMzTabBuilderTest.class.getClassLoader().getResource("submissions/2014/01/PXD000581/generated/PRIDE_Exp_Complete_Ac_32416.mztab").toURI()), System.out)
            .getMZTabFile();
    mzTabFileP1A3 =
        new MZTabFileParser(
                new File(MongoPsmMzTabBuilderTest.class.getClassLoader().getResource("submissions/2014/01/PXD000581/generated/PRIDE_Exp_Complete_Ac_32417_null.mztab").toURI()), System.out)
            .getMZTabFile();
    mzTabFileP2A1 =
        new MZTabFileParser(
                new File(MongoPsmMzTabBuilderTest.class.getClassLoader().getResource("submissions/TST000121/generated/PRIDE_Exp_Complete_Ac_00001.mztab").toURI()),
                System.out)
            .getMZTabFile();
  }



  /** Tests reading PSMs from mzTab files. */
  @Test
  public void testReadPsmsFromMzTabFilesDirectory() {
    Map<String, List<PrideMongoPSM>> psms = new HashMap<>();
    psms.put(
        PROJECT_1_ASSAY_1, MongoPsmMzTabBuilder.readPsmsFromMzTabFile(
            PROJECT_1_ACCESSION, PROJECT_1_ASSAY_1, mzTabFileP1A1));
    psms.put(
        PROJECT_1_ASSAY_2, MongoPsmMzTabBuilder.readPsmsFromMzTabFile(
            PROJECT_1_ACCESSION, PROJECT_1_ASSAY_2, mzTabFileP1A2));
    psms.put(
        PROJECT_1_ASSAY_3,  MongoPsmMzTabBuilder.readPsmsFromMzTabFile(
            PROJECT_1_ACCESSION, PROJECT_1_ASSAY_3, mzTabFileP1A3));
    Assert.assertEquals(psms.size(), NUM_ASSAYS);
    for (Map.Entry<String, List<PrideMongoPSM>> stringLinkedListEntry : psms.entrySet()) {
      for (PrideMongoPSM psm : stringLinkedListEntry.getValue()) {
        logger.debug(psm.getSpectrumAccession());
        Assert.assertTrue(
            psm.getSpectrumAccession()
                .startsWith(
                    PROJECT_1_ACCESSION + FILE_PRE + stringLinkedListEntry.getKey() + FILE_POST));
        logger.debug("PSM Database: " + psm.getDatabase());
        logger.debug("PSM Database version: " + psm.getDatabase().getVersion());
        logger.debug("PSM Project Accession: " + psm.getExternalProjectAccession());
        logger.debug("PSM Assay Accession: " + psm.getExternalAnalysisAccession());
        logger.debug("PSM Peptide sequence : " + psm.getPeptideSequence());
        List<CvParamProvider> cvParams = new ArrayList<>();
        CvParamProvider cvParam = new DefaultCvParam("label", "accession", "name", "value");
        cvParams.add(cvParam);
        psm.setBestPSMScore(new Tuple<CvParamProvider,CvParamProvider>(cvParam, null));
      }
    }
  }

  /** Tests reading PSMs from ab mztab file, including extra comparison checks. */
  @Test
  public void testReadPsmFromMzTabFileAndCompare() {
    Map<String, List<PrideMongoPSM>> psms = new HashMap<>();
    psms.put(
        PROJECT_2_ASSAY_1,
        MongoPsmMzTabBuilder.readPsmsFromMzTabFile(
            PROJECT_2_ACCESSION, PROJECT_2_ASSAY_1, mzTabFileP2A1));
    Assert.assertEquals(1, psms.size());
    PrideMongoPSM firstPsm = psms.entrySet().iterator().next().getValue().get(0);
    Assert.assertEquals(
        "TST000121_00001_175_orf19/5636_QSTSSTPCPYWDTGCLCVMPQFAGAVGNCVAK", firstPsm.getId());
    Assert.assertEquals("175", firstPsm.getReportedFileID());
    Assert.assertEquals(
        "TST000121;result_1_sample_1_dat.pride.xml;spectrum=175", firstPsm.getSpectrumAccession());
    Assert.assertEquals("orf19/5636", firstPsm.getExternalProjectAccession());
    Assert.assertNull(firstPsm.getUnique());
    Assert.assertNull(firstPsm.getRetentionTime());
    Assert.assertNull(firstPsm.getCharge());
    Assert.assertEquals(new Double(1183.8615), firstPsm.getExpMassToCharge());
    Assert.assertNull(firstPsm.getCalculatedMassToCharge());
    Assert.assertNull(firstPsm.getPreAminoAcid());
    Assert.assertNull(firstPsm.getPostAminoAcid());
    Assert.assertEquals((Integer) 61, firstPsm.getStartPosition());
    Assert.assertEquals((Integer) 92, firstPsm.getEndPosition());
  }

  /**
   * Chceks modifications are correct.
   *
   * @param modifications modifications to check
   */
  private void checkModifications(Iterable<ModificationProvider> modifications) {
    Iterator<ModificationProvider> iterator = modifications.iterator();
    ModificationProvider mod = iterator.next();
    Assert.assertEquals(MOD_ACCESSION, mod.getAccession());
    Assert.assertEquals(MOD_NAME, mod.getName());
    Assert.assertEquals((Integer) 8, mod.getMainPosition());
    mod = iterator.next();
    Assert.assertEquals(MOD_ACCESSION, mod.getAccession());
    Assert.assertEquals(MOD_NAME, mod.getName());
    Assert.assertEquals((Integer) 15, mod.getMainPosition());
    mod = iterator.next();
    Assert.assertEquals(MOD_ACCESSION, mod.getAccession());
    Assert.assertEquals(MOD_NAME, mod.getName());
    Assert.assertEquals((Integer) 17, mod.getMainPosition());
    mod = iterator.next();
    Assert.assertEquals(MOD_ACCESSION, mod.getAccession());
    Assert.assertEquals(MOD_NAME, mod.getName());
    Assert.assertEquals((Integer) 29, mod.getMainPosition());
  }

  /**
   * Checks search enginges are correct.
   *
   * @param searchEngines search enginges to check
   */
  private void checkSearchEngine(Iterable<CvParamProvider> searchEngines) {
    CvParamProvider cvParamProvider = searchEngines.iterator().next();
    Assert.assertEquals("MS", cvParamProvider.getCvLabel());
    Assert.assertEquals("MS:1001207", cvParamProvider.getAccession());
    Assert.assertEquals("Mascot", cvParamProvider.getName());
    Assert.assertNull(cvParamProvider.getValue());
  }

  /**
   * Check search engine scores are correct.
   *
   * @param searchEngineScores search engine scores to check.
   */
  private void checkSearchEnginesScores(Iterable<CvParamProvider> searchEngineScores) {
    CvParamProvider cvParamProvider = searchEngineScores.iterator().next();
    Assert.assertEquals("PRIDE", cvParamProvider.getCvLabel());
    Assert.assertEquals("PRIDE:0000069", cvParamProvider.getAccession());
    Assert.assertEquals("Mascot score", cvParamProvider.getName());
    Assert.assertEquals("91.24", cvParamProvider.getValue());
  }
}
