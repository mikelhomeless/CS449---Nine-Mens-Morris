package Tests;

import game.logic.*;
import junit.framework.TestCase;

public class PlayerLogicTest extends TestCase {
    private PlayerLogic playerLogic;

    protected void setUp() throws Exception {
        super.setUp();
        playerLogic = new PlayerLogic();
    }

    public void testActivePlayerChange() {

        assertEquals(PlayerToken.PLAYER2, playerLogic.nextTurn());
        assertEquals(PlayerToken.PLAYER1, playerLogic.nextTurn());
    }

    public void testStartPlayer1() {
        assertEquals(PlayerToken.PLAYER1, playerLogic.getActivePlayer());
    }

    public void testPhaseOneEnds() {
        for (int x = 0; x < 18; x++) {
            playerLogic.placePlayerPiece(x);
            playerLogic.nextTurn();
        }
        assertTrue(playerLogic.isPhaseOneOver());
        assertFalse(playerLogic.isPhaseOne());
    }

    public void testPlacePiece() {
        playerLogic.placePlayerPiece(5);
        assertFalse(playerLogic.placePlayerPiece(5));
    }

    public void testPlacePieceAfterPhaseOne() {
        for (int x = 0; x < 18; x++) {
            playerLogic.placePlayerPiece(x);
            playerLogic.nextTurn();
        }
        assertFalse(playerLogic.placePlayerPiece(19));
    }

    public void testPlacedPieceLocationIsCorrect() {
        playerLogic.placePlayerPiece(2);
        playerLogic.nextTurn();

        playerLogic.placePlayerPiece(6);
        assertSame(playerLogic.getBoardAsPlayerTokens()[2], PlayerToken.PLAYER1);
        assertSame(playerLogic.getBoardAsPlayerTokens()[6], PlayerToken.PLAYER2);
    }

    /***** SPRINT 2 TESTS *****/
    public void testCannotMoveWhenDestinationOccupied() {   //player 1 move invalid when occupied
        playerLogic.placePlayerPiece(5);
        playerLogic.placePlayerPiece(4);
        assertFalse(playerLogic.move(4, 5));
    }

    public void testFlyLessThanOrEq3() {   //player 2 ability to fly
        Player player1 = new Player(PlayerToken.PLAYER1, 3);
        Player player2 = new Player(PlayerToken.PLAYER2, 3);
        PlayerLogic playerLogic = new PlayerLogic(player1, player2);
        playerLogic.placePlayerPiece(0);
        playerLogic.placePlayerPiece(3);
        playerLogic.placePlayerPiece(2);
        playerLogic.nextTurn();
        playerLogic.placePlayerPiece(21);
        playerLogic.placePlayerPiece(12);
        playerLogic.placePlayerPiece(4);
        assertTrue(playerLogic.move(4, 6));
    }

    public void testP1CantMoveP2() {   //player cannot move
        playerLogic.nextTurn();
        playerLogic.placePlayerPiece(0);
        playerLogic.nextTurn();
        assertFalse(playerLogic.move(0, 1));
    }

    public void testCanMoveIfAdjacent() {
        Player player1 = new Player(PlayerToken.PLAYER1, 4);
        Player player2 = new Player(PlayerToken.PLAYER2, 0);  //set pieces to 0 so phase one is registered as over
        PlayerLogic playerLogic = new PlayerLogic(player1, player2);
        playerLogic.placePlayerPiece(0);
        playerLogic.placePlayerPiece(7);
        playerLogic.placePlayerPiece(6);
        playerLogic.placePlayerPiece(2);
        assertTrue(playerLogic.move(0, 1));
    }

    public void testCantMoveIfNotAdjacent() {   //cannot move if the index is not adjacent and pieces > 3
        Player player1 = new Player(PlayerToken.PLAYER1, 4);
        Player player2 = new Player(PlayerToken.PLAYER2, 0);  //set pieces to 0 so phase one is registered as over
        PlayerLogic playerLogic = new PlayerLogic(player1, player2);
        playerLogic.placePlayerPiece(0);
        playerLogic.placePlayerPiece(7);
        playerLogic.placePlayerPiece(6);
        playerLogic.placePlayerPiece(2);
        assertFalse(playerLogic.move(0, 8));
    }

