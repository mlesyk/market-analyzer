package org.mlesyk.orders.profitable.repository;

import org.mlesyk.orders.profitable.model.ProfitableOrders;
import org.mlesyk.orders.profitable.repository.dto.ProfitableOrderBuyIdView;
import org.mlesyk.orders.profitable.repository.dto.ProfitableOrderSellIdView;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProfitableOrdersRepository extends CrudRepository<ProfitableOrders, Integer> {

    List<ProfitableOrders> findBySellOrderIdPKIn(Iterable<Long> sellOrdersIds);
    List<ProfitableOrders> findByBuyOrderIdPKIn(Iterable<Long> buyOrdersIds);

    List<ProfitableOrderBuyIdView> findDistinctProfitableOrderBuyIdBy();
    List<ProfitableOrderSellIdView> findDistinctProfitableOrderSellIdBy();

    void deleteAllByBuyOrderIdPKIn(Iterable<Long> buyOrdersIds);
    void deleteAllBySellOrderIdPKIn(Iterable<Long> sellOrdersIds);

}
