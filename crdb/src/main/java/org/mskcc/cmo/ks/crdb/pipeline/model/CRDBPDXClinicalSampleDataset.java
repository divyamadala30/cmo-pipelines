/*
 * Copyright (c) 2016-2018 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal CMO-Pipelines.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cmo.ks.crdb.pipeline.model;

import java.util.*;

/**
 * Model for CRDBPDXClinicalSampleDataset results.
 *
 * @author averyniceday
 */

public class CRDBPDXClinicalSampleDataset {

    private String PATIENT_ID;
    private String SAMPLE_ID;
    private String PDX_ID;
    private String COLLAB_ID;
    private String AGE_AT_INITIAL_DIAGNOSIS;
    private String PASSAGE_ID;
    private String ONCOTREE_CODE;
    private String STAGE_CODE;
    private String T_STAGE;
    private String N_STAGE;
    private String M_STAGE;
    private String GRADE;
    private String SAMPLE_TYPE;
    private String PRIMARY_SITE;
    private String SAMPLE_CLASS;
    private String PROCEDURE_TYPE;
    private String PRETREATED;
    private String PDX_TREATED;
    private String ER_POSITIVE;
    private String ER_NEGATIVE;
    private String HER2_POSITIVE;
    private String HER2_NEGATIVE;
    private String HPV_POSITIVE;
    private String HPV_NEGATIVE;
    private String P16_POSITIVE;
    private String P16_NEGATIVE;
    private String PR_POSITIVE;
    private String PR_NEGATIVE;
    private String IDH1_POSITIVE;
    private String IDH1_NEGATIVE;
    private String IDH2_POSITIVE;
    private String IDH2_NEGATIVE;
    private String EGFR_POSITIVE;
    private String EGFR_NEGATIVE;
    private String ALK_POSITIVE;
    private String ALK_NEGATIVE;
    private String BRCA1_POSITIVE;
    private String BRCA1_NEGATIVE;
    private String BRCA2_POSITIVE;
    private String BRCA2_NEGATIVE;
    private String C_MYC_POSITIVE;
    private String C_MYC_NEGATIVE;
    private String AR_POSITIVE;
    private String AR_NEGATIVE;
    private String KRAS_POSITIVE;
    private String KRAS_NEGATIVE;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private static List<String> fieldNameList = initializeFieldNameList();

    /**
     * No args constructor for use in serialization
     */
    public CRDBPDXClinicalSampleDataset() {
    }

