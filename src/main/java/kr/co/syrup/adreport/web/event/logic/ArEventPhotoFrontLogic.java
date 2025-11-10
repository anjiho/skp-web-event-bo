package kr.co.syrup.adreport.web.event.logic;

import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.EventExposureTypeDefine;
import kr.co.syrup.adreport.web.event.dto.request.NftBannerReqDto;
import kr.co.syrup.adreport.web.event.dto.request.PhotoPrintCountReqDto;
import kr.co.syrup.adreport.web.event.dto.request.PhotoboxDetailReqDto;
import kr.co.syrup.adreport.web.event.dto.request.SavePrintStatusReqDto;
import kr.co.syrup.adreport.web.event.dto.response.DeviceGpsInfoDto;
import kr.co.syrup.adreport.web.event.dto.response.PhotoPrintCountResDto;
import kr.co.syrup.adreport.web.event.dto.response.PhotoboxDetailResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventDeviceGpsEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventHtmlEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftBannerEntity;
import kr.co.syrup.adreport.web.event.mybatis.vo.PhotoLogPrintCountVO;
import kr.co.syrup.adreport.web.event.service.ArEventPhotoService;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ArEventPhotoFrontLogic {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ArEventPhotoService arEventPhotoService;

    @Autowired
    private ArEventService arEventService;


    public PhotoboxDetailResDto getWebArPhotoDetail(PhotoboxDetailReqDto reqDto) {
        PhotoboxDetailResDto res = new PhotoboxDetailResDto();

        String eventId = reqDto.getEventId();

        ArEventHtmlEntity arEventHtmlEntity = arEventPhotoService.selectArPhotoboxDetail(eventId);

        if (PredicateUtils.isNull(arEventHtmlEntity)) {
            return null;
        }

        res = modelMapper.map(arEventHtmlEntity, PhotoboxDetailResDto.class);

        int eventHtmlId = arEventHtmlEntity.getEventHtmlId();

        List<ArEventNftBannerEntity> arEventNftBannerEntityList = arEventPhotoService.selectArPhotoBannerList(eventHtmlId);
        List<NftBannerReqDto> bannerList = arEventNftBannerEntityList.stream()
                .map(dto -> modelMapper.map(dto, NftBannerReqDto.class))
                .collect(Collectors.toList());

        res.setBannerList(bannerList);

        List<ArEventDeviceGpsEntity> arEventDeviceGpsEntityList = arEventPhotoService.selectArPhotoDeviceGpsList(eventHtmlId);
        List<DeviceGpsInfoDto> deviceGpsList = arEventDeviceGpsEntityList.stream()
                .map(dto -> modelMapper.map(dto, DeviceGpsInfoDto.class))
                .collect(Collectors.toList());

        res.setDeviceGpsList(deviceGpsList);

        return res;
    }

    public PhotoPrintCountResDto getSelectPrintCount(PhotoPrintCountReqDto req) {
        PhotoPrintCountResDto res = new PhotoPrintCountResDto();

        String eventId = req.getEventId();

        long tryCnt = 0;
        long failCnt = 0;
        long printCnt = 0;
        long settingCnt = 0;

        res.setFreePrintable(false); // 출력 가능여부 초기화
        res.setSettingCount(0); // 무료출력 설정 카운트 초기화

        ArEventHtmlEntity arEventHtmlEntity = arEventPhotoService.selectArPhotoboxDetail(eventId);

        if (PredicateUtils.isNull(arEventHtmlEntity)) {
            return null;
        }

        ArEventEntity arEventEntity = arEventService.findArEventByEventId(req.getEventId());
        if (EventExposureTypeDefine.OCB.name().equals(arEventEntity.getEventExposureType())) {
            // OCB
            tryCnt = arEventPhotoService.selectArPhotoOcbPrintTryCount(req.getEventId(), req.getOcbMbrId());
            failCnt = arEventPhotoService.selectArPhotoOcbPrintFailCount(req.getEventId(), req.getOcbMbrId());
        } else if (EventExposureTypeDefine.OPEN.name().equals(arEventEntity.getEventExposureType())) {
            // 오픈브라우저
            tryCnt = arEventPhotoService.selectArPhotoClientPrintTryCount(req.getEventId(), req.getClientUniqueKey());
            failCnt = arEventPhotoService.selectArPhotoClientPrintFailCount(req.getEventId(), req.getClientUniqueKey());
        }

        // 현재 유저의 출력 개수 : 출력시도 - 출력실패
        printCnt = tryCnt - failCnt;
        res.setPrintCount(printCnt);

        if (PredicateUtils.isEqualY(arEventHtmlEntity.getFreePrintControlYn())) {
            settingCnt = arEventHtmlEntity.getFreePrintCustomerCount();
            res.setSettingCount(arEventHtmlEntity.getFreePrintCustomerCount());
            if ((settingCnt - printCnt) > 0) {
                res.setFreePrintable(true);
            }
        }

        res.setSettingCount(settingCnt);

        return res;
    }

    public void savePrintStatus(SavePrintStatusReqDto req) {
        PhotoLogPrintCountVO vo = PhotoLogPrintCountVO.saveOf(req);
        arEventPhotoService.saveArPhotoPrintStatus(vo);
    }

}
