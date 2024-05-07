package ui;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import function.ImageMenuItem;
import function.FileTreeCellRenderer;
import function.TreeNodeEntity;
public class FilePane {
    public JPanel panel1;
    private JTree tree;
    private ImageDisplay imageDisplay;
    private File currentFile;
    private ImageMenuItem imageMenuItem;
    // 创建一个线程池，用于遍历文件夹
    private final ExecutorService executorService;

    public FilePane() {
        this.executorService = Executors.newFixedThreadPool(2);
        imageDisplay = new ImageDisplay();
        imageMenuItem = new ImageMenuItem(imageDisplay);
        panel1 = new JPanel(new BorderLayout()); // 修改布局管理器为BorderLayout
        this.initialTree(); // 初始化树
        addListener();
        this.currentFile = File.listRoots()[0];
        JScrollPane scrollPane = new JScrollPane(tree); // 将JTree添加到JScrollPane中
        panel1.add(scrollPane, BorderLayout.CENTER); // 将JScrollPane添加到JPanel中

    }

    private void initialTree() {
        File[] rootFile = File.listRoots();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        for (File file : rootFile) {
            TreeNodeEntity treeNodeEntity = new TreeNodeEntity();
            treeNodeEntity.setName(file.getPath());
            treeNodeEntity.setPath(file.getPath());
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(treeNodeEntity);
            root.add(node);
        }
        DefaultTreeModel model = new DefaultTreeModel(root);
        model.setAsksAllowsChildren(true);
        this.tree = new JTree(model);
        tree.setRootVisible(false);
    }

    private void addListener() {
        // 设置树的渲染器，使其能够显示文件夹的图标
        tree.setCellRenderer(new FileTreeCellRenderer());
        // 设置树的监听器，使其能够展开节点时加载子节点
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            //节点将要展开时触发
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                //System.out.println("treeWillExpand");
                DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)event.getPath().getLastPathComponent();
                defaultMutableTreeNode.removeAllChildren();
                Object objTreeNodeEntity = defaultMutableTreeNode.getUserObject();
                if(TreeNodeEntity.class.isInstance(objTreeNodeEntity)) {
                    TreeNodeEntity treeNodeEntity = (TreeNodeEntity)objTreeNodeEntity;
                    String path = treeNodeEntity.getPath();
                    addNodes(defaultMutableTreeNode, new File(path));
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            tree.updateUI();
                        }
                    });
                }
            }
            //节点将要关闭时触发
            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
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
                    Object userObject = node.getUserObject();
                    //System.out.println(userObject.getClass());
                    if (userObject instanceof TreeNodeEntity) {
                        TreeNodeEntity treeNodeEntity = (TreeNodeEntity) userObject;
                        File file = new File(treeNodeEntity.getPath());
                        if (file.isDirectory()) {
                            File[] files = file.listFiles();
                            if (files == null) {
                                imageDisplay.getImagePanel().repaint();
                                return;
                            }
                            // 获取文件夹名称和图片
                            String folderName = file.getName();
                            if (folderName.isEmpty()) {
                                folderName = file.getPath();
                            }
                            imageDisplay.addImageOnPane(files, folderName);
                            imageDisplay.getImagePanel().repaint();
                            currentFile = file;
                        }
                    }
                    // 添加事件转发
                    MouseEvent event = SwingUtilities.convertMouseEvent(tree, e, imageDisplay.getScrollPane());
                    imageDisplay.getScrollPane().dispatchEvent(event);

                }
            }
        });
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
                TreeNodeEntity nodeEntity = new TreeNodeEntity();
                nodeEntity.setName(f.getName());
                nodeEntity.setPath(f.getPath());
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeEntity);
                root.add(node);
            }
        }
    }

    // 用于返回ImageDisplay对象，以便在MAIN类中添加到JFrame中
    public ImageDisplay getImageDisplay() {
        return imageDisplay;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }
}
