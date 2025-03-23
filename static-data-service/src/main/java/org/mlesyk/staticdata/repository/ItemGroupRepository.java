package org.mlesyk.staticdata.repository;

import org.mlesyk.staticdata.model.ItemGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemGroupRepository extends CrudRepository<ItemGroup, Integer> {
}