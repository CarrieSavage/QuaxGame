import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import quax.model.board.AbstractCell;
import quax.model.board.QuaxBoard;
import quax.ui.QuaxBoardView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestStrategy {
    private QuaxBoard board;
    private QuaxBoardView view;

    @BeforeEach
    public void setUp() {
        board = new QuaxBoard();
        view = new QuaxBoardView(board);
    }

    @Test
    void testOverlayStrokeHighlight(){
        AbstractCell cell = board.getOctagon(0,0);
        view.showStrategyOverlay(List.of(cell));

        Shape shape = (Shape) view.getChildren().stream().filter(n -> n instanceof Shape).filter(n -> cell.equals(((Shape) n).getUserData())).findFirst().orElse(null);
        assertNotNull(shape);
        assertEquals(4.0, shape.getStrokeWidth());
    }

    @Test
    void testOverlayCorrectNumbers(){
        List<AbstractCell> cells =  List.of(board.getOctagon(0,0), board.getOctagon(0,1), board.getOctagon(0,1), board.getOctagon(0,2));

        view.showStrategyOverlay(cells);
        List<String> numbers = view.getChildren().stream().filter(n -> n instanceof Text).map(n -> ((Text) n).getText()).filter(t -> t.matches("[1-3]")).collect(java.util.stream.Collectors.toList());

        assertTrue(numbers.contains("1"));
        assertTrue(numbers.contains("2"));
        assertTrue(numbers.contains("3"));
    }

    @Test
    void testClearOverlayRemovesLabels(){
        view.showStrategyOverlay(List.of(board.getOctagon(0,0)));
        long before = view.getChildren().stream().filter(n -> n instanceof Text).count();

        view.clearStrategyOverlay();

        long after = view.getChildren().stream().filter(n -> n instanceof Text).count();
        assertTrue(after < before);
    }

    @Test
    void testClearOverlayRestoresStroke(){
        AbstractCell cell = board.getOctagon(0,0);
        view.showStrategyOverlay(List.of(cell));
        view.clearStrategyOverlay();

        Shape shape = (Shape) view.getChildren().stream().filter(n -> n instanceof Shape).filter(n -> cell.equals(((Shape) n).getUserData())).findFirst().orElse(null);
        assertNotNull(shape);
        assertEquals(1.0, shape.getStrokeWidth());
    }

    @Test
    void testEmptyListDoesNothing(){
        long  before = view.getChildren().stream().filter(n -> n instanceof Text).count();
        view.showStrategyOverlay(List.of());
        long after = view.getChildren().stream().filter(n -> n instanceof Text).count();
        assertEquals(before, after);
    }

    @Test
    void testNullDoesNothing(){
        long before = view.getChildren().stream().filter(n -> n instanceof Text).count();
        view.showStrategyOverlay(null);
        long after = view.getChildren().stream().filter(n -> n instanceof Text).count();
        assertEquals(before,after);
    }

}
