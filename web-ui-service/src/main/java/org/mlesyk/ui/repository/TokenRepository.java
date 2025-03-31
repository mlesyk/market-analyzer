package org.mlesyk.ui.repository;

import org.mlesyk.ui.model.oauth.Token;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, Long> {
}