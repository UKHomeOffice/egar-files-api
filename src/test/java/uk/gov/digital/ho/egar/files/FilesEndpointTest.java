package uk.gov.digital.ho.egar.files;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.digital.ho.egar.constants.ServicePathConstants;
import uk.gov.digital.ho.egar.files.api.RestConstants;
import uk.gov.digital.ho.egar.files.client.FileStorageClient;
import uk.gov.digital.ho.egar.files.client.impl.DummyFileStorageClient;
import uk.gov.digital.ho.egar.files.service.FileService;
import uk.gov.digital.ho.egar.files.service.repository.FilePersistedRecordRepository;
import uk.gov.digital.ho.egar.files.service.repository.model.FilePersistedRecord;
import uk.gov.digital.ho.egar.files.tests.utils.FileReaderUtils;

import java.util.List;
import java.util.UUID;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "eureka.client.enabled=false", "spring.cloud.config.discovery.enabled=false",
		"spring.profiles.active=s3-mocks,jms-disabled" })
@AutoConfigureMockMvc
public class FilesEndpointTest {

	private static final String USERID_HEADER = "x-auth-subject";
	private static final String AUTH_HEADER = "Authorization";
	private static final UUID USER_UUID = UUID.randomUUID();
	private static final String AUTH = "values";
	private static final String requestURL = "/api/v1/Files/";
	private static final String detailsURL = "api/v1/FileDetails/";

	private static final String bucket = "egar-file-upload";
	private static final String OBJECT_KEY = "object_key";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FilePersistedRecordRepository repo;

	@Autowired
	private FilesApplication app;
	
	@Autowired
	private FileService service;

	@MockBean
	private JmsTemplate jmsTemplate;

	private final Log logger = LogFactory.getLog(FilesEndpointTest.class);

	@Autowired
	private FileStorageClient fileStorageClient;

	@Before
	public void setup(){
		setDeleteException(false);
		setMoveToScanFolderException(false);
	}

	@Test
	public void shouldLoadContext() {
		logger.info("TEST: Checking if application loads");
		assertThat(app).isNotNull();
	}

	//POST Add File Tests
	@Test
	public void postAddFileDetailsSuccess() throws Exception {

		JSONObject obj = new JSONObject();
		obj.put("file_name", "test123.txt");
		obj.put("file_size", "217");
		obj.put("file_link", "https://egar-file-upload-test.s3.eu-west-2.amazonaws.com/53e32000-fb87-11e7-8934-73e8044b20e3/test.txt");

		final String jsonRequest = obj.toString();
		logger.debug(jsonRequest);

		MvcResult result = this.mockMvc
				.perform(post(requestURL).contentType(MediaType.APPLICATION_JSON_VALUE).header(AUTH_HEADER, AUTH)
						.header(USERID_HEADER, USER_UUID).content(jsonRequest))
				.andDo(print()).andExpect(status().isSeeOther()).andReturn();

		logger.debug(result);

		verify(jmsTemplate, times(1)).convertAndSend(eq("vscan_request"), anyString());

		FilePersistedRecord findFile = repo.findOne(getFileUUID(result));
		assertNotNull(findFile);
		assertEquals(findFile.getFileName(), "test123.txt");
		assertEquals(findFile.getFileLink(), "https://egar-file-upload-test.s3.eu-west-2.amazonaws.com/53e32000-fb87-11e7-8934-73e8044b20e3/test.txt");
	}

	@Test
	public void postAddDetailsBadRequest() throws Exception {

		final String jsonRequest = "{}";

		MvcResult result = this.mockMvc
				.perform(post(requestURL).header(USERID_HEADER, USER_UUID).header(AUTH_HEADER, AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE).content(jsonRequest))
				.andExpect(status().isBadRequest())
				.andReturn();

		String resp = result.getResponse().getContentAsString();

		with(resp).assertThat("$.message[*]", hasItems(
				"fileLink: may not be null","fileName: may not be null", "fileSize: may not be null"));
	}

	@Test
	public void postAddDetailsError() throws Exception {

		setMoveToScanFolderException(true);

		String request = FileReaderUtils.readFileAsString("files/SuccessfulAddRequest.json");

		this.mockMvc
				.perform(post(requestURL).header(USERID_HEADER, USER_UUID).header(AUTH_HEADER, AUTH)
						.contentType(APPLICATION_JSON_UTF8_VALUE).content(request))
				.andExpect(status().isBadGateway());
	}

	//GET File Tests
	@Test
	public void getFileBadRequestIfNoMatch() throws Exception {

		this.mockMvc
				.perform(get(requestURL + UUID.randomUUID() + "/").contentType(MediaType.APPLICATION_JSON_VALUE)
						.header(USERID_HEADER, USER_UUID).header(AUTH_HEADER, AUTH))
				.andDo(print()).andExpect(status().isBadRequest());

	}

