package function;
import ui.ImageDisplay;

import javax.swing.*;
import java.awt.*;
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
            JOptionPane.showMessageDialog(null, "删除成功");
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
            if (imageDisplay.getSelectedImagePaths().size() == 1) {
                String newName = JOptionPane.showInputDialog("请输入新的文件名");
                if (newName == null) {
                    return;
                }
                if (newName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "文件名不能为空");
                    return;
                }
                renameImage(newName, 1, 1);
                JOptionPane.showMessageDialog(null, "重命名成功");
            } else {
                JPanel panel = new JPanel(new GridLayout(3, 2));
                JTextField prefixField = new JTextField();
                JTextField startNumberField = new JTextField();
                JTextField digitField = new JTextField();
                panel.add(new JLabel("名称前缀: "));
                panel.add(prefixField);
                panel.add(new JLabel("起始编号: "));
                panel.add(startNumberField);
                panel.add(new JLabel("编号位数: "));
                panel.add(digitField);
                int result = JOptionPane.showOptionDialog(null, panel, "批量重命名",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                if (result == JOptionPane.OK_OPTION) {
                    String  prefix = prefixField.getText();
                    if (prefix == null || prefix.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "文件名不能为空");
                        return;
                    }
                    int startNumber = Integer.parseInt(startNumberField.getText());
                    int digit = Integer.parseInt(digitField.getText());
                    renameImage(prefix, startNumber, digit);
                    JOptionPane.showMessageDialog(null, "重命名成功");
                }
            }
        });
        popupMenu.add(deleteItem);
        popupMenu.add(copyItem);
        popupMenu.add(pasteItem);
        popupMenu.add(renameItem);
        imageDisplay.getImagePanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (imageDisplay.getSelectedImagePaths().isEmpty()) {
                        deleteItem.setEnabled(false);
                        copyItem.setEnabled(false);
                        renameItem.setEnabled(false);
                    } else {
                        deleteItem.setEnabled(true);
                        copyItem.setEnabled(true);
                        renameItem.setEnabled(true);
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
            String originalFileName = src.getFileName().toString();
            String newFileName = originalFileName;
            Path dest = Paths.get(imageDisplay.getCurrentDirectory() + "/" + newFileName);
            int counter = 1;
            while (Files.exists(dest)) {
                int dotIndex = originalFileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    String nameWithoutExtension = originalFileName.substring(0, dotIndex);
                    String extension = originalFileName.substring(dotIndex);
                    newFileName = nameWithoutExtension + "(" + counter + ")" + extension;
                } else {
                    newFileName = originalFileName + "(" + counter + ")";
                }
                dest = Paths.get(imageDisplay.getCurrentDirectory() + "/" + newFileName);
                counter++;
            }
            try {
                Files.copy(src, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageDisplay.refreshImages();
    }

    // 重命名图片
    public void renameImage(String prefix, int startNumber, int digit) {
        List<String> selectedImagePathsCopy = new ArrayList<>(imageDisplay.getSelectedImagePaths());
        int counter = startNumber;
        for(String path : selectedImagePathsCopy) {
            Path oldPath = Paths.get(path);
            String extension = "";
            int i = path.lastIndexOf('.');
            if (i > 0) {
                extension = path.substring(i + 1);
            }
            String newName;
            if (selectedImagePathsCopy.size() > 1) {
                String format = "%0" + digit + "d";
                newName = prefix + String.format(format, counter);
            } else {
                newName = prefix;
            }
            newName += "." + extension;
            Path newPath = oldPath.resolveSibling(newName);
            try {
                Files.move(oldPath, newPath);
                int index = imageDisplay.getSelectedImagePaths().indexOf(path);
                imageDisplay.getSelectedImagePaths().set(index, newPath.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        }
        imageDisplay.refreshImages();
    }
}
