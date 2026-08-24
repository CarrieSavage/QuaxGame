package quax.ui;

import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import quax.QuaxController;
import quax.model.board.AbstractCell;
import quax.model.board.Octagon;
import quax.model.board.QuaxBoard;
import quax.model.board.Rhombus;
import quax.model.chain.AbstractBlockChain;
import static quax.ui.QuaxUiConstants.*;

import java.util.ArrayList;
import java.util.List;

public class QuaxBoardView extends Pane {

    private final List<AbstractCell> strategyCells = new ArrayList<>();
    private final List<Text> strategyTexts = new ArrayList<>();

    private final QuaxBoard board;

    private Polygon[][] octagons;
    private Rectangle[][] rhombuses;

    private QuaxController controller;

    public QuaxBoardView(QuaxBoard board) {
        this.board = board;
        this.octagons = new Polygon[BOARD_DIMENSION][BOARD_DIMENSION];
        this.rhombuses = new Rectangle[RHOMBUS_GRID_SIZE][RHOMBUS_GRID_SIZE];

        setPrefSize(BOARD_VIEW_SIZE, BOARD_VIEW_SIZE);
        setStyle("-fx-background-color: #FFECD1;");

        initializeBoard();
    }

    public void setController(QuaxController controller) {
        this.controller = controller;
    }

    private void initializeBoard() {
        addBoardBackground();
        addLabels();
        addCells();
    }

    private void addBoardBackground() {
        Rectangle background = createRectangle(
                BOARD_BACKGROUND_X,
                BOARD_BACKGROUND_Y,
                BOARD_BACKGROUND_WIDTH,
                BOARD_BACKGROUND_HEIGHT,
                QuaxController.quaxCreme
        );

        Rectangle topBlack = createRectangle(BORDER_TOP_X, BORDER_TOP_Y, BORDER_TOP_WIDTH, BORDER_TOP_HEIGHT, Color.BLACK);
        Rectangle bottomBlack = createRectangle(
                BORDER_BOTTOM_X,
                BORDER_BOTTOM_Y,
                BORDER_BOTTOM_WIDTH,
                BORDER_BOTTOM_HEIGHT,
                Color.BLACK
        );

        Rectangle leftWhite = createRectangle(BORDER_LEFT_X, BORDER_LEFT_Y, BORDER_LEFT_WIDTH, BORDER_LEFT_HEIGHT, Color.WHITE);
        Rectangle rightWhite = createRectangle(
                BORDER_RIGHT_X,
                BORDER_RIGHT_Y,
                BORDER_RIGHT_WIDTH,
                BORDER_RIGHT_HEIGHT,
                Color.WHITE
        );

        getChildren().addAll(background, leftWhite, rightWhite, topBlack, bottomBlack);
    }

    private Rectangle createRectangle(double x, double y, double width, double height, Color fill) {
        Rectangle r = new Rectangle(x, y, width, height);
        r.setFill(fill);
        return r;
    }

    private void addCells() {
        for (int row = 0; row < BOARD_DIMENSION; row++) {
            for (int col = 0; col < BOARD_DIMENSION; col++) {
                double centerX = START_X + col * GRID_CELL_SPACING;
                double centerY = START_Y + row * GRID_CELL_SPACING;
                Coordinate pos = new Coordinate(row, col);

                addOctagonCell(pos, centerX, centerY);

                if (row < RHOMBUS_GRID_SIZE && col < RHOMBUS_GRID_SIZE) {
                    addRhombusCell(pos, centerX, centerY);
                }
            }
        }
    }

    private void addOctagonCell(Coordinate pos, double centerX, double centerY) {
        Polygon octagon = createOctagon(centerX, centerY);
        octagons[pos.row][pos.col] = octagon;
        getChildren().add(octagon);

        Octagon logicOctagon = board.getOctagon(pos.row, pos.col);
        octagon.setUserData(logicOctagon);
        octagon.setOnMouseClicked(e -> controller.handleCellClick(e));
    }

