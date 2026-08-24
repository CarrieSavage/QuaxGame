import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.*;
import quax.QuaxController;
import quax.model.board.QuaxBoard;
import quax.model.enums.PlayerTurn;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestQuaxController extends JavaFXTestBase {
    private static QuaxController controller;

    private static void runFX(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            action.run();
            latch.countDown();
        });
        latch.await();
    }

    @BeforeEach
    public void setUp() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        runFX(() -> {
            controller = new QuaxController();

            controller.boardContainer = new StackPane();
            controller.statusLabel = new Label();
            controller.labelPolygon = new Polygon();
            controller.labelRhombus = new Rectangle();

            controller.pieRuleButton = new javafx.scene.control.Button();
            controller.strategyButton = new javafx.scene.control.Button();

            controller.initialize();

            controller.board.setPlayerTurn(PlayerTurn.BLACK);
            controller.displayPlayerTurnLabel();
        });
    }

    @Test
    @Order(1)
    void testInitialiseBoardandView() throws InterruptedException {
        runFX(() -> {
            assertNotNull(controller.board, "Board should be created");
            assertNotNull(controller.boardView, "BoardView should be created");
            assertEquals(1, controller.boardContainer.getChildren().size(), "Board view should be added to container");
        });
    }

    @Test
    @Order(2)
    void testLabel() throws InterruptedException {
        runFX(() -> {
            assertEquals("BLACK to play", controller.statusLabel.getText(), "Initial player is black");
        });
    }

    @Test
    @Order(3)
    public void testResetBoard() throws InterruptedException {
            QuaxBoard oldBoard = controller.board;

            runFX(() -> controller.onNewGame());

            runFX(() -> {
                assertNotSame(oldBoard, controller.board, "onNewGame() should create a new board");
                assertEquals(1, controller.boardContainer.getChildren().size(), "BoardView should be replaced");
                assertEquals("WHITE to play", controller.statusLabel.getText(), "Initial player is black");
            });
    }

    @Test
    @Order(4)
    void testDisplayPlayerTurnBlack() throws InterruptedException {
        runFX(() -> {
            controller.board.setPlayerTurn(PlayerTurn.BLACK);
            controller.displayPlayerTurnLabel();

            assertEquals("BLACK to play", controller.statusLabel.getText());
            assertEquals(QuaxController.quaxBlack, controller.labelPolygon.getFill());
            assertEquals(QuaxController.quaxBlack, controller.labelRhombus.getFill());
        });
    }

    @Test
    @Order(5)
    void testDisplayPlayerTurnWhite() throws InterruptedException {
        runFX(() -> {
            controller.board.setPlayerTurn(PlayerTurn.WHITE);
            controller.displayPlayerTurnLabel();

            assertEquals("WHITE to play", controller.statusLabel.getText());
            assertEquals(QuaxController.quaxWhite, controller.labelPolygon.getFill());
            assertEquals(QuaxController.quaxWhite, controller.labelRhombus.getFill());
        });
    }

    @Test
    @Order(6)
    public void testExit() throws InterruptedException {
        runFX(() -> assertDoesNotThrow(() -> controller.onExitGame()));
    }
}