	@Test
	public void getFileSuccess() throws Exception {
		repo.deleteAll();

        UUID userUuid = UUID.randomUUID();
        UUID fileUuid = UUID.randomUUID();
        
        FilePersistedRecord entry = FilePersistedRecord.builder()
        		.fileUuid(fileUuid)
        		.userUuid(userUuid)
        		.fileName("test.txt")
        		.fileSize(217L)
				.deleted(false)
        		.build();
        repo.saveAndFlush(entry);
        

        this.mockMvc
                .perform(get(RestConstants.FILES_ROOT_PATH + ServicePathConstants.ROOT_PATH_SEPERATOR + fileUuid + ServicePathConstants.ROOT_PATH_SEPERATOR)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(USERID_HEADER, userUuid))
                .andDo(print())
                .andExpect(status().isOk())
				.andExpect(jsonPath("$.file_uuid").exists())
				.andExpect(jsonPath("$.file_uuid", is(fileUuid.toString())))
				.andExpect(jsonPath("$.file_name").exists())
				.andExpect(jsonPath("$.file_name", is("test.txt")))
				.andExpect(jsonPath("$.file_size").exists())
				.andExpect(jsonPath("$.file_size", is(217)));

	}
	
	@Test
	public void getFileForOtherUserBadRequest() throws Exception {
		repo.deleteAll();

        UUID userUuid = UUID.fromString("28baf44c-7a32-4ef3-ae33-b279c9d003cf");
        UUID fileUuid = UUID.randomUUID();
        
        FilePersistedRecord entry = FilePersistedRecord.builder()
        		.fileUuid(fileUuid)
        		.userUuid(userUuid)
        		.fileName("test.txt")
        		.fileSize(217L)
				.deleted(false)
        		.build();
        repo.saveAndFlush(entry);
        

        this.mockMvc
                .perform(get(RestConstants.FILES_ROOT_PATH + ServicePathConstants.ROOT_PATH_SEPERATOR + fileUuid + ServicePathConstants.ROOT_PATH_SEPERATOR)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(USERID_HEADER, "c45dc5cb-b624-49df-a797-75e25e55a569"))
                .andDo(print())
                .andExpect(status().isBadRequest());
	}
	
	//Delete File Tests
	@Test
	public void deleteFileSuccess() throws Exception{
		repo.deleteAll();
		UUID userUuid = UUID.randomUUID();
		UUID fileUuid = UUID.randomUUID();
				
		
		FilePersistedRecord entry = FilePersistedRecord.builder()
        		.fileUuid(fileUuid)
        		.userUuid(userUuid)
        		.fileName("abcd.txt")
        		.fileSize(1000L)
				.deleted(false)
        		.fileLink("/url/abc/abcd.txt")
        		.build();
        repo.saveAndFlush(entry);
        
        this.mockMvc
        .perform(delete(RestConstants.FILES_ROOT_PATH + ServicePathConstants.ROOT_PATH_SEPERATOR + fileUuid + ServicePathConstants.ROOT_PATH_SEPERATOR)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(USERID_HEADER, userUuid))
        .andDo(print())
        .andExpect(status().isAccepted());
        
        List<FilePersistedRecord> details = repo.findAll();
        assertTrue(details.get(0).getDeleted() == true);
		
	}

	@Test
	public void deleteFileBadRequestDoesNotExist() throws Exception{
		
		UUID fileUuid = UUID.randomUUID();
		UUID userUuid = UUID.randomUUID();
		
        this.mockMvc
        .perform(delete(RestConstants.FILES_ROOT_PATH + ServicePathConstants.ROOT_PATH_SEPERATOR + fileUuid + ServicePathConstants.ROOT_PATH_SEPERATOR)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(USERID_HEADER, userUuid))
        .andDo(print())
        .andExpect(status().isBadRequest());
        

	}

	@Test
	public void deleteFileBadRequestIfMarkedAsDeleted() throws Exception{
		repo.deleteAll();
		UUID userUuid = UUID.randomUUID();
		UUID fileUuid = UUID.randomUUID();


		FilePersistedRecord entry = FilePersistedRecord.builder()
				.fileUuid(fileUuid)
				.userUuid(userUuid)
				.fileName("abcd.txt")
				.fileSize(1000L)
				.deleted(true)
				.fileLink("/url/abc/abcd.txt")
				.build();
		repo.saveAndFlush(entry);

		this.mockMvc
				.perform(delete(RestConstants.FILES_ROOT_PATH + ServicePathConstants.ROOT_PATH_SEPERATOR + fileUuid + ServicePathConstants.ROOT_PATH_SEPERATOR)
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.header(USERID_HEADER, userUuid))
				.andDo(print())
				.andExpect(status().isBadRequest());

	}

	private void setDeleteException(boolean exception){
		if (fileStorageClient instanceof DummyFileStorageClient){
			((DummyFileStorageClient)fileStorageClient).setDeleteException(exception);
		}
	}

	private void setMoveToScanFolderException(boolean exception){
		if (fileStorageClient instanceof DummyFileStorageClient){
			((DummyFileStorageClient)fileStorageClient).setMoveToScanFolderException(exception);
		}
	}

	private UUID getFileUUID(MvcResult result) {
		String location = result.getResponse().getHeader("location");
		String[] parts = location.split("/");
		return UUID.fromString(parts[parts.length - 1]);
	}

}
