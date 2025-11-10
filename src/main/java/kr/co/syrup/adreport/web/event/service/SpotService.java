package kr.co.syrup.adreport.web.event.service;

import kr.co.syrup.adreport.framework.utils.ModelMapperUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.dto.response.UserWinningInfoResDto;
import kr.co.syrup.adreport.web.event.entity.CommonSettingsEntity;
import kr.co.syrup.adreport.web.event.entity.EventGiveAwayDeliveryEntity;
import kr.co.syrup.adreport.web.event.entity.repository.CommonSettingsEntityRepository;
import kr.co.syrup.adreport.web.event.entity.repository.EventGiveAwayDeliveryEntityRepository;
import kr.co.syrup.adreport.web.event.mybatis.mapper.ArEventMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SpotService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ArEventMapper arEventMapper;

    @Autowired
    private CommonSettingsEntityRepository commonSettingsEntityRepository;

    @Autowired
    private EventGiveAwayDeliveryEntityRepository eventGiveAwayDeliveryEntityRepository;

    @Transactional
    public void saveCommonSettings(String key, String value) {
        CommonSettingsEntity commonSettingsEntity = new CommonSettingsEntity();
        commonSettingsEntity.setSettingKey(key);
        commonSettingsEntity.setValue(value);

        try {
            commonSettingsEntityRepository.save(commonSettingsEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public List<UserWinningInfoResDto> findAllGiveAwayDeliveryByEventIdAndPhoneNumber(String stampEventIds, String phoneNumber) {
        if (PredicateUtils.isNotNull(stampEventIds)) {
            String[] splitEventIds = StringUtils.split(stampEventIds, ",");
            Optional<List<EventGiveAwayDeliveryEntity>> optionalList = eventGiveAwayDeliveryEntityRepository.findAllByEventIdInAndPhoneNumberOrderByEventIdAsc(Arrays.asList(splitEventIds), phoneNumber);
            if (optionalList.isPresent()) {
                List<EventGiveAwayDeliveryEntity> giveAwayDeliveryEntityList = optionalList.get();
                if (!PredicateUtils.isNullList(giveAwayDeliveryEntityList)) {
                    return ModelMapperUtils.convertModelInList(giveAwayDeliveryEntityList, UserWinningInfoResDto.class);
                }
            }
        }
        return null;
    }

    public List<UserWinningInfoResDto> findEventGiveAwayDeliveryListAsStampEvent(String stampEventIds, String phoneNumber, String attendCode) {
        String[] splitEventIds = StringUtils.split(stampEventIds, ",");
        List<EventGiveAwayDeliveryEntity> deliveryEntityList = arEventMapper.selectEventGiveAwayDeliveryListAsStampEvent(Arrays.asList(splitEventIds), phoneNumber, attendCode);
        if (!PredicateUtils.isNullList(deliveryEntityList)) {
            return ModelMapperUtils.convertModelInList(deliveryEntityList, UserWinningInfoResDto.class);
        }
        return null;
    }
}
