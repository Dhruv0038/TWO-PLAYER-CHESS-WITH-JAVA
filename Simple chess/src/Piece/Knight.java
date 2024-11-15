package Piece;

import main.GamePanel;
import main.Type;

public class Knight extends Piece {
    public Knight(int color, int col, int row){
        super(color,col,row);

        type = Type.KNIGHT;

        if(color == GamePanel.WHITE){
            image = getImage("/Piece/w-knight");
        }
        else {
            image = getImage("/Piece/b-knight");
        }
    }
    public boolean canMove(int targetCol,int targetRow){
        if(isWithinBoard(targetCol,targetRow)){
//           KNIGHT CAN MOVE IF ITS MOVEMENT RATIO OF COL AND ROW IS 1:2 OR 2:1
            if(Math.abs(targetCol-preCol) * Math.abs(targetRow-preRow)==2){
                 if(isValidSquare(targetCol,targetRow)){
                     return true;
                 }
            }
        }
        return false;
    }

}
