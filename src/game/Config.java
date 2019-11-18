package game;

import game.board.Board;
import game.board.NineMensMorris;

public class Config {
    public final Board gameBoard;
    public final int piecesPerPlayer;
    public final boolean flyingEnabled;

    public Config(Board boardType, boolean flyingEnabled, int piecesPerPlayer){
        this.gameBoard = boardType;
        this.flyingEnabled = flyingEnabled;
        this.piecesPerPlayer = piecesPerPlayer;
    }

    public static Config NineMensMorris(){
        return new Config(new NineMensMorris(), true, 9);
    }

    public static class Builder {
        public Board gameBoard;
        public int piecesPerPlayer;
        public boolean flyingEnabled = false;

        public Config build() {
            return new Config(gameBoard, flyingEnabled, piecesPerPlayer);
        }
    }
}