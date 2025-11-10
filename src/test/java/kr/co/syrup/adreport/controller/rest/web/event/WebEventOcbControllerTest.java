package kr.co.syrup.adreport.controller.rest.web.event;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@WebAppConfiguration
@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureCache
@AutoConfigureDataJpa
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestEntityManager
@ImportAutoConfiguration
@AutoConfigureMockMvc
class WebEventOcbControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getOcbSessionInfo() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/web-event-ocb/session/find/Tc0aef736077b43a0")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).andReturn();

        log.info("result >>" + result);
    }


    @Test
    void requestOcbPointSave() {
        Map<String, Object>paramMap = new HashMap<>();
        paramMap.put("eventId", "000460");
        paramMap.put("partnerToken", "Td00ab4356e1a43d7");

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = "";
        try {
            requestJson = ow.writeValueAsString(paramMap);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }

        try {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/web-event-ocb/point/save")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andReturn();

            log.info("result >>> " + result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}