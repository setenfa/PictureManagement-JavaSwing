import javax.swing.*;
import java.awt.*;

public class MAIN {
    public static void main(String[] args) {
        FilePane fileNode = new FilePane();
        JFrame frame = new JFrame("电子图片管理程序");
        frame.add(fileNode.panel1, BorderLayout.WEST);
        frame.add(fileNode.getImageDisplay().getScrollPane(), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}