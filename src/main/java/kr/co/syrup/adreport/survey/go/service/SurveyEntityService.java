package kr.co.syrup.adreport.survey.go.service;

import kr.co.syrup.adreport.framework.common.annotation.LoggingTimeFilter;
import kr.co.syrup.adreport.framework.utils.ModelMapperUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.survey.go.define.ImgVideoRegTypeDefine;
import kr.co.syrup.adreport.survey.go.dto.request.*;
import kr.co.syrup.adreport.survey.go.entity.*;
import kr.co.syrup.adreport.survey.go.entity.repository.*;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SurveyEntityService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SurveySubjectEntityRepository surveySubjectEntityRepository;

    @Autowired
    private SurveySubjectCategoryEntityRepository surveySubjectCategoryEntityRepository;

    @Autowired
    private SurveyExampleEntityRepository surveyExampleEntityRepository;

    @Autowired
    private SurveyExampleQuestionEntityRepository surveyExampleQuestionEntityRepository;

    @Autowired
    private SurveySubjectPopupImageEntityRepository surveySubjectPopupImageEntityRepository;

    @Autowired
    private SurveyTargetAgeGenderLimitEntityRepository targetAgeGenderLimitEntityRepository;

    @Autowired
    private SurveyLogAttendEntityRepository surveyLogAttendEntityRepository;

    @Autowired
    private SurveyGoSodarService surveyGoSodarService;

    public SurveyExampleEntity findSurveyExampleBySurveyExampleId(long surveyExampleId) {
        return surveyExampleEntityRepository.findBySurveyExampleId(surveyExampleId);
    }

    @Cacheable(cacheNames = "findSurveySubjectPopupImageBySurveySubjectId", keyGenerator = "customKeyGenerator")
    public List<SurveySubjectPopupImageEntity> findSurveySubjectPopupImageBySurveySubjectId(long surveySubjectId) {
        return surveySubjectPopupImageEntityRepository.findAllBySurveySubjectId(surveySubjectId, null).orElseGet(ArrayList::new);
    }

    public SurveyLogAttendEntity findSurveyLogAttendBySurveyLogAttendId(String surveyLogAttendId) {
        return surveyLogAttendEntityRepository.findBySurveyLogAttendId(surveyLogAttendId);
    }

    //---------------------------------------------------------------------------------------------------------------------

    public SurveyTargetAgeGenderLimitEntity findSurveyTargetAgeGenderLimitById(long id) {
        return targetAgeGenderLimitEntityRepository.findById(id).orElseGet(SurveyTargetAgeGenderLimitEntity::new);
    }

    public List<SurveyTargetAgeGenderLimitEntity> findSurveyTargetAgeGenderLimitByArEventId(int arEventId) {
        return targetAgeGenderLimitEntityRepository.findAllByArEventId(arEventId).orElseGet(ArrayList::new);
    }

    public SurveySubjectEntity findSurveySubjectById(long index) {
        return surveySubjectEntityRepository.findById(index).orElseGet(SurveySubjectEntity::new);
    }

    public List<SurveySubjectEntity> findAllSurveySubjectByArEventId(int arEventId) {
        return surveySubjectEntityRepository.findAllByArEventId(arEventId, null).orElseGet(ArrayList::new);
    }

    @Cacheable(cacheNames = "findAllSurveySubjectByArEventIdAtCache", keyGenerator = "customKeyGenerator")
    public List<SurveySubjectEntity> findAllSurveySubjectByArEventIdAtCache(int arEventId) {
        return surveySubjectEntityRepository.findAllByArEventId(arEventId, null).orElseGet(ArrayList::new);
    }

    public List<SurveySubjectEntity> findAllSurveySubjectByArEventIdOrderBySortAsc(int arEventId) {
        List<SurveySubjectEntity> list = surveySubjectEntityRepository.findAllByArEventId(arEventId, orderBySort(Sort.Direction.ASC.name())).orElseGet(ArrayList::new);
        //[DTWS-335]
        if (!PredicateUtils.isNullList(list)) {
            list.stream().forEach(entity -> {
                if (PredicateUtils.isNull(entity.getImgVideoRegType())) {
                    entity.setImgVideoRegType(ImgVideoRegTypeDefine.N.name());
                }
            });
        }
        return list;
    }

    public List<SurveyExampleEntity> findAllSurveyExampleBySurveySubjectId(long surveySubjectId) {
        return surveyExampleEntityRepository.findAllBySurveySubjectId(surveySubjectId, null).orElseGet(ArrayList::new);
    }

    @Cacheable(cacheNames = "findAllSurveyExampleBySurveySubjectIdAtCache", keyGenerator = "customKeyGenerator")
    public List<SurveyExampleEntity> findAllSurveyExampleBySurveySubjectIdAtCache(long surveySubjectId) {
        return surveyExampleEntityRepository.findAllBySurveySubjectId(surveySubjectId, null).orElseGet(ArrayList::new);
    }

    public List<SurveyExampleEntity> findAllSurveyExampleBySurveySubjectIdOrderBySortAsc(long surveySubjectId) {
        return surveyExampleEntityRepository.findAllBySurveySubjectId(surveySubjectId, orderBySort(Sort.Direction.ASC.name())).orElseGet(ArrayList::new);
    }

    public SurveyExampleQuestionEntity findSurveyExampleQuestionById(long id) {
        return surveyExampleQuestionEntityRepository.findById(id).orElseGet(SurveyExampleQuestionEntity::new);
    }

    public List<SurveyExampleQuestionEntity> findAllSurveyExampleQuestionBySurveySubjectId(long surveySubjectId) {
        return surveyExampleQuestionEntityRepository.findAllBySurveySubjectId(surveySubjectId, null).orElseGet(ArrayList::new);
    }

    @Cacheable(cacheNames = "findAllSurveyExampleQuestionBySurveySubjectIdAtCache", keyGenerator = "customKeyGenerator")
    public List<SurveyExampleQuestionEntity> findAllSurveyExampleQuestionBySurveySubjectIdAtCache(long surveySubjectId) {
        return surveyExampleQuestionEntityRepository.findAllBySurveySubjectId(surveySubjectId, null).orElseGet(ArrayList::new);
    }

    public List<SurveyExampleQuestionEntity> findAllSurveyExampleQuestionBySurveySubjectIdOrderBySortAsc(long surveySubjectId) {
        return surveyExampleQuestionEntityRepository.findAllBySurveySubjectId(surveySubjectId, orderBySort(Sort.Direction.ASC.name())).orElseGet(ArrayList::new);
    }

    public List<SurveySubjectCategoryEntity> findAllSurveySubjectCategoryByArEventId(int arEventId) {
        return surveySubjectCategoryEntityRepository.findAllByArEventId(arEventId, null).orElseGet(ArrayList::new);
    }

    public List<SurveySubjectCategoryEntity> findAllSurveySubjectCategoryByArEventIdOrderBySortAsc(int arEventId) {
        return surveySubjectCategoryEntityRepository.findAllByArEventId(arEventId, orderBySort(Sort.Direction.ASC.name())).orElseGet(ArrayList::new);
    }

    public SurveySubjectPopupImageEntity findSurveySubjectPopupImageById(long id) {
        return surveySubjectPopupImageEntityRepository.findById(id).orElseGet(SurveySubjectPopupImageEntity::new);
    }

    public List<SurveySubjectPopupImageEntity> findAllSurveySubjectPopupImageBySurveySubjectId(long surveySubjectId) {
        return surveySubjectPopupImageEntityRepository.findAllBySurveySubjectId(surveySubjectId, null).orElseGet(ArrayList::new);
    }

    public List<SurveySubjectPopupImageEntity> findAllSurveySubjectPopupImageBySurveySubjectIdOrderBySortAsc(long surveySubjectId) {
        return surveySubjectPopupImageEntityRepository.findAllBySurveySubjectId(surveySubjectId, orderBySort(Sort.Direction.ASC.name())).orElseGet(ArrayList::new);
    }

    @LoggingTimeFilter
    @Transactional
    public void saveAllTargetAgeGenderLimit(int arEventId, List<GenderAgeLimitSodarReqDto> reqDtoList) {
        if (!PredicateUtils.isNullList(reqDtoList)) {
            List<SurveyTargetAgeGenderLimitEntity> entityList = ModelMapperUtils.convertModelInList(reqDtoList, SurveyTargetAgeGenderLimitEntity.class);
            if (!PredicateUtils.isNullList(entityList)) {
                entityList.forEach(entity -> entity.setArEventId(arEventId));

                try {
                    targetAgeGenderLimitEntityRepository.saveAll(entityList);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Transactional
    public void saveTargetAgeGenderLimit(SurveyTargetAgeGenderLimitEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                targetAgeGenderLimitEntityRepository.save(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public SurveySubjectEntity saveSurveySubjectByReqDto(int arEventId, SurveySubjectSodarReqDto reqDto) {
        if (PredicateUtils.isNotNull(reqDto) && arEventId > 0) {
            SurveySubjectEntity subjectEntity = ModelMapperUtils.convertModel(reqDto, SurveySubjectEntity.class);
            subjectEntity.setArEventId(arEventId);

            try {
                surveySubjectEntityRepository.save(subjectEntity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return subjectEntity;
        }
        return null;
    }

    @Transactional
    public SurveyExampleEntity saveSurveyExampleByReqDto(long surveySubjectId, SubjectExampleSodarReqDto reqDto) {
        if (PredicateUtils.isNotNull(reqDto) && surveySubjectId > 0L) {
            SurveyExampleEntity entity = ModelMapperUtils.convertModel(reqDto, SurveyExampleEntity.class);
            entity.setSurveySubjectId(surveySubjectId);

            try {
                surveyExampleEntityRepository.save(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return entity;
        }
        return null;
    }

    @Transactional
    public void saveAllSurveyExampleQuestionByReqDtoList(long surveySubjectId, List<SurveyExampleQuestionSodarReqDto> reqDtoList) {
        if (!PredicateUtils.isNullList(reqDtoList)) {
            List<SurveyExampleQuestionEntity> questionEntityList = ModelMapperUtils.convertModelInList(reqDtoList, SurveyExampleQuestionEntity.class);

            if (!PredicateUtils.isNullList(questionEntityList)) {
                questionEntityList.forEach(entity -> entity.setSurveySubjectId(surveySubjectId));

                try {
                    surveyExampleQuestionEntityRepository.saveAll(questionEntityList);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Transactional
    public void saveSurveyExampleQuestion(SurveyExampleQuestionEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                surveyExampleQuestionEntityRepository.save(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void saveAllSurveySubjectCategoryByReqDto(int arEventId, List<SurveySubjectCategoryReqDto> reqDtoList) {
        if (!PredicateUtils.isNullList(reqDtoList)) {
            List<SurveySubjectCategoryEntity> categoryEntityList = ModelMapperUtils.convertModelInList(reqDtoList, SurveySubjectCategoryEntity.class);

            if (!PredicateUtils.isNullList(categoryEntityList)) {
                categoryEntityList.forEach(entity -> entity.setArEventId(arEventId));

                try {
                    surveySubjectCategoryEntityRepository.saveAll(categoryEntityList);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Transactional
    public void saveAllSurveySubjectPopupImageByReqDto(long surveySubjectId, List<SurveySubjectPopupImageReqDto> reqDtoList) {
        if (!PredicateUtils.isNullList(reqDtoList)) {
            List<SurveySubjectPopupImageEntity> popupImageEntityList = ModelMapperUtils.convertModelInList(reqDtoList, SurveySubjectPopupImageEntity.class);

            if (!PredicateUtils.isNullList(popupImageEntityList)) {
                popupImageEntityList.forEach(entity -> entity.setSurveySubjectId(surveySubjectId));

                try {
                    surveySubjectPopupImageEntityRepository.saveAll(popupImageEntityList);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Transactional
    public void saveSurveySubjectPopupImage(SurveySubjectPopupImageEntity entity) {
        if (PredicateUtils.isNotNull(entity)) {
            try {
                surveySubjectPopupImageEntityRepository.save(entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    //====================================== DELETE ====================================== //

    @Transactional
    public void deleteSurveyTargetAgeGenderLimitById(long id) {
        if (id > 0L) {
            try {
                targetAgeGenderLimitEntityRepository.deleteById(id);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void deleteSurveySubjectIndexIn(List<Long>indexList) {
        try {
            surveySubjectEntityRepository.deleteAllById(indexList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSurveyExampleIndexIn(List<Long>indexList) {
        try {
            surveyExampleEntityRepository.deleteAllById(indexList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSurveyExampleBySubjectSubjectIdIn(List<Long> surveySubjectIds) {
        try {
            surveyExampleEntityRepository.deleteBySurveySubjectIdIn(surveySubjectIds);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSurveyExampleQuestionIndexIn(List<Long>indexList) {
        try {
            surveyExampleQuestionEntityRepository.deleteAllById(indexList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSurveyExampleQuestionBySurveySubjectIdIn(List<Long> surveySubjectIds) {
        try {
            surveyExampleQuestionEntityRepository.deleteBySurveySubjectIdIn(surveySubjectIds);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSurveySubjectPopupImageBySurveySubjectIdIn(List<Long> surveySubjectIds) {
        try {
            surveySubjectPopupImageEntityRepository.deleteBySurveySubjectIdIn(surveySubjectIds);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSurveyExampleQuestionBySubjectSubjectId(long surveySubjectId) {
        try {
            surveyExampleQuestionEntityRepository.deleteBySurveySubjectId(surveySubjectId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSurveySubjectCategoryIndexIn(List<Long>indexList) {
        try {
            surveySubjectCategoryEntityRepository.deleteAllById(indexList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteSurveyExampleBySurveySubjectId(long surveySubjectId) {
        try {
            surveyExampleEntityRepository.deleteBySurveySubjectId(surveySubjectId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    //====================================== DELETE ====================================== //

    public Sort orderBySort(String sortType) {
        if (PredicateUtils.isEqualsStr(sortType, "ASC")) {
            return Sort.by(Sort.Direction.ASC, "sort");
        }
        if (PredicateUtils.isEqualsStr(sortType, "DESC")) {
            return Sort.by(Sort.Direction.DESC, "sort");
        }
        return Sort.by(Sort.Direction.ASC, "sort");
    }

}
