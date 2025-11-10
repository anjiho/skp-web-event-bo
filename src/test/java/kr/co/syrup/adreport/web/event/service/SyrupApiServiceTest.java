package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.web.event.dto.response.api.SodarMemberResDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;

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
class SyrupApiServiceTest {

    @Autowired
    private SodarApiService sodarApiService;

    @Autowired
    private OkHttpService okHttpService;


    @Test
    void sodarIAMemberCheckTest() {

        try {
            HashMap<String, String> condition = new HashMap<>();
            condition.put("Cookie", "ISSESSIONID=K4Ibo7tRu093xsDZAfnx60GvqdhRkP9oW5uvjRQo6UfN7gELNFz5TRdsITai; SITETYPE=IA");
            SodarMemberResDto resDto = okHttpService.callGetApi("https://sodaradmindev.syrup.co.kr/member", condition, SodarMemberResDto.class);
//            HttpResponse<String> response = Unirest.get("https://sodaradmindev.syrup.co.kr/member")
//                    .header("Cookie", "ISSESSIONID=ej6KEc35aLoVQEgwd9nyjiYiEWWFEOO1qgLQFOBfmvpbPqX8s6w0SQD4rxti; cookie=1700888464000")
//                    .asString();
//            log.info(">>" + response.getBody());
            log.info(">>" + resDto.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}