    public void testPlayerCantRemoveOwnPiece() {
        playerLogic.placePlayerPiece(5);
        assertFalse(playerLogic.removePiece(5));
        playerLogic.nextTurn();

        playerLogic.placePlayerPiece(7);
        assertFalse(playerLogic.removePiece(7));
    }

    public void testPiecesDecrementedAfterRemoved() {
        Player player1 = new Player(PlayerToken.PLAYER1, 9);
        Player player2 = new Player(PlayerToken.PLAYER2, 9);
        playerLogic = new PlayerLogic(player1, player2);

        playerLogic.placePlayerPiece(6);
        playerLogic.nextTurn();

        assertTrue(playerLogic.removePiece(6));
        assertEquals(8, player1.getPiecesLeft());
    }

    public void testCanMoveInPhaseOne() {
        Player player1 = new Player(PlayerToken.PLAYER1, 9);
        Player player2 = new Player(PlayerToken.PLAYER2, 9);
        playerLogic = new PlayerLogic(player1, player2);

        // both players have moves at the beginning
        assertTrue(playerLogic.canMove(player1));
        assertTrue(playerLogic.canMove(player2));

        // fill up the board
        for (int i = 0; i < 18; i++) {
            playerLogic.placePlayerPiece(i);
            playerLogic.nextTurn();
        }

        // as well as at the end of placement
        assertTrue(playerLogic.canMove(player1));
        assertTrue(playerLogic.canMove(player2));
    }

    public void testCanMoveAfterPhaseOne() {
        Player player1 = new Player(PlayerToken.PLAYER1, 4);
        Player player2 = new Player(PlayerToken.PLAYER2, 4);
        playerLogic = new PlayerLogic(player1, player2);

        // set up pieces so that player 1 can't possibly make a move
        playerLogic.placePlayerPiece(1);
        playerLogic.placePlayerPiece(9);
        playerLogic.placePlayerPiece(14);
        playerLogic.placePlayerPiece(22);
        playerLogic.nextTurn();
        playerLogic.placePlayerPiece(0);
        playerLogic.placePlayerPiece(2);
        playerLogic.placePlayerPiece(21);
        playerLogic.placePlayerPiece(23);
        assertFalse(playerLogic.canMove(player2));

        // remove pieces until player 1 has 3, then should be able to fly, then player 1 can move anywhere
        player2.decrementPiecesLeft();
        assertTrue(playerLogic.canMove(player1));
    }

    public void testWinCheckFor2PiecesLeftP1() {
        Player player1 = new Player(PlayerToken.PLAYER1, 9);
        Player player2 = new Player(PlayerToken.PLAYER2, 9);
        playerLogic = new PlayerLogic(player1, player2);
        // verify game state at beginning
        assertSame(PlayerLogic.GameState.PLACEMENT, playerLogic.getCurrentGameState());
        // force player moves left to be 2
        for (int i = 0; i < 7; i++) {
            player1.decrementPiecesLeft();
        }
        playerLogic.winCheck();
        assertSame(playerLogic.getCurrentGameState(), PlayerLogic.GameState.PLAYER2_WIN);
    }

    public void testWinCheckFor2PiecesLeftP2() {
        Player player1 = new Player(PlayerToken.PLAYER1, 9);
        Player player2 = new Player(PlayerToken.PLAYER2, 9);
        playerLogic = new PlayerLogic(player1, player2);
        // verify game state at beginning
        assertSame(PlayerLogic.GameState.PLACEMENT, playerLogic.getCurrentGameState());
        // force player moves left to be 2
        for (int i = 0; i < 7; i++) {
            player2.decrementPiecesLeft();
        }
        playerLogic.winCheck();
        assertSame(playerLogic.getCurrentGameState(), PlayerLogic.GameState.PLAYER1_WIN);
    }

    public void testWinCheckP2CannotMove() {
        Player player1 = new Player(PlayerToken.PLAYER1, 4);
        Player player2 = new Player(PlayerToken.PLAYER2, 4);
        playerLogic = new PlayerLogic(player1, player2);

        // set up pieces so that player 2 can't possibly make a move
        playerLogic.placePlayerPiece(1);
        playerLogic.placePlayerPiece(9);
        playerLogic.placePlayerPiece(14);
        playerLogic.placePlayerPiece(22);
        playerLogic.nextTurn();
        playerLogic.placePlayerPiece(0);
        playerLogic.placePlayerPiece(2);
        playerLogic.placePlayerPiece(21);
        playerLogic.placePlayerPiece(23);
        playerLogic.winCheck();
        assertSame(playerLogic.getCurrentGameState(), PlayerLogic.GameState.PLAYER1_WIN);
    }