    public CRDBPDXClinicalSampleDataset(String PATIENT_ID, String SAMPLE_ID, String PDX_ID,
                                        String COLLAB_ID, String AGE_AT_INITIAL_DIAGNOSIS, String PASSAGE_ID, String ONCOTREE_CODE,
                                        String STAGE_CODE, String T_STAGE, String N_STAGE, String M_STAGE, String GRADE,
                                        String SAMPLE_TYPE, String PRIMARY_SITE, String SAMPLE_CLASS, String PROCEDURE_TYPE,
                                        String PRETREATED, String PDX_TREATED, String ER_POSITIVE, String ER_NEGATIVE,
                                        String HER2_POSITIVE, String HER2_NEGATIVE, String HPV_POSITIVE, String HPV_NEGATIVE,
                                        String P16_POSITIVE, String P16_NEGATIVE, String PR_POSITIVE, String PR_NEGATIVE,
                                        String IDH1_POSITIVE, String IDH1_NEGATIVE, String IDH2_POSITIVE, String IDH2_NEGATIVE,
                                        String EGFR_POSITIVE, String EGFR_NEGATIVE, String ALK_POSITIVE, String ALK_NEGATIVE,
                                        String BRCA1_POSITIVE, String BRCA1_NEGATIVE, String BRCA2_POSITIVE, String BRCA2_NEGATIVE,
                                        String C_MYC_POSITIVE, String C_MYC_NEGATIVE, String AR_POSITIVE, String AR_NEGATIVE,
                                        String KRAS_POSITIVE, String KRAS_NEGATIVE) {
        this.PATIENT_ID = PATIENT_ID == null ? "NA" : PATIENT_ID;
        this.SAMPLE_ID = SAMPLE_ID == null ? "NA" : SAMPLE_ID;
        this.PDX_ID = PDX_ID == null ? "NA" : PDX_ID;
        this.COLLAB_ID = COLLAB_ID == null ? "NA" : COLLAB_ID;
        this.AGE_AT_INITIAL_DIAGNOSIS = AGE_AT_INITIAL_DIAGNOSIS == null ? "NA" : AGE_AT_INITIAL_DIAGNOSIS;
        this.PASSAGE_ID = PASSAGE_ID == null ? "NA" : PASSAGE_ID;
        this.ONCOTREE_CODE = ONCOTREE_CODE == null ? "NA" : ONCOTREE_CODE;
        this.STAGE_CODE = STAGE_CODE == null ? "NA" : STAGE_CODE;
        this.T_STAGE = T_STAGE == null ? "NA" : T_STAGE;
        this.N_STAGE = N_STAGE == null ? "NA" : N_STAGE;
        this.M_STAGE = M_STAGE == null ? "NA" : M_STAGE;
        this.GRADE = GRADE == null ? "NA" : GRADE;
        this.SAMPLE_TYPE = SAMPLE_TYPE == null ? "NA" : SAMPLE_TYPE;
        this.PRIMARY_SITE = PRIMARY_SITE == null ? "NA" : PRIMARY_SITE;
        this.SAMPLE_CLASS = SAMPLE_CLASS == null ? "NA" : SAMPLE_CLASS;
        this.PROCEDURE_TYPE = PROCEDURE_TYPE == null ? "NA" : PROCEDURE_TYPE;
        this.PRETREATED = PRETREATED == null ? "NA" : PRETREATED;
        this.PDX_TREATED = PDX_TREATED == null ? "NA" : PDX_TREATED;
        this.ER_POSITIVE = ER_POSITIVE == null ? "NA" : ER_POSITIVE;
        this.ER_NEGATIVE = ER_NEGATIVE == null ? "NA" : ER_NEGATIVE;
        this.HER2_POSITIVE = HER2_POSITIVE == null ? "NA" : HER2_POSITIVE;
        this.HER2_NEGATIVE = HER2_NEGATIVE == null ? "NA" : HER2_NEGATIVE;
        this.HPV_POSITIVE = HPV_POSITIVE == null ? "NA" : HPV_POSITIVE;
        this.HPV_NEGATIVE = HPV_NEGATIVE == null ? "NA" : HPV_NEGATIVE;
        this.P16_POSITIVE = P16_POSITIVE == null ? "NA" : P16_POSITIVE;
        this.P16_NEGATIVE = P16_NEGATIVE == null ? "NA" : P16_NEGATIVE;
        this.PR_POSITIVE = PR_POSITIVE == null ? "NA" : PR_POSITIVE;
        this.PR_NEGATIVE = PR_NEGATIVE == null ? "NA" : PR_NEGATIVE;
        this.IDH1_POSITIVE = IDH1_POSITIVE == null ? "NA" : IDH1_POSITIVE;
        this.IDH1_NEGATIVE = IDH1_NEGATIVE == null ? "NA" : IDH1_NEGATIVE;
        this.IDH2_POSITIVE = IDH2_POSITIVE == null ? "NA" : IDH2_POSITIVE;
        this.IDH2_NEGATIVE = IDH2_NEGATIVE == null ? "NA" : IDH2_NEGATIVE;
        this.EGFR_POSITIVE = EGFR_POSITIVE == null ? "NA" : EGFR_POSITIVE;
        this.EGFR_NEGATIVE = EGFR_NEGATIVE == null ? "NA" : EGFR_NEGATIVE;
        this.ALK_POSITIVE = ALK_POSITIVE == null ? "NA" : ALK_POSITIVE;
        this.ALK_NEGATIVE = ALK_NEGATIVE == null ? "NA" : ALK_NEGATIVE;
        this.BRCA1_POSITIVE = BRCA1_POSITIVE == null ? "NA" : BRCA1_POSITIVE;
        this.BRCA1_NEGATIVE = BRCA1_NEGATIVE == null ? "NA" : BRCA1_NEGATIVE;
        this.BRCA2_POSITIVE = BRCA2_POSITIVE == null ? "NA" : BRCA2_POSITIVE;
        this.BRCA2_NEGATIVE = BRCA2_NEGATIVE == null ? "NA" : BRCA2_NEGATIVE;
        this.C_MYC_POSITIVE = C_MYC_POSITIVE == null ? "NA" : C_MYC_POSITIVE;
        this.C_MYC_NEGATIVE = C_MYC_NEGATIVE == null ? "NA" : C_MYC_NEGATIVE;
        this.AR_POSITIVE = AR_POSITIVE == null ? "NA" : AR_POSITIVE;
        this.AR_NEGATIVE = AR_NEGATIVE == null ? "NA" : AR_NEGATIVE;
        this.KRAS_POSITIVE = KRAS_POSITIVE == null ? "NA" : KRAS_POSITIVE;
        this.KRAS_NEGATIVE = KRAS_NEGATIVE == null ? "NA" : KRAS_NEGATIVE;
    }

