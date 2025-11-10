package kr.co.syrup.adreport.web.event.entity.repository;

import kr.co.syrup.adreport.web.event.entity.WebEventIpAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WebEventIpAccessRepository extends JpaRepository<WebEventIpAccess, Integer> {
    Optional<List<WebEventIpAccess>> findAllByBuildLevelAndUrlPath(String buildLevel, String urlPath);
}
