package kr.co.syrup.adreport.framework.utils;


import kr.co.syrup.adreport.web.event.dto.request.PhotoLogicalReqDto;
import kr.co.syrup.adreport.web.event.entity.ArPhotoLogicalEntity;
import org.modelmapper.ModelMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelMapperUtils {

    private static ModelMapper modelMapper = new ModelMapper();

    public static ModelMapper getModelMapper() {
        return modelMapper;
    }

    /**
     * list 안의 클래스를 변경해준다
      * @param originList 변경할 리스트
     * @param convertClass 변경되어야 할 클래스
     * @return
     * @param <T>
     */
    public static <T> List<T> convertModelInList(List<?> originList, Class<T> convertClass) {
        return originList.stream()
                .map(map -> modelMapper.map(map, convertClass))
                .collect(Collectors.toList());
    }

    public static <T> LinkedList<T> convertModelInLinkedList(LinkedList<?> originList, Class<T> convertClass) {
        return originList.stream()
                .map(map -> modelMapper.map(map, convertClass))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * 모델 변경
     * @param obj
     * @param convertClass
     * @return
     */
    public static <T> T convertModel(Object obj, Class<T> convertClass) {
        return (T) modelMapper.map(obj, convertClass);
    }

    public static void main(String[] args) {
        ArPhotoLogicalEntity entity = new ArPhotoLogicalEntity();
        entity.setTutorialYn("N");
        PhotoLogicalReqDto dto = convertModel(entity, PhotoLogicalReqDto.class);
        System.out.println("dto >>" + dto.getTutorialYn());

    }
}
