package net.eracube.commons.games;

import java.util.HashMap;

public enum GameState {
    LOADING("loading"),
    WAITING_PLAYERS("waiting_players"),
    STARTING("starting"),
    PLAYING("playing"),
    ENDING("ending"),
    STOPPING("stopping");

    private final static HashMap<String, GameState> gameStates;
    private String id;

    GameState(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    static {
        gameStates = new HashMap<>();
        for (GameState value : values())
            gameStates.put(value.getId(), value);
    }

    public static GameState getGameStateById(String id) {
        return gameStates.get(id);
    }
}
