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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cmo.ks.redcap.source.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.mskcc.cmo.ks.redcap.models.ProjectInfoResponse;
import org.mskcc.cmo.ks.redcap.models.RedcapAttributeMetadata;
import org.mskcc.cmo.ks.redcap.models.RedcapProjectAttribute;
import org.mskcc.cmo.ks.redcap.models.RedcapToken;
import org.mskcc.cmo.ks.redcap.source.ClinicalDataSource;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Repository
public class RedcapSessionManager {

    private static final String CSFR_TOKEN_REGEX = "var redcap_csrf_token = '([^']+)';";

    private static URI redcapBaseURI = null;
    private static URI redcapApiURI = null;
    private static Boolean cachedUsePostRequestForEraseProjectData = null;

    @Value("${redcap_base_url}")
    private String redcapBaseUrl;
    @Value("${redcap_erase_project_data_url_path}")
    private String redcapEraseProjectDataUrlPath;
    @Value("${redcap_project_setup_url_path}")
    private String redcapProjectSetupUrlPath;
    @Value("${redcap_erase_project_data_http_method}")
    private String redcapEraseProjectDataHttpMethod;
    @Value("${redcap_username}")
    private String redcapUsername;
    @Value("${redcap_password}")
    private String redcapPassword;
    @Value("${redcap_login_hidden_input_name}")
    private String redcapLoginHiddenInputName;
    @Value("${mapping_token}")
    private String mappingToken;
    @Value("${metadata_project}")
    private String metadataProjectStableId;
    @Value("${namespace_project}")
    private String namespaceProjectStableId;

    // entire token cache (used for looking up project titles during import)
    private Map<String, String> allTokensProjectTitleToApiTokenMap = null;
    private Map<String, String> allTokensApiTokenToProjectTitleMap = null;
    private Map<String, List<String>> allTokensStableIdToApiTokenListMap = null;
    private Map<String, String> allTokensApiTokenToStableIdMap = null;
    String metadataToken = null;
    String namespaceToken = null;
    // selected token cache (used for looking up stable ids during export)
    private Map<String, String> selectedClinicalDataTokens = null;
    private Map<String, String> selectedClinicalTimelineTokens = null;
    private String selectedStableId = null;

    public static final String REDCAP_FIELD_NAME_FOR_RECORD_ID = "record_id";

    private final Logger log = Logger.getLogger(RedcapSessionManager.class);

    // SECTION : URI construction

