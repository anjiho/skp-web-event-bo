package kr.co.syrup.adreport.stamp.event.service;

import kr.co.syrup.adreport.framework.common.annotation.LoggingTimeFilter;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.stamp.event.dto.StampEventPanDto;
import kr.co.syrup.adreport.stamp.event.dto.WebEventBaseDto;
import kr.co.syrup.adreport.stamp.event.dto.request.StampAlimtokReqDto;
import kr.co.syrup.adreport.stamp.event.dto.request.WebEventStampTypeListReqDto;
import kr.co.syrup.adreport.stamp.event.dto.response.StampAlimtokInfoResDto;
import kr.co.syrup.adreport.stamp.event.model.*;
import kr.co.syrup.adreport.stamp.event.mybatis.mapper.StampSodarMapper;
import kr.co.syrup.adreport.web.event.entity.WebEventBaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StampSodarService {

    @Autowired
    private StampSodarMapper stampSodarMapper;

    /**
     * STAMP_EVENT_MAIN 입력, 수정
     * @param stampEventMainModel
     */
    @LoggingTimeFilter
    @Transactional
    public Integer upsertStampEventMain(StampEventMainModel stampEventMainModel, String eventId) {
        try {
            stampEventMainModel.setEventId(eventId);
            stampSodarMapper.upsertStampEventMain(stampEventMainModel);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            return stampEventMainModel.getStpId();
        }
    }

    /**
     * STAMP_EVENT_PAN 입력, 수정
     * @param stampEventPanModel
     */
    @LoggingTimeFilter
    @Transactional
    public Integer upsertStampEventPan(StampEventPanModel stampEventPanModel) {
        try {
            stampSodarMapper.upsertStampEventPan(stampEventPanModel);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (PredicateUtils.isNotNull(stampEventPanModel.getStpPanId())) {
                return stampEventPanModel.getStpPanId();
            } else {
                return null;
            }
        }
    }

    /**
     * STAMP_EVENT_PAN_TR 입력, 수정, 삭제
     * @param list
     * @param stpPanId
     */
    @LoggingTimeFilter
    @Transactional
    public void upsertDeleteListStampEventPanTr(List<StampEventPanTrModel>list, Integer stpPanId) {
        if (PredicateUtils.isNotNullList(list) && PredicateUtils.isNotNull(stpPanId)) {
            List<StampEventPanTrModel>newSaveList = new ArrayList<>();
            //저장되어있는 항목 조회
            List<StampEventPanTrModel>savedList = stampSodarMapper.selectStampEventPanTrListByStpPanId(stpPanId);

            for (StampEventPanTrModel updateModel : list) {
                //인덱스가 없으면 신규 저장
                if (PredicateUtils.isNull(updateModel.getStpPanTrId())) {
                    updateModel.setStpPanId(stpPanId);
                    newSaveList.add(updateModel);
                } else {
                    //저장되어 데이터가 있을때
                    if (PredicateUtils.isNotNullList(savedList)) {
                        StampEventPanTrModel findModel = stampSodarMapper.selectStampEventPanTrById(updateModel.getStpPanTrId());
                        if (PredicateUtils.isNotNull(findModel)) {
                            //DB 조회 항목이 있으면 저장되어있는 배열 데이터에서 row 데이터 삭제처리
                            savedList.removeIf(model -> Objects.equals(model.getStpPanTrId(), findModel.getStpPanTrId()));
                        }
                    }
                }
                //수정
                try {
                    stampSodarMapper.updateStampPanTr(updateModel);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            //입력
            try {
                if (PredicateUtils.isNotNullList(newSaveList)) {
                    stampSodarMapper.insertStampPanTrList(newSaveList);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            //삭제
            try {
                if (PredicateUtils.isNotNullList(savedList)) {
                    List<Long> idList = savedList.stream().map(StampEventPanTrModel::getStpPanTrId).collect(Collectors.toList());
                    stampSodarMapper.deleteStampPanTrByIdIn(idList);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @LoggingTimeFilter
    @Transactional
    public void upsertDeleteListStampAlimtok(StampAlimtokReqDto reqDto, int stpId) {
        if (PredicateUtils.isNotNull(reqDto)) {
            StampAlimtokModel stampAlimtok = new StampAlimtokModel();
            if (PredicateUtils.isNotNull(reqDto.getStpAlimtokId())) {
                stampAlimtok = StampAlimtokModel.ofUpdate(reqDto.getStpAlimtokId(), reqDto.getStpAlimtokTxt(), reqDto.getStpAlimtokSendType());
            } else {
                stampAlimtok = StampAlimtokModel.of(reqDto.getStpAlimtokTxt(), reqDto.getStpAlimtokSendType());
            }


            //알림톡 발송 조건, 문구 저장 및 업데이트
            if (PredicateUtils.isNull(stampAlimtok.getStpAlimtokId())) {
                stampAlimtok.setStpId(stpId);
                try {
                    stampSodarMapper.insertStampAlimtok(stampAlimtok);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                try {
                    stampSodarMapper.updateStampAlimtok(stampAlimtok);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            List<StampAlimtokButtonModel>newSaveList = new ArrayList<>();
            //
            List<StampAlimtokButtonModel>savedList = stampSodarMapper.selectStampAlimtokButtonListByStpAlimtokId(stampAlimtok.getStpAlimtokId());
            for (StampAlimtokButtonModel alimtokButton : reqDto.getStampAlimtokButton()) {
                //인덱스가 없으면 신규 저장
                if (PredicateUtils.isNull(alimtokButton.getStpAlimtokBtnId())) {
                    alimtokButton.setStpAlimtokId(stampAlimtok.getStpAlimtokId());
                    newSaveList.add(alimtokButton);
                } else {
                    if (PredicateUtils.isNotNullList(savedList)) {
                        StampAlimtokButtonModel findModel = stampSodarMapper.selectStampAlimtokButtonById(alimtokButton.getStpAlimtokBtnId());
                        if (PredicateUtils.isNotNull(findModel)) {
                            savedList.removeIf(model -> Objects.equals(model.getStpAlimtokBtnId(), findModel.getStpAlimtokBtnId()));
                        }
                    }
                }
                try {
                    //수정
                    stampSodarMapper.updateStampAlimtokButton(alimtokButton);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            try {
                //입력
                if (PredicateUtils.isNotNullList(newSaveList)) {
                    stampSodarMapper.insertStampAlimtokButtonList(newSaveList);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            try {
                if (PredicateUtils.isNotNullList(savedList)) {
                    List<Long>idList = savedList.stream().map(StampAlimtokButtonModel::getStpAlimtokBtnId).collect(Collectors.toList());
                    stampSodarMapper.deleteStampAlimtokButtonIdIn(idList);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public WebEventBaseEntity findWebEventBaseByStpId(int stpId) {
        return stampSodarMapper.selectWebEventBaseByStpId(stpId);
    }

    public void deleteStampEventGateCodeByStpId(int stpId) {
        try {
            stampSodarMapper.deleteStampEventGateCodeByStpId(stpId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public StampEventMainModel findStampEventMainByEventId(String eventId) {
        return stampSodarMapper.selectStampEventMainByEventId(eventId);
    }

    public StampEventPanModel findStampEventPanByStpId(int stpId) {
        return stampSodarMapper.selectStampEventPanByStpId(stpId);
    }

    @Cacheable(cacheNames = "findStampEventPanTrListByStpId", keyGenerator = "customKeyGenerator")
    public List<StampEventPanTrModel> findStampEventPanTrListByStpId(int stpId) {
        return stampSodarMapper.selectStampEventPanTrListByStpId(stpId);
    }

    @Cacheable(cacheNames = "findStampEventPanTrListByStpPanId", keyGenerator = "customKeyGenerator")
    public List<StampEventPanTrModel> findStampEventPanTrListByStpPanId(int stpPanId) {
        return stampSodarMapper.selectStampEventPanTrListByStpPanId(stpPanId);
    }

    public List<StampEventPanTrModel> findStampEventPanTrListNoCacheByStpId(int stpId) {
        return stampSodarMapper.selectStampEventPanTrListByStpPanId(stpId);
    }

    public StampAlimtokInfoResDto findStampAlimtokInfoByStpId(int stpId) {
        return stampSodarMapper.selectStampAlimtokInfoByStpId(stpId);
    }

    @Cacheable(cacheNames = "findStampEventMainById", keyGenerator = "customKeyGenerator")
    public StampEventMainModel findStampEventMainById(int stpId) {
        return stampSodarMapper.selectStampEventMainById(stpId);
    }

    public List<WebEventBaseDto> findWebEventStampTypeList(WebEventStampTypeListReqDto dto) {
        int size = dto.getSize();
        int offset = (dto.getPage() - 1) * dto.getSize();
        return stampSodarMapper.selectWebEventStampTypeList(size, offset, dto.getSearchType(), dto.getSearchWord(), dto.getSearchMode());
    }

    public long findWebEventStampTypeListCount(WebEventStampTypeListReqDto dto){
        return stampSodarMapper.selectWebEventStampTypeListCnt(dto.getSearchType(), dto.getSearchWord(), dto.getSearchMode());
    }

    @Transactional
    public void updateStpAttendCodeCountFromStampEventMain(int stpId, int attendCodeCount) {
        stampSodarMapper.updateStpAttendCodeCountFromStampEventMain(stpId, attendCodeCount);
    }

    public List<String> findAllStpTrEventIdList() {
        return stampSodarMapper.selectAllStpTrEventIdList();
    }

    public List<String> findStpTrEventIdListByStpTrEventIdNotIn(List<String> stpTrEventIdList) {
        return stampSodarMapper.selectStpTrEventIdListByStpTrEventIdNotIn(stpTrEventIdList);
    }

    @Transactional
    public void rejectStampEventPanTrByRel(String stampEventId) {
        StampEventPanDto stampEventPanDto = stampSodarMapper.selectStampEventPanByStampEventId(stampEventId);
        if (PredicateUtils.isNotNull(stampEventPanDto.getStpPanId())) {
            stampSodarMapper.rejectStampEventPanTrByRel(stampEventPanDto.getStpPanId());
        }
    }


}
