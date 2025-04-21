package com.phasmidsoftware.dsaipg.projects.mcts.connect4;

import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.*;

public class Connect4State implements State<Connect4> {
    private final int[][] board;
    private final int lastPlayer;
    private final Connect4 game;

    public Connect4State(Connect4 game) {
        this.game = game;
        this.board = new int[Connect4.ROWS][Connect4.COLS];
        for (int[] row : board)
            Arrays.fill(row, Connect4.EMPTY);
        this.lastPlayer = Connect4.EMPTY;
    }

    @Override
    public Random random() {
        return new Random();
    }

    public Connect4State(Connect4 game, int[][] board, int lastPlayer) {
        this.game = game;
        this.board = board;
        this.lastPlayer = lastPlayer;
    }

    public Connect4 game() {
        return game;
    }

    public int player() {
        return (lastPlayer == Connect4.PLAYER_ONE || lastPlayer == Connect4.EMPTY)
                ? Connect4.PLAYER_TWO
                : Connect4.PLAYER_ONE;
    }

    public Collection<Move<Connect4>> moves(int player) {
        List<Move<Connect4>> legalMoves = new ArrayList<>();
        for (int col = 0; col < Connect4.COLS; col++) {
            if (board[0][col] == Connect4.EMPTY) {
                legalMoves.add(new Connect4Move(player, col));
            }
        }
        return legalMoves;
    }

    public State<Connect4> next(Move<Connect4> move) {
        Connect4Move m = (Connect4Move) move;
        int[][] newBoard = copyBoard();
        for (int row = Connect4.ROWS - 1; row >= 0; row--) {
            if (newBoard[row][m.column()] == Connect4.EMPTY) {
                newBoard[row][m.column()] = m.player();
                break;
            }
        }
        return new Connect4State(game, newBoard, m.player());
    }

    public Optional<Integer> winner() {
        for (int row = 0; row < Connect4.ROWS; row++) {
            for (int col = 0; col < Connect4.COLS; col++) {
                int player = board[row][col];
                if (player == Connect4.EMPTY)
                    continue;
                if (col + 3 < Connect4.COLS && player == board[row][col + 1] && player == board[row][col + 2]
                        && player == board[row][col + 3])
                    return Optional.of(player);
                if (row + 3 < Connect4.ROWS && player == board[row + 1][col] && player == board[row + 2][col]
                        && player == board[row + 3][col])
                    return Optional.of(player);
                if (row + 3 < Connect4.ROWS && col + 3 < Connect4.COLS && player == board[row + 1][col + 1]
                        && player == board[row + 2][col + 2] && player == board[row + 3][col + 3])
                    return Optional.of(player);
                if (row + 3 < Connect4.ROWS && col - 3 >= 0 && player == board[row + 1][col - 1]
                        && player == board[row + 2][col - 2] && player == board[row + 3][col - 3])
                    return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    public boolean isTerminal() {
        return winner().isPresent() || moves(player()).isEmpty();
    }

    public int[][] position() {
        return board;
    }

    public void render() {
        for (int[] row : board) {
            for (int cell : row) {
                char symbol = (cell == Connect4.PLAYER_ONE) ? 'O' : (cell == Connect4.PLAYER_TWO) ? 'X' : '.';
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private int[][] copyBoard() {
        int[][] newBoard = new int[Connect4.ROWS][Connect4.COLS];
        for (int i = 0; i < Connect4.ROWS; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, Connect4.COLS);
        }
        return newBoard;
    }
}