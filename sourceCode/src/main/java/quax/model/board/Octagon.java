package quax.model.board;

import java.util.ArrayList;
import java.util.List;

public class Octagon extends AbstractCell {
    private static final int[][] BORDERING_OCTAGON_OFFSETS = {
            {0, -1},  // left
            {0, 1},   // right
            {-1, 0},  // above
            {1, 0}    // below
    };

    private static final int[][] BORDERING_RHOMBUS_OFFSETS = {
            {-1, 0},   // top-right
            {-1, -1},  // top-left
            {0, 0},    // bottom-right
            {0, -1}    // bottom-left
    };

    public Octagon(int row, int column) {
        super(row, column);
    }

    public static boolean isPositionLegal(int row, int column) {
        return row >= 0
                && row < QuaxBoard.OCTAGON_GRID_SIZE
                && column >= 0
                && column < QuaxBoard.OCTAGON_GRID_SIZE;
    }

    @Override
    public boolean borders(AbstractCell cell) {
        if (cell instanceof Octagon) {
            return bordersUsingOffsets(cell, BORDERING_OCTAGON_OFFSETS);
        }

        if (cell instanceof Rhombus) {
            return bordersUsingOffsets(cell, BORDERING_RHOMBUS_OFFSETS);
        }

        return false;
    }

    @Override
    public List<int[]> getBorderingOctagonsPositions() {
        return legalPositionsFromOffsets(
                BORDERING_OCTAGON_OFFSETS,
                Octagon::isPositionLegal
        );
    }

    @Override
    public List<int[]> getBorderingRhombusPositions() {
        return legalPositionsFromOffsets(
                BORDERING_RHOMBUS_OFFSETS,
                Rhombus::isPositionLegal
        );
    }

    @Override
    public String toString() {
        return "quax.model.board.Octagon:" + super.toString();
    }

    private boolean bordersUsingOffsets(AbstractCell cell, int[][] offsets) {
        for (int[] offset : offsets) {
            int otherRow = getRow() + offset[0];
            int otherColumn = getColumn() + offset[1];

            if (otherRow == cell.getRow() && otherColumn == cell.getColumn()) {
                return true;
            }
        }

        return false;
    }

    private List<int[]> legalPositionsFromOffsets(
            int[][] offsets,
            PositionValidator validator) {

        List<int[]> borderingCells = new ArrayList<>(offsets.length);

        for (int[] offset : offsets) {
            int row = getRow() + offset[0];
            int column = getColumn() + offset[1];

            if (validator.isLegal(row, column)) {
                borderingCells.add(new int[]{row, column});
            }
        }

        return borderingCells;
    }

    @FunctionalInterface
    private interface PositionValidator {
        boolean isLegal(int row, int column);
    }
}