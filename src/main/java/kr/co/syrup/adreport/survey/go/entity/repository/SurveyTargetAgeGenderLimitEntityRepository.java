package kr.co.syrup.adreport.survey.go.entity.repository;

import kr.co.syrup.adreport.survey.go.entity.SurveyTargetAgeGenderLimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurveyTargetAgeGenderLimitEntityRepository extends JpaRepository<SurveyTargetAgeGenderLimitEntity, Long> {
    Optional<List<SurveyTargetAgeGenderLimitEntity>> findAllByArEventId(int arEventId);
}