    private void addRhombusCell(Coordinate pos, double octagonCenterX, double octagonCenterY) {
        double rhombusCenterX = octagonCenterX + RHOMBUS_OFFSET_FROM_OCTAGON;
        double rhombusCenterY = octagonCenterY + RHOMBUS_OFFSET_FROM_OCTAGON;

        Rectangle rhombus = new Rectangle(
                rhombusCenterX - RHOMBUS_HALF_SIZE,
                rhombusCenterY - RHOMBUS_HALF_SIZE,
                RHOMBUS_SIZE,
                RHOMBUS_SIZE
        );

        rhombus.setFill(QuaxController.quaxRed);
        rhombus.setStroke(QuaxController.quaxDarkRed);
        rhombus.setStrokeWidth(SHAPE_STROKE_WIDTH);
        rhombus.setRotate(RHOMBUS_ROTATION_DEGREES);

        rhombuses[pos.row][pos.col] = rhombus;
        getChildren().add(rhombus);

        Rhombus logicRhombus = board.getRhombus(pos.row, pos.col);
        rhombus.setUserData(logicRhombus);
        rhombus.setOnMouseClicked(e -> controller.handleCellClick(e));
    }

    private Polygon createOctagon(double centerX, double centerY) {
        Polygon octagon = new Polygon();
        octagon.getPoints().addAll(
                centerX - OCTAGON_INNER_OFFSET, centerY + OCTAGON_OUTER_RADIUS,
                centerX + OCTAGON_INNER_OFFSET, centerY + OCTAGON_OUTER_RADIUS,
                centerX + OCTAGON_OUTER_RADIUS, centerY + OCTAGON_INNER_OFFSET,
                centerX + OCTAGON_OUTER_RADIUS, centerY - OCTAGON_INNER_OFFSET,
                centerX + OCTAGON_INNER_OFFSET, centerY - OCTAGON_OUTER_RADIUS,
                centerX - OCTAGON_INNER_OFFSET, centerY - OCTAGON_OUTER_RADIUS,
                centerX - OCTAGON_OUTER_RADIUS, centerY - OCTAGON_INNER_OFFSET,
                centerX - OCTAGON_OUTER_RADIUS, centerY + OCTAGON_INNER_OFFSET
        );

        octagon.setFill(QuaxController.quaxLightRed);
        octagon.setStroke(QuaxController.quaxDarkRed);
        octagon.setStrokeWidth(SHAPE_STROKE_WIDTH);

        return octagon;
    }

    private void addLabels() {
        addColumnLabels(COLUMN_LABEL_TOP_Y);
        addColumnLabels(COLUMN_LABEL_BOTTOM_Y);
        addRowLabels(true);
        addRowLabels(false);
    }

    private void addColumnLabels(double y) {
        for (int i = 0; i < BOARD_DIMENSION; i++) {
            double x = COLUMN_LABEL_START_X + i * LABEL_X_SPACING;
            Text label = new Text(x, y, COLUMN_LABELS[i]);
            label.setFont(LABEL_FONT);
            getChildren().add(label);
        }
    }

    private void addRowLabels(boolean leftSide) {
        for (int i = 0; i < BOARD_DIMENSION; i++) {
            String rowNum = String.valueOf(BOARD_DIMENSION - i);
            boolean singleDigit = rowNum.length() == 1;

            double x = getRowLabelX(leftSide, singleDigit);
            double y = ROW_LABEL_START_Y + i * LABEL_Y_SPACING;

            Text label = new Text(x, y, rowNum);
            label.setFont(LABEL_FONT);
            getChildren().add(label);
        }
    }

    private double getRowLabelX(boolean leftSide, boolean singleDigit) {
        if (leftSide) {
            return singleDigit ? LEFT_ROW_LABEL_SINGLE_DIGIT_X : LEFT_ROW_LABEL_DOUBLE_DIGIT_X;
        }

        return singleDigit ? RIGHT_ROW_LABEL_SINGLE_DIGIT_X : RIGHT_ROW_LABEL_DOUBLE_DIGIT_X;
    }

