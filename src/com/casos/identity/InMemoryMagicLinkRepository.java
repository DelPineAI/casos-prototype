package com.casos.identity;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Prototype store for magic links. */
public class InMemoryMagicLinkRepository implements MagicLinkRepository {

    private final Map<String, MagicLink> links = new ConcurrentHashMap<>();

    @Override
    public void save(MagicLink link) {
        links.put(link.token(), link);
    }

    @Override
    public Optional<MagicLink> findByToken(String token) {
        return Optional.ofNullable(links.get(token));
    }
}
