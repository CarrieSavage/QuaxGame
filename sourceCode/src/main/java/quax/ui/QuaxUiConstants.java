package quax.ui;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import quax.QuaxController;

public class QuaxUiConstants {
    public QuaxUiConstants() {}

    public static final int BOARD_DIMENSION = 11;
    public static final int RHOMBUS_GRID_SIZE = BOARD_DIMENSION - 1;

    public static final double BOARD_VIEW_SIZE = 600.0;

    public static final double GRID_CELL_SPACING = 40.0;
    public static final double START_X = 102.0;
    public static final double START_Y = 95.0;

    public static final double BOARD_BACKGROUND_X = 51.0;
    public static final double BOARD_BACKGROUND_Y = 60.0;
    public static final double BOARD_BACKGROUND_WIDTH = 500.0;
    public static final double BOARD_BACKGROUND_HEIGHT = 470.0;

    public static final double BORDER_TOP_X = 72.5;
    public static final double BORDER_TOP_Y = 60.0;
    public static final double BORDER_TOP_WIDTH = 460.0;
    public static final double BORDER_TOP_HEIGHT = 25.0;

    public static final double BORDER_BOTTOM_X = 72.5;
    public static final double BORDER_BOTTOM_Y = 505.0;
    public static final double BORDER_BOTTOM_WIDTH = 460.0;
    public static final double BORDER_BOTTOM_HEIGHT = 24.0;

    public static final double BORDER_LEFT_X = 72.5;
    public static final double BORDER_LEFT_Y = 60.0;
    public static final double BORDER_LEFT_WIDTH = 20.0;
    public static final double BORDER_LEFT_HEIGHT = 460.0;

    public static final double BORDER_RIGHT_X = 512.0;
    public static final double BORDER_RIGHT_Y = 60.0;
    public static final double BORDER_RIGHT_WIDTH = 20.0;
    public static final double BORDER_RIGHT_HEIGHT = 460.0;

    public static final double RHOMBUS_OFFSET_FROM_OCTAGON = 17.5;
    public static final double RHOMBUS_HALF_SIZE = 4.0;
    public static final double RHOMBUS_SIZE = 13.0;
    public static final double RHOMBUS_ROTATION_DEGREES = 45.0;

    public static final double SHAPE_STROKE_WIDTH = 1.0;

    public static final double OCTAGON_OUTER_RADIUS = 20.0;
    public static final double OCTAGON_INNER_OFFSET = 10.71;

    public static final double COLUMN_LABEL_START_X = 97.0;
    public static final double COLUMN_LABEL_TOP_Y = 55.0;
    public static final double COLUMN_LABEL_BOTTOM_Y = 550.0;

    public static final double ROW_LABEL_START_Y = 100.0;
    public static final double LABEL_X_SPACING = GRID_CELL_SPACING;
    public static final double LABEL_Y_SPACING = GRID_CELL_SPACING;

    public static final double LEFT_ROW_LABEL_SINGLE_DIGIT_X = 58.0;
    public static final double LEFT_ROW_LABEL_DOUBLE_DIGIT_X = 55.0;
    public static final double RIGHT_ROW_LABEL_SINGLE_DIGIT_X = 538.0;
    public static final double RIGHT_ROW_LABEL_DOUBLE_DIGIT_X = 535.0;

    public static final Font LABEL_FONT = Font.font("System", FontWeight.BOLD, 12);
    public static final String[] COLUMN_LABELS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K"};

    public static final Color STRATEGY_STROKE = QuaxController.quaxBlue;
    public static final double STRATEGY_STROKE_WIDTH = 4.0;
    public static final Font STRATEGY_FONT_OCTAGON = Font.font("System", FontWeight.BOLD, 16);
    public static final Font STRATEGY_FONT_RHOMBUS = Font.font("System", FontWeight.BOLD, 11);

    public static final double CENTER_FACTOR = 0.5;              // divide by 2
    public static final double BASELINE_NUDGE_FACTOR = 0.25;     // divide by 4

}
