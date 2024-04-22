package function;
import ui.ImageDisplay;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ImageMenuItem {
    private ImageDisplay imageDisplay;
    private JPopupMenu popupMenu;
    public ImageMenuItem(ImageDisplay imageDisplay) {
        this.imageDisplay = imageDisplay;
        this.popupMenu = createPopupMenu();
    }

    public JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("删除");
        JMenuItem copyItem = new JMenuItem("复制");
        deleteItem.addActionListener((ActionEvent e) -> {
            int response = JOptionPane.showConfirmDialog(null, "确定删除选中的图片吗？", "删除图片", JOptionPane.YES_NO_OPTION);
            // 删除选中的图片
            if (response == JOptionPane.YES_OPTION) {
                List<String> selectedImagePathsCopy = new ArrayList<>(imageDisplay.getSelectedImagePaths());
                for (String path : selectedImagePathsCopy) {
                    imageDisplay.deleteImage(path);
                }
            }
        });

        popupMenu.add(deleteItem);
        imageDisplay.getImagePanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (imageDisplay.getSelectedImagePaths().isEmpty()) {
                        deleteItem.setEnabled(false);
                        copyItem.setEnabled(false);
                    } else {
                        deleteItem.setEnabled(true);
                        copyItem.setEnabled(true);
                    }
                    popupMenu.show(imageDisplay.getImagePanel(), e.getX(), e.getY());
                }
            }
        });
        return popupMenu;

    }
}
