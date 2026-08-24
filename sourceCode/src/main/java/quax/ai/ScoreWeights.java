package quax.ai;

public final class ScoreWeights {
    private ScoreWeights() {}

    public static final int WIN_MOVE = 2_000_000;
    public static final int BLOCK_WIN = 999_999;

    public static final int CONNECT_NEW_EDGE = 120;
    public static final int CONNECT_CHAIN = 35;

    public static final int CENTER = 5;
    public static final int MAX_LINEAR_DISTANCE = 11;

    public static final int SHORTEST_PATH_MULTIPLIER = 4;
    public static final int DIRECTIONAL_MULTIPLIER = 3;

    public static final int BLOCK_PATH_IMPORTANCE = 5;
    public static final int SHORT_PATH_BENEFIT = 10;
    public static final int CHAINS_TOUCHES_OTHER_EDGE = 20;
}