/*
 * Copyright (c) 2017 Memorial Sloan-Kettering Cancer Center.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cmo.ks.redcap.source.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mockito;
import org.mskcc.cmo.ks.redcap.models.RedcapAttributeMetadata;
import org.mskcc.cmo.ks.redcap.models.RedcapProjectAttribute;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.mskcc.cmo.ks.redcap.source.internal.RedcapSourceTestConfiguration;
import org.mskcc.cmo.ks.redcap.source.internal.RedcapRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.junit.Assert;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RedcapSourceTestConfiguration.class)
public class RedcapRepositoryTest {

    @Autowired
    private RedcapRepository redcapRepository;

    @Test
    public void testGetDataForRedcapProjectID() {
        if (redcapRepository == null) {
            Assert.fail("redcapRepository was not initialized properly for testing");
        }
        List<Map<String, String>> dataReturned = redcapRepository.getRedcapDataForProject(RedcapSourceTestConfiguration.ONE_DIGIT_PROJECT_ID_TOKEN);
        List<Map<String, String>> expectedReturnValue = createExpectedReturnExportFromRedcapOneNumericAttributeName();
        String differenceBetweenReturnedAndExpected = getDifferenceBetweenReturnedAndExpected(dataReturned, expectedReturnValue);
        if (differenceBetweenReturnedAndExpected != null && differenceBetweenReturnedAndExpected.length() > 0) {
            Assert.fail("difference between returned and expected values: " + differenceBetweenReturnedAndExpected);
        }
    }

    private String getDifferenceBetweenReturnedAndExpected(List<Map<String, String>> dataReturned, List<Map<String, String>> expectedReturnValue) {
        if (dataReturned == null) {
            return "Data returned was null";
        }
        if (expectedReturnValue == null) {
            return "Expected return value was null";
        }
        if (dataReturned.size() != expectedReturnValue.size()) {
            return "Different number of records returned (" + Integer.toString(dataReturned.size()) + ") versus expected (" + Integer.toString(expectedReturnValue.size()) + ")";
        }
        StringBuilder result = new StringBuilder();
        for (int pos = 0; pos < dataReturned.size() ; pos++) {
            Map<String, String> dataReturnedItem = dataReturned.get(pos);
            Map<String, String> expectedReturnValueItem = expectedReturnValue.get(pos);
            if (dataReturnedItem.size() != expectedReturnValueItem.size()) {
                result.append("Different number of attribute for items at index " + Integer.toString(pos) + " returnedItem has (" + Integer.toString(dataReturnedItem.size()) + ") versus expectedItem has  (" + Integer.toString(expectedReturnValueItem.size()) + ")");
            }
            for (String key : dataReturnedItem.keySet()) {
                if (!expectedReturnValueItem.containsKey(key)) {
                    result.append("attribute name '" + key + "' returned but expected value does not contain any key with that name (in record number " + Integer.toString(pos) + ")");
                } else {
                    String dataReturnedValue = dataReturnedItem.get(key);
                    String expectedValue = expectedReturnValueItem.get(key);
                    if (!dataReturnedValue.equals(expectedValue)) {
                        result.append("attribute name '" + key + "' has value '" + dataReturnedValue + "' in returned value in record number " + Integer.toString(pos) + ", but expected value was '" + expectedValue);
                    }
               }
            }
        }
        return result.toString();
    }

    private List<Map<String, String>> createExpectedReturnExportFromRedcapOneNumericAttributeName() {
        List<Map<String, String>> returnValue = new ArrayList<Map<String, String>>();
        Map<String, String> returnValue1 = new HashMap<String, String>();
        Map<String, String> returnValue2 = new HashMap<String, String>();
        Map<String, String> returnValue3 = new HashMap<String, String>();
        returnValue1.put("PATIENT_ID", "P-0000004");
        returnValue1.put("CRDB_CONSENT_DATE_DAYS", "14484");
        returnValue1.put("12_245_PARTA_CONSENTED", "YES");
        returnValue.add(returnValue1);
        returnValue2.put("PATIENT_ID", "P-0000012");
        returnValue2.put("CRDB_CONSENT_DATE_DAYS", "21192");
        returnValue2.put("12_245_PARTA_CONSENTED", "YES");
        returnValue.add(returnValue2);
        returnValue3.put("PATIENT_ID", "P-9999999");
        returnValue3.put("CRDB_CONSENT_DATE_DAYS", "99999");
        returnValue3.put("12_245_PARTA_CONSENTED", "");
        returnValue.add(returnValue3);
        return returnValue;
    }

}