    public String getPATIENT_ID() {
        return PATIENT_ID;
    }

    /**
     * @param PATIENT_ID
     */
    public void setPATIENT_ID(String PATIENT_ID) {
        this.PATIENT_ID = PATIENT_ID;
    }

    public String getSAMPLE_ID() {
        return SAMPLE_ID;
    }

    /**
     * @param SAMPLE_ID
     */
    public void setSAMPLE_ID(String SAMPLE_ID) {
        this.SAMPLE_ID = SAMPLE_ID;
    }

    public String getPDX_ID() {
        return PDX_ID;
    }

    /**
     * @param PDX_ID
     */
    public void setPDX_ID(String PDX_ID) {
        this.PDX_ID = PDX_ID;
    }

    public String getCOLLAB_ID() {
        return COLLAB_ID;
    }

    /**
     * @param COLLAB_ID
     */
    public void setCOLLAB_ID(String COLLAB_ID) {
        this.COLLAB_ID = COLLAB_ID;
    }

    public String getAGE_AT_INITIAL_DIAGNOSIS() {
        return AGE_AT_INITIAL_DIAGNOSIS;
    }

    /**
     * @param AGE_AT_INITIAL_DIAGNOSIS
     */
    public void setAGE_AT_INITIAL_DIAGNOSIS(String AGE_AT_INITIAL_DIAGNOSIS) {
        this.AGE_AT_INITIAL_DIAGNOSIS = AGE_AT_INITIAL_DIAGNOSIS;
    }

    public String getPASSAGE_ID() {
        return PASSAGE_ID;
    }

    /**
     * @param PASSAGE_ID
     */
    public void setPASSAGE_ID(String PASSAGE_ID) {
        this.PASSAGE_ID = PASSAGE_ID;
    }

    public String getONCOTREE_CODE() {
        return ONCOTREE_CODE;
    }

    /**
     * @param ONCOTREE_CODE
     */
    public void setONCOTREE_CODE(String ONCOTREE_CODE) {
        this.ONCOTREE_CODE = ONCOTREE_CODE;
    }

    public String getSTAGE_CODE() {
        return STAGE_CODE;
    }

    /**
     * @param STAGE_CODE
     */
    public void setSTAGE_CODE(String STAGE_CODE) {
        this.STAGE_CODE = STAGE_CODE;
    }

    public String getT_STAGE() {
        return T_STAGE;
    }

    /**
     * @param T_STAGE
     */
    public void setT_STAGE(String t_STAGE) {
        T_STAGE = t_STAGE;
    }

    public String getN_STAGE() {
        return N_STAGE;
    }

