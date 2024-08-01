package fun.bo.controller;

import lombok.Data;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author xiaobo
 */
@RestController
@RequestMapping("/api")
public class GeoController {

    // 真实业务场景中不建议将业务代码放到controller层，且下面的实体放到dto或者vo中

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final String GEO_KEY = "locations";

    // 新增坐标点
    @PostMapping("/addLocation")
    public ResponseEntity<?> addLocation(@RequestBody GeoLocation location) {
        // 增加坐标点
        redisTemplate.opsForGeo().add(GEO_KEY, new Point(location.getLongitude(), location.getLatitude()), location.getName());
        return ResponseEntity.ok("坐标添加成功");
    }

    // 查询附近的点
    @PostMapping("/searchNearby")
    public ResponseEntity<?> searchNearby(@RequestBody SearchRequest request) {
        Circle circle = new Circle(new Point(request.getLongitude(), request.getLatitude()), new Distance(request.getRadius(), Metrics.KILOMETERS));
        // 获取范围坐标点
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo()
                .radius(GEO_KEY, circle, RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates().limit(10));
        return ResponseEntity.ok(results);
    }

    // 辅助类定义
    @Data
    public static class GeoLocation {
        private String name;
        private double latitude;
        private double longitude;

        // 省略getter和setter方法
    }

    @Data
    public static class SearchRequest {
        private double latitude;
        private double longitude;
        private double radius;

        // 省略getter和setter方法
    }
}
