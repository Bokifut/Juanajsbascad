package org.example.juegofinalsupremo.contracts;

import org.example.juegofinalsupremo.exceptions.GameException;
import org.example.juegofinalsupremo.model.Direction;

public interface GameActions {
    void move(Direction direction) throws GameException;

    void attack(Direction direction) throws GameException;

    void pickUp(Direction direction) throws GameException;

    void openDoor(Direction direction) throws GameException;

    void useInventoryItem(int index) throws GameException;
}
