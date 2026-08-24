package quax.model.chain;

import quax.model.board.Edge;
import quax.model.enums.Colour;

public final class WhiteBlockChain extends AbstractBlockChain {

    public WhiteBlockChain() {
        super(Colour.WHITE);
    }

    @Override
    public String toString() {
        return "White chain: " + chain
                + " touch left edge: " + touches(Edge.LEFT)
                + ", touch right edge: " + touches(Edge.RIGHT)
                + ", win: " + win;
    }
}