    public void testWinCheckP1CannotMove() {
        Player player1 = new Player(PlayerToken.PLAYER1, 4);
        Player player2 = new Player(PlayerToken.PLAYER2, 4);
        playerLogic = new PlayerLogic(player1, player2);

        // set up pieces so that player 1 can't possibly make a move
        playerLogic.placePlayerPiece(0);
        playerLogic.placePlayerPiece(2);
        playerLogic.placePlayerPiece(21);
        playerLogic.placePlayerPiece(23);
        playerLogic.nextTurn();
        playerLogic.placePlayerPiece(1);
        playerLogic.placePlayerPiece(9);
        playerLogic.placePlayerPiece(14);
        playerLogic.placePlayerPiece(22);
        playerLogic.winCheck();
        assertSame(playerLogic.getCurrentGameState(), PlayerLogic.GameState.PLAYER1_WIN);
    }

    public void testWinCheckTie() {
        Player player1 = new Player(PlayerToken.PLAYER1, 12);
        Player player2 = new Player(PlayerToken.PLAYER2, 12);
        playerLogic = new PlayerLogic(player1, player2);

        for (int i = 0; i < 24; i++) {
            playerLogic.placePlayerPiece(i);
            playerLogic.nextTurn();
        }

        playerLogic.winCheck();
        assertSame(playerLogic.getCurrentGameState(), PlayerLogic.GameState.END);
    }

    public void testPlayerCannotRemoveMillPiece() {
        playerLogic.placePlayerPiece(0);
        playerLogic.placePlayerPiece(1);
        playerLogic.placePlayerPiece(2);
        playerLogic.placePlayerPiece(21);
        playerLogic.nextTurn();
        assertFalse(playerLogic.removePiece(1));
    }

    public void testPlayerCanRemoveMillPiece() {
        playerLogic.placePlayerPiece(12);
        playerLogic.nextTurn();
        playerLogic.placePlayerPiece(0);
        playerLogic.placePlayerPiece(1);
        playerLogic.placePlayerPiece(2);
        playerLogic.removePiece(12);
        playerLogic.nextTurn();
        assertTrue(playerLogic.removePiece(1));
    }

    public void testGameStateChanges() {
        Player player1 = new Player(PlayerToken.PLAYER1, 6);
        Player player2 = new Player(PlayerToken.PLAYER2, 6);
        playerLogic = new PlayerLogic(player1, player2);

        playerLogic.placePlayerPiece(0);
        playerLogic.nextTurn();
        assertSame(playerLogic.getCurrentGameState(), PlayerLogic.GameState.PLACEMENT);
        playerLogic.placePlayerPiece(1);
        playerLogic.placePlayerPiece(4);
        playerLogic.placePlayerPiece(7);

        // player should not have changed as we should now be in elimination state from mill creation
        assertSame(playerLogic.nextTurn(), PlayerToken.PLAYER2);
        assertSame(playerLogic.getCurrentGameState(), PlayerLogic.GameState.ELIMINATION);
        playerLogic.removePiece(0);
        assertSame(playerLogic.getCurrentGameState(), PlayerLogic.GameState.PLACEMENT);
        // player should now have changed
        assertSame(playerLogic.nextTurn(), PlayerToken.PLAYER1);
        playerLogic.placePlayerPiece(0);
        playerLogic.placePlayerPiece(2);
        playerLogic.placePlayerPiece(23);
        playerLogic.placePlayerPiece(21);
        playerLogic.placePlayerPiece(10);
        playerLogic.nextTurn();
        playerLogic.placePlayerPiece(16);
        playerLogic.placePlayerPiece(18);
        playerLogic.placePlayerPiece(22);
        playerLogic.nextTurn();
        assertSame(playerLogic.getCurrentGameState(), PlayerLogic.GameState.MOVEMENT);
        playerLogic.nextTurn();
        playerLogic.move(18, 19);
        assertSame(playerLogic.getCurrentGameState(), PlayerLogic.GameState.ELIMINATION);
        playerLogic.removePiece(2);
        assertSame(playerLogic.getCurrentGameState(), PlayerLogic.GameState.MOVEMENT);
    }
}