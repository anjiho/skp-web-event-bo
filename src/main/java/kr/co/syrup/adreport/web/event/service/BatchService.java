package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.common.annotation.LoggingTimeFilter;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.stamp.event.model.StampEventGateCodeModel;
import kr.co.syrup.adreport.survey.go.mybatis.vo.SurveyLogAttendResultResVO;
import kr.co.syrup.adreport.web.event.entity.ArEventGateCodeEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftCouponInfoEntity;
import kr.co.syrup.adreport.web.event.mybatis.mapper.StaticsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BatchService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StaticsMapper staticsMapper;

    @Transactional
    public void batchUpdateEncryptEventGiveAwayDelivery(List<Map<String, Object>>mapList) {
        String sql = "";
        if (PredicateUtils.isNotNullList(mapList)) {
            sql = "UPDATE event_give_away_delivery " +
                    "SET name = ?, "
                    + " phone_number = ?, "
                    + " address = ?, "
                    + " address_detail = ? "
                    + " WHERE give_away_id = ?";

            try {
                jdbcTemplate.batchUpdate(
                        sql, mapList, 1000,
                        new ParameterizedPreparedStatementSetter<Map<String, Object>>() {
                            @Override
                            public void setValues(PreparedStatement ps, Map<String, Object> argument) throws SQLException {
                                ps.setString(1, PredicateUtils.isNull(argument.get("name")) ? "" : String.valueOf(argument.get("name")) );
                                ps.setString(2, PredicateUtils.isNull(argument.get("phoneNumber")) ? "" :  String.valueOf(argument.get("phoneNumber")) );
                                ps.setString(3, PredicateUtils.isNull(argument.get("address")) ? "" : String.valueOf(argument.get("address")));
                                ps.setString(4, PredicateUtils.isNull(argument.get("addressDetail")) ? "" : String.valueOf(argument.get("addressDetail")));
                                ps.setInt(5, (int)argument.get("giveAwayId"));

                            }
                        });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * AR_EVENT_GAGE_CODE 배치 저장
     * @param arEventGateCodeEntityList
     */
    @LoggingTimeFilter
    public void saveBatchArEventGateCode(List<ArEventGateCodeEntity>arEventGateCodeEntityList) {
        String sql = "";
        if (PredicateUtils.isNotNull(arEventGateCodeEntityList) || PredicateUtils.isGreaterThanZero(arEventGateCodeEntityList.size())) {
            sql = "INSERT INTO ar_event_gate_code " +
                    "( event_id,"
                    + " attend_code,"
                    + " use_yn,"
                    + " created_date )"
                    + " VALUES (?,?,?,now()) ";

            try {
                jdbcTemplate.batchUpdate(
                        sql, arEventGateCodeEntityList, 10000,
                        new ParameterizedPreparedStatementSetter<ArEventGateCodeEntity>() {
                            @Override
                            public void setValues(PreparedStatement ps, ArEventGateCodeEntity argument) throws SQLException {
                                ps.setString(1, argument.getEventId());
                                ps.setString(2, argument.getAttendCode());
                                ps.setBoolean(3, false);

                            }
                        });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void insertArEventNftCoupon(List<ArEventNftCouponInfoEntity> list) {
        if (PredicateUtils.isNotNullList(list)) {
            String sql = "INSERT INTO ar_event_nft_coupon_info " +
                    "( ar_event_id,"
                    + " stp_id,"
                    + " ar_event_winning_id,"
                    + " nft_coupon_id,"
                    + " is_payed,"
                    + " upload_excel_file_name,"
                    + " created_date )"
                    + " VALUES (?, ?, ?, ?, ?, ?, now()) ";

            try {
                jdbcTemplate.batchUpdate(
                        sql, list, 10000,
                        new ParameterizedPreparedStatementSetter<ArEventNftCouponInfoEntity>() {
                            @Override
                            public void setValues(PreparedStatement ps, ArEventNftCouponInfoEntity entity) throws SQLException {
                                ps.setInt(1, PredicateUtils.isNull(entity.getArEventId()) ? 0 : entity.getArEventId());
                                ps.setInt(2, PredicateUtils.isNull(entity.getStpId()) ? 0 : entity.getStpId());
                                ps.setInt(3, PredicateUtils.isNull(entity.getArEventWinningId()) ? 0 : entity.getArEventId());
                                ps.setString(4, entity.getNftCouponId());
                                ps.setBoolean(5, PredicateUtils.isNull(entity.getIsPayed()) ? false : entity.getIsPayed());
                                ps.setString(6, entity.getUploadExcelFileName());
                            }
                        });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 스탬프 참여코드 배치 저장
     * @param stampEventGateCodeModelList
     */
    @LoggingTimeFilter
    public void saveBatchStampEventGateCode(List<StampEventGateCodeModel>stampEventGateCodeModelList) {
        String sql = "";
        if (PredicateUtils.isNotNullList(stampEventGateCodeModelList)) {
            sql = "INSERT INTO stamp_event_gate_code " +
                    "( stp_id,"
                    + " attend_code )"
                    + " VALUES (?,?) ";

            try {
                jdbcTemplate.batchUpdate(
                        sql, stampEventGateCodeModelList, 10000,
                        new ParameterizedPreparedStatementSetter<StampEventGateCodeModel>() {
                            @Override
                            public void setValues(PreparedStatement ps, StampEventGateCodeModel argument) throws SQLException {
                                ps.setInt(1, argument.getStpId());
                                ps.setString(2, argument.getAttendCode());

                            }
                        });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void saveBulkArEventNftCouponTemp(long tempSeq, List<String>couponCodeList) {
        String sql = "";
        if (tempSeq > 0l && PredicateUtils.isNotNullList(couponCodeList)) {
            sql = "INSERT INTO ar_event_nft_coupon_info_temp" +
                    " ( temp_seq, coupon_code )"
                    + " VALUES (?,?) ";

            try {
                jdbcTemplate.batchUpdate(
                    sql, couponCodeList, 10000,
                    new ParameterizedPreparedStatementSetter<String>() {
                        @Override
                        public void setValues(PreparedStatement ps, String couponCode) throws SQLException {
                            ps.setLong(1, tempSeq);
                            ps.setString(2, couponCode);
                        }
                    });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void saveBulkSurveyAnswerStatics(List<Object>titleList, List<List<String>> answerList) {
        if (PredicateUtils.isNotNullList(answerList)) {
            String sql = "";
            sql += "INSERT INTO survey_answer_statics";
            String sql2 = "VALUES ";

            List<List<String>>processedAnswerList = new ArrayList<>();


            for (int i = 0; i < answerList.size(); i++) {
                List<String>answer = answerList.get(i);
                processedAnswerList.add(answer);

                for (int j = 0; j <= answer.size(); j++) {
                    if (j == 0) {
                        sql += "( survey_log_attend_id, ";
                        sql2 += "(";
                    } else {
                        if (j < answerList.size()) {
                            sql += "answer_" + (j) + ",";
                            sql2 += "?,";
                        } else if (j == answerList.size()) {
                            sql += "answer_" + (j) + ")";
                            sql2 += "?)";
                        }
                    }
                }
            }
            //log.info(sql + sql2);

            staticsMapper.saveAnswerList(titleList, processedAnswerList);
        }
    }

    public void saveBulkSurveyLogAttendResult(List<SurveyLogAttendResultResVO>list) {
        String sql = "";
        sql = "INSERT INTO survey_log_attend_result" +
                " ( survey_log_attend_id, survey_subject_id, survey_example_id, subject_sort, example_sort, is_answer, question_answer )"
                + " VALUES (?,?,?,?,?,?,?) ";

        try {
            jdbcTemplate.batchUpdate(
                    sql, list, 10,
                    new ParameterizedPreparedStatementSetter<SurveyLogAttendResultResVO>() {
                        @Override
                        public void setValues(PreparedStatement ps, SurveyLogAttendResultResVO vo) throws SQLException {
                            ps.setString(1, vo.getSurveyLogAttendId());
                            ps.setLong(2, vo.getSurveySubjectId());
                            if (PredicateUtils.isNull(vo.getSurveyExampleId())) {
                                ps.setNull(3, java.sql.Types.NULL);
                            } else {
                                ps.setLong(3, vo.getSurveyExampleId());
                            }

                            ps.setInt(4, vo.getSubjectSort());

                            if (PredicateUtils.isNull(vo.getExampleSort())) {
                                ps.setNull(5, java.sql.Types.NULL);
                            } else {
                                ps.setInt(5, vo.getExampleSort());
                            }
                            ps.setBoolean(6, vo.getIsAnswer());

                            if (PredicateUtils.isNull(vo.getQuestionAnswer())) {
                                ps.setNull(7, java.sql.Types.NULL);
                            } else {
                                ps.setString(7, vo.getQuestionAnswer());
                            }

                        }
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
