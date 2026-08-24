package quax.model.chain;

import quax.model.board.AbstractCell;
import quax.model.board.Edge;
import quax.model.enums.Colour;
import quax.model.exception.ColourMismatchException;

import java.util.LinkedHashSet;

public abstract class AbstractBlockChain {
    private static final int FIRST_TARGET_EDGE = 0;
    private static final int SECOND_TARGET_EDGE = 1;

    protected final LinkedHashSet<AbstractCell> chain;
    protected final Colour chainColour;

    protected boolean win;
    protected boolean[] touchesEdge = new boolean[2];

    public AbstractBlockChain(Colour colour) {
        this.chain = new LinkedHashSet<>();
        this.chainColour = colour;
        this.win = false;
    }

    public void addCell(AbstractCell cell) {
        if (cell.getColour() != chainColour) {
            return;
        }

        if (chain.isEmpty() || connects(cell)) {
            chain.add(cell);
            updateTouchesEdge(cell);
            updateWin();
        }
    }

    public boolean connects(AbstractCell cell) {
        for (AbstractCell chainCell : chain) {
            if (chainCell.borders(cell)) {
                return true;
            }
        }

        return false;
    }

    private void updateTouchesEdge(AbstractCell cell) {
        Edge[] targetEdges = Edge.getTargetEdges(chainColour);

        if (!touchesEdge[FIRST_TARGET_EDGE]) {
            touchesEdge[FIRST_TARGET_EDGE] = cell.isOn(targetEdges[FIRST_TARGET_EDGE]);
        }

        if (!touchesEdge[SECOND_TARGET_EDGE]) {
            touchesEdge[SECOND_TARGET_EDGE] = cell.isOn(targetEdges[SECOND_TARGET_EDGE]);
        }
    }

    private void updateWin() {
        win = touchesEdge[FIRST_TARGET_EDGE] && touchesEdge[SECOND_TARGET_EDGE];
    }

    public boolean getWin() {
        return win;
    }

    public Colour getColour() {
        return chainColour;
    }

    /*
     * Caller is responsible for checking that the two chains are adjacent.
     */
    public static void mergeInto(AbstractBlockChain target, AbstractBlockChain source) {
        if (target.getColour() != source.getColour()) {
            throw new ColourMismatchException("Only chains of same colour can be merged");
        }

        target.chain.addAll(source.chain);

        target.touchesEdge[FIRST_TARGET_EDGE] =
                target.touchesEdge[FIRST_TARGET_EDGE] || source.touchesEdge[FIRST_TARGET_EDGE];

        target.touchesEdge[SECOND_TARGET_EDGE] =
                target.touchesEdge[SECOND_TARGET_EDGE] || source.touchesEdge[SECOND_TARGET_EDGE];

        target.updateWin();
    }

    public abstract String toString();

    public LinkedHashSet<AbstractCell> getChain() {
        return chain;
    }

    public boolean touches(Edge edge) {
        if (edge.getColour() != chainColour) {
            return false;
        }

        return touchesEdge[indexOf(edge)];
    }

    public boolean isEmpty() {
        return chain.isEmpty();
    }

    private static int indexOf(Edge edge) {
        return edge.getCoordinate() == 0
                ? FIRST_TARGET_EDGE
                : SECOND_TARGET_EDGE;
    }
}