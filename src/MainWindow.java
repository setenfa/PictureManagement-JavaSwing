import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import ui.Button;

public class MainWindow {

    private JPanel main;

    private FilePane fileNode;

    private JPanel pathBar;

    public MainWindow(FilePane fileNode, int width, int height) {
        this.fileNode = fileNode;
        initialPathBar(width);
        initialMainPanel(width, height);
        initialMainWindow(width, height);
        this.fileNode.getImageDisplay().getScrollPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updatePath();
            }
        });
    }

    private void initialMainWindow(int width, int height) {
        JFrame mainWindow = new JFrame("电子图片管理程序");
        mainWindow.setSize(width, height);
        mainWindow.add(this.main);
        mainWindow.setBackground(Color.WHITE);
        mainWindow.setVisible(true);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initialMainPanel(int width, int height) {
        this.main = new JPanel(new MainWindowLayout());
        this.main.setBounds(0, 0, width, height);
        this.main.add(this.fileNode.panel1);
        this.main.add(this.fileNode.getImageDisplay().getScrollPane());
        this.main.add(this.pathBar);
    }

    // 初始化路径栏
    private void initialPathBar(int width) {
        this.pathBar = new JPanel();
        this.pathBar.setBounds(0, 0, width, 50);
        this.pathBar.setBackground(Color.WHITE);
        this.pathBar.setLayout(null);
        JTextField path = new JTextField(fileNode.getCurrentFile().getPath());
        path.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        path.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        path.setBounds(30, 0, width - 24, 24);
        Button button1 = new Button("后退", 24, 24);
        Button button2 = new Button("前进", 24, 24);
        Button button3 = new Button("上一级", 24, 24);
        Button button4 = new Button("前往", 24, 24);
        this.pathBar.add(button1);
        this.pathBar.add(button2);
        this.pathBar.add(button3);
        this.pathBar.add(path);
        this.pathBar.add(button4);
    }

    //更新路径
    private void updatePath() {
        Component[] components = pathBar.getComponents();
        for (Component component : components) {
            if (component instanceof JTextField path && component.isVisible()) {
                path.setText(fileNode.getCurrentFile().getPath());
            }
        }
    }

    private void initialToolBar(int width) {

    }

    class MainWindowLayout extends MyLayoutManager {
        @Override
        public void layoutContainer(Container parent) {
            Component[] components = parent.getComponents();
            for (Component component : components) {
                if (component.equals(fileNode.panel1)) {
                    component.setBounds(component.getX(), component.getY(), component.getWidth(), main.getHeight() - pathBar.getHeight());
                } else if (component.equals(fileNode.getImageDisplay().getScrollPane())) {
                    component.setBounds(component.getX(), component.getY(), main.getWidth() - fileNode.panel1.getWidth(), main.getHeight() - pathBar.getHeight());
                } else if (component.equals(pathBar)) {
                    Component[] pathBarComponents = pathBar.getComponents();
                    int width = 4;
                    int height = 4;
                    Component path = null;
                    for (Component comp : pathBarComponents) {
                        if (comp instanceof Button) {
                            if (width >= main.getWidth() && path != null) {
                                path.setSize(path.getWidth() - comp.getWidth() - 8, path.getHeight());
                                width -= comp.getWidth() + 4;
                                comp.setBounds(path.getX() + path.getWidth() + 4, height, comp.getWidth(), comp.getHeight());
                            } else {
                                comp.setBounds(width, height, comp.getWidth(), comp.getHeight());
                            }
                            width += comp.getWidth() + 4;
                        } else if (comp instanceof JTextField) {
                            path = comp;
                            comp.setBounds(width, height, main.getWidth() - width, comp.getHeight());
                            width += comp.getWidth() + 4;
                        }

                    }
                    component.setBounds(0, 0, main.getWidth(), 50);
                }
            }
        }

        @Override
        public void addLayoutComponent(Component comp, Object constraints) {
            super.addLayoutComponent(comp, constraints);
            if (comp.equals(fileNode.panel1)) {
                comp.setBounds(0, 50, (int) (main.getWidth() * 0.3), main.getHeight() - pathBar.getHeight());
            }
            if (comp.equals(fileNode.getImageDisplay().getScrollPane())) {
                comp.setBounds((int) (main.getWidth() * 0.3 + 2), 50, (int) (main.getWidth() * 0.7), main.getHeight() - pathBar.getHeight());
                comp.setPreferredSize(new Dimension((int) (main.getWidth() * 0.7), main.getHeight()));
            }
            if (comp.equals(pathBar)) {
                comp.setBounds(0, 0, main.getWidth(), 50);
            }
        }
    }
}



