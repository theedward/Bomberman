package com.cmov.bomberman.model;

import java.util.Map;

/**
 * Created by JoãoEduardo on 15-04-2014.
 */
public interface Playable {
        Map<String, Action> possibleActions();
        void play(State state);
}
