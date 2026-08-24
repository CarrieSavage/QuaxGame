package quax.model.board;

import quax.model.enums.Colour;
import quax.model.enums.PlayerTurn;
import quax.model.chain.AbstractBlockChain;

import java.util.List;

abstract public class AbstractCell {
    private final int[] position;
    protected Colour colour;

    private int connectScore;
    private int blockScore;

    public AbstractCell(int x, int y) {
        this.position = new int[]{x, y};
        this.colour = Colour.UNOCCUPIED;
        this.connectScore = 0;
        this.blockScore = 0;
    }

    public abstract boolean borders(AbstractCell cell);

    /*
     * Applies a move if the cell is empty and toggles the turn.
     */
    public void playerClick(QuaxBoard board) {
        if (colour != Colour.UNOCCUPIED) {
            return;
        }

        if (board.getPlayerTurn() == PlayerTurn.BLACK) {
            setColour(Colour.BLACK);
            board.setPlayerTurn(PlayerTurn.WHITE);
        } else {
            setColour(Colour.WHITE);
            board.setPlayerTurn(PlayerTurn.BLACK);
        }
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public Colour getColour() {
        return colour;
    }

    public int getConnectScore() {
        return connectScore;
    }

    public void setConnectScore(int connectScore) {
        this.connectScore = connectScore;
    }

    public int getBlockScore() {
        return blockScore;
    }

    public void setBlockScore(int blockScore) {
        this.blockScore = blockScore;
    }

    public int[] getPosition() {
        return position;
    }

    public int getRow() {
        return position[0];
    }

    public int getColumn() {
        return position[1];
    }

    public abstract List<int[]> getBorderingOctagonsPositions();

    public abstract List<int[]> getBorderingRhombusPositions();

    /*
     * Only octagons can lie on edges.
     */
    public boolean isOn(Edge edge) {
        if (this instanceof Rhombus) {
            return false;
        }
        return edge.isOnEdge(this);
    }

    /*
     * Used in heuristics: checks if this cell would connect to any given chain.
     */
    public boolean connectsToAny(List<AbstractBlockChain> chains) {
        return chains.stream().anyMatch(chain -> chain.connects(this));
    }

    @Override
    public String toString() {
        return colour + "{(" + position[0] + ", " + position[1] + ")}";
    }
}