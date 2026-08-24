package quax.model.board;

import quax.model.enums.Colour;

public class Edge {
    private static final int ROW_AXIS = 0;
    private static final int COLUMN_AXIS = 1;

    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 10;

    public static final Edge TOP = new Edge(Colour.BLACK, ROW_AXIS, MIN_COORDINATE);
    public static final Edge BOTTOM = new Edge(Colour.BLACK, ROW_AXIS, MAX_COORDINATE);
    public static final Edge LEFT = new Edge(Colour.WHITE, COLUMN_AXIS, MIN_COORDINATE);
    public static final Edge RIGHT = new Edge(Colour.WHITE, COLUMN_AXIS, MAX_COORDINATE);

    private final Colour colour;
    private final int axis;
    private final int coordinate;

    private Edge(Colour colour, int axis, int coordinate) {
        this.colour = colour;
        this.axis = axis;
        this.coordinate = coordinate;
    }

    public boolean isOnEdge(AbstractCell cell) {
        return cell.getPosition()[axis] == coordinate;
    }

    public int getCoordinate() {
        return coordinate;
    }

    public int distanceTo(AbstractCell cell) {
        return Math.abs(cell.getPosition()[axis] - coordinate);
    }

    public Colour getColour() {
        return colour;
    }

    public Edge getOppositeEdge() {
        Edge[] targetEdges = getTargetEdges(colour);

        if (targetEdges[0] == this) {
            return targetEdges[1];
        }

        return targetEdges[0];
    }

    public static Edge[] getTargetEdges(Colour colour) {
        if (colour == Colour.WHITE) {
            return new Edge[]{LEFT, RIGHT};
        }

        return new Edge[]{TOP, BOTTOM};
    }
}