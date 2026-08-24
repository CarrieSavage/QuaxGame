
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import quax.model.board.AbstractCell;
import quax.model.board.QuaxBoard;
import quax.model.enums.PlayerTurn;

import static org.junit.jupiter.api.Assertions.*;

public class TestPieRule {

    private QuaxBoard board;

    @BeforeEach
    public void setUp() {
        board = new QuaxBoard();
    }

    @Test
    void notAvailableOnStart(){
        assertFalse(board.isPieRuleAvailable(), "Pie rule should not be available before any moves");
    }

    @Test
    void availableAfterFirstMove(){
        AbstractCell cell = board.getOctagon(0,0);
        board.playerClick(cell);

        assertTrue(board.isPieRuleAvailable(), "Pie rule should be available");
    }

    @Test
    void notAvailableAfterSecondMove(){
        board.playerClick(board.getOctagon(0,0));
        board.playerClick(board.getOctagon(0,1));
        assertFalse(board.isPieRuleAvailable(), "Pie rule should not be available");
    }

    @Test
    void buttonSwitchesToWhite(){
        board.playerClick(board.getOctagon(0,0));
        board.activatePieRule();
        assertEquals(PlayerTurn.BLACK, board.getPlayerTurn(),"quax.model.player.Player turn should be white");
    }

    @Test
    void buttonGoneAfterUse(){
        board.playerClick(board.getOctagon(0,0));
        board.activatePieRule();
        assertFalse(board.isPieRuleAvailable(), "Pie rule should not be available");
    }
}
