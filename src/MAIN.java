import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MAIN {
    public static void main(String[] args) {
        FilePano fileNode = new FilePano();
        JFrame frame = new JFrame("File Tree");
        frame.add(fileNode.panel1, BorderLayout.WEST);
        frame.add(fileNode.getImageDisplay().getScrollPane(), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}