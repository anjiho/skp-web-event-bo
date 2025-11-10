package kr.co.syrup.adreport.survey.go.service;

import kr.co.syrup.adreport.framework.common.annotation.InjectCreatedModifyName;
import kr.co.syrup.adreport.framework.utils.ModelMapperUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.survey.go.dto.response.SurveyTargetAgeGenderLimitResDto;
import kr.co.syrup.adreport.survey.go.entity.*;
import kr.co.syrup.adreport.survey.go.entity.repository.*;
import kr.co.syrup.adreport.survey.go.mybatis.mapper.SurveyGoSodarMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SurveyGoSodarService {

    @Autowired
    private SurveyGoSodarMapper surveyGoSodarMapper;

    @Autowired
    private SurveyTargetAgeGenderLimitEntityRepository targetAgeGenderLimitEntityRepository;

    @Autowired
    private SurveyEntityService surveyEntityService;


    //====================================== UPDATE ====================================== //
    @Transactional
    public void updateTargetAgeGenderLimit(SurveyTargetAgeGenderLimitEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                surveyGoSodarMapper.updateSurveyTargetAgeGenderLimit(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @InjectCreatedModifyName
    @Transactional
    public void updateSurveyTargetAgeGenderLimitFromSodar(List<SurveyTargetAgeGenderLimitEntity>updateEntityList, List<SurveyTargetAgeGenderLimitEntity>savedEntityList, int arEventId) {
        if (!PredicateUtils.isNullList(updateEntityList)) {
            for (SurveyTargetAgeGenderLimitEntity updateEntity : updateEntityList) {
                //인덱스가 없으면 신규 저장
                if (PredicateUtils.isNull(updateEntity.getSurveyTargetAgeGenderLimitId())) {
                    updateEntity.setArEventId(arEventId);
                    surveyEntityService.saveTargetAgeGenderLimit(updateEntity);
                } else {
                    //저장되어 있는 성/연령별 제한 데이터가 있을때
                    if (!PredicateUtils.isNullList(savedEntityList)) {
                        //저장되어있는 데이터가 있는지 DB조회
                        SurveyTargetAgeGenderLimitEntity findEntity = surveyEntityService.findSurveyTargetAgeGenderLimitById(updateEntity.getSurveyTargetAgeGenderLimitId());
                        if (PredicateUtils.isNotNull(findEntity)) {
                            //DB 조회 항목이 있으면 저장되어있는 배열 데이터에서 row 데이터 삭제처리
                            //savedEntityList.removeIf(entity -> entity.getSurveyTargetAgeGenderLimitId() == findEntity.getSurveyTargetAgeGenderLimitId());
                            savedEntityList.removeIf(entity -> Objects.equals(entity.getSurveyTargetAgeGenderLimitId(), findEntity.getSurveyTargetAgeGenderLimitId()));
                        }
                    }
                    //인덱스가 있는 데이터는 수정
                    this.updateTargetAgeGenderLimit(updateEntity);
                }
            }
            //삭제되어야 할 배열 데이터가 있으면 삭제 로직 처리
            if (!PredicateUtils.isNullList(savedEntityList)) {
                savedEntityList.forEach(entity -> {
                    try {
                        surveyEntityService.deleteSurveyTargetAgeGenderLimitById(entity.getSurveyTargetAgeGenderLimitId());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
            }
        }
    }

    public void updateSurveyExampleQuestionFromSodar(List<SurveyExampleQuestionEntity>updateEntityList, List<SurveyExampleQuestionEntity>savedEntityList, long surveySubjectId) {
        if (!PredicateUtils.isNullList(updateEntityList)) {
            for (SurveyExampleQuestionEntity updateEntity : updateEntityList) {
                //인덱스가 없으면 신규 저장
                if (PredicateUtils.isNull(updateEntity.getSurveyExampleQuestionId())) {
                    updateEntity.setSurveySubjectId(surveySubjectId);

                    try {
                        surveyEntityService.saveSurveyExampleQuestion(updateEntity);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                } else {
                    //저장되어 있는 성/연령별 제한 데이터가 있을때
                    if (!PredicateUtils.isNullList(savedEntityList)) {
                        //저장되어있는 데이터가 있는지 DB조회
                        SurveyExampleQuestionEntity findEntity = surveyEntityService.findSurveyExampleQuestionById(updateEntity.getSurveyExampleQuestionId());
                        if (PredicateUtils.isNotNull(findEntity)) {
                            //DB 조회 항목이 있으면 저장되어있는 배열 데이터에서 row 데이터 삭제처리
                            ///savedEntityList.removeIf(entity -> entity.getSurveyExampleQuestionId() == findEntity.getSurveyExampleQuestionId());
                            savedEntityList.removeIf(entity -> Objects.equals(entity.getSurveyExampleQuestionId(), findEntity.getSurveyExampleQuestionId()));
                        }
                    }
                    //인덱스가 있는 데이터는 수정
                    this.updateSurveyExampleQuestion(updateEntity);
                }
            }
            //삭제되어야 할 배열 데이터가 있으면 삭제 로직 처리
            if (!PredicateUtils.isNullList(savedEntityList)) {
                try {
                    surveyEntityService.deleteSurveyExampleQuestionIndexIn(
                            savedEntityList.stream().map(SurveyExampleQuestionEntity::getSurveyExampleQuestionId).collect(Collectors.toList())
                    );
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public void updateSurveySubjectPopupImageFromSodar(List<SurveySubjectPopupImageEntity>updateEntityList, List<SurveySubjectPopupImageEntity>savedEntityList, long surveySubjectId) {
        if (!PredicateUtils.isNullList(updateEntityList)) {
            for (SurveySubjectPopupImageEntity updateEntity : updateEntityList) {
                //인덱스가 없으면 신규 저장
                if (PredicateUtils.isNull(updateEntity.getSurveySubjectPopupImageId())) {
                    updateEntity.setSurveySubjectId(surveySubjectId);

                    try {
                        surveyEntityService.saveSurveySubjectPopupImage(updateEntity);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                } else {
                    //저장되어 있는 성/연령별 제한 데이터가 있을때
                    if (!PredicateUtils.isNullList(savedEntityList)) {
                        //저장되어있는 데이터가 있는지 DB조회
                        SurveySubjectPopupImageEntity findEntity = surveyEntityService.findSurveySubjectPopupImageById(updateEntity.getSurveySubjectPopupImageId());
                        if (PredicateUtils.isNotNull(findEntity)) {
                            //DB 조회 항목이 있으면 저장되어있는 배열 데이터에서 row 데이터 삭제처리
                            savedEntityList.removeIf(entity -> Objects.equals(entity.getSurveySubjectPopupImageId(), findEntity.getSurveySubjectPopupImageId()));
                        }
                    }
                    //인덱스가 있는 데이터는 수정
                    this.updateSurveySubjectPopupImage(updateEntity);
                }
            }
            //삭제되어야 할 배열 데이터가 있으면 삭제 로직 처리
            if (!PredicateUtils.isNullList(savedEntityList)) {
                try {
                    surveyEntityService.deleteSurveyExampleQuestionIndexIn(
                            savedEntityList.stream().map(SurveySubjectPopupImageEntity::getSurveySubjectPopupImageId).collect(Collectors.toList())
                    );
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Transactional
    public void updateSurveySubject(SurveySubjectEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                surveyGoSodarMapper.updateSurveySubject(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void updateSurveyExample(SurveyExampleEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                surveyGoSodarMapper.updateSurveyExample(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void updateSurveyExampleQuestion(SurveyExampleQuestionEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                surveyGoSodarMapper.updateSurveyExampleQuestion(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void updateSurveySubjectCategory(SurveySubjectCategoryEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                surveyGoSodarMapper.updateSurveySubjectCategory(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void updateSurveySubjectPopupImage(SurveySubjectPopupImageEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                surveyGoSodarMapper.updateSurveySubjectPopupImage(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    //====================================== UPDATE ====================================== //

    public List<SurveyTargetAgeGenderLimitResDto> findSurveyTargetAgeGenderLimitListByArEventId(int arEventId) {
        Optional<List<SurveyTargetAgeGenderLimitEntity>> optionals = targetAgeGenderLimitEntityRepository.findAllByArEventId(arEventId);
        if (optionals.isPresent()) {
            return ModelMapperUtils.convertModelInList(optionals.get(), SurveyTargetAgeGenderLimitResDto.class);
        }
        return new ArrayList<>();
    }
}
