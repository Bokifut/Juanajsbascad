package org.example.juegofinalsupremo.contracts;

import org.example.juegofinalsupremo.exceptions.GameStorageException;
import org.example.juegofinalsupremo.model.GameState;

public interface Repository {
    void save(GameState state, String path) throws GameStorageException;

    GameState load(String path) throws GameStorageException;
}
