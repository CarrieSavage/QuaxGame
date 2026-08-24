import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import quax.model.board.QuaxBoard;
import quax.model.enums.PlayerTurn;

public class TestQuaxBoard {

    private QuaxBoard board;

    @BeforeEach
    public void setup() {
        board = new QuaxBoard();
    }

    @Test
    public void testBoardInitialization() {
        for (int r = 0; r < 11; r++) {
            for (int c = 0; c < 11; c++) {
                Assertions.assertNotNull(board.getOctagon(r, c),
                        "quax.model.board.Octagon should be initialized at (" + r + "," + c + ")");
            }
        }

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                Assertions.assertNotNull(board.getRhombus(r, c),
                        "quax.model.board.Rhombus should be initialized at (" + r + "," + c + ")");
            }
        }
    }

    @Test
    public void testInitialPlayerTurnIsBlack() {
        Assertions.assertEquals(PlayerTurn.BLACK, board.getPlayerTurn(),
                "Initial player turn should be BLACK");
    }

    @Test
    public void testSetPlayerTurn() {
        board.setPlayerTurn(PlayerTurn.WHITE);
        Assertions.assertEquals(PlayerTurn.WHITE, board.getPlayerTurn());
    }

}