    public URI getRedcapURI() {
        if (redcapBaseURI == null) {
            try {
                redcapBaseURI = new URI(redcapBaseUrl + "/");
             } catch (URISyntaxException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return redcapBaseURI;
    }

    public URI getRedcapApiURI() {
        if (redcapApiURI == null) {
            URI base = getRedcapURI();
            redcapApiURI = base.resolve("api/");
        }
        return redcapApiURI;
    }

    private boolean usePostRequestForEraseProjectData() {
        if (cachedUsePostRequestForEraseProjectData == null) {
            if (redcapEraseProjectDataHttpMethod != null && redcapEraseProjectDataHttpMethod.trim().equalsIgnoreCase("GET")) {
                cachedUsePostRequestForEraseProjectData = Boolean.FALSE;
            } else {
                cachedUsePostRequestForEraseProjectData = Boolean.TRUE;
            }
        }
        return cachedUsePostRequestForEraseProjectData.booleanValue();
    }

    private URI getRedcapProjectSetupURI(String projectId) {
        URI base = getRedcapURI();
        return base.resolve(redcapProjectSetupUrlPath + "?pid=" + projectId);
    }

    private URI getRedcapEraseProjectDataURI(String projectId) {
        URI base = getRedcapURI();
        if (usePostRequestForEraseProjectData()) {
            return base.resolve(redcapEraseProjectDataUrlPath + "?pid=" + projectId);
        } else {
            return base.resolve(redcapEraseProjectDataUrlPath + "?pid=" + projectId + "&action=erase_data");
        }
    }

    // SECTION : RedCap project token handling

    public String getMetadataToken() {
        checkAllTokensHaveBeenFetched();
        return metadataToken;
    }

    public String getNamespaceToken() {
        checkAllTokensHaveBeenFetched();
        return namespaceToken;
    }

    public String getTokenByProjectTitle(String projectTitle) {
        checkAllTokensHaveBeenFetched();
        return allTokensProjectTitleToApiTokenMap.get(projectTitle);
    }

    public Map<String, String> getClinicalTokenMapByStableId(String stableId) {
        checkTokensHaveBeenSelectedByStableId(stableId);
        return new HashMap<String, String>(selectedClinicalDataTokens);
    }

    public Map<String, String> getTimelineTokenMapByStableId(String stableId) {
        checkTokensHaveBeenSelectedByStableId(stableId);
        return new HashMap<String, String>(selectedClinicalTimelineTokens);
    }

    public boolean tokensAreSelected() {
        return selectedClinicalTimelineTokens != null && selectedClinicalDataTokens != null;
    }

    public boolean redcapDataTypeIsTimeline(String projectTitle) {
        return projectTitle.toUpperCase().contains("TIMELINE");
    }

    private void checkAllTokensHaveBeenFetched() {
        if (allTokensProjectTitleToApiTokenMap != null && allTokensApiTokenToProjectTitleMap != null &&
                allTokensStableIdToApiTokenListMap != null && allTokensApiTokenToStableIdMap != null &&
                metadataToken != null && namespaceToken != null) {
            return;
        }
        // fetch tokens
        allTokensProjectTitleToApiTokenMap = new HashMap<String, String>();
        allTokensApiTokenToProjectTitleMap = new HashMap<String, String>();
        allTokensStableIdToApiTokenListMap = new HashMap<String, List<String>>();
        allTokensApiTokenToStableIdMap = new HashMap<String, String>();
        metadataToken = null;
        namespaceToken = null;
        RestTemplate restTemplate = new RestTemplate();

        log.info("Getting tokens for clinical data processor...");

        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", mappingToken);
        uriVariables.add("content", "record");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity(uriVariables);
        ResponseEntity<RedcapToken[]> responseEntity = restTemplate.exchange(getRedcapApiURI(), HttpMethod.POST, requestEntity, RedcapToken[].class);

        for (RedcapToken token : responseEntity.getBody()) {
            String tokenStableId = token.getStableId();
            String tokenProjectTitle = token.getStudyId();
            String tokenApiToken = token.getApiToken();
            if (tokenStableId == null || tokenProjectTitle == null || tokenApiToken == null) {
                throwRuntimeExceptionForIncompleteToken(tokenStableId, tokenProjectTitle, tokenApiToken);
            }
            if (token.getStableId().equals(metadataProjectStableId)) {
                metadataToken = tokenApiToken;
                continue;
            }
            if (token.getStableId().equals(namespaceProjectStableId)) {
                namespaceToken = tokenApiToken;
                continue;
            }
            if (allTokensProjectTitleToApiTokenMap.put(tokenProjectTitle, tokenApiToken) != null) {
                throwRuntimeExceptionForDuplicatedProjectTitle(tokenProjectTitle);
            }
            if (allTokensApiTokenToProjectTitleMap.put(tokenApiToken, tokenProjectTitle) != null) {
                throwRuntimeExceptionForDuplicatedApiToken(tokenApiToken);
            }
            allTokensApiTokenToStableIdMap.put(tokenApiToken, tokenStableId);
            List<String> apiTokenList = allTokensStableIdToApiTokenListMap.get(tokenStableId);
            if (apiTokenList == null) {
                apiTokenList = new LinkedList<String>();
                allTokensStableIdToApiTokenListMap.put(tokenStableId, apiTokenList);
            }
            apiTokenList.add(tokenApiToken);
        }
        if (metadataToken == null || namespaceToken == null) {
            throwRuntimeExceptionForMissingMetadataOrNamespaceToken();
        }
    }

    private void throwRuntimeExceptionForMissingMetadataOrNamespaceToken() {
        String errorMessage = "Error : RedCap mapping project is missing a necessary token. (Either stable_id " + metadataProjectStableId + " or stable_id " + namespaceProjectStableId + ")";
        log.error(errorMessage);
        throw new RuntimeException(errorMessage);
    }

    private void throwRuntimeExceptionForDuplicatedProjectTitle(String projectTitle) {
        String errorMessage = "Error : RedCap mapping project has multiple tokens with projectTitle/(studyId) : " + projectTitle;
        log.error(errorMessage);
        throw new RuntimeException(errorMessage);
    }

    private void throwRuntimeExceptionForDuplicatedApiToken(String apiToken) {
        String errorMessage = "Error : RedCap mapping project has multiple tokens with apiToken value : " + apiToken;
        log.error(errorMessage);
        throw new RuntimeException(errorMessage);
    }

    private void throwRuntimeExceptionForIncompleteToken(String tokenStableId, String tokenProjectTitle, String tokenApiToken) {
        String errorMessage = "Error : token from RedCap mapping project has missing information in fields :";
        if (tokenStableId == null) {
            errorMessage = errorMessage + " stable_id";
        }
        if (tokenApiToken == null) {
            errorMessage = errorMessage + " api_token";
        }
        if (tokenProjectTitle == null) {
            errorMessage = errorMessage + " study_id";
        }
        log.error(errorMessage);
        throw new RuntimeException(errorMessage);
    }

    private void checkTokensHaveBeenSelectedByStableId(String stableId) {
        if (selectedStableId != null && selectedStableId != stableId) {
            throw new RuntimeException("Error : RedCap token selection (by Stable Id) has changed from " +
                    selectedStableId + " to " + stableId +
                    " : once tokens have been selected, the selection cannot be changed within the same instance of RedcapSessionManager");
        }
        selectedStableId = stableId;
        checkAllTokensHaveBeenFetched();
        selectedClinicalTimelineTokens = new HashMap<>();
        selectedClinicalDataTokens = new HashMap<>();

        List<String> apiTokenList = allTokensStableIdToApiTokenListMap.get(stableId);
        if (apiTokenList == null) {
            return;
        }
        for (String apiToken : apiTokenList) {
            String projectTitle = allTokensApiTokenToProjectTitleMap.get(apiToken);
            if (redcapDataTypeIsTimeline(projectTitle)) {
                selectedClinicalTimelineTokens.put(projectTitle, apiToken);
            } else {
                selectedClinicalDataTokens.put(projectTitle, apiToken);
            }
        }
    }

    // SECTION : utility functions for doing RedCap specific requests

    public Integer getMaximumRecordIdInRedcapProjectIfPresent(String projectToken) {
        if (!projectContainsRecordIdField(projectToken)) {
            return null;
        }
        RestTemplate restTemplate = new RestTemplate();
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", projectToken);
        uriVariables.add("content", "record");
        uriVariables.add("format", "csv");
        uriVariables.add("fields", REDCAP_FIELD_NAME_FOR_RECORD_ID);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity(uriVariables);
        ResponseEntity<String> responseEntity = restTemplate.exchange(getRedcapApiURI(), HttpMethod.POST, requestEntity, String.class);
        String responseString = responseEntity.getBody().replaceAll("\r","");
        String[] records = responseString.split("\n");
        int maxRecordId = 0;
        for (int index = 1; index < records.length; index++) {
            String[] fields = records[index].split(",",-1);
            try {
                int recordId = Integer.parseInt(fields[0]);
                if (recordId > maxRecordId) {
                    maxRecordId = recordId;
                }
            } catch (NumberFormatException e) {
                String errorString = "error: field in record_id column in RedCap project contains a non-integer: " + fields[0];
                log.error(errorString);
                throw new RuntimeException(errorString);
            }
        }
        return new Integer(maxRecordId);
    }

    public void deleteRedcapProjectData(String token) {
        String cookie = getSessionCookieFromRedcap();
        if (cookie == null) {
            log.warn("RedCap session cookie not available; unable to delete project data");
            throw new RuntimeException("RedCap session cookie not available; unable to delete project data");
        }
        String projectId = getProjectIdFromRedcap(token, cookie);
        if (projectId == null) {
            log.warn("ProjectId not available from RedCap getProjectData API request");
            throw new RuntimeException("ProjectId not available from RedCap getProjectData API request");
        }
        log.info("deleting all records for RedCap projectId: " + projectId);
        deleteRedcapProjectData(cookie, projectId);
    }

    public void importClinicalData(String token, String dataForImport) {
        log.info("importing data ... (" + dataForImport.length() + " characters)");
        RestTemplate restTemplate = new RestTemplate();
        LinkedMultiValueMap<String, String> importRecordUriVariables = new LinkedMultiValueMap<>();
        importRecordUriVariables.add("token", token);
        importRecordUriVariables.add("content", "record");
        importRecordUriVariables.add("format", "csv");
        importRecordUriVariables.add("overwriteBehavior", "overwrite");
        importRecordUriVariables.add("data", dataForImport);
        HttpEntity<LinkedMultiValueMap<String, Object>> importRecordRequestEntity = getRequestEntity(importRecordUriVariables);
        ResponseEntity<String> importRecordResponseEntity = restTemplate.exchange(getRedcapApiURI(), HttpMethod.POST, importRecordRequestEntity, String.class);
        HttpStatus responseStatus = importRecordResponseEntity.getStatusCode();
        if (!responseStatus.is2xxSuccessful() && !responseStatus.is3xxRedirection()) {
            log.warn("RedCap import record API call failed. HTTP status code = " + Integer.toString(importRecordResponseEntity.getStatusCode().value()));
            throw new RuntimeException("RedCap import record API call failed. HTTP status code");
        }
        log.info("Return from call to Import Recap Record API: " + importRecordResponseEntity.getBody());
    }

    public HttpEntity getRequestEntity(LinkedMultiValueMap<String, String> uriVariables) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return new HttpEntity<LinkedMultiValueMap<String, String>>(uriVariables, headers);
    }

    private boolean projectContainsRecordIdField(String projectToken) {
        RestTemplate restTemplate = new RestTemplate();
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", projectToken);
        uriVariables.add("content", "exportFieldNames");
        uriVariables.add("format", "csv");
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity(uriVariables);
        ResponseEntity<String> responseEntity = restTemplate.exchange(getRedcapApiURI(), HttpMethod.POST, requestEntity, String.class);
        String responseString = responseEntity.getBody().replaceAll("\r","");
        String[] records = responseString.split("\n");
        for (int index = 1; index < records.length; index++) {
            String[] fields = records[index].split(",",-1);
            if (REDCAP_FIELD_NAME_FOR_RECORD_ID.equals(fields[0])) {
                return true;
            }
        }
        return false;
    }

    private String getSessionCookieFromRedcap() {
        RestTemplate restTemplate = new RestTemplate();
        LinkedMultiValueMap<String, String> loginUriVariables = new LinkedMultiValueMap<>();
        loginUriVariables.add("username", redcapUsername);
        loginUriVariables.add("password", redcapPassword);
        loginUriVariables.add(redcapLoginHiddenInputName, "");
        loginUriVariables.add("submitted", "1");
        HttpEntity<LinkedMultiValueMap<String, Object>> loginRequestEntity = getRequestEntity(loginUriVariables);
        ResponseEntity<String> loginResponseEntity = restTemplate.exchange(getRedcapURI(), HttpMethod.POST, loginRequestEntity, String.class);
        HttpStatus responseStatus = loginResponseEntity.getStatusCode();
        if (!responseStatus.is2xxSuccessful() && !responseStatus.is3xxRedirection()) {
            log.warn("RedCap login with username/password/hiddenInput attempt failed. HTTP status code = " + Integer.toString(loginResponseEntity.getStatusCode().value()));
            return null;
        }
        Predicate<String> notDeleted = (String s) -> !s.contains("deleted");
        List<String> cookies = loginResponseEntity.getHeaders().get("Set-Cookie").stream().filter(notDeleted).collect(Collectors.toList());
        if (cookies.size() < 1) {
            log.warn("RedCap login succeeded but no Set-Cookie header field was included.");
            return null;
        }
        return cookies.get(0).split(";")[0];
    }

    private String getProjectIdFromRedcap(String token, String cookie) {
        RestTemplate restTemplate = new RestTemplate();
        LinkedMultiValueMap<String, String> projectInfoUriVariables = new LinkedMultiValueMap<>();
        projectInfoUriVariables.add("token", token);
        projectInfoUriVariables.add("content", "project");
        projectInfoUriVariables.add("format", "json");
        projectInfoUriVariables.add("returnFormat", "json");
        HttpEntity<LinkedMultiValueMap<String, Object>> projectInfoRequestEntity = getRequestEntity(projectInfoUriVariables);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookie);
        HttpEntity<?> rq = new HttpEntity<>(headers);
        ResponseEntity<ProjectInfoResponse> projectInfoResponseEntity = restTemplate.exchange(getRedcapApiURI(), HttpMethod.POST, projectInfoRequestEntity, ProjectInfoResponse.class);
        HttpStatus responseStatus = projectInfoResponseEntity.getStatusCode();
        if (!responseStatus.is2xxSuccessful() && !responseStatus.is3xxRedirection()) {
            log.warn("RedCap request for project data failed. HTTP status code = " + Integer.toString(projectInfoResponseEntity.getStatusCode().value()));
            return null;
        }
        return projectInfoResponseEntity.getBody().getProjectId();
    }

