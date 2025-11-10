package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.EventGiveAwayDeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventGiveAwayDeliveryEntityRepository extends JpaRepository<EventGiveAwayDeliveryEntity, Integer> {
    List<EventGiveAwayDeliveryEntity> findAllByEventIdAndPhoneNumberOrderByCreatedDateAsc(String eventId, String phoneNumber);
    long countByEventIdAndPhoneNumberAndGiveAwayPasswordIsNotNull(String eventId, String phoneNumber);
    long countByEventIdAndPhoneNumberAndGiveAwayPasswordOrGiveAwayPasswordIsNull(String eventId, String phoneNumber, String password);
    long countByEventIdAndPhoneNumber(String eventId, String phoneNumber);
    EventGiveAwayDeliveryEntity findFirstByOrderByGiveAwayIdDesc();
    long countByEventIdAndArEventWinningIdAndWinningTypeAndPhoneNumberAndMemberBirthAndGifticonResultCd(String eventId, int arEventWinningId, String winningType, String phoneNumber, String memberBirth, String gifticonResultCd);
    long countByEventLogWinningId(long eventLogWinningId);
    Optional<List<EventGiveAwayDeliveryEntity>> findAllByGiveAwayIdBetween(int startNum, int endNum);

    Optional<List<EventGiveAwayDeliveryEntity>> findAllByEventIdInAndPhoneNumberOrderByEventIdAsc(List<String>eventIds, String phoneNumber);

}
