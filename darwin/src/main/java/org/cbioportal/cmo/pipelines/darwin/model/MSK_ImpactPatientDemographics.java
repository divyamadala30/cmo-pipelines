/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin.model;

import java.util.*;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author jake
 */
public class MSK_ImpactPatientDemographics {
    private final Integer currentYear = Calendar.getInstance().get(1);
    private String PT_ID_DEMO;
    private String DMP_ID_DEMO;
    private String GENDER;
    private String RACE;
    private String RELIGION;
    private Integer AGE_AT_DATE_OF_DEATH_IN_DAYS;
    private String DEATH_SOURCE_DESCRIPTION;
    private String VITAL_STATUS;
    private String PT_COUNTRY;
    private String PT_STATE;
    private String PT_ZIP3_CD;
    private Integer PT_BIRTH_YEAR;
    private String PT_SEX_DESC;
    private String PT_VITAL_STATUS;
    private String PT_MARITAL_STS_DESC;
    private Integer PT_DEATH_YEAR;
    private String PT_MRN_CREATE_YEAR;
    private Integer TM_DX_YEAR;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public MSK_ImpactPatientDemographics() {
    }

    public MSK_ImpactPatientDemographics(String PT_ID_DEMO, String DMP_ID_DEMO, String GENDER,
            String RACE, String RELIGION, Integer AGE_AT_DATE_OF_DEATH_IN_DAYS, String DEATH_SOURCE_DESCRIPTION, String VITAL_STATUS,
            String PT_COUNTRY, String PT_STATE, String PT_ZIP3_CD, Integer PT_BIRTH_YEAR, String PT_SEX_DESC,
            String PT_VITAL_STATUS, String PT_MARITAL_STS_DESC, Integer PT_DEATH_YEAR, String PT_MRN_CREATE_YEAR, Integer TM_DX_YEAR) {
        this.PT_ID_DEMO = PT_ID_DEMO != null ? PT_ID_DEMO : "NA";
        this.DMP_ID_DEMO = DMP_ID_DEMO != null ? DMP_ID_DEMO : "NA";
        this.GENDER = GENDER != null ? GENDER : "NA";
        this.RACE = RACE != null ? RACE : "NA";
        this.RELIGION = RELIGION != null ? RELIGION : "NA";
        this.AGE_AT_DATE_OF_DEATH_IN_DAYS = AGE_AT_DATE_OF_DEATH_IN_DAYS != null ? AGE_AT_DATE_OF_DEATH_IN_DAYS : -1;
        this.DEATH_SOURCE_DESCRIPTION = DEATH_SOURCE_DESCRIPTION != null ? DEATH_SOURCE_DESCRIPTION : "NA";
        this.VITAL_STATUS = VITAL_STATUS != null ? VITAL_STATUS : "NA";
        this.PT_COUNTRY = PT_COUNTRY != null ? PT_COUNTRY : "NA";
        this.PT_STATE = PT_STATE != null ? PT_STATE : "NA";
        this.PT_ZIP3_CD = PT_ZIP3_CD != null ? PT_ZIP3_CD : "NA";
        this.PT_BIRTH_YEAR = PT_BIRTH_YEAR != null ? PT_BIRTH_YEAR : -1;
        this.PT_SEX_DESC = PT_SEX_DESC != null ? PT_SEX_DESC : "NA";
        this.PT_VITAL_STATUS = PT_VITAL_STATUS != null ? PT_VITAL_STATUS : "NA";
        this.PT_MARITAL_STS_DESC = PT_MARITAL_STS_DESC != null ? PT_MARITAL_STS_DESC : "NA";
        this.PT_DEATH_YEAR = PT_DEATH_YEAR != null ? PT_DEATH_YEAR : -1;
        this.PT_MRN_CREATE_YEAR = PT_MRN_CREATE_YEAR != null ? PT_MRN_CREATE_YEAR : "NA";
        this.TM_DX_YEAR = TM_DX_YEAR != null ? TM_DX_YEAR : -1;
    }
    
