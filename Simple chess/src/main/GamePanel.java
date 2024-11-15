package main;
import java.util.ArrayList;
import Piece.Piece;
import Piece.Pawn;
import Piece.King;
import Piece.Queen;
import Piece.Rook;
import Piece.Bishop;
import Piece.Knight;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    //    this is for size of our game panel
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 240;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activeP;
    Piece checkingP;
    public static Piece castlingP;

    //COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    //    BOOLEANS
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;


    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
//        testPromotion();
//        testingIllegal();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();

    }

    public void setPieces() {
        //WHITE TEAM
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        //BLACK TEAM
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }

//    public void testPromotion() {
//        pieces.add(new Pawn(WHITE, 0, 3));
//        pieces.add(new Pawn(BLACK, 5, 4));
//
//
//    }
//
//    public void testingIllegal() {
////        pieces.add(new Pawn(WHITE, 7, 6));
//        pieces.add(new King(WHITE, 2, 4));
//        pieces.add(new King(BLACK, 0, 3));
////        pieces.add(new Bishop(BLACK, 1, 4));
//        pieces.add(new Queen(WHITE, 2, 1));
//    }
    @Override
    public void run() {
//       used for implementing the game loop
        double drawInterval = 10000000000d/FPS; //tells until when each frame should stay on the screen
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {

        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    private void update() {

        if (promotion) {
            promoting();
        }
        else if(gameover==false && stalemate == false) {
            //        MOUSE BUTTON PRESSED
            if (mouse.pressed) {
                if (activeP == null) {
                    for (Piece piece : simPieces) {
                        if (piece.color == currentColor &&
                                piece.col == mouse.x / Board.SQUARE_SIZE &&
                                piece.row == mouse.y / Board.SQUARE_SIZE) {

                            activeP = piece;
                        }
                    }
                } else {
//                IF  THE PLAYER IS HOLDING A PIECE , SIMULATE THE MOVE
                    simulate();
                }
            }
//        MOUSE BUTTON RELEASED
            if (mouse.pressed == false) {
                if (activeP != null) {

                    if (validSquare) {
//                    MOVE CONFIRMED

//                    UPDATE THE PIECES LIST IN CASE A PIECE HAS BEEN CAPTURED AND REMOVED
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();
                        if (castlingP != null) {
                            castlingP.updatePosition();
                        }
                        if (isKingInCheck() && isCheckmate()){
                            gameover = true;
                        } else if (isStalemate()) {
                            stalemate = true;
                            return;
                            
                        } else {// THE GAME IS STILL GOING ON
                            if (canPromote()) {
                                promotion = true;
                            } else {
                                changePlayer();
                            }
                        }
                    } else {
//                   THE MOVE IS NOT VALID RESET EVERYTHING
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    }
                }
            }

        }

//        MOUSE BUTTON PRESSED
        if (mouse.pressed) {
            if (activeP == null) {
                for (Piece piece : simPieces) {
                    if (piece.color == currentColor &&
                            piece.col == mouse.x / Board.SQUARE_SIZE &&
                            piece.row == mouse.y / Board.SQUARE_SIZE) {

                        activeP = piece;
                    }
                }
            } else {
//                IF  THE PLAYER IS HOLDING A PIECE , SIMULATE THE MOVE
                simulate();
            }
        }
//        MOUSE BUTTON RELEASED
        if (mouse.pressed == false) {
            if (activeP != null) {

                if (validSquare) {
//                    MOVE CONFIRMED

//                    UPDATE THE PIECES LIST IN CASE A PIECE HAS BEEN CAPTURED AND REMOVED
                    copyPieces(simPieces, pieces);
                    activeP.updatePosition();
                    if (castlingP != null) {
                        castlingP.updatePosition();
                    }

                        if (canPromote()) {
                            promotion = true;
                        } else {
                            changePlayer();
                        }
                    }

                    else {
//                   THE MOVE IS NOT VALID RESET EVERYTHING
                    copyPieces(pieces, simPieces);
                    activeP.resetPosition();
                    activeP = null;
                }
            }
        }
    }

    private void simulate() {

        canMove = false;
        validSquare = false;

//        RESET THE PIECE LIST IN EVERY LOOP
//        THIS IS BASICALLY FOR RESTORING THE REMOVED PIECE DURING THE SIMULATION

        copyPieces(pieces, simPieces);
//                   RESET THE CASTLING PIECE'S POSITION
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }
//        MAKES THE PIECE MOVE WITH THE MOUSE CURSOR UNTIL IT IS RELEASED

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

//        CHECK IF THE PIECE IS HOVERING OVER THE REACHABLE SQUARE (FOR RIGHT PIECE MOVEMENT)
        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;
//            IF HITTING A PIECE , REMOVE IT FROM THE LIST
            if (activeP.hittingP != null) {
                simPieces.remove(activeP.hittingP.getIndex());
            }
            checkCastling();

            if (isIllegal(activeP) == false && opponentCanCaptureKing()==false) {
                validSquare = true;
            }
        }
    }

    private boolean isIllegal(Piece king) {
        if (king.type == Type.KING) {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean opponentCanCaptureKing(){
        Piece king = getKing(false);
        for(Piece piece : simPieces){
            if(piece.color != king.color && piece.canMove(king.col,king.row)){
                return true;
            }
        }
        return false;
    }

    private boolean isKingInCheck() {

        Piece king = getKing(true);
        if(activeP.canMove(king.col,king.row)){
            checkingP= activeP;
            return true;
        }
        else{
            checkingP = null;
        }

        return false;
    }

    private Piece getKing(boolean opponent) {
        Piece king = null;
        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    king = piece;
                }
            }
        else {
            if(piece.type == Type.KING && piece.color == currentColor){
            king = piece;
               }
            }
        }
        return king;
    }
    private boolean isCheckmate(){
        Piece king = getKing(true);
        if(kingCanMove(king)){
            return false;
        }
        else{
//            BUT YOU STILL HAVE CHANCE!!
//            CHECK IF YOU CAN BLOCK THE ATTACK WITH YOUR PIECE

//            CHECK THE POSITION OF THE CHECKING PIECE AND THE KING IN CHECK
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);

            if(colDiff == 0){
//                THE CHECKING PIECE IS ATTACKING VERTICALLY
                if(checkingP.row < king.row){
//                    CHECKING PIECE IS ABOVE THE KING
                    for(int row = checkingP.row; row < king.row; row++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col,row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.row > king.row){
//                    THE CHECKING PIECE IS BELOW THE KING
                    for(int row = checkingP.row; row > king.row; row--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingP.col,row)){
                                return false;
                            }
                        }
                    }

                }
            }
            else if(rowDiff == 0){
//                THE CHECKING PIECE IS ATTACKING HORIZONTALLY
                if(checkingP.col < king.col){
//                    THE CHECKING PIECE IS TO THE LEFT
                    for(int col = checkingP.col; col < king.col; col++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col,checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.col > king.col){
//                    THE CHECKING PIECE IIS TO THE LEFT
                    for(int col = checkingP.col; col > king.col; col--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col,checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
            }
            else if (colDiff == rowDiff) {
//                CHECKING PIECE IS ATTACKING DIAGONALLY
                if(checkingP.row < king.row){
//                    THE CHECKING PIECE IS ABOVE THE KING
                    if(checkingP.row < king.col){
//                        THE CHECKING PIECE IS IN THE UPPER LEFT
                        for(int col = checkingP.col, row = checkingP.row; col < king.col; col++ , row++){
                            for(Piece piece: simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col,row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){
//                        THE CHECKING PIECE IS IN THE UPPER RIGHT
                        for(int col = checkingP.col, row = checkingP.row; col > king.col; col-- , row++){
                            for(Piece piece: simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col,row)){
                                    return false;
                                }
                            }
                        }
                    }

                }
                if(checkingP.row > king.row){
//                    THE CHECKING PIECE IS BELOW THE KING
                    if(checkingP.row < king.col){
//                        THE CHECKING PIECE IS IN THE LOWER LEFT
                        for(int col = checkingP.col, row = checkingP.row; col < king.col; col++ , row--){
                            for(Piece piece: simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col,row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){
//                        THE CHECKING PIECE IS IN THE LOWER RIGHT
                        for(int col = checkingP.col, row = checkingP.row; col > king.col; col-- , row--){
                            for(Piece piece: simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col,row)){
                                    return false;
                                }
                            }
                        }
                    }
                }

            }
            else{
//                THE CHECKING PIECE IS KNIGHT ATTACK CANNOT BE BLOCKED
            }

        }
        return true;

    }
    private boolean kingCanMove(Piece king){
//       SIMULATE IF THERE IS ANY SQUARE WHERE KING CAN MOVE TO
        if(isValidMove(king, -1, -1)) {return true;}
        if(isValidMove(king, 0, -1)) {return true;}
        if(isValidMove(king, 1, -1)) {return true;}
        if(isValidMove(king, -1, 0)) {return true;}
        if(isValidMove(king, 1, 0)) {return true;}
        if(isValidMove(king, -1, 1)) {return true;}
        if(isValidMove(king, 0, 1)) {return true;}
        if(isValidMove(king, 1, 1)) {return true;}

        return false;

    }
    private boolean isValidMove(Piece king, int colPlus, int rowPlus){
         boolean isValidMove = false;
//         UPDATE THE KING'S POSITION FOR A SECOND
        king.col += colPlus;
        king.row += rowPlus;

        if(king.canMove(king.col, king.row)) {
            if (king.hittingP != null) {
                simPieces.remove(king.hittingP.getIndex());
            }
            if (isIllegal(king) == false) {
                isValidMove = true;
            }
        }
//        RESET THE KING'S POSITION AND RESTORE THE REMOVED PIECE
        king.resetPosition();
        copyPieces(pieces,simPieces);
        return isValidMove;
    }
    private boolean isStalemate(){
        int count = 0;
//        COUNT THE NUMBER OF PIECES LEFT
        for(Piece piece: simPieces){
            if(piece.col != currentColor){
                count++;
            }
        }
//        IF THE COUNT IS ONE THAT MEANS ONLY KING IS LEFT
        if(count == 1){
            if(kingCanMove(getKing(true))==false){
                return true;
            }
        }
        return false;
    }
    private void checkCastling(){
        if(castlingP != null){
            if(castlingP.col == 0){
                castlingP.col += 3;
            }
            else if (castlingP.col == 7){
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }
    private void changePlayer(){
        if(currentColor == WHITE){
            currentColor=BLACK;
//            RESET BLACK'S TWO STEPPED STATUS
            for(Piece  piece: pieces){
                if(piece.color == BLACK){
                    piece.twoStepped = false;
                }
            }
        }
        else{
            currentColor = WHITE;
//            RESET WHITE'S TWO STEPPED STATUS
            for(Piece  piece: pieces){
                if(piece.color == WHITE){
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }
    private boolean canPromote(){

        if(activeP.type == Type.PAWN){
            if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7)
            {
                promoPieces.clear();
                promoPieces.add(new Rook (currentColor,9,2));
                promoPieces.add(new Knight (currentColor,9,3));
                promoPieces.add(new Bishop (currentColor,9,4));
                promoPieces.add(new Queen (currentColor,9,5));
                return true;
            }
        }

        return false;
    }
    private void promoting(){
         if(mouse.pressed){
             for(Piece piece : promoPieces){
                 if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE){
                     switch (piece.type) {
                         case ROOK:
                             simPieces.add(new Rook(currentColor, activeP.col, activeP.row));
                             break;
                         case KNIGHT:
                             simPieces.add(new Knight(currentColor, activeP.col, activeP.row));
                             break;
                         case BISHOP:
                             simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));
                             break;
                         case QUEEN:
                             simPieces.add(new Queen(currentColor, activeP.col, activeP.row));
                             break;
                         default:
                             break;
                     }
                     simPieces.remove(activeP.getIndex());
                     copyPieces(simPieces,pieces);
                     activeP = null;
                     promotion = false;
                     changePlayer();

                 }
             }
         }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //BOARD
        board.draw(g2);

        //PIECES
        for (Piece p : simPieces) {
            p.draw(g2);
        }
        if (activeP != null) {
            if (canMove) {
                if (isIllegal(activeP) || opponentCanCaptureKing()) {
                    g2.setColor(Color.gray);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                } else {
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }
//            DRAW THE ACTIVE PIECE IN THE END SO IT WON'T BE HIDDEN BY THE BOARD OR COLOURED SQUARE
            activeP.draw(g2);
        }
//        STATUS MESSAGES
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if (promotion) {
            g2.drawString("Promote to:", 840, 150);
            for (Piece piece : promoPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        } else {

            if (currentColor == WHITE) {
                g2.drawString("White's Turn", 840, 550);
                if (checkingP != null && checkingP.color == BLACK) {
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in check!", 840, 700);
                }
            } else {
                g2.drawString("Black's turn", 840, 250);
                if (checkingP != null && checkingP.color == WHITE) {
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 100);
                    g2.drawString("is in check!", 840, 150);
                }
            }
        }
        if(gameover){
            String s= "";
            if(currentColor == WHITE){
                s="Black wins";
            }
            else{
                s="White wins";
            }
            g2.setFont(new Font("Arial",Font.PLAIN,90));
            g2.setColor(Color.green);
            g2.drawString(s,200,420);
        }
        if(stalemate){
            g2.setFont(new Font("Arial",Font.PLAIN,90));
            g2.setColor(Color.lightGray);
            g2.drawString("Stalemate",200,420);
        }
    }
}