    public void updateCell(AbstractCell cell) {
        javafx.scene.shape.Shape shape = getShapeForCell(cell);
        shape.setFill(QuaxController.getLogicCellColor(cell));
    }

    public void updateChain(AbstractBlockChain chain) {
        for (AbstractCell cell : chain.getChain()) {
            updateCell(cell);
        }
    }

    /**
     * Highlights up to {topCells.size()} candidate cells with a numbered overlay,
     * so the player can see which moves the bot considers strongest.
     */
    public void showStrategyOverlay(List<AbstractCell> topCells) {
        clearStrategyOverlay();

        if (topCells == null || topCells.isEmpty()) {
            return;
        }

        for (int i = 0; i < topCells.size(); i++) {
            AbstractCell cell = topCells.get(i);
            javafx.scene.shape.Shape shape = getShapeForCell(cell);

            shape.setStroke(STRATEGY_STROKE);
            shape.setStrokeWidth(STRATEGY_STROKE_WIDTH);

            Text t = createStrategyNumberText(cell, i + 1, shape);
            getChildren().add(t);

            strategyCells.add(cell);
            strategyTexts.add(t);
        }
    }

    /**
     * Creates a centred number label for the strategy overlay.
     * The nudge factor compensates for text baseline vs visual centre.
     */
    private Text createStrategyNumberText(AbstractCell cell, int number, javafx.scene.shape.Shape shape) {
        Text t = new Text(String.valueOf(number));
        t.setFont((cell instanceof Rhombus) ? STRATEGY_FONT_RHOMBUS : STRATEGY_FONT_OCTAGON);
        t.setFill(Color.WHITE);
        t.setMouseTransparent(true);

        Bounds b = shape.getBoundsInParent();
        t.setX(b.getMinX() + b.getWidth() * CENTER_FACTOR);
        t.setY(b.getMinY() + b.getHeight() * CENTER_FACTOR);

        Bounds tb = t.getLayoutBounds();
        t.setX(t.getX() - tb.getWidth() * CENTER_FACTOR);
        t.setY(t.getY() + tb.getHeight() * BASELINE_NUDGE_FACTOR);

        return t;
    }

    public void clearStrategyOverlay() {
        for (Text t : strategyTexts) {
            getChildren().remove(t);
        }
        strategyTexts.clear();

        for (AbstractCell cell : strategyCells) {
            javafx.scene.shape.Shape shape = getShapeForCell(cell);
            shape.setStroke(QuaxController.quaxDarkRed);
            shape.setStrokeWidth(SHAPE_STROKE_WIDTH);
        }
        strategyCells.clear();
    }

    /**
     * Returns the JavaFX shape that visually represents the given logic cell.
     *
     * @throws IllegalArgumentException if the cell is null, has an unrecognised type,
     *                                  or its grid position is out of bounds.
     */
    private javafx.scene.shape.Shape getShapeForCell(AbstractCell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("Cannot retrieve shape for null cell.");
        }

        int row = cell.getPosition()[0];
        int col = cell.getPosition()[1];

        if (cell instanceof Octagon) {
            if (inBounds(row, col, BOARD_DIMENSION, BOARD_DIMENSION)) {
                throw new IllegalArgumentException("Octagon position out of bounds: [" + row + ", " + col + "]");
            }
            return octagons[row][col];
        }

        if (cell instanceof Rhombus) {
            if (inBounds(row, col, RHOMBUS_GRID_SIZE, RHOMBUS_GRID_SIZE)) {
                throw new IllegalArgumentException("Rhombus position out of bounds: [" + row + ", " + col + "]");
            }
            return rhombuses[row][col];
        }

        throw new IllegalArgumentException("Unrecognised cell type: " + cell.getClass().getSimpleName());
    }

    private boolean inBounds(int row, int col, int rowCount, int colCount) {
        return row < 0 || row >= rowCount || col < 0 || col >= colCount;
    }

    public Polygon[][] getOctagons(){
        return octagons;
    }

    public Rectangle[][] getRhombuses(){
        return rhombuses;
    }
}