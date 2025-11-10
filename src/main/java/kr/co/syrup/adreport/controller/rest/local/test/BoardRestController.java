package kr.co.syrup.adreport.controller.rest.local.test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import kr.co.syrup.adreport.framework.utils.EventUtils;
import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/board")
public class BoardRestController {

    @Autowired
    private BoardMapper boardMapper;

    @GetMapping("/list")
    public ResponseEntity<ApiResultObjectDto> getBoardList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                           @RequestParam(value = "size", defaultValue = "5") int size) {
        ApiResultObjectDto dto = new ApiResultObjectDto();

        HashMap<String, Object> resultMap = new HashMap<>();

        int start = EventUtils.getPagingStartNumber((page+1), size);

        resultMap.put("totalCount", boardMapper.countByBoardTotal());
        resultMap.put("list", boardMapper.selectBoardListByPaging(start, size));

        dto.setResult(resultMap);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/detail/{idx}")
    public ResponseEntity<BoardInfoDto> getBoardDetail(@PathVariable Long idx) {
        return ResponseEntity.ok(boardMapper.selectBoardById(idx));
    }

    @PostMapping("/save")
    public ResponseEntity<HashMap<String, Object>> saveBoard(@RequestBody String jsonStr) {
        String title = GsonUtils.parseStringJsonStr(jsonStr, "title");
        String contents = GsonUtils.parseStringJsonStr(jsonStr, "contents");

        HashMap<String, Object> map = new HashMap<>();
        boolean isSuccess = true;
        try {
            boardMapper.saveBoard(title, contents);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            isSuccess = false;
        } finally {
            map.put("isSuccess", isSuccess);
        }
        return ResponseEntity.ok(map);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<HashMap<String, Object>> deleteBoard(@RequestBody String jsonStr) {
        JsonArray idJsonArray = GsonUtils.parseJsonArrayJsonStr(jsonStr, "items");

        List<Long> idList = new ArrayList<>();
        if (PredicateUtils.isNotNull(idJsonArray)) {
            Gson gson = new Gson();
            idList = gson.fromJson(idJsonArray, List.class);
        }
        HashMap<String, Object> map = new HashMap<>();
        boolean isSuccess = true;

        try {
            if (idList.size() > 0) {
                boardMapper.deleteBoardByIdxList(idList);
            }
        } catch (Exception e) {
            isSuccess = false;
            log.error(e.getMessage(), e);
        } finally {
            map.put("isSuccess", isSuccess);
        }
        return ResponseEntity.ok(map);
    }

    @PostMapping("/modify")
    public ResponseEntity<HashMap<String, Object>> updateBoard(@RequestBody String jsonStr) {
        HashMap<String, Object> map = new HashMap<>();
        boolean isSuccess = true;

        if (PredicateUtils.isNotNull(jsonStr)) {
            Long idx = GsonUtils.parseLongFromJsonStr(jsonStr, "idx");
            String title = GsonUtils.parseStringJsonStr(jsonStr, "title");
            String contents = GsonUtils.parseStringJsonStr(jsonStr, "contents");

            try {
                boardMapper.modifyBoard(idx, title, contents);
            } catch (Exception e) {
                isSuccess = false;
                log.error(e.getMessage(), e);
            } finally {
                map.put("isSuccess", isSuccess);
            }
        }
        return ResponseEntity.ok(map);
    }

}
