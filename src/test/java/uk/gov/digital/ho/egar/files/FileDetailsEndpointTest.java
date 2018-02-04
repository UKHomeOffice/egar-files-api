package uk.gov.digital.ho.egar.files;

import static com.jayway.jsonassert.JsonAssert.with;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import uk.gov.digital.ho.egar.files.client.FileStorageClient;
import uk.gov.digital.ho.egar.files.client.impl.DummyFileStorageClient;
import uk.gov.digital.ho.egar.files.service.FileService;
import uk.gov.digital.ho.egar.files.service.repository.FilePersistedRecordRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"eureka.client.enabled=false", "spring.cloud.config.discovery.enabled=false",
        "spring.profiles.active=s3-mocks,jms-disabled"})
@AutoConfigureMockMvc
public class FileDetailsEndpointTest {

    private static final String USERID_HEADER = "x-auth-subject";
    private static final String AUTH_HEADER = "Authorization";
    private static final UUID USER_UUID = UUID.randomUUID();
    private static final String AUTH = "values";
    private static final String DETAILS_URL = "/api/v1/FileDetails/";
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

    private final Log logger = LogFactory.getLog(FileDetailsEndpointTest.class);

    @Autowired
    private FileStorageClient fileStorageClient;

    @Before
    public void setup() {
        setFileLinkException(false);
    }


    //Post File Details Tests
    @Test
    public void postFileDetailsSuccess() throws Exception {
        String request = "{\"file_link\":\"https://egar-file-upload-test.s3.eu-west-2.amazonaws.com/53e32000-fb87-11e7-8934-73e8044b20e3/test.txt\"}";
        this.mockMvc
                .perform(post(DETAILS_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(USERID_HEADER, USER_UUID)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.file_link").exists())
                .andExpect(jsonPath("$.file_link", is("https://egar-file-upload-test.s3.eu-west-2.amazonaws.com/53e32000-fb87-11e7-8934-73e8044b20e3/test.txt")))
                .andExpect(jsonPath("$.file_name").exists())
                .andExpect(jsonPath("$.file_size").exists());
    }


    @Test
    public void postFileDetailsBadRequest() throws Exception {
        String request = "{}";


        MvcResult result = this.mockMvc
                .perform(post(DETAILS_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                        .header(USERID_HEADER, USER_UUID))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String resp = result.getResponse().getContentAsString();

        with(resp).assertThat("$.message[0]", is("fileLink: may not be null"));

    }

    @Test
    public void postFileDetailsBadGateway() throws Exception {
        setFileLinkException(true);
        String request = "{\"file_link\":\"https://egar-file-upload-test.s3.eu-west-2.amazonaws.com/53e32000-fb87-11e7-8934-73e8044b20e3/test.txt\"}";

        this.mockMvc
                .perform(post(DETAILS_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request)
                        .header(USERID_HEADER, USER_UUID))
                .andDo(print())
                .andExpect(status().isBadGateway());
    }


    private void setFileLinkException(boolean exception) {
        if (fileStorageClient instanceof DummyFileStorageClient) {
            ((DummyFileStorageClient) fileStorageClient).setFileLinkException(exception);
        }
    }
}
