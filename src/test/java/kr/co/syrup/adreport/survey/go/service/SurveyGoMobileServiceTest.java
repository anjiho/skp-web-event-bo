package kr.co.syrup.adreport.survey.go.service;

import kr.co.syrup.adreport.survey.go.mybatis.mapper.SurveyGoMobileMapper;
import kr.co.syrup.adreport.survey.go.mybatis.mapper.SurveyGoSodarMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
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
import org.springframework.util.Assert;

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
class SurveyGoMobileServiceTest {

    @Autowired
    private SurveyGoMobileMapper mapper;

    @Test
    void findSurveyTargetLimitCountByArEventIdAndGenderAndAge() {
       Integer cnt = mapper.selectSurveyTargetLimitCountByArEventIdAndGenderAndAge(470, "F", 2);
       Assert.notNull(cnt, "cnt is null");
    }

    @Test
    void findSurveySubjectCategorySortByIndex() {
    }

    @Test
    void findSurveyTargetAgeGenderLimitListByArEventId() {
    }
}