package org.mlesyk.staticdata.repository;

import org.mlesyk.staticdata.model.Item;
import org.mlesyk.staticdata.model.rest.ItemNameToIdDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {

    @Query("SELECT new org.mlesyk.staticdata.model.rest.ItemNameToIdDTO(m.id, m.name) " +
            "FROM Item m")
    List<ItemNameToIdDTO> findAllItemNameToIdDTO();

}