    /**
     * @param N_STAGE
     */
    public void setN_STAGE(String n_STAGE) {
        N_STAGE = n_STAGE;
    }

    public String getM_STAGE() {
        return M_STAGE;
    }

    /**
     * @param M_STAGE
     */
    public void setM_STAGE(String m_STAGE) {
        M_STAGE = m_STAGE;
    }

    public String getGRADE() {
        return GRADE;
    }

    /**
     * @param GRADE
     */
    public void setGRADE(String GRADE) {
        this.GRADE = GRADE;
    }

    public String getSAMPLE_TYPE() {
        return SAMPLE_TYPE;
    }

    /**
     * @param SAMPLE_TYPE
     */
    public void setSAMPLE_TYPE(String SAMPLE_TYPE) {
        this.SAMPLE_TYPE = SAMPLE_TYPE;
    }

    public String getPRIMARY_SITE() {
        return PRIMARY_SITE;
    }

    /**
    * @param PRIMARY_SITE
    */
    public void setPRIMARY_SITE(String PRIMARY_SITE) {
        this.PRIMARY_SITE = PRIMARY_SITE;
    }

    public String getSAMPLE_CLASS() {
        return SAMPLE_CLASS;
    }

    /**
     * @param SAMPLE_CLASS
     */
    public void setSAMPLE_CLASS(String SAMPLE_CLASS) {
        this.SAMPLE_CLASS = SAMPLE_CLASS;
    }

    public String getPROCEDURE_TYPE() {
        return PROCEDURE_TYPE;
    }

    /**
     * @param PROCEDURE_TYPE
     */
    public void setPROCEDURE_TYPE(String PROCEDURE_TYPE) {
        this.PROCEDURE_TYPE = PROCEDURE_TYPE;
    }

    public String getPRETREATED() {
        return PRETREATED;
    }

    /**
     * @param PRETREATED
     */
    public void setPRETREATED(String PRETREATED) {
        this.PRETREATED = PRETREATED;
    }

    public String getPDX_TREATED() {
        return PDX_TREATED;
    }

    /**
     * @param PDX_TREATED
     */
    public void setPDX_TREATED(String PDX_TREATED) {
        this.PDX_TREATED = PDX_TREATED;
    }

    public String getER_POSITIVE() {
        return ER_POSITIVE;
    }

    public void setER_POSITIVE(String ER_POSITIVE) {
        this.ER_POSITIVE = ER_POSITIVE;
    }

    public String getER_NEGATIVE() {
        return ER_NEGATIVE;
    }

    public void setER_NEGATIVE(String ER_NEGATIVE) {
        this.ER_NEGATIVE = ER_NEGATIVE;
    }

    public String getHER2_POSITIVE() {
        return HER2_POSITIVE;
    }

    public void setHER2_POSITIVE(String HER2_POSITIVE) {
        this.HER2_POSITIVE = HER2_POSITIVE;
    }

    public String getHER2_NEGATIVE() {
        return HER2_NEGATIVE;
    }

    public void setHER2_NEGATIVE(String HER2_NEGATIVE) {
        this.HER2_NEGATIVE = HER2_NEGATIVE;
    }

    public String getHPV_POSITIVE() {
        return HPV_POSITIVE;
    }

    public void setHPV_POSITIVE(String HPV_POSITIVE) {
        this.HPV_POSITIVE = HPV_POSITIVE;
    }

    public String getHPV_NEGATIVE() {
        return HPV_NEGATIVE;
    }

    public String getP16_POSITIVE() {
        return P16_POSITIVE;
    }

    public void setP16_POSITIVE(String p16_POSITIVE) {
        P16_POSITIVE = p16_POSITIVE;
    }

    public String getP16_NEGATIVE() {
        return P16_NEGATIVE;
    }

    public void setP16_NEGATIVE(String p16_NEGATIVE) {
        P16_NEGATIVE = p16_NEGATIVE;
    }

