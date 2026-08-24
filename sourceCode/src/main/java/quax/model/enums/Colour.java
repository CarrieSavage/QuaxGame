package quax.model.enums;

public enum Colour {
    WHITE,
    BLACK,
    UNOCCUPIED;

    @Override
    public String toString() {
        return switch (this) {
            case BLACK -> "B";
            case WHITE -> "W";
            case UNOCCUPIED -> "U";
        };
    }

    public Colour opponentColour() {
        return this==WHITE? BLACK:WHITE;
    }
}
