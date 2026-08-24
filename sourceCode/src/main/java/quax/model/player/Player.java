package quax.model.player;

import quax.model.board.Edge;
import quax.model.board.QuaxBoard;
import quax.model.chain.ChainState;
import quax.model.enums.Colour;

abstract public class Player {
    protected final Colour colour;
    protected final Edge[] edges;
    protected final QuaxBoard board;
    protected ChainState chainState;

    Player (Colour colour, QuaxBoard board) {
        this.colour = colour;
        this.board = board;
        edges = Edge.getTargetEdges(colour);
        refreshState();
    }

    public void refreshState() {
        this.chainState = ChainState.from(board, colour);
    }
}
