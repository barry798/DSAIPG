/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.connect4.Connect4;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class TicTacToeNode implements Node<TicTacToe> {

    /**
     * @return true if this node is a leaf node (in which case no further exploration is possible).
     */
    public boolean isLeaf() {
        return state().isTerminal();
    }

    /**
     * @return the State of the Game G that this Node represents.
     */
    public State<TicTacToe> state() {
        return state;
    }

    /**
     * Method to determine if the player who plays to this node is the opening player (by analogy with chess).
     * For this method, we assume that X goes first so is "white."
     * NOTE: this assumes a two-player game.
     *
     * @return true if this node represents a "white" move; false for "black."
     */
    public boolean white() {
        return state.player() == state.game().opener();
    }

    /**
     * @return the children of this Node.
     */
    public Collection<Node<TicTacToe>> children() {
        return children;
    }

    /**
     * Method to add a child to this Node.
     *
     * @param state the State for the new chile.
     */
    public void addChild(State<TicTacToe> state) {
        children.add(new TicTacToeNode(state, this));
    }

    /**
     * This method sets the number of wins and playouts according to the children states.
     */
    public void backPropagate() {
        playouts = 0;
        wins = 0;
        for (Node<TicTacToe> child : children) {
            wins += child.wins();
            playouts += child.playouts();
        }
    }

    /**
     * @return the score for this Node and its descendents a win is worth 2 points, a draw is worth 1 point.
     */
    public int wins() {
        return wins;
    }

    /**
     * @return the number of playouts evaluated (including this node). A leaf node will have a playouts value of 1.
     */
    public int playouts() {
        return playouts;
    }

//    public TicTacToeNode(State<TicTacToe> state) {
//        this.state = state;
//        children = new ArrayList<>();
//        initializeNodeData();
//    }

    private void initializeNodeData() {
        if (isLeaf()) {
            playouts = 1;
            Optional<Integer> winner = state.winner();
            if (winner.isPresent())
                wins = 2; // CONSIDER check that the winner is the correct player. We shouldn't need to.
            else
                wins = 1; // a draw.
        } else {
            Collection<State<TicTacToe>> childrenStates = this.state().moves(this.state().player())
                    .stream()
                    .map(this.state()::next)
                    .toList();
            for (State<TicTacToe> state : childrenStates) {
                this.addChild(state);
            }
        }
    }
    public boolean visitedAllMoves() {
        boolean visited = true;
        for(Node<TicTacToe> child : children){
            if(child.playouts() == 0){
                visited = false;
            }
        }
        return visited;
    }


    public Node<TicTacToe> exploreUnexpandedMoves() {
        for(Node<TicTacToe> child : children){
            if(child.playouts() == 0){
                return child;
            }
        }
        return null;
    }

    public Node<TicTacToe> ucb(){
        Node<TicTacToe> maxChild = null;
        double max = Double.NEGATIVE_INFINITY;

        for(Node<TicTacToe> child : children){
            if (child.playouts() == 0) {
                return child;
            }
//            System.out.println("Math.log(child.playouts()):" + Math.log(child.playouts()));
//            System.out.println("Math.sqrt(2 * Math.log(child.playouts()) / child.playouts():" + Math.sqrt(2 * Math.log(child.playouts()) / child.playouts()));
//            System.out.println("(double)child.wins() / (double) child.playouts()) + Math.sqrt(2 * Math.log(child.playouts()) / child.playouts():" + ((double)child.wins() / (double) child.playouts()) + Math.sqrt(2 * Math.log(child.playouts()) / child.playouts()));

            double currentUCB = ((double)child.wins() / (double) child.playouts()) + Math.sqrt(2 * Math.log(this.playouts()) / child.playouts());
            if(currentUCB > max){
                maxChild = child;
                max = currentUCB;
            }
        }
        return maxChild;
    }

    public TicTacToeNode(State<TicTacToe> state) {
        this(state, null);
    }

    public TicTacToeNode(State<TicTacToe> state, TicTacToeNode parent) {
        this.state = state;
        this.parent = parent;
        children = new ArrayList<>();
        initializeNodeData();
    }

    public TicTacToeNode getParent() {
        return parent;
    }

    public void incrementWins(int result) {
        wins += result;
    }

    public void incrementPlayouts() {
        playouts++;
    }
    private final TicTacToeNode parent;
    private final State<TicTacToe> state;
    private final ArrayList<Node<TicTacToe>> children;
    private int wins;
    private int playouts;


}
