package kr.co.syrup.adreport.stamp.event.service;

import kr.co.syrup.adreport.stamp.event.define.StampWinningTypeDefine;
import kr.co.syrup.adreport.stamp.event.dto.response.StampAlimtokInfoResDto;
import kr.co.syrup.adreport.stamp.event.model.*;
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
class StampSodarServiceTest {

    @Autowired
    private StampSodarService stampSodarService;

    @Test
    void upsertStampEventMainTest() {
        StampEventMainModel model = new StampEventMainModel();
        model.setStpId(9);
        model.setEventId("S000004");
        model.setStpEventTitle("스탬프 이벤트 명11");
        model.setStpMainSettingYn("N");
        model.setStpAttendAuthCondition("ATTEND");
        model.setStpAttendCodeDigit(8);
        model.setStpAttendCodeCount(1000);
        model.setStpAttendCodeFileName("ldajflsdfja;ldfja;lsdkjal;skdfal;skfd.xlxs");
        model.setStpAttendCodeMisTxt("참여번호 비매칭시 문구");
        model.setAlimtokSendYn("Y");
        model.setDuplicateWinningType("Y");
        model.setDuplicateWinningLimitType(1);
        model.setDuplicateWinningCount(100);
        model.setWinningRaffleStartPoint("2,4,6");

        stampSodarService.upsertStampEventMain(model, "SSS");
        log.info("stpId >> " + model.getStpId());

    }

    @Test
    void upsertStampEventPanTest() {
        StampEventPanModel model = new StampEventPanModel();
        //model.setStpPanId(3);
        //model.setStpId(7);
        model.setStpPanTitle("스탬프 판 제목20");
        model.setStpPanTheme("PRIVATE");
        model.setStpNumber(4);
        model.setStpPanBgImgUrl("ldsfjasdfaklsjdhflkasjdhfklsajdfasldfjalskjdhfklasj.jpg");
        model.setStpPanImgUrl("ldsfjasdfaklsjdhflkasjdhfklsajdfasldfjalskjdhfklasj.jpg");
        model.setAttendSortSettingYn("N");
        model.setStpImgSettingType("COMMON");

        stampSodarService.upsertStampEventPan(StampEventPanModel.ofSave(model, 6));
        log.info("stpId >> " + model.getStpPanId());
    }

    @Test
    void saveListStampEventPanTrTest() {
        List<StampEventPanTrModel> list = new ArrayList<>();

        StampEventPanTrModel model = new StampEventPanTrModel();
        model.setStpPanTrId(1l);
        model.setStpTrSort(1);
        model.setStpTrTxt("스탬프판 TR11");
        model.setStpTrType("LOCATION");
        model.setStpTrEventId("000123");
//        model.setStpPanNotAccImgUrl("https://asljaldsjalsjkdlaksjdlakjds.jpg");
//        model.setStpPanAccImgUrl("https://asljaldsjalsjkdlaksjdlakjds.jpg");
//        model.setStpWinningType(StampWinningTypeDefine.RAFFLE.name());
//        model.setInformationProvisionAgreementTextSetting("N");

        StampEventPanTrModel model1 = new StampEventPanTrModel();
        model1.setStpPanTrId(2l);
        model1.setStpTrSort(2);
        model1.setStpTrTxt("스탬프판 TR2");
        model1.setStpTrType("LOCATION");
        model1.setStpTrPid("D23094isjkd");
//        model1.setStpPanLocationMsgAttend("참여가능11");
//        model1.setStpPanLocationMisMsgAttend("참여불가능11");
//        model1.setStpPanNotAccImgUrl("https://asljaldsjalsjkdlaksjdlakjds.jpg");
//        model1.setStpPanAccImgUrl("https://asljaldsjalsjkdlaksjdlakjds.jpg");
//        model1.setStpWinningType(StampWinningTypeDefine.EXCHANGE.name());
//        model1.setInformationProvisionAgreementTextSetting("Y");
//        model1.setInformationProvisionRecipient("제공받는 자1");
//        model1.setInformationProvisionConsignor("위탁업체1");
//        model1.setInformationProvisionPurposeUse("이용목적1");

//        StampEventPanTrModel model2 = new StampEventPanTrModel();
//        model2.setStpTrSort(3);
//        model2.setStpTrTxt("스탬프판 TR3");
//        model2.setStpTrType("LOCATION");
//        model2.setStpTrPid("D23094isjkd");
//        model2.setStpPanLocationMsgAttend("참여가능");
//        model2.setStpPanLocationMisMsgAttend("참여불가능");
//        model2.setStpPanNotAccImgUrl("https://asljaldsjalsjkdlaksjdlakjds.jpg");
//        model2.setStpPanAccImgUrl("https://asljaldsjalsjkdlaksjdlakjds.jpg");
//        model2.setStpWinningType(StampWinningTypeDefine.EXCHANGE.name());
//        model2.setInformationProvisionAgreementTextSetting("Y");
//        model2.setInformationProvisionRecipient("제공받는 자");
//        model2.setInformationProvisionConsignor("위탁업체");
//        model2.setInformationProvisionPurposeUse("이용목적");

        list.add(model);
        //list.add(model1);
        //list.add(model2);

        stampSodarService.upsertDeleteListStampEventPanTr(list, 4);
    }

    @Test
    void upsertDeleteListStampAlimtokTest() {
//        StampAlimtokSaveReqDto saveReqDto = new StampAlimtokSaveReqDto();
//        StampAlimtokModel alimtokModel = new StampAlimtokModel();
//        List<StampAlimtokButtonModel> btnList = new ArrayList<>();
//
//        alimtokModel.setStpAlimtokId(3);
//        //alimtokModel.setStpId(1);
//        alimtokModel.setStpAlimtokTxt("ldsjfla;sdf;lasjfwieurojvslajdfa;lsjk;laskjdf;laskjls;akjdf;laskjdf;lsajk11dfsfsfsdfsfd");
//        alimtokModel.setStpAlimtokSendType("STAMP");
//
//        StampAlimtokButtonModel alimtokButtonModel = new StampAlimtokButtonModel();
//        alimtokButtonModel.setStpAlimtokBtnId(1l);
//        alimtokButtonModel.setStpAlimtokBtnTxt("버튼문구3");
//        alimtokButtonModel.setStpAlimtokBtnUrl("http://alskjasl;dfjasl;kfjals;f11234551");
//        alimtokButtonModel.setStpAlimtokBtnSort(2);
//
//        StampAlimtokButtonModel alimtokButtonModel2 = new StampAlimtokButtonModel();
//        //alimtokButtonModel2.setStpAlimtokBtnId(2l);
//        alimtokButtonModel2.setStpAlimtokBtnTxt("버튼문구4");
//        alimtokButtonModel2.setStpAlimtokBtnUrl("http://alskjasl;dfjasl;kfjals;fasdads1111");
//        alimtokButtonModel2.setStpAlimtokBtnSort(1);
//
//        btnList.add(alimtokButtonModel);
//        btnList.add(alimtokButtonModel2);
//
//        saveReqDto.setStampAlimtok(alimtokModel);
//        saveReqDto.setStampAlimtokButton(btnList);
//
//        stampSodarService.upsertDeleteListStampAlimtok(saveReqDto, 1);
    }

    @Test
    void findStampAlimtokInfoByStpIdTest() {
        StampAlimtokInfoResDto resDto = stampSodarService.findStampAlimtokInfoByStpId(20);
        log.info(">>>>>> " + resDto.toString());
    }
}