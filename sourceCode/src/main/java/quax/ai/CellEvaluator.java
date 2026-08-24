package quax.ai;

import quax.model.board.AbstractCell;
import quax.model.board.Edge;
import quax.model.board.QuaxBoard;
import quax.model.chain.AbstractBlockChain;
import quax.model.chain.ChainState;
import quax.model.chain.ChainUtils;
import quax.model.enums.Colour;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CellEvaluator {
    private final QuaxBoard board;
    private final Colour botColour;
    private final ChainState state;
    private final Edge[] targetEdges;

    public CellEvaluator(QuaxBoard board, Colour botColour, ChainState state) {
        this.board = board;
        this.botColour = botColour;
        this.state = state;
        this.targetEdges = Edge.getTargetEdges(botColour);
    }

    public int totalScore(AbstractCell cell, AbstractBlockChain chain) {
        int connective = evaluateConnectiveScore(cell);
        int positional = evaluatePositionalScore(cell);
        int directional = evaluateDirectional(cell, chain);
        int path = evaluateShortestPathScore(cell, chain);

        return connective + positional + directional + path;
    }

    /*
     * Winning checks need the whole board state, not only the chain that produced
     * the candidate.
     */
    private int evaluateConnectiveScore(AbstractCell cell) {
        boolean[] connectsToEdgeChain = new boolean[2];

        connectsToEdgeChain[0] =
                cell.connectsToAny(ChainUtils.chainsTouchingEdge(state.getChains(), targetEdges[0]));
        connectsToEdgeChain[1] =
                cell.connectsToAny(ChainUtils.chainsTouchingEdge(state.getChains(), targetEdges[1]));

        if (isWinningPlacement(connectsToEdgeChain, cell)) {
            return ScoreWeights.WIN_MOVE;
        }

        if ((cell.isOn(targetEdges[0]) && !state.touches(targetEdges[0]))
                || (cell.isOn(targetEdges[1]) && !state.touches(targetEdges[1]))) {
            return ScoreWeights.CONNECT_NEW_EDGE;
        }

        int chainHits = 0;

        for (boolean hit : ChainUtils.connectsChains(state.getChains(), cell)) {
            if (hit) {
                chainHits++;
            }
        }

        return chainHits * ScoreWeights.CONNECT_CHAIN;
    }

    /*
     * Direction is chain-local: a chain touching TOP should mainly care about
     * reaching BOTTOM, not both target edges equally.
     */
    private int evaluateDirectional(AbstractCell cell, AbstractBlockChain chain) {
        int score = 0;

        for (Edge edge : targetEdges) {
            if (!chain.touches(edge)) {
                int distance = edge.distanceTo(cell);
                score += (ScoreWeights.MAX_LINEAR_DISTANCE - distance)
                        * ScoreWeights.DIRECTIONAL_MULTIPLIER;
            }
        }

        return score;
    }

    public int evaluatePositionalScore(AbstractCell cell) {
        int[] position = cell.getPosition();

        return (ScoreWeights.CENTER - Math.abs(position[0] - 5))
                + (ScoreWeights.CENTER - Math.abs(position[1] - 5));
    }

    private int evaluateShortestPathScore(AbstractCell cell, AbstractBlockChain chain) {
        List<Edge> neededEdges = Arrays.stream(targetEdges)
                .filter(edge -> !chain.touches(edge))
                .collect(Collectors.toList());

        if (neededEdges.isEmpty()) {
            return 0;
        }

        Edge closestNeededEdge = neededEdges.stream()
                .min(Comparator.comparingInt(edge -> edge.distanceTo(cell)))
                .get();

        List<AbstractCell> path = PathFinder.findShortestPathTo(board, cell, closestNeededEdge);

        return -path.size() * ScoreWeights.SHORTEST_PATH_MULTIPLIER;
    }

    private static boolean isWinningPlacement(
            boolean[] connectsToEdgeChain,
            AbstractCell cell) {

        Edge[] targetEdges = Edge.getTargetEdges(cell.getColour());

        return (connectsToEdgeChain[0] && connectsToEdgeChain[1])
                || (connectsToEdgeChain[0] && cell.isOn(targetEdges[1]))
                || (connectsToEdgeChain[1] && cell.isOn(targetEdges[0]));
    }
}