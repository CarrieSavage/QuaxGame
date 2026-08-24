package quax.ai;

import quax.model.board.AbstractCell;
import quax.model.board.Edge;
import quax.model.board.Octagon;
import quax.model.board.QuaxBoard;
import quax.model.enums.Colour;

import java.util.*;

public class PathFinder {

    private PathFinder() {}

    public static List<AbstractCell> findShortestPathTo(
            QuaxBoard board, AbstractCell start, Edge targetEdge) {

        if (board == null || start == null || targetEdge == null) {
            return Collections.emptyList();
        }

        Colour movingColour = start.getColour();
        Colour opponentColour = opponentOf(movingColour);

        Queue<AbstractCell> queue = new LinkedList<>();
        Map<AbstractCell, AbstractCell> cameFrom = new HashMap<>();

        queue.add(start);
        cameFrom.put(start, null);

        AbstractCell goal = null;

        while (!queue.isEmpty()) {
            AbstractCell current = queue.poll();

            if (current instanceof Octagon && targetEdge.isOnEdge(current)) {
                goal = current;
                break;
            }

            for (AbstractCell neighbour : board.getBorderingCells(current)) {
                if (neighbour.getColour() == opponentColour) {
                    continue;
                }

                if (!cameFrom.containsKey(neighbour)) {
                    cameFrom.put(neighbour, current);
                    queue.add(neighbour);
                }
            }
        }

        return reconstructPath(cameFrom, goal);
    }

    private static List<AbstractCell> reconstructPath(
            Map<AbstractCell, AbstractCell> cameFrom,
            AbstractCell goal) {

        if (goal == null) {
            return Collections.emptyList();
        }

        List<AbstractCell> path = new ArrayList<>();

        for (AbstractCell cell = goal; cell != null; cell = cameFrom.get(cell)) {
            path.add(cell);
        }

        Collections.reverse(path);
        return path;
    }

    public static Colour opponentOf(Colour colour) {
        return colour == Colour.BLACK ? Colour.WHITE : Colour.BLACK;
    }
}