    public MSK_ImpactPatientDemographics(String DMP_ID_DEMO, String GENDER, String RACE, String RELIGION, String VITAL_STATUS, Integer PT_BIRTH_YEAR, Integer PT_DEATH_YEAR, Integer TM_DX_YEAR){
        this.DMP_ID_DEMO = DMP_ID_DEMO != null ? DMP_ID_DEMO : "NA";
        this.GENDER = GENDER != null ? GENDER : "NA";
        this.RACE = RACE != null ? RACE : "NA";
        this.RELIGION = RELIGION != null ? RELIGION : "NA";
        this.VITAL_STATUS = VITAL_STATUS != null ? VITAL_STATUS : "NA";
        this.TM_DX_YEAR = TM_DX_YEAR != null ? TM_DX_YEAR : -1;
        this.PT_BIRTH_YEAR = PT_BIRTH_YEAR != null ? PT_BIRTH_YEAR : -1;
        this.PT_DEATH_YEAR = PT_DEATH_YEAR != null ? PT_DEATH_YEAR : -1;
    }
    public MSK_ImpactPatientDemographics(String DMP_ID_DEMO, String GENDER, String RACE, String RELIGION, String VITAL_STATUS, Integer PT_BIRTH_YEAR, Integer PT_DEATH_YEAR){
        this.DMP_ID_DEMO = DMP_ID_DEMO != null ? DMP_ID_DEMO : "NA";
        this.GENDER = GENDER != null ? GENDER : "NA";
        this.RACE = RACE != null ? RACE : "NA";
        this.RELIGION = RELIGION != null ? RELIGION : "NA";
        this.VITAL_STATUS = VITAL_STATUS != null ? VITAL_STATUS : "NA";
        this.TM_DX_YEAR = -1;
        this.PT_BIRTH_YEAR = PT_BIRTH_YEAR != null ? PT_BIRTH_YEAR : -1;
        this.PT_DEATH_YEAR = PT_DEATH_YEAR != null ? PT_DEATH_YEAR : -1;
    }
    
    public Integer getTM_DX_YEAR() {
        return TM_DX_YEAR;
    }

    public void setTM_DX_YEAR(Integer TM_DX_YEAR) {
        this.TM_DX_YEAR = TM_DX_YEAR != null ? TM_DX_YEAR : -1;
    }
    
    public String getAGE_AT_DIAGNOSIS(){
        if(this.PT_BIRTH_YEAR>-1 && this.TM_DX_YEAR>-1 && this.TM_DX_YEAR>this.PT_BIRTH_YEAR){
                Integer i = this.TM_DX_YEAR - this.PT_BIRTH_YEAR;
                //Age > 90 is considered identifying
                if(i<90){
                    return i.toString();
                }
                else{
                    return "NA";
                }
        }
        else{
            return "NA";
        }
    }

    public String getPT_ID_DEMO() {
        return PT_ID_DEMO;
    }

    public void setPT_ID_DEMO(String PT_ID_DEMO) {
        this.PT_ID_DEMO = PT_ID_DEMO != null ? PT_ID_DEMO : "NA";
    }

    public String getDMP_ID_DEMO() {
        return DMP_ID_DEMO;
    }

    public void setDMP_ID_DEMO(String DMP_ID_DEMO) {
        this.DMP_ID_DEMO = DMP_ID_DEMO != null ? DMP_ID_DEMO : "NA";
    }

    public String getGENDER() {
        return GENDER;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER != null ? GENDER : "NA";
    }

    public String getRACE() {
        return RACE;
    }

    public void setRACE(String RACE) {
        this.RACE = RACE != null ? RACE : "NA";
    }

    public String getRELIGION() {
        return RELIGION;
    }

    public void setRELIGION(String RELIGION) {
        this.RELIGION = RELIGION != null ? RELIGION : "NA";
    }

    public Integer getAGE_AT_DATE_OF_DEATH_IN_DAYS() {
        return AGE_AT_DATE_OF_DEATH_IN_DAYS;
    }

    public void setAGE_AT_DATE_OF_DEATH_IN_DAYS(Integer AGE_AT_DATE_OF_DEATH_IN_DAYS) {
        this.AGE_AT_DATE_OF_DEATH_IN_DAYS = AGE_AT_DATE_OF_DEATH_IN_DAYS != null ? AGE_AT_DATE_OF_DEATH_IN_DAYS : -1;
    }

    public String getDEATH_SOURCE_DESCRIPTION() {
        return DEATH_SOURCE_DESCRIPTION;
    }

    public void setDEATH_SOURCE_DESCRIPTION(String DEATH_SOURCE_DESCRIPTION) {
        this.DEATH_SOURCE_DESCRIPTION = DEATH_SOURCE_DESCRIPTION != null ? DEATH_SOURCE_DESCRIPTION : "NA";
    }
    
