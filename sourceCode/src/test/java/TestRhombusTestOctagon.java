import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TestRhombusTestOctagon {
    @Test
    void testNoRhombusBorder() {
        quax.model.board.Rhombus r1 = new quax.model.board.Rhombus(5, 5);
        quax.model.board.Rhombus r2 = new quax.model.board.Rhombus(6, 6);

        assertFalse(r1.borders(r2), "Two rhombi should never border");
    }

    @Test
    void testTopRightBorder() {
        quax.model.board.Rhombus r = new quax.model.board.Rhombus(5, 5);
        quax.model.board.Octagon o = new quax.model.board.Octagon(6, 6);

        assertTrue(r.borders(o), "quax.model.board.Rhombus borders the top right border");
    }

    @Test
    void testTopLeftBorder() {
        quax.model.board.Rhombus r = new quax.model.board.Rhombus(5, 5);
        quax.model.board.Octagon o = new quax.model.board.Octagon(6, 5);

        assertTrue(r.borders(o), "quax.model.board.Rhombus borders the top left border");
    }

    @Test
    void testBottomRightBorder() {
        quax.model.board.Rhombus r = new quax.model.board.Rhombus(5, 5);
        quax.model.board.Octagon o = new quax.model.board.Octagon(5, 6);

        assertTrue(r.borders(o), "quax.model.board.Rhombus borders the bottom right border");
    }

    @Test
    void testBottomLeftBorder() {
        quax.model.board.Rhombus r = new quax.model.board.Rhombus(5, 5);
        quax.model.board.Octagon o = new quax.model.board.Octagon(5, 5);

        assertTrue(r.borders(o), "quax.model.board.Rhombus borders the bottom left border");
    }

    @Test
    void testGapInBorder() {
        quax.model.board.Rhombus r = new quax.model.board.Rhombus(5, 5);
        quax.model.board.Octagon o = new quax.model.board.Octagon(7, 7);

        assertFalse(r.borders(o), "quax.model.board.Octagon and quax.model.board.Rhombus too far away");
    }

    @Test
    void testWrongColumn() {
        quax.model.board.Rhombus r = new quax.model.board.Rhombus(5, 5);
        quax.model.board.Octagon o = new quax.model.board.Octagon(5, 8);

        assertFalse(r.borders(o), "quax.model.board.Octagon and quax.model.board.Rhombus should not boarder");
    }

    @Test
    void testWrongRow() {
        quax.model.board.Rhombus r = new quax.model.board.Rhombus(5, 5);
        quax.model.board.Octagon o = new quax.model.board.Octagon(9, 5);

        assertFalse(r.borders(o), "quax.model.board.Octagon and quax.model.board.Rhombus should not boarder");
    }

    @Test
    void testNullInput() {
        quax.model.board.Rhombus r = new quax.model.board.Rhombus(5, 5);

        assertDoesNotThrow(() -> r.borders(null), "Method should not throw null");
        assertFalse(r.borders(null), "Null should not be considered");
    }

    @Test
    void testOctagonBordersLeft() {
        quax.model.board.Octagon o1 = new quax.model.board.Octagon(5, 5);
        quax.model.board.Octagon o2 = new quax.model.board.Octagon(5, 4);

        assertTrue(o1.borders(o2));
    }

    @Test
    void testOctagonBordersRight() {
        quax.model.board.Octagon o1 = new quax.model.board.Octagon(5, 5);
        quax.model.board.Octagon o2 = new quax.model.board.Octagon(5, 6);

        assertTrue(o1.borders(o2));
    }

    @Test
    void testOctagonBordersAbove() {
        quax.model.board.Octagon o1 = new quax.model.board.Octagon(5, 5);
        quax.model.board.Octagon o2 = new quax.model.board.Octagon(6, 5);

        assertTrue(o1.borders(o2));
    }

    @Test
    void testOctagonBordersBelow() {
        quax.model.board.Octagon o1 = new quax.model.board.Octagon(5, 5);
        quax.model.board.Octagon o2 = new quax.model.board.Octagon(4, 5);

        assertTrue(o1.borders(o2));
    }
}

