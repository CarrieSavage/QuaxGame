package quax.model.board;

import quax.model.enums.Colour;
import quax.model.enums.GameState;
import quax.model.enums.PlayerTurn;
import quax.model.chain.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class QuaxBoard {
    public static final int OCTAGON_GRID_SIZE = 11;
    public static final int RHOMBUS_GRID_SIZE = 10;

    private static final int MAX_NEIGHBOURS = 8;
    private static final int EXPECTED_CHAIN_NEIGHBOURS = 50;

    private Octagon[][] octagons = new Octagon[OCTAGON_GRID_SIZE][OCTAGON_GRID_SIZE];
    private Rhombus[][] rhombuses = new Rhombus[RHOMBUS_GRID_SIZE][RHOMBUS_GRID_SIZE];

    private LinkedList<AbstractBlockChain> chains;

    private PlayerTurn playerTurn;
    private GameState gameState;

    private int moveCount = 0;
    private boolean pieRuleUsed = false;

    public QuaxBoard() {
        for (int row = 0; row < OCTAGON_GRID_SIZE; row++) {
            for (int col = 0; col < OCTAGON_GRID_SIZE; col++) {
                this.octagons[row][col] = new Octagon(row, col);
                if (col < RHOMBUS_GRID_SIZE && row < RHOMBUS_GRID_SIZE) {
                    this.rhombuses[row][col] = new Rhombus(row, col);
                }
            }
        }

        playerTurn = PlayerTurn.BLACK;
        gameState = GameState.IN_GAME;

        chains = new LinkedList<>();
    }

    public Octagon getOctagon(int row, int col) {
        return this.octagons[row][col];
    }

    public Rhombus getRhombus(int row, int col) {
        return this.rhombuses[row][col];
    }

    public LinkedList<AbstractBlockChain> getBlockChains() {
        return chains;
    }

    public List<AbstractCell> getUnoccupiedBorderingCells(AbstractBlockChain chain) {
        Set<AbstractCell> set = new HashSet<>(EXPECTED_CHAIN_NEIGHBOURS);

        for (AbstractCell cell : chain.getChain()) {
            for (AbstractCell boardingCell : getBorderingCells(cell)) {
                if (boardingCell.getColour() == Colour.UNOCCUPIED) {
                    set.add(boardingCell);
                }
            }
        }

        return new ArrayList<>(set);
    }

    public PlayerTurn getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(PlayerTurn playerTurn) {
        this.playerTurn = playerTurn;
    }

    public boolean isPieRuleAvailable() {
        return !pieRuleUsed && moveCount == 1;
    }

    public void activatePieRule() {
        if (!isPieRuleAvailable()) {
            return;
        }

        pieRuleUsed = true;
        playerTurn = PlayerTurn.BLACK;
    }

    private void addChain(AbstractCell cell) {
        AbstractBlockChain newChain = (cell.getColour() == Colour.BLACK)
                ? new BlackBlockChain()
                : new WhiteBlockChain();

        newChain.addCell(cell);
        chains.add(newChain);
    }

    private void mergeChains(List<Integer> hits) {
        int baseIdx = hits.get(0);
        AbstractBlockChain base = chains.get(baseIdx);

        for (int j = hits.size() - 1; j >= 1; j--) {
            int idx = hits.get(j);
            AbstractBlockChain.mergeInto(base, chains.get(idx));
            chains.remove(idx);
        }
    }

    private void addToChains(AbstractCell cell) {
        List<Integer> hits = new ArrayList<>(chains.size());

        for (int i = 0; i < chains.size(); i++) {
            AbstractBlockChain chain = chains.get(i);

            if (chain.getColour() == cell.getColour() && chain.connects(cell)) {
                chain.addCell(cell);
                hits.add(i);
            }
        }

        if (hits.isEmpty()) {
            addChain(cell);
            updateGameState();
            return;
        }

        mergeChains(hits);
        updateGameState();
    }

    public void switchChainColours() {
        LinkedList<AbstractBlockChain> newChains = new LinkedList<>();

        for (AbstractBlockChain chain : chains) {
            AbstractBlockChain newChain = (chain.getColour() == Colour.BLACK)
                    ? new WhiteBlockChain()
                    : new BlackBlockChain();

            Colour targetColour = (chain.getColour() == Colour.BLACK) ? Colour.WHITE : Colour.BLACK;

            for (AbstractCell cell : chain.getChain()) {
                cell.setColour(targetColour);
                newChain.addCell(cell);
            }

            newChains.add(newChain);
        }

        chains = newChains;
    }

    public void updateGameState() {
        for (AbstractBlockChain chain : chains) {
            if (chain.getWin()) {
                gameState = (chain.getColour() == Colour.BLACK) ? GameState.BLACK_WON : GameState.WHITE_WON;
                return;
            }
        }
    }

    public List<AbstractCell> getBorderingCells(AbstractCell center) {
        List<AbstractCell> neighbours = new ArrayList<>(MAX_NEIGHBOURS);

        for (int[] arr : center.getBorderingOctagonsPositions()) {
            neighbours.add(getOctagon(arr[0], arr[1]));
        }

        for (int[] arr : center.getBorderingRhombusPositions()) {
            neighbours.add(getRhombus(arr[0], arr[1]));
        }

        return neighbours;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void playerClick(AbstractCell cell) {
        cell.playerClick(this);
        addToChains(cell);
        moveCount++;
    }
}