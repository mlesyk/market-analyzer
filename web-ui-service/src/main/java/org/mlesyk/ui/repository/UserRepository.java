package org.mlesyk.ui.repository;

import org.mlesyk.ui.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByName(String name);
}