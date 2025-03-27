package org.mlesyk.orders.analyzer.repository;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.mlesyk.orders.analyzer.model.RegionOrder;
import org.mlesyk.orders.analyzer.repository.dto.RegionOrderCalculatedValueDTO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface RegionOrderRepository extends CrudRepository<RegionOrder, Long> {

    @Query("SELECT DISTINCT r.orderId FROM RegionOrder r")
    List<Long> getAllOrdersIds();

    @Query("SELECT DISTINCT r.typeId " +
            "FROM RegionOrder r WHERE r.orderValue > :orderValue")
    List<Integer> findDistinctValuableOrderTypesId(Double orderValue);

    List<RegionOrder> findAllByIsBuyOrderAndTypeId(Boolean isBuyOrder, Integer typeId);

    @Query("SELECT new org.mlesyk.orders.analyzer.repository.dto.RegionOrderCalculatedValueDTO(r.orderId, r.locationId, r.isBuyOrder, r.price, r.systemId, r.typeId, r.volumeRemain, r.regionId, r.orderValue) " +
            "FROM RegionOrder r WHERE r.isBuyOrder = TRUE AND r.orderValue > :orderValue AND r.typeId = :typeId")
    List<RegionOrderCalculatedValueDTO> findValuableBuyOrdersByTypeId(@Param("orderValue") Double orderValue, @Param("typeId") Integer typeId);

    @Query("SELECT new org.mlesyk.orders.analyzer.repository.dto.RegionOrderCalculatedValueDTO(r.orderId, r.locationId, r.isBuyOrder, r.price, r.systemId, r.typeId, r.volumeRemain, r.regionId, r.orderValue) " +
            "FROM RegionOrder r WHERE r.isBuyOrder = FALSE AND r.orderValue > :orderValue AND r.typeId = :typeId")
    List<RegionOrderCalculatedValueDTO> findValuableSellOrdersByTypeId(@Param("orderValue") Double orderValue, @Param("typeId") Integer typeId);

    @Query("SELECT new org.mlesyk.orders.analyzer.repository.dto.RegionOrderCalculatedValueDTO(r.orderId, r.locationId, r.isBuyOrder, r.price, r.systemId, r.typeId, r.volumeRemain, r.regionId, r.orderValue) " +
            "FROM RegionOrder r WHERE r.isBuyOrder = TRUE AND r.orderValue > :orderValue AND r.typeId IN :typeIdList")
    List<RegionOrderCalculatedValueDTO> findValuableBuyOrdersByTypeIdList(@Param("orderValue") Double orderValue, @Param("typeIdList") List<Integer> typeIdList);

    @Query("SELECT new org.mlesyk.orders.analyzer.repository.dto.RegionOrderCalculatedValueDTO(r.orderId, r.locationId, r.isBuyOrder, r.price, r.systemId, r.typeId, r.volumeRemain, r.regionId, r.orderValue) " +
            "FROM RegionOrder r WHERE r.isBuyOrder = FALSE AND r.orderValue > :orderValue AND r.typeId IN :typeIdList")
    List<RegionOrderCalculatedValueDTO> findValuableSellOrdersByTypeIdList(@Param("orderValue") Double orderValue, @Param("typeIdList") List<Integer> typeIdList);

    @Query(value = "SELECT r.type_id, SUM(r.price * r.volume_remain) / SUM(r.volume_remain) as avg_price " +
            "FROM region_order r " +
            "JOIN (" +
            "  SELECT type_id, MIN(price) * 5 as max_sell_price" +
            "  FROM region_order" +
            "  WHERE is_buy_order=false AND system_id in (30000142,30002187,30002659)" +
            "  GROUP BY type_id" +
            ") sell_prices ON r.type_id = sell_prices.type_id AND r.price < sell_prices.max_sell_price " +
            "WHERE is_buy_order=false " +
            "GROUP BY r.type_id", nativeQuery = true)
    List<Tuple> findSellOrdersAvgPrice();

    @Query(value = "SELECT r.type_id, SUM(r.price * r.volume_remain) / SUM(r.volume_remain) as avg_price " +
            "FROM region_order r " +
            "JOIN (" +
            "  SELECT type_id, MAX(price) / 5 as min_buy_price" +
            "  FROM region_order" +
            "  WHERE is_buy_order=true AND system_id in (30000142,30002187,30002659)" +
            "  GROUP BY type_id" +
            ") buy_prices ON r.type_id = buy_prices.type_id AND r.price < buy_prices.min_buy_price " +
            "WHERE is_buy_order=true " +
            "GROUP BY r.type_id", nativeQuery = true)
    List<Tuple> findBuyOrdersAvgPrice();

    @Transactional
    @Modifying
    @Query(
            value = "truncate table region_order",
            nativeQuery = true
    )
    void truncateRegionOrderTable();

    @Transactional
    void deleteByOrderIdIn(Set<Long> ids);
}