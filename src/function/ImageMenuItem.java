package function;
import ui.ImageDisplay;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ImageMenuItem {
    private ImageDisplay imageDisplay;
    private JPopupMenu popupMenu;
    private List<String> copiedImagePaths;
    public ImageMenuItem(ImageDisplay imageDisplay) {
        this.copiedImagePaths = new ArrayList<>();
        this.imageDisplay = imageDisplay;
        this.popupMenu = createPopupMenu();
    }

    public JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("删除");
        JMenuItem copyItem = new JMenuItem("复制");
        JMenuItem pasteItem = new JMenuItem("粘贴");
        JMenuItem renameItem = new JMenuItem("重命名");
        deleteItem.addActionListener((ActionEvent e) -> {
            int response = JOptionPane.showConfirmDialog(null, "确定删除选中的图片吗？", "删除图片", JOptionPane.YES_NO_OPTION);
            // 删除选中的图片
            if (response == JOptionPane.YES_OPTION) {
                List<String> selectedImagePathsCopy = new ArrayList<>(imageDisplay.getSelectedImagePaths());
                for (String path : selectedImagePathsCopy) {
                    deleteImage(path);
                }
            }
        });
        copyItem.addActionListener((ActionEvent e) -> {
            copiedImagePaths.clear();
            copiedImagePaths.addAll(imageDisplay.getSelectedImagePaths());
            JOptionPane.showMessageDialog(null, "复制成功");
        });
        pasteItem.addActionListener((ActionEvent e) ->{
            pasteImage();
            JOptionPane.showMessageDialog(null, "粘贴成功");
        });
        renameItem.addActionListener((ActionEvent e) -> {

        });
        popupMenu.add(deleteItem);
        popupMenu.add(copyItem);
        popupMenu.add(pasteItem);
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
                    pasteItem.setEnabled(!copiedImagePaths.isEmpty());
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        return popupMenu;
    }
    public void deleteImage(String imagePath){
        Path path = Paths.get(imagePath);
        try{
            if (Files.exists(path)) {
                Files.delete(path);
            }
            imageDisplay.getSmallPanels().removeIf(panel -> imagePath.equals(panel.getClientProperty("imagePath")));
            imageDisplay.getSelectedImagePaths().remove(imagePath);
            imageDisplay.refreshImages();
        } catch (IOException E){
            E.printStackTrace();
        }
    }

    // 粘贴图片
    public void pasteImage(){
        for (String path : copiedImagePaths) {
            Path src = Paths.get(path);
            Path dest = Paths.get(imageDisplay.getCurrentDirectory() + "/" + src.getFileName());
            try {
                Files.copy(src, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageDisplay.refreshImages();
    }
}
