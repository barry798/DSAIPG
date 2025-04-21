package com.phasmidsoftware.dsaipg.projects.mcts.connect4;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class Connect4Node implements Node<Connect4> {

    public Connect4Node(State<Connect4> state) {
        this.state = state;
        this.children = new ArrayList<>();
        initializeNodeData();
    }

    private void initializeNodeData() {
        if (isLeaf()) {
            playouts = 1;
            Optional<Integer> winner = state.winner();
            wins = winner.map(w -> 2).orElse(1); // 2 for win, 1 for draw
        }
    }

    @Override
    public boolean isLeaf() {
        return state.isTerminal();
    }

    @Override
    public State<Connect4> state() {
        return state;
    }

    @Override
    public boolean white() {
        return state.player() == state.game().opener();
    }

    @Override
    public Collection<Node<Connect4>> children() {
        return children;
    }

    @Override
    public void addChild(State<Connect4> childState) {
        children.add(new Connect4Node(childState));
    }

    @Override
    public void backPropagate() {
        playouts = 0;
        wins = 0;
        for (Node<Connect4> child : children) {
            playouts += child.playouts();
            wins += child.wins();
        }
    }

    @Override
    public int wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }

    public void incrementPlayouts() {
        this.playouts++;
    }

    public void addWins(int result) {
        this.wins += result;
    }

    private final State<Connect4> state;
    private final ArrayList<Node<Connect4>> children;
    private int wins;
    private int playouts;
}
