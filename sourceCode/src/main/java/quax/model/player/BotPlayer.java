package quax.model.player;

import quax.ai.BlockingEvaluator;
import quax.model.board.AbstractCell;
import quax.model.board.QuaxBoard;
import quax.model.chain.AbstractBlockChain;
import quax.model.chain.ChainState;
import quax.model.enums.Colour;
import quax.ai.CellEvaluator;

import java.util.*;

public class BotPlayer extends Player {
    private ChainState ownState;
    private ChainState opponentState;

    public BotPlayer(Colour colour, QuaxBoard board) {
        super(colour, board);
        ownState = super.chainState;
        opponentState = ChainState.from(board, colour.opponentColour());
    }

    public PriorityQueue<AbstractCell> getMove() {
        resetCellScores();
        updateStates();

        CellEvaluator cellEvaluator = new CellEvaluator(board, colour, ownState);
        BlockingEvaluator blockingEvaluator =
                new BlockingEvaluator(board, colour, opponentState);

        List<AbstractCell> blockCandidates = blockingEvaluator.evaluateBlockingCells();
        List<AbstractCell> connectCandidates = scoreConnectCandidatesByChain(cellEvaluator);

        if (connectCandidates.isEmpty() && blockCandidates.isEmpty()) {
            return positionalFallback(cellEvaluator);
        }

        return buildPriorityQueue(connectCandidates, blockCandidates);
    }

    public Colour getColour() {
        return colour;
    }

    private void updateStates() {
        ownState = ChainState.from(board, colour);
        opponentState = ChainState.from(board, colour.opponentColour());
    }

    /*
     * Scores each candidate in the context of the specific chain it extends.
     * If a cell borders multiple chains, keep its strongest connection score.
     */
    private List<AbstractCell> scoreConnectCandidatesByChain(CellEvaluator cellEvaluator) {
        List<AbstractCell> candidates = new ArrayList<>();

        for (AbstractBlockChain chain : ownState.getChains()) {
            for (AbstractCell cell : board.getUnoccupiedBorderingCells(chain)) {
                int score = cellEvaluator.totalScore(cell, chain);

                if (score > cell.getConnectScore()) {
                    if (cell.getConnectScore() == 0) {
                        candidates.add(cell);
                    }

                    cell.setConnectScore(score);
                }
            }
        }

        return candidates;
    }

    private PriorityQueue<AbstractCell> buildPriorityQueue(
            List<AbstractCell> connectCandidates,
            List<AbstractCell> blockCandidates) {

        Set<AbstractCell> candidates =
                new HashSet<>(connectCandidates.size() + blockCandidates.size());

        candidates.addAll(connectCandidates);
        candidates.addAll(blockCandidates);

        PriorityQueue<AbstractCell> moves = createMaxPriorityQueue();
        moves.addAll(candidates);

        return moves;
    }

    private PriorityQueue<AbstractCell> positionalFallback(CellEvaluator cellEvaluator) {
        AbstractCell best = null;

        for (int i = 0; i < QuaxBoard.OCTAGON_GRID_SIZE; i++) {
            for (int j = 0; j < QuaxBoard.OCTAGON_GRID_SIZE; j++) {
                AbstractCell cell = board.getOctagon(i, j);

                if (cell.getColour() != Colour.UNOCCUPIED) {
                    continue;
                }

                cell.setConnectScore(cellEvaluator.evaluatePositionalScore(cell));

                if (best == null || cell.getConnectScore() > best.getConnectScore()) {
                    best = cell;
                }
            }
        }

        PriorityQueue<AbstractCell> moves = createMaxPriorityQueue();

        if (best != null) {
            moves.add(best);
        }

        return moves;
    }

    /*
     * A move that is both good for connecting and blocking should outrank a move
     * that is strong in only one of those areas.
     */
    private static PriorityQueue<AbstractCell> createMaxPriorityQueue() {
        return new PriorityQueue<>(
                Comparator.comparingInt((AbstractCell cell) -> {
                    int max = Math.max(cell.getConnectScore(), cell.getBlockScore());
                    int min = Math.min(cell.getConnectScore(), cell.getBlockScore());

                    return max * 2 + min;
                }).reversed()
        );
    }

    private void resetCellScores() {
        for (int i = 0; i < QuaxBoard.OCTAGON_GRID_SIZE; i++) {
            for (int j = 0; j < QuaxBoard.OCTAGON_GRID_SIZE; j++) {
                board.getOctagon(i, j).setBlockScore(0);
                board.getOctagon(i, j).setConnectScore(0);

                if (i < QuaxBoard.RHOMBUS_GRID_SIZE && j < QuaxBoard.RHOMBUS_GRID_SIZE) {
                    board.getRhombus(i, j).setBlockScore(0);
                    board.getRhombus(i, j).setConnectScore(0);
                }
            }
        }
    }
}