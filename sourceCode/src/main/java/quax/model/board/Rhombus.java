package quax.model.board;

import java.util.ArrayList;
import java.util.List;

public class Rhombus extends AbstractCell {
    private static final int[][] BORDERING_OCTAGON_OFFSETS = {
            {0, 1},  // top-right
            {0, 0},  // top-left
            {1, 1},  // bottom-right
            {1, 0}   // bottom-left
    };

    public Rhombus(int x, int y) {
        super(x, y);
    }

    public static boolean isPositionLegal(int row, int col) {
        return row >= 0
                && row < QuaxBoard.RHOMBUS_GRID_SIZE
                && col >= 0
                && col < QuaxBoard.RHOMBUS_GRID_SIZE;
    }

    @Override
    public boolean borders(AbstractCell cell) {
        if (!(cell instanceof Octagon)) {
            return false;
        }

        int row = getRow();
        int col = getColumn();

        int otherRow = cell.getRow();
        int otherCol = cell.getColumn();

        for (int[] offset : BORDERING_OCTAGON_OFFSETS) {
            if (row + offset[0] == otherRow && col + offset[1] == otherCol) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<int[]> getBorderingOctagonsPositions() {
        List<int[]> borderingCells = new ArrayList<>(4);

        int row = getRow();
        int col = getColumn();

        for (int[] offset : BORDERING_OCTAGON_OFFSETS) {
            int otherRow = row + offset[0];
            int otherCol = col + offset[1];

            if (Octagon.isPositionLegal(otherRow, otherCol)) {
                borderingCells.add(new int[]{otherRow, otherCol});
            }
        }

        return borderingCells;
    }

    @Override
    public List<int[]> getBorderingRhombusPositions() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "quax.model.board.Rhombus:" + super.toString();
    }
}