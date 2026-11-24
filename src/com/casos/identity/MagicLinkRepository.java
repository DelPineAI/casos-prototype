package com.casos.identity;

import java.util.Optional;

public interface MagicLinkRepository {

    void save(MagicLink link);

    Optional<MagicLink> findByToken(String token);
}
