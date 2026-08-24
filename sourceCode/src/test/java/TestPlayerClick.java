import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.*;
import quax.QuaxController;
import quax.model.board.AbstractCell;
import quax.model.enums.Colour;
import quax.model.enums.PlayerTurn;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestPlayerClick extends JavaFXTestBase {

    private QuaxController controller;

    private static void runFX(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @BeforeEach
    public void setUp() throws InterruptedException {
        runFX(() -> {
            controller = new QuaxController();

            controller.boardContainer = new StackPane();
            controller.statusLabel = new Label();
            controller.labelPolygon = new Polygon();
            controller.labelRhombus = new Rectangle();

            controller.initialize();
        });
    }

    private Shape getOctagon(int row, int col) {
        return controller.boardView.getOctagons()[row][col];
    }

    private Shape getRhombus(int row, int col) {
        return controller.boardView.getRhombuses()[row][col];
    }

    private void emulateClick(Shape visualCell) throws InterruptedException {
        runFX(() -> {
            AbstractCell logicCell = (AbstractCell) visualCell.getUserData();
            logicCell.playerClick(controller.board);
            controller.displayPlayerTurnLabel();

            if (logicCell.getColour() == Colour.BLACK) {
                visualCell.setFill(QuaxController.quaxBlack);
            } else if (logicCell.getColour() == Colour.WHITE) {
                visualCell.setFill(QuaxController.quaxWhite);
            }
        });
    }

    @Test
    @Order(1)
    public void testOctagonClickBlackMove() throws InterruptedException {

        Shape visual = getOctagon(0, 0);
        AbstractCell cell = (AbstractCell) visual.getUserData();

        assertEquals(PlayerTurn.BLACK, controller.board.getPlayerTurn());
        assertEquals(Colour.UNOCCUPIED, cell.getColour());

        emulateClick(visual);

        assertAll(
                () -> assertEquals(Colour.BLACK, cell.getColour()),
                () -> assertEquals(PlayerTurn.WHITE, controller.board.getPlayerTurn()),
                () -> assertEquals(QuaxController.quaxBlack, visual.getFill()),
                () -> assertEquals("WHITE to play", controller.statusLabel.getText())
        );
    }

    @Test
    @Order(2)
    public void testOctagonClickWhiteMove() throws InterruptedException {

        Shape first = getOctagon(0, 0);
        emulateClick(first);

        assertEquals(PlayerTurn.WHITE, controller.board.getPlayerTurn());

        Shape second = getOctagon(0, 1);
        AbstractCell cell = (AbstractCell) second.getUserData();

        assertEquals(Colour.UNOCCUPIED, cell.getColour());

        emulateClick(second);

        assertAll(
                () -> assertEquals(Colour.WHITE, cell.getColour()),
                () -> assertEquals(PlayerTurn.BLACK, controller.board.getPlayerTurn()),
                () -> assertEquals(QuaxController.quaxWhite, second.getFill()),
                () -> assertEquals("BLACK to play", controller.statusLabel.getText())
        );
    }

    @Test
    @Order(3)
    public void testOctagonClickOnOccupied() throws InterruptedException {

        Shape visual = getOctagon(0, 0);
        emulateClick(visual);

        AbstractCell cell = (AbstractCell) visual.getUserData();

        Colour colourBefore = cell.getColour();
        Object fillBefore = visual.getFill();
        String labelBefore = controller.statusLabel.getText();
        PlayerTurn turnBefore = controller.board.getPlayerTurn();

        runFX(() -> cell.playerClick(controller.board));

        assertAll(
                () -> assertEquals(colourBefore, cell.getColour()),
                () -> assertEquals(turnBefore, controller.board.getPlayerTurn()),
                () -> assertEquals(fillBefore, visual.getFill()),
                () -> assertEquals(labelBefore, controller.statusLabel.getText())
        );
    }

    @Test
    @Order(4)
    public void testRhombusClickBlackMove() throws InterruptedException {

        runFX(() -> controller.board.setPlayerTurn(PlayerTurn.BLACK));

        Shape visual = getRhombus(0, 0);
        AbstractCell cell = (AbstractCell) visual.getUserData();

        assertEquals(Colour.UNOCCUPIED, cell.getColour());

        emulateClick(visual);

        assertAll(
                () -> assertEquals(Colour.BLACK, cell.getColour()),
                () -> assertEquals(PlayerTurn.WHITE, controller.board.getPlayerTurn()),
                () -> assertEquals(QuaxController.quaxBlack, visual.getFill()),
                () -> assertEquals("WHITE to play", controller.statusLabel.getText())
        );
    }

    @Test
    @Order(5)
    public void testRhombusClickOnOccupied() throws InterruptedException {

        runFX(() -> controller.board.setPlayerTurn(PlayerTurn.BLACK));

        Shape visual = getRhombus(0, 0);
        emulateClick(visual);

        AbstractCell cell = (AbstractCell) visual.getUserData();

        Colour colourBefore = cell.getColour();
        Object fillBefore = visual.getFill();
        String labelBefore = controller.statusLabel.getText();
        PlayerTurn turnBefore = controller.board.getPlayerTurn();

        runFX(() -> cell.playerClick(controller.board));

        assertAll(
                () -> assertEquals(colourBefore, cell.getColour()),
                () -> assertEquals(turnBefore, controller.board.getPlayerTurn()),
                () -> assertEquals(fillBefore, visual.getFill()),
                () -> assertEquals(labelBefore, controller.statusLabel.getText())
        );
    }
}