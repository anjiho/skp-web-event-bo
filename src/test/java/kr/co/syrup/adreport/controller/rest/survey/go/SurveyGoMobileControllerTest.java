package kr.co.syrup.adreport.controller.rest.survey.go;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.survey.go.dto.request.*;
import kr.co.syrup.adreport.survey.go.dto.response.SurveyInfoSelectMobileResDto;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningReqDto;
import kr.co.syrup.adreport.web.event.dto.request.GiveAwayDeliverySaveReqDto;
import kr.co.syrup.adreport.web.event.dto.request.WebArGateReqDto;
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
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

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
class SurveyGoMobileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper mapper;

    @Test
    void surveyGoLogicTest() throws Exception {
        String eventId = "000675";
        //================================= 서베이고 참여코드 발행 시작 ========================================//
        for (int j=0; j<1; j++) {

            WebArGateReqDto webArGateReqDto = new WebArGateReqDto().builder()
                    .eventId(eventId)
                    .phoneNumber("01062585226")
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();

            String requestJson = ow.writeValueAsString(webArGateReqDto);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/survey-go-mobile/survey-attend/possible")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andReturn();

            JsonObject resultJsonObj = new Gson().fromJson(result.getResponse().getContentAsString(), JsonObject.class);
            log.info(">>>>>>>>" + resultJsonObj);
            JsonObject parseJsonObj = resultJsonObj.getAsJsonObject("result");

            Assert.notNull(parseJsonObj, "survey-attend/possible 결과값 없음");

            String surveyLogAttendId = parseJsonObj.get("surveyLogAttendId").getAsString();

            log.info("surveyLogAttendId >>>>>>>>" + surveyLogAttendId);
            Assert.notNull(surveyLogAttendId, "surveyLogAttendId 가 없음");
            //================================= 서베이고 참여코드 발행 끝 ========================================//

            //================================= 문항정보 조회 시작 ========================================//
            SurveyInfoSelectMobileReqDto selectMobileReqDto = new SurveyInfoSelectMobileReqDto();
            selectMobileReqDto.setEventId(eventId);
            selectMobileReqDto.setSurveyLogAttendId(surveyLogAttendId);

            String requestDetailJson = ow.writeValueAsString(selectMobileReqDto);

            MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/survey-go-mobile/detail")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .content(requestDetailJson))
                    .andReturn();

            JsonObject resultJsonObj2 = new Gson().fromJson(result2.getResponse().getContentAsString(), JsonObject.class);
            log.info(">>>>>>>>" + resultJsonObj2);
            JsonObject parseJsonObj2 = resultJsonObj2.getAsJsonObject("result");


            SurveyInfoSelectMobileResDto selectMobileResDto = mapper.readValue(parseJsonObj2.toString(), SurveyInfoSelectMobileResDto.class);
            Assert.notNull(selectMobileResDto, "문항정보가 없음");
            //================================= 문항정보 조회 시작 ========================================//

            //================================= 답변 만들기 시작 ========================================//
            List<SurveySubjectInfoMobileDto> subjectList = selectMobileResDto.getSurveySubjectInfo();

            SurveyResultSaveMobileReqDto answerInfo = new SurveyResultSaveMobileReqDto();
            answerInfo.setEventId(eventId);
            answerInfo.setSurveyLogAttendId(surveyLogAttendId);
            List<SurveyAnswerInfoDto> answerList = new ArrayList<>();

            for (int i = 0; i < subjectList.size(); i++) {
                SurveySubjectInfoMobileDto subject = subjectList.get(i);
                SurveyAnswerInfoDto answerInfoDto = new SurveyAnswerInfoDto();

                answerInfoDto.setSurveySubjectId(subject.getSurveySubjectId());
                answerInfoDto.setSubjectSort(subject.getSort());

                List<SurveyAnswerExampleDto> exampleList = new ArrayList<>();
                if (PredicateUtils.isEqualsStr(subject.getMultipleAnswerYn(), "N")) {
                    List<SubjectExampleSodarReqDto> exampleSubjectList = selectMobileResDto.getSurveySubjectInfo().get(i).getExampleInfo();

                    for (SubjectExampleSodarReqDto example : exampleSubjectList) {
                        if (example.getSort() == 1) {
                            SurveyAnswerExampleDto exampleDto = new SurveyAnswerExampleDto();
                            exampleDto.setExampleSort(example.getSort());
                            exampleDto.setSurveyExampleId(example.getSurveyExampleId());

                            exampleList.add(exampleDto);
                            answerInfoDto.setSurveyExampleList(exampleList);
                            break;
                        }
                    }
                }
                answerList.add(answerInfoDto);
            }
            answerInfo.setAnswerList(answerList);

            String requestSaveResultJson = ow.writeValueAsString(answerInfo);
            MvcResult result3 = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/survey-go-mobile/answerSave")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .content(requestSaveResultJson))
                    .andReturn();

            JsonObject resultJsonObj3 = new Gson().fromJson(result3.getResponse().getContentAsString(), JsonObject.class);
            log.info(">>>>>>>>" + resultJsonObj3);
            int resultCode = resultJsonObj3.getAsJsonPrimitive("resultCode").getAsInt();
            boolean isSuccess = true;
            if (resultCode != 200) {
                isSuccess = false;
            }
            Assert.isTrue(isSuccess, "서베이고 결과 입력 실패");
            //================================= 답변 만들기 끝 ========================================//

            //================================= 당첨 만들기 시작 ========================================//
            EventWinningReqDto winningReqDto = new EventWinningReqDto();
            winningReqDto.setEventId(eventId);
            winningReqDto.setSurveyLogAttendId(surveyLogAttendId);
            winningReqDto.setPhoneNumber("01062585226");

            String requestWinningJson = ow.writeValueAsString(winningReqDto);
            MvcResult result4 = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/web-event-front/winning-process")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .content(requestWinningJson))
                    .andReturn();

            JsonObject resultJsonObj4 = new Gson().fromJson(result4.getResponse().getContentAsString(), JsonObject.class);
            log.info(">>>>>>>>" + resultJsonObj4);
            JsonObject parseJsonObj4 = resultJsonObj4.getAsJsonObject("result");
            Long eventLogWinningId = parseJsonObj4.get("eventLogWinningId").getAsLong();

            Assert.notNull(eventLogWinningId, "eventLogWinningId 없음");
            //================================= 당첨 만들기 끝 ========================================//

            //================================= 당첨정보 입력 만들기 시작 ========================================//
//            GiveAwayDeliverySaveReqDto giveAwayDeliverySaveReqDto = new GiveAwayDeliverySaveReqDto();
//            giveAwayDeliverySaveReqDto.setEventId(eventId);
//            giveAwayDeliverySaveReqDto.setArEventWinningId(1568);
//            giveAwayDeliverySaveReqDto.setName("테스터");
//            giveAwayDeliverySaveReqDto.setPhoneNumber("01062585228");
//            giveAwayDeliverySaveReqDto.setMemberBirth("19820128");
//            giveAwayDeliverySaveReqDto.setEventLogWinningId(eventLogWinningId);
//            giveAwayDeliverySaveReqDto.setArEventWinningButtonId(2323);
//            giveAwayDeliverySaveReqDto.setSurveyLogAttendId(surveyLogAttendId);
//
//            String requestGiveAwayJson = ow.writeValueAsString(giveAwayDeliverySaveReqDto);
//            MvcResult result5 = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/web-event-front/give-away-delivery/save")
//                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
//                            .content(requestGiveAwayJson))
//                    .andReturn();
//
//            JsonObject resultJsonObj5 = new Gson().fromJson(result5.getResponse().getContentAsString(), JsonObject.class);
//            log.info(">>>>>>>>" + resultJsonObj5);
//            JsonObject parseJsonObj5 = resultJsonObj5.getAsJsonObject("result");
//            log.info(">>>>>>>>" + parseJsonObj5);
//            //================================= 당첨정보 입력 만들기 시작 ========================================//
//            if (j == 100) {
//                Thread.sleep(1000);
//            }
        }
    }
}