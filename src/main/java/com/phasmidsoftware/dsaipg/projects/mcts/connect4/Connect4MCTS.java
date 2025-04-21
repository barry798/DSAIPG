package com.phasmidsoftware.dsaipg.projects.mcts.connect4;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Connect4MCTS {

    private final Random random = new Random();
    private final Node<Connect4> root;
    private final int iterations;

    public Connect4MCTS(Node<Connect4> root, int iterations) {
        this.root = root;
        this.iterations = iterations;
    }

    public Node<Connect4> run() {
        for (int i = 0; i < iterations; i++) {
            Node<Connect4> node = select(root);
            if (!node.state().isTerminal())
                expand(node);
            int result = simulate(node.state());
            backpropagate(node, result);
        }
        return bestChild(root);
    }

    private Node<Connect4> select(Node<Connect4> node) {
        while (!node.isLeaf() && !node.children().isEmpty()) {
            node = bestUCT(node);
        }
        return node;
    }

    private void expand(Node<Connect4> node) {
        Collection<State<Connect4>> childrenStates = node.state().moves(node.state().player())
                .stream()
                .map(node.state()::next)
                .toList();
        for (State<Connect4> state : childrenStates) {
            node.addChild(state);
        }
    }

    private int simulate(State<Connect4> state) {
        State<Connect4> current = state;
        int player = current.player();
        while (!current.isTerminal()) {
            List<State<Connect4>> nextStates = current.moves(player).stream()
                    .map(current::next).toList();
            current = nextStates.get(random.nextInt(nextStates.size()));
            player = (player == Connect4.PLAYER_ONE) ? Connect4.PLAYER_TWO : Connect4.PLAYER_ONE;
        }
        return current.winner().map(w -> w == root.state().player() ? 2 : 0).orElse(1);
    }

    private void backpropagate(Node<Connect4> node, int result) {
        if (node instanceof Connect4Node n) {
            n.incrementPlayouts();
            n.addWins(result);
        }
        if (node != root) {
            for (Node<Connect4> parent : root.children()) {
                if (parent.children().contains(node)) {
                    backpropagate(parent, result);
                    break;
                }
            }
        }
    }

    private Node<Connect4> bestUCT(Node<Connect4> node) {
        double logParentPlayouts = Math.log(node.playouts() + 1);
        return node.children().stream().max((a, b) -> Double.compare(
                uctScore(a, logParentPlayouts), uctScore(b, logParentPlayouts))).orElseThrow();
    }

    private double uctScore(Node<Connect4> node, double logParentPlayouts) {
        if (node.playouts() == 0)
            return Double.MAX_VALUE;
        return (double) node.wins() / node.playouts()
                + Math.sqrt(2 * logParentPlayouts / node.playouts());
    }

    private Node<Connect4> bestChild(Node<Connect4> node) {
        return node.children().stream().max((a, b) -> Integer.compare(a.wins(), b.wins())).orElseThrow();
    }

    public static void main(String[] args) {
        Connect4 game = new Connect4();
        Node<Connect4> root = new Connect4Node(game.start());
        Connect4MCTS mcts = new Connect4MCTS(root, 1000);
        Node<Connect4> best = mcts.run();

        ((Connect4State) best.state()).render();
        System.out.println("Estimated win score: " + best.wins() + "/" + best.playouts());
    }

}
