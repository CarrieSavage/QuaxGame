import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import quax.model.board.AbstractCell;
import quax.model.board.Edge;
import quax.model.board.Octagon;
import quax.model.board.Rhombus;
import quax.model.chain.*;
import quax.model.enums.Colour;
import quax.model.exception.ColourMismatchException;
import quax.model.exception.EmptyChainException;

import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestChains {

    private LinkedHashSet<AbstractCell> setOf(AbstractCell... cells) {
        LinkedHashSet<AbstractCell> set = new LinkedHashSet<>();
        for (AbstractCell c : cells) set.add(c);
        return set;
    }

    @Nested
    class AddCellTests {

        @Test
        void testConstructorInitialState() {
            BlackBlockChain chain = new BlackBlockChain();

            assertTrue(chain.isEmpty());
            assertFalse(chain.getWin());
            assertEquals(Colour.BLACK, chain.getColour());
        }

        @Test
        void testAddCellCorrectColourAdded() {
            BlackBlockChain chain = new BlackBlockChain();
            Octagon cell = new Octagon(1, 1);
            cell.setColour(Colour.BLACK);

            chain.addCell(cell);

            assertEquals(setOf(cell), chain.getChain());
        }

        @Test
        void testAddCellWongColourIgnored() {
            BlackBlockChain chain = new BlackBlockChain();
            Octagon cell = new Octagon(1, 1);
            cell.setColour(Colour.WHITE);

            chain.addCell(cell);

            assertTrue(chain.isEmpty());
        }

        @Test
        void testAddCellNonConnectingCellIgnored() {
            WhiteBlockChain chain = new WhiteBlockChain();

            Octagon a = new Octagon(1, 1);
            Octagon b = new Octagon(10, 10);

            a.setColour(Colour.WHITE);
            b.setColour(Colour.WHITE);

            chain.addCell(a);
            chain.addCell(b);

            assertEquals(setOf(a), chain.getChain());
        }

        @Test
        void testAddCellConnectingCellAdded() {
            WhiteBlockChain chain = new WhiteBlockChain();

            Octagon a = new Octagon(5, 5);
            Octagon b = new Octagon(6, 5);

            a.setColour(Colour.WHITE);
            b.setColour(Colour.WHITE);

            chain.addCell(a);
            chain.addCell(b);

            assertEquals(setOf(a, b), chain.getChain());
        }

        @Test
        void testAddCellSetsEdgeFlags() {
            BlackBlockChain chain = new BlackBlockChain();

            Octagon edgeCell = new Octagon(0, 5);
            edgeCell.setColour(Colour.BLACK);

            chain.addCell(edgeCell);

            Edge edge = Edge.getTargetEdges(Colour.BLACK)[0];
            assertTrue(chain.touches(edge));
        }

        @Test
        void testAddCellTriggersWinViaMerge() {
            BlackBlockChain c1 = new BlackBlockChain();
            BlackBlockChain c2 = new BlackBlockChain();

            Octagon e1 = new Octagon(0, 5);
            Octagon e2 = new Octagon(10, 5);

            e1.setColour(Colour.BLACK);
            e2.setColour(Colour.BLACK);

            c1.addCell(e1);
            c2.addCell(e2);

            AbstractBlockChain.mergeInto(c1, c2);

            assertTrue(c1.getWin());
        }

        @Nested
        class ConnectsTests {

            @Test
            void testConnectsTrueWhenNeighbour() {
                WhiteBlockChain chain = new WhiteBlockChain();

                Octagon a = new Octagon(5, 5);
                Octagon b = new Octagon(6, 5);

                a.setColour(Colour.WHITE);
                b.setColour(Colour.WHITE);

                chain.addCell(a);

                assertTrue(chain.connects(b));
            }

            @Test
            void testConnectsFalseWhenNotNeighbour() {
                WhiteBlockChain chain = new WhiteBlockChain();

                Octagon a = new Octagon(1, 1);
                Octagon b = new Octagon(9, 9);

                a.setColour(Colour.WHITE);
                b.setColour(Colour.WHITE);

                chain.addCell(a);

                assertFalse(chain.connects(b));
            }
        }

        @Nested
        class MergeTests {

            @Test
            void testMergeCombinesChains() {
                BlackBlockChain c1 = new BlackBlockChain();
                BlackBlockChain c2 = new BlackBlockChain();

                Octagon a = new Octagon(1, 1);
                Octagon b = new Octagon(2, 1);

                a.setColour(Colour.BLACK);
                b.setColour(Colour.BLACK);

                c1.addCell(a);
                c2.addCell(b);

                AbstractBlockChain.mergeInto(c1, c2);

                assertEquals(setOf(a, b), c1.getChain());
            }

            @Test
            void testMergeRemovesDuplicates() {
                BlackBlockChain c1 = new BlackBlockChain();
                BlackBlockChain c2 = new BlackBlockChain();

                Octagon a = new Octagon(1, 1);
                Octagon b = new Octagon(2, 1);

                a.setColour(Colour.BLACK);
                b.setColour(Colour.BLACK);

                c1.addCell(a);
                c1.addCell(b);
                c2.addCell(b);

                AbstractBlockChain.mergeInto(c1, c2);

                assertEquals(2, c1.getChain().size());
            }

            @Test
            void testMergePropagatesEdges() {
                BlackBlockChain c1 = new BlackBlockChain();
                BlackBlockChain c2 = new BlackBlockChain();

                Octagon edgeCell = new Octagon(0, 5);
                edgeCell.setColour(Colour.BLACK);

                c2.addCell(edgeCell);

                AbstractBlockChain.mergeInto(c1, c2);

                Edge edge = Edge.getTargetEdges(Colour.BLACK)[0];
                assertTrue(c1.touches(edge));
            }

            @Test
            void testMergeTriggersWin() {
                BlackBlockChain c1 = new BlackBlockChain();
                BlackBlockChain c2 = new BlackBlockChain();

                Octagon e1 = new Octagon(0, 5);
                Octagon e2 = new Octagon(10, 5);

                e1.setColour(Colour.BLACK);
                e2.setColour(Colour.BLACK);

                c1.addCell(e1);
                c2.addCell(e2);

                AbstractBlockChain.mergeInto(c1, c2);

                assertTrue(c1.getWin());
            }

            @Test
            void testMergeDifferentColour() {
                BlackBlockChain black = new BlackBlockChain();
                WhiteBlockChain white = new WhiteBlockChain();

                assertThrows(ColourMismatchException.class,
                        () -> AbstractBlockChain.mergeInto(black, white));
            }
        }

        @Nested
        class ChainUtilsTests {

            @Test
            void testChainsTouchingEdgeReturnsOnlyMatchingChains() {
                BlackBlockChain c1 = new BlackBlockChain();
                BlackBlockChain c2 = new BlackBlockChain();

                Octagon edgeCell = new Octagon(0, 5);
                edgeCell.setColour(Colour.BLACK);

                c1.addCell(edgeCell);

                List<AbstractBlockChain> chains = List.of(c1, c2);

                Edge edge = Edge.getTargetEdges(Colour.BLACK)[0];

                List<AbstractBlockChain> result =
                        ChainUtils.chainsTouchingEdge(chains, edge);

                assertEquals(1, result.size());
                assertTrue(result.contains(c1));
            }

            @Test
            void testConnectsChainsCorrectMapping() {
                WhiteBlockChain c1 = new WhiteBlockChain();
                WhiteBlockChain c2 = new WhiteBlockChain();

                Octagon a = new Octagon(5, 5);
                Octagon b = new Octagon(6, 5);
                Octagon far = new Octagon(10, 10);

                a.setColour(Colour.WHITE);
                b.setColour(Colour.WHITE);
                far.setColour(Colour.WHITE);

                c1.addCell(a);
                c2.addCell(far);

                List<AbstractBlockChain> chains = List.of(c1, c2);

                boolean[] result = ChainUtils.connectsChains(chains, b);

                assertTrue(result[0]);
                assertFalse(result[1]);
            }

            @Test
            void testClosestCellToEdgeReturnsOneOfChainCells() {
                BlackBlockChain chain = new BlackBlockChain();

                Octagon a = new Octagon(5,5);
                Octagon b = new Octagon(1,5);

                a.setColour(Colour.BLACK);
                b.setColour(Colour.BLACK);

                chain.addCell(a);
                chain.addCell(b);

                Edge edge = Edge.getTargetEdges(Colour.BLACK)[0];

                AbstractCell result =
                        ChainUtils.closestCellToEdgeInChain(chain, edge);

                assertNotNull(result);
            }

            @Test
            void testClosestCellToEdgePrefersOctagonOnTie() {
                BlackBlockChain chain = new BlackBlockChain();

                AbstractCell oct = new Octagon(2, 5);
                AbstractCell rho = new Rhombus(2, 5);

                oct.setColour(Colour.BLACK);
                rho.setColour(Colour.BLACK);

                chain.addCell(rho);
                chain.addCell(oct);

                Edge edge = Edge.getTargetEdges(Colour.BLACK)[0];

                AbstractCell result =
                        ChainUtils.closestCellToEdgeInChain(chain, edge);

                assertTrue(result instanceof Octagon);
            }

            @Test
            void testClosestCellToEdgeEmptyChainThrows() {
                BlackBlockChain chain = new BlackBlockChain();

                Edge edge = Edge.getTargetEdges(Colour.BLACK)[0];

                assertThrows(EmptyChainException.class,
                        () -> ChainUtils.closestCellToEdgeInChain(chain, edge));
            }
        }
    }
}



