package kr.co.syrup.adreport.survey.go.service;

import kr.co.syrup.adreport.framework.utils.EventUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.survey.go.define.AgeTypeDefine;
import kr.co.syrup.adreport.survey.go.define.ExampleTypeDefine;
import kr.co.syrup.adreport.survey.go.define.GenderTypeDefine;
import kr.co.syrup.adreport.survey.go.mybatis.mapper.SurveyGoLogMapper;
import kr.co.syrup.adreport.survey.go.mybatis.mapper.SurveyGoStaticsMapper;

import kr.co.syrup.adreport.survey.go.mybatis.vo.*;

import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.entity.ArEventHtmlEntity;
import kr.co.syrup.adreport.web.event.mybatis.mapper.LogMapper;
import kr.co.syrup.adreport.web.event.mybatis.mapper.StaticsMapper;
import kr.co.syrup.adreport.web.event.mybatis.vo.HourlyMapperVO;
import kr.co.syrup.adreport.web.event.service.BatchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SurveyGoStaticsService {

    @Autowired
    private SurveyGoStaticsMapper surveyGoStaticsMapper;

    @Autowired
    private SurveyGoLogMapper surveyGoLogMapper;

    @Autowired
    private LogMapper logMapper;
    @Autowired
    private StaticsMapper staticsMapper;

    @Autowired
    private BatchService batchService;

    public List<SurveyTableSubjectStaticsResVO> findSurveyTableSubjectStaticsList(String eventId) {
        return surveyGoStaticsMapper.selectSurveyTableSubjectStatics(eventId);
    }

    public List<SurveyTableExampleStaticsResVO> findSurveyTableExampleStaticsList(Long surveySubjectId, String etcOpinionReceiveYn) {
        List<SurveyTableExampleStaticsResVO> list = surveyGoStaticsMapper.selectSurveyTableExampleStatics(surveySubjectId);
        if (PredicateUtils.isEqualsStr(etcOpinionReceiveYn, StringDefine.Y.name())) {
            int etcCount = surveyGoStaticsMapper.countSurveyTableExampleEtc(surveySubjectId);
            SurveyTableExampleStaticsResVO vo = new SurveyTableExampleStaticsResVO();
            vo.setExampleTitle("기타");
            vo.setSort(-1);
            vo.setExampleTotalCount(etcCount);

            list.add(list.size(), vo);
        }
        return list;
    }

    public SurveyTableExampleStaticsResVO makeSurveyQuizQuestionWrong(int wrongCnt) {
        SurveyTableExampleStaticsResVO resVO = new SurveyTableExampleStaticsResVO();
        resVO.setExampleTitle("오답");
        resVO.setExampleTotalCount(wrongCnt);
        return resVO;
    }

    public List<SurveyTableCategoryStaticsResVO> findSurveySubjectCategoryStatics(Integer arEventId, String eventLogicalType) {
        return surveyGoStaticsMapper.selectSurveySubjectCategoryStaticsByQuiz(arEventId, eventLogicalType);
    }

    /**
     * 서베이고 통계 엑셀 다운로드 > raw_table 의 제목 만들기
     * @param arEventId
     * @return
     */
    public List<String> makeSurveyRawTableTitle(final int arEventId) {
        List<SurveyTableRawTitleResVO> answerTitleList = surveyGoStaticsMapper.selectSurveyTableRawTitleList(arEventId);
        List<String> titleList = new ArrayList<>();

        titleList.add("참여인덱스 코드");
        //titleList.add("당첨인덱스 코드");
        titleList.add("응답시작시간");
        titleList.add("성별");
        titleList.add("연령대");

        for (SurveyTableRawTitleResVO vo : answerTitleList) {
            titleList.add(vo.getTitle());
            if (PredicateUtils.isNotNull(vo.getEtcTitle()) || StringUtils.isNotEmpty(vo.getEtcTitle())) {
                titleList.add(vo.getEtcTitle());
            }
        }

        titleList.add("응답종료시간");
        titleList.add("당첨여부");
        titleList.add("당첨상품명");

        return titleList;
    }

    public List<List<Object>> makeSurveyRawTableValue(final String eventId, int listCount, int arEventId) {
        List<List<Object>> rowList = new ArrayList<>();
        //총개수
        int totalSurveyLogCount = surveyGoStaticsMapper.countSurveyLogAttendByEventId(eventId);
        //루프 개수
        int loopCount = totalSurveyLogCount / listCount;
        if (loopCount == 0) {
            if (totalSurveyLogCount > 0) {
                loopCount = 1;
            }
        }

        List<SurveyTableRawAnswerResVO> answerRowList = this.selectSurveyTableRawAnswerList(arEventId);

        for (int i=0; i<=(loopCount+1); i++) {
            if (i == 0) {
                i = 1;
            }
            //시작번호
            int start = EventUtils.getPagingStartNumber(i, listCount);
            //서베이고를 완료한 목록 가져오기
            List<SurveyTableRawResVO> valueList = surveyGoStaticsMapper.selectSurveyTableRawStatics2(eventId, start, listCount);


            for (SurveyTableRawResVO row : valueList) {
                List<Object>rowDataList = new ArrayList<>();

                //서베이고를 완료한 survey_log_attend_id 로 참여한 서베이고 로그 결과 리스트 가져오기
                List<SurveyLogAttendResultResVO> attendResultList = surveyGoLogMapper.selectSurveyLogAttendResultByAttendIdAndArEventId(row.getSurveyLogAttendId(), row.getArEventId());

                //참여인덱스
                rowDataList.add(row.getSurveyLogAttendId());
                //당첨인덱스
                //rowDataList.add(row.getGiveAwayId());

                //응답시작시간
                rowDataList.add(row.getAttendStartDate());
                //성별
                rowDataList.add(GenderTypeDefine.getGenderTypeStr(StringTools.convertNullToEmptyString(row.getGender())));
                //연령대
                rowDataList.add(AgeTypeDefine.getAgeTypeStr(row.getAge()));

                //해당 서베이고 이벤트의 문항 목록 가져오기
                //List<SurveyTableRawAnswerResVO> answerRowList = surveyGoStaticsMapper.selectSurveyTableRawAnswerList(row.getArEventId(), row.getSurveyLogAttendId());
                //List<SurveyTableRawAnswerResVO> answerRowList = this.selectSurveyTableRawAnswerList(row.getArEventId());

                if (!PredicateUtils.isNullList(attendResultList)) {
                    // =========================== 문항 리스트 시작 ===============================
                    for (SurveyTableRawAnswerResVO answerRow : answerRowList) {
                        //참여한 로그의 보기값과 문항의 보기 값이 같은 목록 정렬
                        List<SurveyLogAttendResultResVO> machtedAnswerCollectList = attendResultList.stream()
                                .filter(data -> Objects.equals(data.getSubjectSort(), answerRow.getSubjectSort()))
                                .collect(Collectors.toList());

                        if (PredicateUtils.isNotNullList(machtedAnswerCollectList)) {
                            //참여한 로그의 보기값과 문항의 보기 값이 같은 목록이 하나면 사용자가 기록한 보기의 값을 주입
                            if (machtedAnswerCollectList.size() == 1) {

                                if (PredicateUtils.isNotNull(machtedAnswerCollectList.get(0).getExampleTitle())) {
                                    //주관식이 아니면 주입
                                    rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getExampleTitle()));
                                }
                                //기타항목이 있는 문항이면 기타 값 주입
                                if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                                    if (PredicateUtils.isNull(machtedAnswerCollectList.get(0).getSurveyExampleId()) && PredicateUtils.isNull(machtedAnswerCollectList.get(0).getExampleSort())) {
                                        rowDataList.add("");
                                        rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                                    } else {
                                        rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                                    }
                                } else {
                                    //주관식
                                    if (PredicateUtils.isEqualsStr(answerRow.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                                        rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                                    }
                                }
                            }

                            //참여한 로그의 보기값과 문항의 보기 값이 같은 목록이 하나 이상이면 사용자가 기록한 보기의 값을 문자열(,) 기준으로 만들고 주입
                            if (machtedAnswerCollectList.size() > 1) {
                                List<String> sortList = new ArrayList<>();
                                for (SurveyLogAttendResultResVO result : machtedAnswerCollectList) {
                                    if (PredicateUtils.isNotNull(result.getExampleTitle())) {
                                        sortList.add(result.getExampleTitle());
                                    }
                                }
                                rowDataList.add(String.join(",", sortList));

                                //기타항목이 있는 문항이면 기타 값 주입
                                if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                                    //기타항목 데이터가 존재하는 오브젝트 확인
                                    Optional<SurveyLogAttendResultResVO> findQuestionAnswerOptional = attendResultList.stream()
                                            .filter( data -> PredicateUtils.isEqualNumber(data.getSubjectSort(), answerRow.getSubjectSort()))
                                            .filter(data -> PredicateUtils.isNotNull(data.getQuestionAnswer()))
                                            .findFirst();
                                    //기타항목 데이터가 있으면 기타항목 값 주입
                                    if (findQuestionAnswerOptional.isPresent()) {
                                        rowDataList.add(StringTools.convertNullToEmptyString(findQuestionAnswerOptional.get().getQuestionAnswer()));
                                    } else {
                                        //기타항목 데이터가 없으면 공백 값
                                        rowDataList.add("");
                                    }
                                }
                            }

                            if (machtedAnswerCollectList.size() == 0) {
                                rowDataList.add("");
                            }
                        } else {
                            List<SurveyLogAttendResultResVO> notMachtedAnswerCollectList = attendResultList.stream()
                                    .filter(data -> !Objects.equals(data.getSubjectSort(), answerRow.getSubjectSort()))
                                    .collect(Collectors.toList());


                            if (PredicateUtils.isNotNullList(notMachtedAnswerCollectList)) {
                                for (SurveyLogAttendResultResVO result : notMachtedAnswerCollectList) {
                                    if (Objects.equals(result.getSubjectSort(), answerRow.getSubjectSort())) {
                                        rowDataList.add(StringTools.convertNullToEmptyString(notMachtedAnswerCollectList.get(0).getExampleTitle()));
                                        if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                                            rowDataList.add(StringTools.convertNullToEmptyString(notMachtedAnswerCollectList.get(0).getQuestionAnswer()));
                                        }
                                    } else {
                                        if (PredicateUtils.isEqualN(answerRow.getEtcOpinionReceiveYn())) {
                                            rowDataList.add("");
                                            break;
                                        } else {
                                            rowDataList.add("");
                                            rowDataList.add("");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // =========================== 문항 리스트 끝 ===============================
                }

                //참여한 로그 결과 값이 없으면
                if (PredicateUtils.isNullList(attendResultList)) {
                    for (SurveyTableRawAnswerResVO answerRow : answerRowList) {
                        rowDataList.add("");
                        if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                            rowDataList.add("");
                        }
                    }
                }

                //응답종료시간
                rowDataList.add(row.getAttendEndDate());
                //당첨여부
                rowDataList.add(PredicateUtils.isNotNull(row.getGiveAwayId()) ? StringDefine.Y.name() : StringDefine.N.name());
                //당첨상품명
                rowDataList.add(StringTools.convertNullToEmptyString(row.getProductName()));

                //하나의 raw_table 값을 배열에 담는다.
                rowList.add(rowDataList);
            }
        }
        return rowList;
    }

    public List<List<String>> makeSurveyRawTableValue2(final String eventId, int listCount, int arEventId) {
        List<List<String>> rowList = new ArrayList<>();
        //총개수
        int totalSurveyLogCount = surveyGoStaticsMapper.countSurveyLogAttendByEventId(eventId);
        //루프 개수
        int loopCount = totalSurveyLogCount / listCount;
        if (loopCount == 0) {
            if (totalSurveyLogCount > 0) {
                loopCount = 1;
            }
        }

        List<SurveyTableRawAnswerResVO> answerRowList = this.selectSurveyTableRawAnswerList(arEventId);

        for (int i=0; i<=(loopCount+1); i++) {
            if (i == 0) {
                i = 1;
            }
            //시작번호
            int start = EventUtils.getPagingStartNumber(i, listCount);
            //서베이고를 완료한 목록 가져오기
            List<SurveyTableRawResVO> valueList = surveyGoStaticsMapper.selectSurveyTableRawStatics2(eventId, start, listCount);

            List<String>attendLogIdList = valueList.stream().map(SurveyTableRawResVO::getSurveyLogAttendId).collect(Collectors.toList());
            if (attendLogIdList == null || attendLogIdList.isEmpty()) {
                break;
            }
            //서베이고를 완료한 survey_log_attend_id 로 참여한 서베이고 로그 결과 리스트 가져오기
            List<SurveyLogAttendResultResVO> attendResultListIn = surveyGoLogMapper.selectSurveyLogAttendResultByAttendIdAndArEventId2(attendLogIdList, arEventId);

            for (SurveyTableRawResVO row : valueList) {
//                List<Object>rowDataList = new ArrayList<>();
                List<String>rowDataList = new ArrayList<>();
                rowDataList.add(eventId);
                //참여인덱스
                rowDataList.add(row.getSurveyLogAttendId());
                //당첨인덱스
                //rowDataList.add(row.getGiveAwayId());

                //응답시작시간
                rowDataList.add(row.getAttendStartDate());
                //성별
                rowDataList.add(GenderTypeDefine.getGenderTypeStr(StringTools.convertNullToEmptyString(row.getGender())));
                //연령대
                rowDataList.add(AgeTypeDefine.getAgeTypeStr(row.getAge()));

                //해당 서베이고 이벤트의 문항 목록 가져오기
                //List<SurveyTableRawAnswerResVO> answerRowList = surveyGoStaticsMapper.selectSurveyTableRawAnswerList(row.getArEventId(), row.getSurveyLogAttendId());
                //List<SurveyTableRawAnswerResVO> answerRowList = this.selectSurveyTableRawAnswerList(row.getArEventId());

                List<SurveyLogAttendResultResVO> attendResultList = attendResultListIn.stream().filter(data -> data.getSurveyLogAttendId().equals(row.getSurveyLogAttendId())).collect(Collectors.toList());

                if (!PredicateUtils.isNullList(attendResultList)) {
                    // =========================== 문항 리스트 시작 ===============================
                    for (SurveyTableRawAnswerResVO answerRow : answerRowList) {
                        //참여한 로그의 보기값과 문항의 보기 값이 같은 목록 정렬
                        List<SurveyLogAttendResultResVO> machtedAnswerCollectList = attendResultList.stream()
                                .filter(data -> Objects.equals(data.getSubjectSort(), answerRow.getSubjectSort()))
                                .collect(Collectors.toList());

                        if (PredicateUtils.isNotNullList(machtedAnswerCollectList)) {
                            //참여한 로그의 보기값과 문항의 보기 값이 같은 목록이 하나면 사용자가 기록한 보기의 값을 주입
                            if (machtedAnswerCollectList.size() == 1) {

                                if (PredicateUtils.isNotNull(machtedAnswerCollectList.get(0).getExampleTitle())) {
                                    //주관식이 아니면 주입
                                    rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getExampleTitle()));
                                }
                                //기타항목이 있는 문항이면 기타 값 주입
                                if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                                    if (PredicateUtils.isNull(machtedAnswerCollectList.get(0).getSurveyExampleId()) && PredicateUtils.isNull(machtedAnswerCollectList.get(0).getExampleSort())) {
                                        rowDataList.add("");
                                        rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                                    } else {
                                        rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                                    }
                                } else {
                                    //주관식
                                    if (PredicateUtils.isEqualsStr(answerRow.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                                        rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                                    }
                                }
                            }

                            //참여한 로그의 보기값과 문항의 보기 값이 같은 목록이 하나 이상이면 사용자가 기록한 보기의 값을 문자열(,) 기준으로 만들고 주입
                            if (machtedAnswerCollectList.size() > 1) {
                                List<String> sortList = new ArrayList<>();
                                for (SurveyLogAttendResultResVO result : machtedAnswerCollectList) {
                                    if (PredicateUtils.isNotNull(result.getExampleTitle())) {
                                        sortList.add(result.getExampleTitle());
                                    }
                                }
                                rowDataList.add(String.join(",", sortList));

                                //기타항목이 있는 문항이면 기타 값 주입
                                if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                                    //기타항목 데이터가 존재하는 오브젝트 확인
                                    Optional<SurveyLogAttendResultResVO> findQuestionAnswerOptional = attendResultList.stream()
                                            .filter( data -> PredicateUtils.isEqualNumber(data.getSubjectSort(), answerRow.getSubjectSort()))
                                            .filter(data -> PredicateUtils.isNotNull(data.getQuestionAnswer()))
                                            .findFirst();
                                    //기타항목 데이터가 있으면 기타항목 값 주입
                                    if (findQuestionAnswerOptional.isPresent()) {
                                        rowDataList.add(StringTools.convertNullToEmptyString(findQuestionAnswerOptional.get().getQuestionAnswer()));
                                    } else {
                                        //기타항목 데이터가 없으면 공백 값
                                        rowDataList.add("");
                                    }
                                }
                            }

                            if (machtedAnswerCollectList.size() == 0) {
                                rowDataList.add("");
                            }
                        } else {
                            List<SurveyLogAttendResultResVO> notMachtedAnswerCollectList = attendResultList.stream()
                                    .filter(data -> !Objects.equals(data.getSubjectSort(), answerRow.getSubjectSort()))
                                    .collect(Collectors.toList());


                            if (PredicateUtils.isNotNullList(notMachtedAnswerCollectList)) {
                                for (SurveyLogAttendResultResVO result : notMachtedAnswerCollectList) {
                                    if (Objects.equals(result.getSubjectSort(), answerRow.getSubjectSort())) {
                                        rowDataList.add(StringTools.convertNullToEmptyString(notMachtedAnswerCollectList.get(0).getExampleTitle()));
                                        if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                                            rowDataList.add(StringTools.convertNullToEmptyString(notMachtedAnswerCollectList.get(0).getQuestionAnswer()));
                                        }
                                    } else {
                                        if (PredicateUtils.isEqualN(answerRow.getEtcOpinionReceiveYn())) {
                                            rowDataList.add("");
                                            break;
                                        } else {
                                            rowDataList.add("");
                                            rowDataList.add("");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // =========================== 문항 리스트 끝 ===============================
                }

                //참여한 로그 결과 값이 없으면
                if (PredicateUtils.isNullList(attendResultList)) {
                    for (SurveyTableRawAnswerResVO answerRow : answerRowList) {
                        rowDataList.add("");
                        if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                            rowDataList.add("");
                        }
                    }
                }

                //응답종료시간
                rowDataList.add(row.getAttendEndDate());
                //당첨여부
                rowDataList.add(PredicateUtils.isNotNull(row.getGiveAwayId()) ? StringDefine.Y.name() : StringDefine.N.name());
                //당첨상품명
                rowDataList.add(StringTools.convertNullToEmptyString(row.getProductName()));

                //하나의 raw_table 값을 배열에 담는다.
                rowList.add(rowDataList);
            }
        }
        return rowList;
    }

    public void makeSurveyRawTableValue3(final String eventId, int listCount, int arEventId, List<Object>fieldValueList) {

        //총개수
        int totalSurveyLogCount = surveyGoStaticsMapper.countSurveyLogAttendByEventId(eventId);
        //루프 개수
        int loopCount = totalSurveyLogCount / listCount;
        if (loopCount == 0) {
            if (totalSurveyLogCount > 0) {
                loopCount = 1;
            }
        }

        List<SurveyTableRawAnswerResVO> answerRowList = this.selectSurveyTableRawAnswerList(arEventId);

        for (int i=0; i<=(loopCount+1); i++) {
            List<List<String>> rowList = new ArrayList<>();
            if (i == 0) {
                i = 1;
            }
            //시작번호
            int start = EventUtils.getPagingStartNumber(i, listCount);
            //서베이고를 완료한 목록 가져오기
            List<SurveyTableRawResVO> valueList = surveyGoStaticsMapper.selectSurveyTableRawStatics2(eventId, start, listCount);

            List<String>attendLogIdList = valueList.stream().map(SurveyTableRawResVO::getSurveyLogAttendId).collect(Collectors.toList());
            if (attendLogIdList == null || attendLogIdList.isEmpty()) {
                break;
            }
            //서베이고를 완료한 survey_log_attend_id 로 참여한 서베이고 로그 결과 리스트 가져오기
            List<SurveyLogAttendResultResVO> attendResultListIn = surveyGoLogMapper.selectSurveyLogAttendResultByAttendIdAndArEventId2(attendLogIdList, arEventId);

            for (SurveyTableRawResVO row : valueList) {
//                List<Object>rowDataList = new ArrayList<>();
                List<String>rowDataList = new ArrayList<>();
                rowDataList.add(eventId);
                //참여인덱스
                rowDataList.add(row.getSurveyLogAttendId());
                //당첨인덱스
                //rowDataList.add(row.getGiveAwayId());

                //응답시작시간
                rowDataList.add(row.getAttendStartDate());
                //성별
                rowDataList.add(GenderTypeDefine.getGenderTypeStr(StringTools.convertNullToEmptyString(row.getGender())));
                //연령대
                rowDataList.add(AgeTypeDefine.getAgeTypeStr(row.getAge()));

                //해당 서베이고 이벤트의 문항 목록 가져오기
                //List<SurveyTableRawAnswerResVO> answerRowList = surveyGoStaticsMapper.selectSurveyTableRawAnswerList(row.getArEventId(), row.getSurveyLogAttendId());
                //List<SurveyTableRawAnswerResVO> answerRowList = this.selectSurveyTableRawAnswerList(row.getArEventId());

                List<SurveyLogAttendResultResVO> attendResultList = attendResultListIn.stream().filter(data -> data.getSurveyLogAttendId().equals(row.getSurveyLogAttendId())).collect(Collectors.toList());

                if (!PredicateUtils.isNullList(attendResultList)) {
                    // =========================== 문항 리스트 시작 ===============================
                    for (SurveyTableRawAnswerResVO answerRow : answerRowList) {
                        //참여한 로그의 보기값과 문항의 보기 값이 같은 목록 정렬
                        List<SurveyLogAttendResultResVO> machtedAnswerCollectList = attendResultList.stream()
                                .filter(data -> Objects.equals(data.getSubjectSort(), answerRow.getSubjectSort()))
                                .collect(Collectors.toList());

                        if (PredicateUtils.isNotNullList(machtedAnswerCollectList)) {
                            //참여한 로그의 보기값과 문항의 보기 값이 같은 목록이 하나면 사용자가 기록한 보기의 값을 주입
                            if (machtedAnswerCollectList.size() == 1) {

                                if (PredicateUtils.isNotNull(machtedAnswerCollectList.get(0).getExampleTitle())) {
                                    //주관식이 아니면 주입
                                    rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getExampleTitle()));
                                }
                                //기타항목이 있는 문항이면 기타 값 주입
                                if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                                    if (PredicateUtils.isNull(machtedAnswerCollectList.get(0).getSurveyExampleId()) && PredicateUtils.isNull(machtedAnswerCollectList.get(0).getExampleSort())) {
                                        rowDataList.add("");
                                        rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                                    } else {
                                        rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                                    }
                                } else {
                                    //주관식
                                    if (PredicateUtils.isEqualsStr(answerRow.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                                        rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                                    }
                                }
                            }

                            //참여한 로그의 보기값과 문항의 보기 값이 같은 목록이 하나 이상이면 사용자가 기록한 보기의 값을 문자열(,) 기준으로 만들고 주입
                            if (machtedAnswerCollectList.size() > 1) {
                                List<String> sortList = new ArrayList<>();
                                for (SurveyLogAttendResultResVO result : machtedAnswerCollectList) {
                                    if (PredicateUtils.isNotNull(result.getExampleTitle())) {
                                        sortList.add(result.getExampleTitle());
                                    }
                                }
                                rowDataList.add(String.join(",", sortList));

                                //기타항목이 있는 문항이면 기타 값 주입
                                if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                                    //기타항목 데이터가 존재하는 오브젝트 확인
                                    Optional<SurveyLogAttendResultResVO> findQuestionAnswerOptional = attendResultList.stream()
                                            .filter( data -> PredicateUtils.isEqualNumber(data.getSubjectSort(), answerRow.getSubjectSort()))
                                            .filter(data -> PredicateUtils.isNotNull(data.getQuestionAnswer()))
                                            .findFirst();
                                    //기타항목 데이터가 있으면 기타항목 값 주입
                                    if (findQuestionAnswerOptional.isPresent()) {
                                        rowDataList.add(StringTools.convertNullToEmptyString(findQuestionAnswerOptional.get().getQuestionAnswer()));
                                    } else {
                                        //기타항목 데이터가 없으면 공백 값
                                        rowDataList.add("");
                                    }
                                }
                            }

                            if (machtedAnswerCollectList.size() == 0) {
                                rowDataList.add("");
                            }
                        } else {
                            List<SurveyLogAttendResultResVO> notMachtedAnswerCollectList = attendResultList.stream()
                                    .filter(data -> !Objects.equals(data.getSubjectSort(), answerRow.getSubjectSort()))
                                    .collect(Collectors.toList());


                            if (PredicateUtils.isNotNullList(notMachtedAnswerCollectList)) {
                                for (SurveyLogAttendResultResVO result : notMachtedAnswerCollectList) {
                                    if (Objects.equals(result.getSubjectSort(), answerRow.getSubjectSort())) {
                                        rowDataList.add(StringTools.convertNullToEmptyString(notMachtedAnswerCollectList.get(0).getExampleTitle()));
                                        if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                                            rowDataList.add(StringTools.convertNullToEmptyString(notMachtedAnswerCollectList.get(0).getQuestionAnswer()));
                                        }
                                    } else {
                                        if (PredicateUtils.isEqualN(answerRow.getEtcOpinionReceiveYn())) {
                                            rowDataList.add("");
                                            break;
                                        } else {
                                            rowDataList.add("");
                                            rowDataList.add("");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // =========================== 문항 리스트 끝 ===============================
                }

                //참여한 로그 결과 값이 없으면
                if (PredicateUtils.isNullList(attendResultList)) {
                    for (SurveyTableRawAnswerResVO answerRow : answerRowList) {
                        rowDataList.add("");
                        if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                            rowDataList.add("");
                        }
                    }
                }

                //응답종료시간
                rowDataList.add(row.getAttendEndDate());
                //당첨여부
                rowDataList.add(PredicateUtils.isNotNull(row.getGiveAwayId()) ? StringDefine.Y.name() : StringDefine.N.name());
                //당첨상품명
                rowDataList.add(StringTools.convertNullToEmptyString(row.getProductName()));

                //하나의 raw_table 값을 배열에 담는다.
                rowList.add(rowDataList);
            }
            batchService.saveBulkSurveyAnswerStatics(fieldValueList, rowList);
        }

//        return rowList;
    }


    public List<String> makeSurveyAnswerStaticsData(int arEventId, String surveyLogAttendId) {
            //서베이고를 완료한 survey_log_attend_id 로 참여한 서베이고 로그 결과 리스트 가져오기

        List<String>rowDataList = new ArrayList<>();

        SurveyTableRawResVO row = surveyGoStaticsMapper.selectSurveyTableRawStaticsByArEventIdAndSurveyLogAttendId(arEventId, surveyLogAttendId);
        //서베이고를 완료한 survey_log_attend_id 로 참여한 서베이고 로그 결과 리스트 가져오기
        List<SurveyLogAttendResultResVO> attendResultList = surveyGoLogMapper.selectSurveyLogAttendResultByAttendIdAndArEventId(surveyLogAttendId, arEventId);

        //참여인덱스
        rowDataList.add(row.getSurveyLogAttendId());
        //당첨인덱스
        //rowDataList.add(row.getGiveAwayId());

        //응답시작시간
        rowDataList.add(row.getAttendStartDate());
        //성별
        rowDataList.add(GenderTypeDefine.getGenderTypeStr(StringTools.convertNullToEmptyString(row.getGender())));
        //연령대
        rowDataList.add(AgeTypeDefine.getAgeTypeStr(row.getAge()));

        //해당 서베이고 이벤트의 문항 목록 가져오기
        //List<SurveyTableRawAnswerResVO> answerRowList = surveyGoStaticsMapper.selectSurveyTableRawAnswerList(row.getArEventId(), row.getSurveyLogAttendId());
        List<SurveyTableRawAnswerResVO> answerRowList = this.selectSurveyTableRawAnswerList(row.getArEventId());

        if (!PredicateUtils.isNullList(attendResultList)) {
            // =========================== 문항 리스트 시작 ===============================
            for (SurveyTableRawAnswerResVO answerRow : answerRowList) {
                //참여한 로그의 보기값과 문항의 보기 값이 같은 목록 정렬
                List<SurveyLogAttendResultResVO> machtedAnswerCollectList = attendResultList.stream()
                        .filter(data -> Objects.equals(data.getSubjectSort(), answerRow.getSubjectSort()))
                        .collect(Collectors.toList());

                if (PredicateUtils.isNotNullList(machtedAnswerCollectList)) {
                    //참여한 로그의 보기값과 문항의 보기 값이 같은 목록이 하나면 사용자가 기록한 보기의 값을 주입
                    if (machtedAnswerCollectList.size() == 1) {

                        if (PredicateUtils.isNotNull(machtedAnswerCollectList.get(0).getExampleTitle())) {
                            //주관식이 아니면 주입
                            rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getExampleTitle()));
                        }
                        //기타항목이 있는 문항이면 기타 값 주입
                        if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                            if (PredicateUtils.isNull(machtedAnswerCollectList.get(0).getSurveyExampleId()) && PredicateUtils.isNull(machtedAnswerCollectList.get(0).getExampleSort())) {
                                rowDataList.add("");
                                rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                            } else {
                                rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                            }
                        } else {
                            //주관식
                            if (PredicateUtils.isEqualsStr(answerRow.getSubjectExampleType(), ExampleTypeDefine.QUESTION.name())) {
                                rowDataList.add(StringTools.convertNullToEmptyString(machtedAnswerCollectList.get(0).getQuestionAnswer()));
                            }
                        }
                    }

                    //참여한 로그의 보기값과 문항의 보기 값이 같은 목록이 하나 이상이면 사용자가 기록한 보기의 값을 문자열(,) 기준으로 만들고 주입
                    if (machtedAnswerCollectList.size() > 1) {
                        List<String> sortList = new ArrayList<>();
                        for (SurveyLogAttendResultResVO result : machtedAnswerCollectList) {
                            if (PredicateUtils.isNotNull(result.getExampleTitle())) {
                                sortList.add(result.getExampleTitle());
                            }
                        }
                        rowDataList.add(String.join(",", sortList));

                        //기타항목이 있는 문항이면 기타 값 주입
                        if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                            //기타항목 데이터가 존재하는 오브젝트 확인
                            Optional<SurveyLogAttendResultResVO> findQuestionAnswerOptional = attendResultList.stream()
                                    .filter( data -> PredicateUtils.isEqualNumber(data.getSubjectSort(), answerRow.getSubjectSort()))
                                    .filter(data -> PredicateUtils.isNotNull(data.getQuestionAnswer()))
                                    .findFirst();
                            //기타항목 데이터가 있으면 기타항목 값 주입
                            if (findQuestionAnswerOptional.isPresent()) {
                                rowDataList.add(StringTools.convertNullToEmptyString(findQuestionAnswerOptional.get().getQuestionAnswer()));
                            } else {
                                //기타항목 데이터가 없으면 공백 값
                                rowDataList.add("");
                            }
                        }
                    }

                    if (machtedAnswerCollectList.size() == 0) {
                        rowDataList.add("");
                    }
                } else {
                    List<SurveyLogAttendResultResVO> notMachtedAnswerCollectList = attendResultList.stream()
                            .filter(data -> !Objects.equals(data.getSubjectSort(), answerRow.getSubjectSort()))
                            .collect(Collectors.toList());


                    if (PredicateUtils.isNotNullList(notMachtedAnswerCollectList)) {
                        for (SurveyLogAttendResultResVO result : notMachtedAnswerCollectList) {
                            if (Objects.equals(result.getSubjectSort(), answerRow.getSubjectSort())) {
                                rowDataList.add(StringTools.convertNullToEmptyString(notMachtedAnswerCollectList.get(0).getExampleTitle()));
                                if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                                    rowDataList.add(StringTools.convertNullToEmptyString(notMachtedAnswerCollectList.get(0).getQuestionAnswer()));
                                }
                            } else {
                                if (PredicateUtils.isEqualN(answerRow.getEtcOpinionReceiveYn())) {
                                    rowDataList.add("");
                                    break;
                                } else {
                                    rowDataList.add("");
                                    rowDataList.add("");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            // =========================== 문항 리스트 끝 ===============================
        }

        //참여한 로그 결과 값이 없으면
        if (PredicateUtils.isNullList(attendResultList)) {
            for (SurveyTableRawAnswerResVO answerRow : answerRowList) {
                rowDataList.add("");
                if (PredicateUtils.isEqualsStr(answerRow.getEtcOpinionReceiveYn(), StringDefine.Y.name())) {
                    rowDataList.add("");
                }
            }
        }

        //응답종료시간
        rowDataList.add(row.getAttendEndDate());
        //당첨여부
        rowDataList.add(PredicateUtils.isNotNull(row.getGiveAwayId()) ? StringDefine.Y.name() : StringDefine.N.name());
        //당첨상품명
        rowDataList.add(StringTools.convertNullToEmptyString(row.getProductName()));

        return rowDataList;
    }

    public String findSurveyAttendStatics(String eventId, String searchDate, Boolean isSubmit) {
        return surveyGoStaticsMapper.selectSurveyAttendStatics(eventId, searchDate, isSubmit);
    }

    public List<HourlyMapperVO> findHourlySurveyAttendStatics(String eventId, String searchDate, Boolean isSubmit){
        return surveyGoStaticsMapper.selectHourlySurveyAttendStatics(eventId, searchDate, isSubmit);
    }

    public List<SurveyTableExampleStaticsResVO> findSurveyExampleQuestionAnswerStaticsByQuiz(long surveySubjectId) {
        return surveyGoStaticsMapper.selectSurveyExampleQuestionAnswerStaticsByQuiz(surveySubjectId);
    }

    public List<SurveyTableRawAnswerResVO> selectSurveyTableRawAnswerList(int arEventId) {
        List<SurveyTableRawAnswerResVO> answerRowList = surveyGoStaticsMapper.selectSurveyTableRawAnswerList(arEventId);
        return answerRowList;
    }

    public List<Map<String, String>> getSurveyAnswerStaticsListByEventId(final String eventId) {
        int listCount = 1000;
        //총개수
        int totalSurveyLogCount = surveyGoStaticsMapper.countSurveyLogAttendByEventId(eventId);
        //루프 개수
        int loopCount = totalSurveyLogCount / listCount;
        if (loopCount == 0) {
            if (totalSurveyLogCount > 0) {
                loopCount = 1;
            }
        }

        List<Map<String, String>>answerMapList = new ArrayList<>();
        for (int i=0; i<=(loopCount+1); i++) {
            if (i == 0) {
                i = 1;
            }
            //시작번호
            int start = EventUtils.getPagingStartNumber(i, listCount);
            //서베이고를 완료한 목록 가져오기
            answerMapList.addAll(staticsMapper.selectAnswerStaticsListByEventId(eventId, start, listCount));
        }
        return answerMapList;
    }

}
