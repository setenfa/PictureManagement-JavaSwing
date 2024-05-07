package function;

import function.TreeNodeEntity;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;

public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
    private FileSystemView fileSystemView;

    public FileTreeCellRenderer() {
        fileSystemView = FileSystemView.getFileSystemView();
    }
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof TreeNodeEntity) {
            TreeNodeEntity treeNodeEntity = (TreeNodeEntity) node.getUserObject();
            File file = new File(treeNodeEntity.getPath());
            setIcon(fileSystemView.getSystemIcon(file));
            setText(fileSystemView.getSystemDisplayName(file));
        }
        return c;
    }
}
