import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class FilePano {
    public JPanel panel1;
    private JTree tree;
    private ImageDisplay imageDisplay;

    public FilePano() {
        imageDisplay = new ImageDisplay();
        panel1 = new JPanel(new BorderLayout()); // 修改布局管理器为BorderLayout
        // 获取系统的根目录
        File rootFile = new File("C:/Users");
        // 遍历根目录，获得目录树
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootFile);
        addNodes(root, rootFile);
        tree = new JTree(root);
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
                        imageDisplay.addImageOnPane(files);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(tree); // 将JTree添加到JScrollPane中
        panel1.add(scrollPane, BorderLayout.CENTER); // 将JScrollPane添加到JPanel中

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
                addNodes(node, f);
            }
        }
    }
    public ImageDisplay getImageDisplay() {
        return imageDisplay;
    }
}