package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.stamp.event.model.StampEventMainModel;
import kr.co.syrup.adreport.stamp.event.mybatis.vo.StpPanTrRowNumByWinningVO;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningReqDto;
import kr.co.syrup.adreport.web.event.dto.response.GifticonOrderResDto;
import kr.co.syrup.adreport.web.event.dto.response.WinningButtonResDto;
import kr.co.syrup.adreport.web.event.dto.response.WinningResultResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningEntity;
import kr.co.syrup.adreport.web.event.mybatis.vo.ArEventByIdAtWinningProcessMapperVO;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface EventWinning {

    /**
     * 꽝 결과 버튼 목록
     * @param eventId
     * @param eventLogWinningId
     * @param eventIndex ( AR, SURVEY, PHOHO : ar_event_id.arEventId, STAMP : stamp_event_main.stpId )
     * @param eventType
     * @return
     */
    WinningResultResDto getWinningResultButtonInfo(String eventId, Long eventLogWinningId, Integer eventIndex, String eventType, Boolean isFail);

    /**
     * AR, SURVEY, PHOTO 당첨 결과 버튼 목록
     * @param eventId
     * @param eventLogWinningId
     * @param stpConnectYn
     * @param arEventWinning
     * @return
     */
    WinningResultResDto getWebEventSuccessWinningResultButtonInfo(String eventId, Long eventLogWinningId, String stpConnectYn, ArEventWinningEntity arEventWinning);

    /**
     * 쿠폰 지급하기
     * @param eventIndex ( AR, SURVEY, PHOHO : ar_event_id.arEventId, STAMP : stamp_event_main.stpId )
     * @param arEventWinningId
     * @param eventWinningLogId
     * @param eventType
     * @return
     */
    Long payCoupon(Integer eventIndex, Integer arEventWinningId, Long eventWinningLogId, String eventType);

    /**
     * 기프티콘 지급하기
     * @param eventId
     * @param arEventWinningId
     * @param phoneNumber
     * @return
     */
    GifticonOrderResDto payGifticon(String eventId, int arEventWinningId, String phoneNumber, String eventType);

    /**
     * 스탬프 이벤트가 아닌 다른 이벤트 당첨정보 버튼 정보 > 스탬프판, 다음이벤트 URL PATH 주입
     * @param eventId
     * @param winningButtonList
     */
    void injectStampPanAndStampNextEventUrlPath(String eventId, List<WinningButtonResDto>winningButtonList);

    /**
     * 맵핑되어 있는 당첨 목록 가져오기
     * @param eventIndex
     * @param mappingNumber
     * @param eventType
     * @return
     */
    LinkedList<ArEventWinningEntity> getArEventWinningListByEventIndexAndMappingNumber(int eventIndex, int mappingNumber, String eventType);

    /**
     * 스탬프 참여현재 당첨시도하는 참여 순번과 일치하는 당첨항목이 있는지 체크해서 당첨 목록을 가져오기
     * @param stpId
     * @param attendType
     * @param attendValue
     * @return
     */
    List<StpPanTrRowNumByWinningVO> getStpPanTrRowNumByWinning(int stpId, String attendType, String attendValue);

    /**
     * 스탬프 > 참여값(attendValue)을 핸드폰번호와 참여코드중 분리하여 주입 하기
     * @param eventWinningReqDto
     * @param stampEventMainModel
     */
    void injectStampAttendValue(EventWinningReqDto eventWinningReqDto, StampEventMainModel stampEventMainModel);
}