    public String getPR_POSITIVE() {
        return PR_POSITIVE;
    }

    public void setPR_POSITIVE(String PR_POSITIVE) {
        this.PR_POSITIVE = PR_POSITIVE;
    }

    public String getPR_NEGATIVE() {
        return PR_NEGATIVE;
    }

    public void setPR_NEGATIVE(String PR_NEGATIVE) {
        this.PR_NEGATIVE = PR_NEGATIVE;
    }

    public String getIDH1_POSITIVE() {
        return IDH1_POSITIVE;
    }

    public void setIDH1_POSITIVE(String IDH1_POSITIVE) {
        this.IDH1_POSITIVE = IDH1_POSITIVE;
    }

    public String getIDH1_NEGATIVE() {
        return IDH1_NEGATIVE;
    }

    public void setIDH1_NEGATIVE(String IDH1_NEGATIVE) {
        this.IDH1_NEGATIVE = IDH1_NEGATIVE;
    }

    public String getIDH2_POSITIVE() {
        return IDH2_POSITIVE;
    }

    public void setIDH2_POSITIVE(String IDH2_POSITIVE) {
        this.IDH2_POSITIVE = IDH2_POSITIVE;
    }

    public String getIDH2_NEGATIVE() {
        return IDH2_NEGATIVE;
    }

    public void setIDH2_NEGATIVE(String IDH2_NEGATIVE) {
        this.IDH2_NEGATIVE = IDH2_NEGATIVE;
    }

    public String getEGFR_POSITIVE() {
        return EGFR_POSITIVE;
    }

    public void setEGFR_POSITIVE(String EGFR_POSITIVE) {
        this.EGFR_POSITIVE = EGFR_POSITIVE;
    }

    public String getEGFR_NEGATIVE() {
        return EGFR_NEGATIVE;
    }

    public void setEGFR_NEGATIVE(String EGFR_NEGATIVE) {
        this.EGFR_NEGATIVE = EGFR_NEGATIVE;
    }

    public String getALK_POSITIVE() {
        return ALK_POSITIVE;
    }

    public void setALK_POSITIVE(String ALK_POSITIVE) {
        this.ALK_POSITIVE = ALK_POSITIVE;
    }

    public String getALK_NEGATIVE() {
        return ALK_NEGATIVE;
    }

    public void setALK_NEGATIVE(String ALK_NEGATIVE) {
        this.ALK_NEGATIVE = ALK_NEGATIVE;
    }

    public String getBRCA1_POSITIVE() {
        return BRCA1_POSITIVE;
    }

    public void setBRCA1_POSITIVE(String BRCA1_POSITIVE) {
        this.BRCA1_POSITIVE = BRCA1_POSITIVE;
    }

    public String getBRCA1_NEGATIVE() {
        return BRCA1_NEGATIVE;
    }

    public void setBRCA1_NEGATIVE(String BRCA1_NEGATIVE) {
        this.BRCA1_NEGATIVE = BRCA1_NEGATIVE;
    }

    public String getBRCA2_POSITIVE() {
        return BRCA2_POSITIVE;
    }

    public void setBRCA2_POSITIVE(String BRCA2_POSITIVE) {
        this.BRCA2_POSITIVE = BRCA2_POSITIVE;
    }

    public String getBRCA2_NEGATIVE() {
        return BRCA2_NEGATIVE;
    }

    public void setBRCA2_NEGATIVE(String BRCA2_NEGATIVE) {
        this.BRCA2_NEGATIVE = BRCA2_NEGATIVE;
    }

    public String getC_MYC_POSITIVE() {
        return C_MYC_POSITIVE;
    }

    public void setC_MYC_POSITIVE(String c_MYC_POSITIVE) {
        C_MYC_POSITIVE = c_MYC_POSITIVE;
    }

    public String getC_MYC_NEGATIVE() {
        return C_MYC_NEGATIVE;
    }

    public void setC_MYC_NEGATIVE(String c_MYC_NEGATIVE) {
        C_MYC_NEGATIVE = c_MYC_NEGATIVE;
    }

