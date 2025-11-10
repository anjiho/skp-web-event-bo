package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.CommonSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommonSettingsEntityRepository extends JpaRepository<CommonSettingsEntity, Integer> {
    Optional<CommonSettingsEntity> findBySettingKey(String settingKey);
}
