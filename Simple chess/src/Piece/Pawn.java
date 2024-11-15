package Piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece{

    public Pawn(int color, int col, int row){
        super(color,col,row);

        type = Type.PAWN;

        if(color == GamePanel.WHITE){
            image = getImage("/Piece/w-pawn");
        }
        else {
            image = getImage("/Piece/b-pawn");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)== false){
//         DEFINE MOVE VALUE BASED ON COLOR , IF WHITE UP , IF BLACK DOWN
            int moveValue;
            if(color == GamePanel.WHITE){
                moveValue = -1;
            }
            else {
                moveValue = 1;
            }
//            CHECK THE HITTING PIECE
            hittingP = getHittingP(targetCol,targetRow);

//            1 SQUARE MOVEMENT
            if(targetCol == preCol && targetRow == preRow+ moveValue && hittingP==null){
                return true;
            }
//            2 SQUARES MOVEMENT
            if(targetCol == preCol && targetRow==preRow+moveValue*2 && hittingP == null && moved == false && pieceIsOnDiagonalLine(targetCol,targetRow)==false){
                return true;
            }
//            CAPTURING PIECES
            if(Math.abs(targetCol-preCol)==1 && targetRow == preRow+moveValue && hittingP != null && hittingP.color!=color){
                return true;
            }
//        EN PASSANT
            if(Math.abs(targetCol-preCol)==1 && targetRow == preRow+moveValue){
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == targetCol && piece.row == preRow && piece.twoStepped==true){
                        hittingP = piece;
                        return true;
                    }
                }
            }

            }
        return false;
    }
}
