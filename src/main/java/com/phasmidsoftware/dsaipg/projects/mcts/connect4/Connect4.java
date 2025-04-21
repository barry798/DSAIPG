package com.phasmidsoftware.dsaipg.projects.mcts.connect4;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Game;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;

public class Connect4 implements Game<Connect4> {
    public static final int ROWS = 6;
    public static final int COLS = 7;
    public static final int EMPTY = -1;
    public static final int PLAYER_ONE = 0;
    public static final int PLAYER_TWO = 1;

    public static void main(String[] args) {
        Connect4 game = new Connect4();
        State<Connect4> state = game.runGame();
        ((Connect4State) state).render();
        System.out.println("Winner: " + state.winner().orElse(-1));
    }

    @Override
    public int opener() {
        return PLAYER_ONE;
    }

    @Override
    public State<Connect4> start() {
        return new Connect4State(this);
    }

    public State<Connect4> runGame() {
        int step = 1;
        State<Connect4> state = start();
        int player = opener();
        while (!state.isTerminal()) {
            Connect4Node node = new Connect4Node(state);
            Connect4MCTS mcts = new Connect4MCTS(node, 500);
            Node<Connect4> best = mcts.run();
            state = best.state();
            System.out.println("Step " + step++ + ":");
            ((Connect4State) state).render();
            player = (player == PLAYER_ONE) ? PLAYER_TWO : PLAYER_ONE;
        }
        return state;
    }
}
