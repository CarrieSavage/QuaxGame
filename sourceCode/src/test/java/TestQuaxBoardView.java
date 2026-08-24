import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.*;
import quax.QuaxController;
import quax.model.board.QuaxBoard;
import quax.ui.QuaxBoardView;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestQuaxBoardView extends JavaFXTestBase {

    private QuaxBoardView view;
    private QuaxBoard board;
    private QuaxController controller;

    private static void runFX(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            action.run();
            latch.countDown();
        });
        latch.await();
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        runFX(() -> {
            board = new QuaxBoard();
            controller = new QuaxController();
            view = new QuaxBoardView(board);
            view.setController(controller);
        });
    }

    @Test
    @Order(1)
    void testViewCreated() {
        assertNotNull(view, "quax.ui.QuaxBoardView should be constructed");
        assertNotNull(board, "Board should be initialized");
    }

    @Test
    @Order(2)
    void testOctagonsCreated() {
        assertEquals(11, view.getOctagons().length, "There should be 11 octagon rows");

        for (int r = 0; r < 11; r++) {
            for (int c = 0; c < 11; c++) {
                Polygon poly = view.getOctagons()[r][c];
                assertNotNull(poly, "quax.model.board.Octagon should exist at " + r + "," + c);
                assertNotNull(poly.getUserData(), "quax.model.board.Octagon userData should store logic quax.model.board.Octagon");
                assertEquals(8 * 2, poly.getPoints().size(), "quax.model.board.Octagon should have 8 vertices");
            }
        }
    }

    @Test
    @Order(3)
    void testRhombusesCreated() {
        assertEquals(10, view.getRhombuses().length, "There should be 10 rhombus rows");

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                Rectangle rect = view.getRhombuses()[r][c];
                assertNotNull(rect, "quax.model.board.Rhombus should exist at " + r + "," + c);
                assertNotNull(rect.getUserData(), "quax.model.board.Rhombus userData should store logic quax.model.board.Rhombus");
                assertEquals(13, rect.getWidth(), "quax.model.board.Rhombus width should be 13");
                assertEquals(13, rect.getHeight(), "quax.model.board.Rhombus height should be 13");
                assertEquals(45, rect.getRotate(), "quax.model.board.Rhombus should be rotated 45°");
            }
        }
    }

    @Test
    @Order(4)
    void testLabelsCreated() {
        long textCount = view.getChildren().stream().filter(n -> n instanceof Text).count();
        assertEquals(44, textCount, "There should be 44 coordinate labels");
    }

    @Test
    @Order(5)
    void testBackgroundRectanglesExist() {
        long rectCount = view.getChildren().stream().filter(n -> n instanceof Rectangle).count();
        assertEquals(105, rectCount, "There should be 5 background rectangles + 100 rhombuses");
    }

    @Test
    @Order(6)
    void testOctagonColors() {
        Polygon sample = view.getOctagons()[0][0];
        assertEquals(QuaxController.quaxLightRed, sample.getFill());
        assertEquals(QuaxController.quaxDarkRed, sample.getStroke());
        assertEquals(1, sample.getStrokeWidth());
    }

    @Test
    @Order(7)
    void testRhombusColors() {
        Rectangle sample = view.getRhombuses()[0][0];
        assertEquals(QuaxController.quaxRed, sample.getFill());
        assertEquals(QuaxController.quaxDarkRed, sample.getStroke());
        assertEquals(1, sample.getStrokeWidth());
    }

    @Test
    @Order(8)
    void testChildrenCountRoughEstimate() {
        int children = view.getChildren().size();
        assertTrue(children > 250 && children < 290,
                "Children count should be around 270; actual = " + children);
    }
}