    public String getVITAL_STATUS(){
        return VITAL_STATUS;
    }
    
    public void setVITAL_STATUS(){
        this.VITAL_STATUS = VITAL_STATUS != null ? VITAL_STATUS : "NA";
    }

    public String getPT_COUNTRY() {
        return PT_COUNTRY;
    }

    public void setPT_COUNTRY(String PT_COUNTRY) {
        this.PT_COUNTRY = PT_COUNTRY != null ? PT_COUNTRY : "NA";
    }

    public String getPT_STATE() {
        return PT_STATE;
    }

    public void setPT_STATE(String PT_STATE) {
        this.PT_STATE = PT_STATE != null ? PT_STATE : "NA";
    }

    public String getPT_ZIP3_CD() {
        return PT_ZIP3_CD;
    }

    public void setPT_ZIP3_CD(String PT_ZIP3_CD) {
        this.PT_ZIP3_CD = PT_ZIP3_CD != null ? PT_ZIP3_CD : "NA";
    }

    public Integer getPT_BIRTH_YEAR() {
        return PT_BIRTH_YEAR;
    }

    public void setPT_BIRTH_YEAR(Integer PT_BIRTH_YEAR) {
        this.PT_BIRTH_YEAR = PT_BIRTH_YEAR != null ? PT_BIRTH_YEAR : -1;
    }

    public String getPT_SEX_DESC() {
        return PT_SEX_DESC;
    }

    public void setPT_SEX_DESC(String PT_SEX_DESC) {
        this.PT_SEX_DESC = PT_SEX_DESC != null ? PT_SEX_DESC : "NA";
    }

    public String getPT_VITAL_STATUS() {
        return PT_VITAL_STATUS;
    }

    public void setPT_VITAL_STATUS(String PT_VITAL_STATUS) {
        this.PT_VITAL_STATUS = PT_VITAL_STATUS != null ? PT_VITAL_STATUS : "NA";
    }

    public String getPT_MARITAL_STS_DESC() {
        return PT_MARITAL_STS_DESC;
    }

    public void setPT_MARITAL_STS_DESC(String PT_MARITAL_STS_DESC) {
        this.PT_MARITAL_STS_DESC = PT_MARITAL_STS_DESC != null ? PT_MARITAL_STS_DESC : "NA";
    }

    public Integer getPT_DEATH_YEAR() {
        return PT_DEATH_YEAR;
    }

    public void setPT_DEATH_YEAR(Integer PT_DEATH_YEAR) {
        this.PT_DEATH_YEAR = PT_DEATH_YEAR != null ? PT_DEATH_YEAR : -1;
    }

    public String getPT_MRN_CREATE_YEAR() {
        return PT_MRN_CREATE_YEAR;
    }

    public void setPT_MRN_CREATE_YEAR(String PT_MRN_CREATE_YEAR) {
        this.PT_MRN_CREATE_YEAR = PT_MRN_CREATE_YEAR != null ? PT_MRN_CREATE_YEAR : "NA";
    }
    public String getDARWIN_PATIENT_AGE(){
        if(this.PT_BIRTH_YEAR>-1){
            if(this.PT_DEATH_YEAR>-1){
                Integer i = this.PT_DEATH_YEAR-this.PT_BIRTH_YEAR;
                //Age > 90 is considered identifying
                if (i >= 90){
                    return "90";
                }
                return i.toString();
            }
            Integer i = currentYear-this.PT_BIRTH_YEAR;
            //Age > 90 is considered identifying
            if (i >= 90) {
                return "90";
            }
            return i.toString();
        }
        else{
            return "NA";
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("DMP_ID_DEMO");
        fieldNames.add("DARWIN_PATIENT_AGE");
        fieldNames.add("RACE");
        fieldNames.add("RELIGION");
        fieldNames.add("GENDER");
        fieldNames.add("VITAL_STATUS");
        fieldNames.add("AGE_AT_DIAGNOSIS");

        return fieldNames;

    }
    public List<String> getHeaders() {
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("PATIENT_ID");
        fieldNames.add("DARWIN_PATIENT_AGE");
        fieldNames.add("DARWIN_RACE");
        fieldNames.add("DARWIN_RELIGION");
        fieldNames.add("DARWIN_SEX");
        fieldNames.add("DARWIN_VITAL_STATUS");
        fieldNames.add("DARWIN_AGE_AT_DIAGNOSIS");

        return fieldNames;

    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

}
