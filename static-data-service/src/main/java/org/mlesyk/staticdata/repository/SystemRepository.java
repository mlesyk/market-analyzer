package org.mlesyk.staticdata.repository;

import org.mlesyk.staticdata.model.UniverseSystem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemRepository extends CrudRepository<UniverseSystem, Integer> {

    List<UniverseSystem> findUniverseSystemBySystemIDIn(List<Integer> ids);

}