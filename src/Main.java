import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Main {
    public static void main(String[] args) {
        FilePane fileNode = new FilePane();
        JFrame frame = new JFrame("电子图片管理程序");
        frame.add(fileNode.panel1, BorderLayout.WEST);
        frame.add(fileNode.getImageDisplay().getScrollPane(), BorderLayout.CENTER);
        frame.add(fileNode.getImageDisplay().getBottomPane(), BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // 让frame在屏幕中居中显示
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // 添加组件监听器
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // 当窗口大小改变时，强制重新布局
                fileNode.getImageDisplay().getScrollPane().revalidate();
                fileNode.getImageDisplay().getScrollPane().repaint();
            }
        });
    }
}