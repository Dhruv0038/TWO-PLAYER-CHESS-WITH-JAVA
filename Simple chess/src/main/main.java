package main;
import javax.swing.JFrame;

public class main {
    public static void main(String[]args){
        JFrame window = new JFrame ("Simple chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false); // to make the window non-resizable
        GamePanel gp =new GamePanel();
        window.add(gp);
        window.pack();

        window.setLocationRelativeTo(null); // this is for getting the window in middle of the screen
        window.setVisible(true);

        gp.launchGame();
}
}