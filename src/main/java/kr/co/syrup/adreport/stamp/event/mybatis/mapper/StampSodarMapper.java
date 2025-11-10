package kr.co.syrup.adreport.stamp.event.mybatis.mapper;

import kr.co.syrup.adreport.stamp.event.dto.StampEventPanDto;
import kr.co.syrup.adreport.stamp.event.dto.WebEventBaseDto;
import kr.co.syrup.adreport.stamp.event.dto.request.WebEventStampTypeListReqDto;
import kr.co.syrup.adreport.stamp.event.dto.response.StampAlimtokInfoResDto;
import kr.co.syrup.adreport.stamp.event.model.*;
import kr.co.syrup.adreport.web.event.dto.request.EventBaseDto;
import kr.co.syrup.adreport.web.event.entity.WebEventBaseEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StampSodarMapper {

    void upsertStampEventMain(StampEventMainModel stampEventMainModel);

    void upsertStampEventPan(StampEventPanModel stampEventPanModel);

    void insertStampPanTrList(@Param("paramList") List<StampEventPanTrModel>paramList);

    List<StampEventPanTrModel> selectStampEventPanTrListByStpId(@Param("stpId") int stpId);

    @Select(" SELECT * FROM stamp_event_pan_tr WHERE stp_pan_id = #{stpPanId} ORDER BY stp_tr_sort ASC")
    List<StampEventPanTrModel> selectStampEventPanTrListByStpPanId(@Param("stpPanId") int stpPanId);

    @Select(" SELECT * FROM stamp_event_pan_tr WHERE stp_pan_tr_id = #{stpPanTrId} ")
    StampEventPanTrModel selectStampEventPanTrById(@Param("stpPanTrId") long stpPanTrId);

    void updateStampPanTr(StampEventPanTrModel stampEventPanTrModel);

    void deleteStampPanTrByIdIn(@Param("idList") List<Long>idList);

    void upsertStampAlimtok(StampAlimtokModel stampAlimtokModel);

    void insertStampAlimtok(StampAlimtokModel stampAlimtokModel);

    void updateStampAlimtok(StampAlimtokModel stampAlimtokModel);

    void upsertStampAlimtokButton(StampAlimtokButtonModel stampAlimtokButtonModel);

    void insertStampAlimtokButtonList(@Param("paramList") List<StampAlimtokButtonModel>paramList);

    void updateStampAlimtokButton(StampAlimtokButtonModel stampAlimtokButtonModel);

    void deleteStampAlimtokButtonIdIn(@Param("idList") List<Long>idList);

    @Select(" SELECT * FROM stamp_alimtok_button WHERE stp_alimtok_id = #{stpAlimtokId} ")
    List<StampAlimtokButtonModel> selectStampAlimtokButtonListByStpAlimtokId(@Param("stpAlimtokId") int stpAlimtokId);

    @Select(" SELECT * FROM stamp_alimtok_button WHERE stp_alimtok_btn_id = #{stpAlimtokBtnId} ")
    StampAlimtokButtonModel selectStampAlimtokButtonById(@Param("stpAlimtokBtnId") long stpAlimtokBtnId);

    WebEventBaseEntity selectWebEventBaseByStpId(@Param("stpId") int stpId);

    @Delete(" DELETE FROM stamp_event_gate_cdoe WHERE stp_id = #{stpId} ")
    void deleteStampEventGateCodeByStpId(@Param("stpId") int stpId);

    @Select(" SELECT * FROM stamp_event_main WHERE event_id = #{eventId} ")
    StampEventMainModel selectStampEventMainByEventId(@Param("eventId") String eventId);

    @Select(" SELECT * FROM stamp_event_main WHERE stp_id = #{stpId} ")
    StampEventMainModel selectStampEventMainById(@Param("stpId") int stpId);

    @Select(" SELECT * FROM stamp_event_pan WHERE stp_id = #{stpId} ")
    StampEventPanModel selectStampEventPanByStpId(@Param("stpId") int stpId);

    StampAlimtokInfoResDto selectStampAlimtokInfoByStpId(@Param("stpId") int stpId);

    List<WebEventBaseDto> selectWebEventStampTypeList(@Param("size") int size, @Param("offset") int offset, @Param("searchType") String searchType, @Param("searchWord") String searchWord, @Param("searchMode") String searchMode);

    long selectWebEventStampTypeListCnt(@Param("searchType") String searchType, @Param("searchWord") String searchWord, @Param("searchMode") String searchMode);

    @Update(" UPDATE stamp_event_main SET stp_attend_code_count = #{stpAttendCodeCount} WHERE stp_id = #{stpId} ")
    void updateStpAttendCodeCountFromStampEventMain(@Param("stpId") int stpId, @Param("stpAttendCodeCount") int stpAttendCodeCount);

    @Select(" SELECT distinct stp_tr_event_id FROM stamp_event_pan_tr WHERE stp_tr_event_id is not null")
    List<String> selectAllStpTrEventIdList();

    List<String> selectStpTrEventIdListByStpTrEventIdNotIn(@Param("stpTrEventIds") List<String> stpTrEventIds);

    StampEventPanDto selectStampEventPanByStampEventId(@Param("eventId") String eventId);

    @Update(" UPDATE stamp_event_pan_tr SET stp_tr_event_id = null, stp_tr_pid = null WHERE stp_pan_id = #{stpPanId} ")
    void rejectStampEventPanTrByRel(@Param("stpPanId") int stpPanId);

}
