package quax.ai;

import quax.model.board.AbstractCell;
import quax.model.board.Edge;
import quax.model.board.QuaxBoard;
import quax.model.chain.AbstractBlockChain;
import quax.model.chain.ChainState;
import quax.model.chain.ChainUtils;
import quax.model.enums.Colour;

import java.util.*;

public class BlockingEvaluator {
    private static final int CHAIN_CONNECTION_BONUS = 25;

    private final QuaxBoard board;
    private final Colour botColour;
    private final ChainState opponentState;

    public BlockingEvaluator(QuaxBoard board, Colour botColour, ChainState opponentState) {
        this.board = board;
        this.botColour = botColour;
        this.opponentState = opponentState;
    }

    public List<AbstractCell> evaluateBlockingCells() {
        List<AbstractCell> candidates = new ArrayList<>();

        for (AbstractBlockChain opponentChain : opponentState.getChains()) {
            tryBlockChainTowardEdges(opponentChain, candidates);
        }

        return candidates;
    }

    /*
     * Tests whether the opponent would win by placing on this cell.
     */
    public boolean opponentWinDetection(AbstractCell cell) {
        Edge[] targetEdges = Edge.getTargetEdges(botColour.opponentColour());

        boolean[] connects = new boolean[2];
        connects[0] = cell.connectsToAny(
                ChainUtils.chainsTouchingEdge(opponentState.getChains(), targetEdges[0])
        );
        connects[1] = cell.connectsToAny(
                ChainUtils.chainsTouchingEdge(opponentState.getChains(), targetEdges[1])
        );

        return (connects[0] && connects[1])
                || (connects[0] && cell.isOn(targetEdges[1]))
                || (connects[1] && cell.isOn(targetEdges[0]));
    }

    private class BlockContext {
        final AbstractBlockChain chain;
        final Edge targetEdge;
        final AbstractCell chainEnd;
        final List<AbstractCell> shortestPath;

        BlockContext(AbstractBlockChain chain, Edge targetEdge) {
            this.chain = chain;
            this.targetEdge = targetEdge;
            this.chainEnd = ChainUtils.closestCellToEdgeInChain(chain, targetEdge);
            this.shortestPath = PathFinder.findShortestPathTo(board, chainEnd, targetEdge);
        }
    }

    private void tryBlockChainTowardEdges(
            AbstractBlockChain opponentChain,
            List<AbstractCell> candidates) {

        for (Edge targetEdge : Edge.getTargetEdges(opponentChain.getColour())) {
            if (opponentChain.touches(targetEdge)) {
                continue;
            }

            BlockContext context = new BlockContext(opponentChain, targetEdge);

            if (context.chainEnd == null) {
                continue;
            }

            scoreBlockCandidates(context, candidates);
        }
    }

    private void scoreBlockCandidates(
            BlockContext context,
            List<AbstractCell> candidates) {

        List<AbstractCell> blockCandidates = getBlockCandidates(opponentState);

        for (AbstractCell blockCell : blockCandidates) {
            scoreBlockCandidate(blockCell, context);
            candidates.add(blockCell);
        }
    }

    private void scoreBlockCandidate(AbstractCell blockCell, BlockContext context) {
        if (opponentWinDetection(blockCell)) {
            blockCell.setBlockScore(ScoreWeights.BLOCK_WIN);
            return;
        }

        blockCell.setBlockScore(computeBlockScore(blockCell, context));
    }

    private int computeBlockScore(AbstractCell blockCell, BlockContext context) {
        int chainSize = context.chain.getChain().size();
        boolean chainTouchesOtherEdge =
                context.chain.touches(context.targetEdge.getOppositeEdge());

        blockCell.setColour(botColour);
        List<AbstractCell> blockedPath =
                PathFinder.findShortestPathTo(board, context.chainEnd, context.targetEdge);
        blockCell.setColour(Colour.UNOCCUPIED);

        int pathLengthScore =
                (ScoreWeights.SHORT_PATH_BENEFIT - context.shortestPath.size())
                        * ScoreWeights.BLOCK_PATH_IMPORTANCE
                        * chainSize;

        int disruptionScore =
                (blockedPath.size() - context.shortestPath.size())
                        * ScoreWeights.BLOCK_PATH_IMPORTANCE
                        * chainSize;

        int edgeBonus = chainTouchesOtherEdge
                ? ScoreWeights.CHAINS_TOUCHES_OTHER_EDGE
                : 0;

        int chainConnections = countOpponentChainConnections(blockCell);

        return Math.floorDiv(
                pathLengthScore + disruptionScore + edgeBonus + chainConnections,
                2
        );
    }

    private int countOpponentChainConnections(AbstractCell cell) {
        int score = 0;

        for (boolean hit : ChainUtils.connectsChains(opponentState.getChains(), cell)) {
            if (hit) {
                score += CHAIN_CONNECTION_BONUS;
            }
        }

        return score;
    }

    private int candidateLimit(int totalCandidates) {
        if (totalCandidates <= 4) {
            return totalCandidates;
        }

        return Math.min(10, Math.max(4, totalCandidates / 2));
    }

    /*
     * For each opponent chain, prefer cells closest to the edge that chain still
     * needs. This avoids wasting blocking moves near an edge the chain already has.
     */
    private List<AbstractCell> getBlockCandidates(ChainState opponentState) {
        Set<AbstractCell> uniqueCandidates = new HashSet<>();
        Map<AbstractCell, Integer> bestDistance = new HashMap<>();

        for (AbstractBlockChain chain : opponentState.getChains()) {

            for (AbstractCell cell : board.getUnoccupiedBorderingCells(chain)) {
                uniqueCandidates.add(cell);

                int distance = distanceToNeededEdge(cell, chain);
                bestDistance.merge(cell, distance, Math::min);
            }
        }

        List<AbstractCell> candidates = new ArrayList<>(uniqueCandidates);
        candidates.sort(Comparator.comparingInt(bestDistance::get));

        return candidates.subList(0, candidateLimit(candidates.size()));
    }

    private int distanceToNeededEdge(
            AbstractCell cell,
            AbstractBlockChain chain) {
        Edge[] opponentEdges = Edge.getTargetEdges(botColour.opponentColour());

        if (chain.touches(opponentEdges[0]) && !chain.touches(opponentEdges[1])) {
            return opponentEdges[1].distanceTo(cell);
        }

        if (chain.touches(opponentEdges[1]) && !chain.touches(opponentEdges[0])) {
            return opponentEdges[0].distanceTo(cell);
        }

        return Math.min(opponentEdges[0].distanceTo(cell),
                        opponentEdges[1].distanceTo(cell));
    }
}