    private void deleteRedcapProjectData(String cookie, String projectId) {
        String csfrToken = null;
        if (usePostRequestForEraseProjectData()) {
            csfrToken = getCSFRTokenFromProjectSetupPage(cookie, projectId);
        }
        submitEraseAllDataRequest(cookie, projectId, csfrToken);
    }

    private String getCSFRTokenFromProjectSetupPage(String cookie, String projectId) {
        String projectSetupPageContent = getProjectSetupPageContent(cookie, projectId);
        Pattern csfrTokenRegexPattern = Pattern.compile(CSFR_TOKEN_REGEX);
        Matcher csfrTokenMatcher = csfrTokenRegexPattern.matcher(projectSetupPageContent);
        if (csfrTokenMatcher.find() && csfrTokenMatcher.groupCount() > 0) {
            return csfrTokenMatcher.group(1);
        }
        return ""; // we expect this to cause failure during the erase_project_data function call .. but ... we can try
    }

    private String getProjectSetupPageContent(String cookie, String projectId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookie);
        HttpEntity<?> rq = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(getRedcapProjectSetupURI(projectId), HttpMethod.GET, rq, String.class);
        return response.getBody();
    }

    private void submitEraseAllDataRequest(String cookie, String projectId, String csfrToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookie);
        if (usePostRequestForEraseProjectData()) {
            MultiValueMap<String, String> args = new LinkedMultiValueMap<String, String>();
            args.add("action", "erase_data");
            args.add("redcap_csrf_token", csfrToken);
            HttpEntity< MultiValueMap<String, String> > rq = new HttpEntity< MultiValueMap<String, String> >(args, headers);
            ResponseEntity<String> response = restTemplate.exchange(getRedcapEraseProjectDataURI(projectId), HttpMethod.POST, rq, String.class);
        } else {
            HttpEntity<?> rq = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(getRedcapEraseProjectDataURI(projectId), HttpMethod.GET, rq, String.class);
        }
    }

    public JsonNode[] getRedcapDataForProjectByToken(String projectToken) {
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", projectToken);
        uriVariables.add("content", "record");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity(uriVariables);
        log.info("Getting data for project...");
        ResponseEntity<JsonNode[]> responseEntity = restTemplate.exchange(getRedcapApiURI(), HttpMethod.POST, requestEntity, JsonNode[].class);
        //TODO check status of http request after completed .. throw exception on failure
        return responseEntity.getBody();
    }

    public String getRedcapInstrumentNameByToken(String projectToken) {
        RedcapProjectAttribute[] attributeArray = getRedcapAttributeByToken(projectToken);
        if (attributeArray == null || attributeArray.length < 1) {
            String errorMessage = "Error retrieving instrument name from project : no attributes available";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        return attributeArray[0].getFormName();
    }

    public RedcapProjectAttribute[] getRedcapAttributeByToken(String projectToken) {
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", projectToken);
        uriVariables.add("content", "metadata");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity(uriVariables);
        log.info("Getting attributes for project...");
        ResponseEntity<RedcapProjectAttribute[]> responseEntity = restTemplate.exchange(getRedcapApiURI(), HttpMethod.POST, requestEntity, RedcapProjectAttribute[].class);
        return responseEntity.getBody();
    }

    public RedcapAttributeMetadata[] getRedcapMetadataByToken(String projectToken) {
        RestTemplate restTemplate = new RestTemplate();
        log.info("Getting attribute metadatas...");
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", projectToken);
        uriVariables.add("content", "record");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity(uriVariables);
        ResponseEntity<RedcapAttributeMetadata[]> responseEntity = restTemplate.exchange(getRedcapApiURI(), HttpMethod.POST, requestEntity, RedcapAttributeMetadata[].class);
        return responseEntity.getBody();
    }

    public RedcapAttributeMetadata[] getRedcapNamespace() {
        RestTemplate restTemplate = new RestTemplate();
        log.info("Getting attribute namespace...");
        String namespaceToken = getNamespaceToken();
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", namespaceToken);
        uriVariables.add("content", "record");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity(uriVariables);
        ResponseEntity<RedcapAttributeMetadata[]> responseEntity = restTemplate.exchange(getRedcapApiURI(), HttpMethod.POST, requestEntity, RedcapAttributeMetadata[].class);
        return responseEntity.getBody();
    }

}
