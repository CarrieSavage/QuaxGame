package quax.model.chain;

import quax.model.board.Edge;
import quax.model.enums.Colour;

public final class BlackBlockChain extends AbstractBlockChain {

    public BlackBlockChain() {
        super(Colour.BLACK);
    }

    @Override
    public String toString() {
        return "Black chain: " + chain
                + " touch top edge: " + touches(Edge.TOP)
                + ", touch bottom edge: " + touches(Edge.BOTTOM)
                + ", win: " + win;
    }
}