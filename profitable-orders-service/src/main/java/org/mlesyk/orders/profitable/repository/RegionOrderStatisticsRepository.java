package org.mlesyk.orders.profitable.repository;

import org.mlesyk.orders.profitable.model.RegionOrderStatistics;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RegionOrderStatisticsRepository extends CrudRepository<RegionOrderStatistics, Long> {

    @Query("SELECT DISTINCT r.typeId FROM RegionOrderStatistics r")
    List<Integer> findDistinctTypesId();

    List<RegionOrderStatistics> findAllByTypeId(Integer typeId);
}