    public String getAR_POSITIVE() {
        return AR_POSITIVE;
    }

    public void setAR_POSITIVE(String AR_POSITIVE) {
        this.AR_POSITIVE = AR_POSITIVE;
    }

    public String getAR_NEGATIVE() {
        return AR_NEGATIVE;
    }

    public void setAR_NEGATIVE(String AR_NEGATIVE) {
        this.AR_NEGATIVE = AR_NEGATIVE;
    }

    public String getKRAS_POSITIVE() {
        return KRAS_POSITIVE;
    }

    public void setKRAS_POSITIVE(String KRAS_POSITIVE) {
        this.KRAS_POSITIVE = KRAS_POSITIVE;
    }

    public String getKRAS_NEGATIVE() {
        return KRAS_NEGATIVE;
    }

    public void setKRAS_NEGATIVE(String KRAS_NEGATIVE) {
        this.KRAS_NEGATIVE = KRAS_NEGATIVE;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /**
     * Returns the field names without additional properties.
     * @return List<String>
     */
    public static List<String> getFieldNames() {
        return CRDBPDXClinicalSampleDataset.fieldNameList;
    }

    private static List<String> initializeFieldNameList() {
        List<String> fieldNameList = new ArrayList<String>(35);
        fieldNameList.add("PATIENT_ID");
        fieldNameList.add("SAMPLE_ID");
        fieldNameList.add("PDX_ID");
        fieldNameList.add("COLLAB_ID");
        fieldNameList.add("AGE_AT_INITIAL_DIAGNOSIS");
        fieldNameList.add("PASSAGE_ID");
        fieldNameList.add("ONCOTREE_CODE");
        fieldNameList.add("STAGE_CODE");
        fieldNameList.add("T_STAGE");
        fieldNameList.add("N_STAGE");
        fieldNameList.add("M_STAGE");
        fieldNameList.add("GRADE");
        fieldNameList.add("SAMPLE_TYPE");
        fieldNameList.add("PRIMARY_SITE");
        fieldNameList.add("SAMPLE_CLASS");
        fieldNameList.add("PROCEDURE_TYPE");
        fieldNameList.add("PRETREATED");
        fieldNameList.add("PDX_TREATED");
        fieldNameList.add("ER_POSITIVE");
        fieldNameList.add("ER_NEGATIVE");
        fieldNameList.add("HER2_POSITIVE");
        fieldNameList.add("HER2_NEGATIVE");
        fieldNameList.add("HPV_POSITIVE");
        fieldNameList.add("HPV_NEGATIVE");
        fieldNameList.add("P16_POSITIVE");
        fieldNameList.add("P16_NEGATIVE");
        fieldNameList.add("PR_POSITIVE");
        fieldNameList.add("PR_NEGATIVE");
        fieldNameList.add("IDH1_POSITIVE");
        fieldNameList.add("IDH1_NEGATIVE");
        fieldNameList.add("IDH2_POSITIVE");
        fieldNameList.add("IDH2_NEGATIVE");
        fieldNameList.add("EGFR_POSITIVE");
        fieldNameList.add("EGFR_NEGATIVE");
        fieldNameList.add("ALK_POSITIVE");
        fieldNameList.add("ALK_NEGATIVE");
        fieldNameList.add("BRCA1_POSITIVE");
        fieldNameList.add("BRCA1_NEGATIVE");
        fieldNameList.add("BRCA2_POSITIVE");
        fieldNameList.add("BRCA2_NEGATIVE");
        fieldNameList.add("C_MYC_POSITIVE");
        fieldNameList.add("C_MYC_NEGATIVE");
        fieldNameList.add("AR_POSITIVE");
        fieldNameList.add("AR_NEGATIVE");
        fieldNameList.add("KRAS_POSITIVE");
        fieldNameList.add("KRAS_NEGATIVE");
        return Collections.unmodifiableList(fieldNameList);
    }
}
