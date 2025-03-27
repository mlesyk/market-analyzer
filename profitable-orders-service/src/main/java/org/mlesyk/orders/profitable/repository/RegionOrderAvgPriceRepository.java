package org.mlesyk.orders.profitable.repository;

import org.mlesyk.orders.profitable.model.RegionOrderAvgPriceCalculated;
import org.mlesyk.orders.profitable.model.RegionOrderAvgPriceCompositeKey;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RegionOrderAvgPriceRepository extends CrudRepository<RegionOrderAvgPriceCalculated, RegionOrderAvgPriceCompositeKey> {

    List<RegionOrderAvgPriceCalculated> findAllByTypeId(Integer typeId);
}
