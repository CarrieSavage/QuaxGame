package quax.model.chain;

import quax.model.board.Edge;
import quax.model.board.QuaxBoard;
import quax.model.enums.Colour;

import java.util.ArrayList;
import java.util.List;

public class ChainState {
    private static final int FIRST_TARGET_EDGE = 0;
    private static final int SECOND_TARGET_EDGE = 1;

    private final Colour colour;
    private final List<AbstractBlockChain> chains;
    private final boolean[] touchesEdge;

    private ChainState(
            Colour colour,
            List<AbstractBlockChain> chains,
            boolean[] touchesEdge) {

        this.colour = colour;
        this.chains = chains;
        this.touchesEdge = touchesEdge;
    }

    public static ChainState from(QuaxBoard board, Colour colour) {
        List<AbstractBlockChain> chains = new ArrayList<>();
        boolean[] touchesEdge = new boolean[2];

        Edge[] targetEdges = Edge.getTargetEdges(colour);

        for (AbstractBlockChain chain : board.getBlockChains()) {
            if (chain.getColour() != colour) {
                continue;
            }

            chains.add(chain);

            if (chain.touches(targetEdges[FIRST_TARGET_EDGE])) {
                touchesEdge[FIRST_TARGET_EDGE] = true;
            }

            if (chain.touches(targetEdges[SECOND_TARGET_EDGE])) {
                touchesEdge[SECOND_TARGET_EDGE] = true;
            }
        }

        return new ChainState(colour, chains, touchesEdge);
    }

    public List<AbstractBlockChain> getChains() {
        return chains;
    }

    public boolean touches(Edge edge) {
        if (edge.getColour() != colour) {
            return false;
        }

        return touchesEdge[indexOf(edge)];
    }

    private static int indexOf(Edge edge) {
        return edge.getCoordinate() == 0
                ? FIRST_TARGET_EDGE
                : SECOND_TARGET_EDGE;
    }
}