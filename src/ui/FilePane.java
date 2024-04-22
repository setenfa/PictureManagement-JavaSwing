package ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import function.ImageMenuItem;
public class FilePane {
    public JPanel panel1;
    private JTree tree;
    private ImageDisplay imageDisplay;
    private File currentFile;
    private ImageMenuItem imageMenuItem;
    // 创建一个线程池，用于遍历文件夹
    private final ExecutorService executorService;
    public FilePane() {
        this.executorService = Executors.newFixedThreadPool(10);
        imageDisplay = new ImageDisplay();
        imageMenuItem = new ImageMenuItem(imageDisplay);
        panel1 = new JPanel(new BorderLayout()); // 修改布局管理器为BorderLayout
        this.initialTree("C:/Users"); // 初始化树
        this.addListener();
        JScrollPane scrollPane = new JScrollPane(tree); // 将JTree添加到JScrollPane中
        panel1.add(scrollPane, BorderLayout.CENTER); // 将JScrollPane添加到JPanel中

    }

    private boolean initialTree(String path) {
        // 获取系统的根目录(暂时用C:/Users代替)
        boolean isVaildPath = true;
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            isVaildPath = false;
            rootFile = new File("C:/Users");
        }
        this.currentFile = rootFile;
        // 遍历根目录，获得目录树
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootFile);
        addNodes(root, rootFile);
        tree = new JTree(root);
        return isVaildPath;
    }

    private void addListener(){
        // 设置树的节点，使其被点击后能返回文件对象，以便后续操作
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                File file = (File) node.getUserObject();
                setText(file.getName());
                return this;
            }
        });
            // 设置树的监听器，使其能够双击文件夹时显示文件夹中的图片
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (node == null) {
                        return;
                    }
                    File file = (File) node.getUserObject();
                    if (file.isDirectory()) {
                        File[] files = file.listFiles();
                        // 获取文件夹名称和图片
                        String folderName = file.getName();
                        imageDisplay.addImageOnPane(files, folderName);
                    }
                    // 添加事件转发
                    currentFile = file;
                    MouseEvent event = SwingUtilities.convertMouseEvent(tree, e, imageDisplay.getScrollPane());
                    imageDisplay.getScrollPane().dispatchEvent(event);
                }
            }
        });
    }

    public boolean updateTree(String path) {
        boolean isSucceed = initialTree(path);
        for (Component comp : panel1.getComponents()) {
            if (comp instanceof JScrollPane) {
                panel1.remove(comp);
            }
        }
        this.addListener();
        panel1.add(new JScrollPane(tree), BorderLayout.CENTER);
        panel1.repaint();
        return isSucceed;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    private void addNodes(DefaultMutableTreeNode root, File file) {
        File[] subFiles = file.listFiles();
        if (subFiles == null) {
            return;
        }
        for (File f : subFiles) {
            if (f.isHidden()) {
                continue;
            }
            if (f.isDirectory()) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(f);
                root.add(node);
                // 递归调用，遍历文件夹
                executorService.submit(() -> addNodes(node, f));
            }
        }
    }
    // 用于返回ImageDisplay对象，以便在MAIN类中添加到JFrame中
    public ImageDisplay getImageDisplay() {
        return imageDisplay;
    }
}