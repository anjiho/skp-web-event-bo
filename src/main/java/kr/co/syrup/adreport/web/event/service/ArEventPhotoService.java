package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.utils.EventUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.*;
import kr.co.syrup.adreport.web.event.dto.request.PhotoContentsReqDto;
import kr.co.syrup.adreport.web.event.entity.ArEventDeviceGpsEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventHtmlEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventNftBannerEntity;
import kr.co.syrup.adreport.web.event.entity.ArPhotoContentsEntity;
import kr.co.syrup.adreport.web.event.entity.repository.*;
import kr.co.syrup.adreport.web.event.mybatis.mapper.PhotoLogPrintMapper;
import kr.co.syrup.adreport.web.event.mybatis.vo.PhotoLogPrintCountVO;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArEventPhotoService {

    @Value("${web.event.domain}")
    private String webEventDomain;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ArEventHtmlEntityRepository arEventHtmlEntityRepository;

    @Autowired
    private ArEventNftBannerRepository arEventNftBannerRepository;

    @Autowired
    private ArEventDeviceGpsRepository arEventDeviceGpsRepository;

    @Autowired
    private ArPhotoContentsEntityRepository arPhotoContentsEntityRepository;

    @Autowired
    PhotoLogPrintMapper photoLogPrintMapper;

    public ArEventHtmlEntity selectArPhotoboxDetail(String eventId) {
        return arEventHtmlEntityRepository.findByEventIdAndHtmlButtonType(eventId, HtmlButtonTypeDefine.PHOTOREPO.name()).orElse(null);
    }

    public List<ArEventNftBannerEntity> selectArPhotoBannerList(int eventHtmlId) {
        return arEventNftBannerRepository.findAllByEventHtmlId(eventHtmlId).orElseGet(ArrayList::new);

    }

    public List<ArEventDeviceGpsEntity> selectArPhotoDeviceGpsList(int eventHtmlId) {
        return arEventDeviceGpsRepository.findAllByEventHtmlIdOrderByIdAsc(eventHtmlId).orElseGet(ArrayList::new);
    }

    public long selectArPhotoOcbPrintTryCount(String eventId, String ocbMbrId) {
        return photoLogPrintMapper.selectOcbPrintStatusCnt(eventId, ocbMbrId, PhotoPrintTypeDefine.TRY.name());
    }

    public long selectArPhotoOcbPrintFailCount(String eventId, String ocbMbrId) {
        return photoLogPrintMapper.selectOcbPrintStatusCnt(eventId, ocbMbrId, PhotoPrintTypeDefine.FAIL.name());
    }

    public long selectArPhotoClientPrintTryCount(String eventId, String clientUniqueKey) {
        return photoLogPrintMapper.selectClientPrintStatusCnt(eventId, clientUniqueKey, PhotoPrintTypeDefine.TRY.name());
    }

    public long selectArPhotoClientPrintFailCount(String eventId, String clientUniqueKey) {
        return photoLogPrintMapper.selectClientPrintStatusCnt(eventId, clientUniqueKey, PhotoPrintTypeDefine.FAIL.name());
    }

    public void saveArPhotoPrintStatus(PhotoLogPrintCountVO vo){
        photoLogPrintMapper.savePhotoPrintLog(vo);
    }

    public List<ArPhotoContentsEntity> findAllByArEventIdAndPhotoContentTypeOrderBySortAsc(int arEventId, String photoContentType) {
        return arPhotoContentsEntityRepository.findAllByArEventIdAndPhotoContentTypeOrderBySortAsc(arEventId, photoContentType).orElseGet(ArrayList::new);
    }

    public List<PhotoContentsReqDto> selectPhotoContentList(int arEventId, String photoContentType) {
        // AR 구동 페이지 조회용
        List<ArPhotoContentsEntity> arPhotoContentsEntitieList = findAllByArEventIdAndPhotoContentTypeOrderBySortAsc(arEventId, photoContentType);
        List<PhotoContentsReqDto> photoContentsReqDtoList = arPhotoContentsEntitieList.stream()
                .map(dto -> modelMapper.map(dto, PhotoContentsReqDto.class))
                .collect(Collectors.toList());

        photoContentsReqDtoList.forEach(info -> {
            boolean isReplaceThumbnail = false;
            boolean isReplaceOriginal = false;

            if (PredicateUtils.isNull(info.getPhotoContentChoiceType()) || PhotoContentChoiceTypeDefine.DIRECT.name().equals(info.getPhotoContentChoiceType())) {
                // 직접등록
                isReplaceThumbnail = true;

                if (PhotoContentTypeDefine.FRAME.name().equals(photoContentType)) {
                    isReplaceOriginal = true;
                }

                if (PhotoContentTypeDefine.TAB.name().equals(photoContentType)) {
                    if (PhotoContentTabMenuTypeDefine.STICKER.name().equals(info.getPhotoContentTabMenuType())) {
                        isReplaceOriginal = true;
                    }
                }

//                if (PhotoContentTypeDefine.FILTER.name().equals(photoContentType)) {
//                }

//                if (PhotoContentTypeDefine.CHARACTER.name().equals(photoContentType)) {
//                }

                if (PhotoContentTypeDefine.STICKER.name().equals(photoContentType)) {
                    isReplaceOriginal = true;
                }
            } else {
                // 라이브러리 선택
                isReplaceThumbnail = false;
                isReplaceOriginal = false;
            }

            if (isReplaceThumbnail == true) {
                info.setPhotoThumbnailImgUrl(EventUtils.replaceUriHost(info.getPhotoThumbnailImgUrl(), webEventDomain + "/sodarimg")); //AR 구동페이지에 넘겨줄 이미지 URL 변환작업( CORS 이슈 ));
            }
            if (isReplaceOriginal == true) {
                info.setPhotoOriginalFileUrl(EventUtils.replaceUriHost(info.getPhotoOriginalFileUrl(), webEventDomain + "/sodarimg")); //AR 구동페이지에 넘겨줄 이미지 URL 변환작업( CORS 이슈 ));
            }
        });

        return photoContentsReqDtoList;
    }

}
