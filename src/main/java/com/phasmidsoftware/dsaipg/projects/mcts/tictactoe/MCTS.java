/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.connect4.Connect4;
import com.phasmidsoftware.dsaipg.projects.mcts.connect4.Connect4Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import java.util.Optional;

/**
 * Class to represent a Monte Carlo Tree Search for TicTacToe.
 */
public class MCTS {
    private final int iterations = 1000;
    public Node<TicTacToe> runGame() {
        Node<TicTacToe> currentNode = null;
        for (int i = 0; i < iterations; i++) {
//            System.out.println("iterations:" + i);
            currentNode = root;
            if (currentNode instanceof TicTacToeNode n) {
                while (n.visitedAllMoves() && !currentNode.children().isEmpty()) {// Selection
                    currentNode = n.ucb();
                    if (currentNode instanceof TicTacToeNode) {
                        n = (TicTacToeNode)currentNode;
                    }
//                    if( currentNode == null){
//                        System.out.println("in first part");
//                    }
                }
            }
            if (currentNode instanceof TicTacToeNode n) {
                if (!n.visitedAllMoves()) {// Expansion
                    currentNode = n.exploreUnexpandedMoves();
//                    if( currentNode == null){
//                        System.out.println("in second part");
//                    }
                }
            }
            State<TicTacToe> state = currentNode.state();// Simulation
            int player = state.player();
            while (!state.isTerminal()) {
                state = state.next(state.chooseMove(player));
                player = 1 - player;
            }

            //Backpropagation
            int resultPlayer = state.winner().map(w -> w == root.state().player() ? 2 : 0).orElse(1);
            backPropagate(currentNode, resultPlayer);
        }
        return bestChild(root);
//        System.out.println("TicTacToe: finished");
    }

    private Node<TicTacToe> bestChild(Node<TicTacToe> node) {
        return node.children().stream().max((a, b) -> Integer.compare(a.wins(), b.wins())).orElseThrow();
    }

    private void backPropagate(Node<TicTacToe> current, int result) {

        while (current != null) {
            if (current instanceof TicTacToeNode n) {
                n.incrementPlayouts();
                n.incrementWins(result);
                current = n.getParent();
            }
        }
    }

    public static void main(String[] args) {
        MCTS mcts = new MCTS(new TicTacToeNode(new TicTacToe().new TicTacToeState()));
        Node<TicTacToe> root = mcts.root;

        // This is where you process the MCTS to try to win the game.
        Node<TicTacToe> choice = mcts.runGame();
//        System.out.println("TicTacToe: finished");

    }

    public MCTS(Node<TicTacToe> root) {
        this.root = root;
    }

    private final Node<TicTacToe> root;
}
