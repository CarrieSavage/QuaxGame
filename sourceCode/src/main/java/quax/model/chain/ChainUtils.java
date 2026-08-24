package quax.model.chain;

import quax.model.board.AbstractCell;
import quax.model.board.Edge;
import quax.model.board.Octagon;
import quax.model.exception.EmptyChainException;

import java.util.*;

/**
 * Captures a snapshot of the board's chain state from one player's perspective.
 * Rebuilt each turn before move evaluation — never mutated mid-turn.
 */
public class ChainUtils {

    private ChainUtils() {}

    public static List<AbstractBlockChain> chainsTouchingEdge(List<AbstractBlockChain> chains, Edge edge) {
        List<AbstractBlockChain> result = new ArrayList<>(chains.size());
        for (AbstractBlockChain chain : chains) {
            if (chain.touches(edge)) result.add(chain);
        }
        return result;
    }

    /**
     * Returns a boolean array indicating which of {@code chains} the given cell connects to.
     * Index i is true if {@code cell} connects to {@code chains.get(i)}.
     */
    public static boolean[] connectsChains(List<AbstractBlockChain> chains, AbstractCell cell) {
        boolean[] hits = new boolean[chains.size()];
        for (int i = 0; i < chains.size(); i++) {
            hits[i] = chains.get(i).connects(cell);
        }
        return hits;
    }

    /**
     * Returns the cell within {@code chain} that is closest to {@code edge},
     * preferring octagons when distances tie.
     */
    public static AbstractCell closestCellToEdgeInChain(AbstractBlockChain chain, Edge edge) {
        if (chain.isEmpty()) throw new EmptyChainException();

        AbstractCell closest = new Octagon(100, 100);
        for (AbstractCell current : chain.getChain()) {
            int closestDist = edge.distanceTo(closest);
            int currentDist = edge.distanceTo(current);
            if (currentDist < closestDist
                    || (currentDist == closestDist && current instanceof Octagon)) {
                closest = current;
            }
        }
        return closest;
    }
}
