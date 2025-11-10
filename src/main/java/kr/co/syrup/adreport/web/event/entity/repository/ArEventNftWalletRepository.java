package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.ArEventNftWalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArEventNftWalletRepository extends JpaRepository<ArEventNftWalletEntity, Integer> {
    Optional<ArEventNftWalletEntity> findByUserPhoneNumber(String userPhoneNumber);
    long countByArEventIdAndUserPhoneNumberAndNftWalletType(int arEventId, String userPhoneNumber, String nftWalletType);
    Optional<ArEventNftWalletEntity> findById(long arEventNftWalletId);
    Optional<ArEventNftWalletEntity> findByNftWalletAddressAndNftWalletTypeAndArEventId(String nftWalletAddress, String nftWalletType, int arEventId);
}
