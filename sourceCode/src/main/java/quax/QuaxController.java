package quax;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import quax.model.board.AbstractCell;
import quax.model.board.QuaxBoard;
import quax.model.chain.AbstractBlockChain;
import quax.model.enums.Colour;
import quax.model.enums.GameState;
import quax.model.enums.PlayerTurn;
import quax.model.player.BotPlayer;
import quax.ui.QuaxBoardView;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import static javafx.application.Platform.exit;

public class QuaxController {
    private static final int STRATEGY_TOP_CELLS = 3;
    private static final int BOT_MOVE_DELAY_MS = 500;

    public static final Color quaxWhite = Color.web("#FBFBF9");
    public static final Color quaxBlack = Color.web("#040200");
    public static final Color quaxLightRed = Color.web("#B35549");
    public static final Color quaxRed = Color.web("#823C35");
    public static final Color quaxDarkRed = Color.web("#331D1C");
    public static final Color quaxCreme = Color.web("#FFECD1");
    public static final Color quaxBlue = Color.web("#15616D");

    @FXML public StackPane boardContainer;
    @FXML public Label statusLabel;
    @FXML public Polygon labelPolygon;
    @FXML public Rectangle labelRhombus;
    @FXML public Button pieRuleButton;
    @FXML public Button strategyButton;

    private boolean strategyShown = false;

    public QuaxBoard board;
    public QuaxBoardView boardView;
    public BotPlayer bot;

    @FXML
    public void initialize() {
        setupBoard();
        setupBot();

        displayPlayerTurnLabel();
        updatePieRuleButton();

        makeBotMove();
    }

    private void setupBoard() {
        board = new QuaxBoard();
        boardView = new QuaxBoardView(board);
        boardView.setController(this);
        boardContainer.getChildren().add(boardView);
    }

    private void setupBot() {
        bot = new BotPlayer(Colour.BLACK, board);
    }

    @FXML
    public void onNewGame() {
        boardContainer.getChildren().clear();
        initialize();
    }

    @FXML
    public void onExitGame() {
        exit();
    }

    @FXML
    public void onToggleStrategy() {
        strategyShown = !strategyShown;
        strategyButton.setText(strategyShown ? "Hide Strategy" : "Show Strategy");

        if (!strategyShown) {
            boardView.clearStrategyOverlay();
            return;
        }

        boardView.showStrategyOverlay(getTopBotCandidates(STRATEGY_TOP_CELLS));
    }

    @FXML
    public void onActivatePieRule() {
        board.activatePieRule();
        switchColours();

        displayPlayerTurnLabel();
        updatePieRuleButton();

        makeBotMove();
    }

    public void handleCellClick(MouseEvent event) {
        if (board.getGameState() != GameState.IN_GAME) {
            return;
        }

        javafx.scene.shape.Shape visualCell = (javafx.scene.shape.Shape) event.getSource();
        AbstractCell logicCell = (AbstractCell) visualCell.getUserData();

        applyMove(logicCell);
        updateAfterHumanMove();

        runBotMoveWithDelay();
    }

    private void runBotMoveWithDelay() {
        boardView.setDisable(true);
        refreshStrategyIfShown();

        PauseTransition pause = new PauseTransition(Duration.millis(BOT_MOVE_DELAY_MS));
        pause.setOnFinished(e -> {
            makeBotMove();
            refreshStrategyIfShown();
            boardView.setDisable(false);
        });
        pause.play();
    }

    private void updateAfterHumanMove() {
        displayInfoLabel();
        updatePieRuleButton();
        refreshStrategyIfShown();
    }

    private void refreshStrategyIfShown() {
        if (!strategyShown) {
            return;
        }
        boardView.showStrategyOverlay(getTopBotCandidates(STRATEGY_TOP_CELLS));
    }

    private void applyMove(AbstractCell cell) {
        board.playerClick(cell);
        boardView.updateCell(cell);
    }

    private void makeBotMove() {
        if (!isBotTurn() || board.getGameState() != GameState.IN_GAME) {
            return;
        }

        PriorityQueue<AbstractCell> botMoves = bot.getMove();
        AbstractCell botCell = botMoves.poll();

        if (botCell == null) {
            return;
        }

        applyMove(botCell);

        displayInfoLabel();
        updatePieRuleButton();
        refreshStrategyIfShown();
    }

    private boolean isBotTurn() {
        return board.getPlayerTurn() == colourToPlayerTurn(bot.getColour());
    }

    public void switchColours() {
        board.switchChainColours();

        for (AbstractBlockChain chain : board.getBlockChains()) {
            boardView.updateChain(chain);
        }
    }

    /**
     * Returns up to {@code n} unoccupied cells from the bot's current move ranking.
     * Works on a copy of the priority queue so the original ranking is not disturbed.
     */
    private List<AbstractCell> getTopBotCandidates(int n) {
        PriorityQueue<AbstractCell> copy = new PriorityQueue<>(bot.getMove());

        List<AbstractCell> topN = new ArrayList<>(n);
        while (topN.size() < n && !copy.isEmpty()) {
            AbstractCell c = copy.poll();
            if (c != null && c.getColour() == Colour.UNOCCUPIED) {
                topN.add(c);
            }
        }

        return topN;
    }

    public void displayPlayerTurnLabel() {
        if (board.getPlayerTurn() == PlayerTurn.BLACK) {
            statusLabel.setText("BLACK to play");
            labelPolygon.setFill(quaxBlack);
            labelRhombus.setFill(quaxBlack);
            return;
        }

        statusLabel.setText("WHITE to play");
        labelPolygon.setFill(quaxWhite);
        labelRhombus.setFill(quaxWhite);
    }

    public void displayInfoLabel() {
        switch (board.getGameState()) {
            case IN_GAME:
                displayPlayerTurnLabel();
                return;
            case BLACK_WON:
                statusLabel.setText("BLACK won!");
                labelPolygon.setFill(quaxBlack);
                labelRhombus.setFill(quaxBlack);
                return;
            case WHITE_WON:
                statusLabel.setText("WHITE won!");
                labelPolygon.setFill(quaxWhite);
                labelRhombus.setFill(quaxWhite);
        }
    }

    public static Color getLogicCellColor(AbstractCell cell) {
        return cell.getColour() == Colour.BLACK ? quaxBlack : quaxWhite;
    }

    private PlayerTurn colourToPlayerTurn(Colour colour) {
        return (colour == Colour.BLACK) ? PlayerTurn.BLACK : PlayerTurn.WHITE;
    }

    private void updatePieRuleButton() {
        boolean show = board.isPieRuleAvailable();
        pieRuleButton.setVisible(show);
        pieRuleButton.setManaged(show);
    }
}