package com.phasmidsoftware.dsaipg.projects.mcts.connect4;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

public class Connect4Move implements Move<Connect4> {
    private final int player;
    private final int column;

    public Connect4Move(int player, int column) {
        this.player = player;
        this.column = column;
    }

    public int player() {
        return player;
    }

    public int column() {
        return column;
    }
}