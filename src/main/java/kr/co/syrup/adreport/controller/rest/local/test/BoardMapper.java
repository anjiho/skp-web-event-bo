package kr.co.syrup.adreport.controller.rest.local.test;

import jdk.internal.net.http.common.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {

    List<BoardInfoDto> selectBoardList();

    List<BoardInfoDto> selectBoardListByPaging(@Param("start")  int start, @Param("size") int size);

    Long countByBoardTotal();

    BoardInfoDto selectBoardById(Long boardId);

    void saveBoard(@Param("title") String title, @Param("contents") String contents);

    void deleteBoardByIdxList(@Param("list") List<Long> items);

    void modifyBoard(@Param("idx") Long idx, @Param("title") String title, @Param("contents") String contents);

}
