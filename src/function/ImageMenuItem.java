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
    private List<Icon> copiedIcons;
    public ImageMenuItem(ImageDisplay imageDisplay) {
        this.copiedImagePaths = new ArrayList<>();
        this.copiedIcons = new ArrayList<>();
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
            for(String path : copiedImagePaths) {
                for(JPanel panel : imageDisplay.getSmallPanels()) {
                    if (path.equals(panel.getClientProperty("imagePath"))) {
                        copiedIcons.add(((JLabel)panel.getComponent(0)).getIcon());
                        break;
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "复制成功");
        });
        pasteItem.addActionListener((ActionEvent e) ->{
            pasteImage();
            copiedImagePaths.clear();
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
            imageDisplay.setTotalSize(imageDisplay.getTotalSize() - Files.size(path));
            if (Files.exists(path)) {
                Files.delete(path);
            }
            imageDisplay.setNumOfImages(imageDisplay.getNumOfImages() - 1);
            imageDisplay.getOriginalIcons().removeIf(icon -> imagePath.equals(icon.getDescription()));
            imageDisplay.getSmallPanels().removeIf(panel -> imagePath.equals(panel.getClientProperty("imagePath")));
            imageDisplay.getSelectedImagePaths().remove(imagePath);
            imageDisplay.refreshImages();
            // System.out.println(imageDisplay.getOriginalIcons().size());
        } catch (IOException E){
            E.printStackTrace();
        }
    }

    // 粘贴图片
    public void pasteImage(){
        long totalSize = 0;
        Icon newIcon = null;
        for (String path : copiedImagePaths) {
            Path src = Paths.get(path);
            String originalFileName = src.getFileName().toString();
            String newFileName = originalFileName;
            //System.out.println(imageDisplay.getCurrentDirectory());
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
                imageDisplay.getOriginalIcons().add(new ImageIcon(dest.toString()));
                totalSize += Files.size(src);
                JLabel label2 = new JLabel();
                label2.setIcon(copiedIcons.get(copiedImagePaths.indexOf(path)));
                label2.setPreferredSize(new Dimension(100, 100));
                JPanel panel = new JPanel();
                panel.putClientProperty("imagePath", dest.toString());
                if (newFileName.length() > 10) {
                    newFileName = newFileName.substring(0, 10) + "...";
                }
                JTextField textField = new JTextField(newFileName);
                textField.setBorder(null);
                textField.setEditable(false);
                textField.setHorizontalAlignment(SwingConstants.CENTER);
                panel.setLayout(new BorderLayout());
                panel.add(label2, BorderLayout.CENTER);
                panel.add(textField, BorderLayout.PAGE_END);
                imageDisplay.getSmallPanels().add(panel);
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            new ImageSlideshowWindow(imageDisplay.getSmallPanels().indexOf(panel), imageDisplay).showImage();
                        } else if (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1) {
                            if (panel.getBorder() != null) {
                                panel.setBorder(null);
                                imageDisplay.getSelectedImagePaths().remove(panel.getClientProperty("imagePath").toString());
                                imageDisplay.setSelectedImages(imageDisplay.getSelectedImages() - 1);
                                imageDisplay.getBottomPane().updateInfo(imageDisplay.getNumOfImages(), imageDisplay.getTotalSize(), imageDisplay.getSelectedImages());
                            } else {
                                panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                                imageDisplay.getSelectedImagePaths().add(panel.getClientProperty("imagePath").toString());
                                imageDisplay.setSelectedImages(imageDisplay.getSelectedImages() + 1);
                                imageDisplay.getBottomPane().updateInfo(imageDisplay.getNumOfImages(), imageDisplay.getTotalSize(), imageDisplay.getSelectedImages());
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON1) {
                            if (panel.getBorder() == null) {
                                for (JPanel smallPanel : imageDisplay.getSmallPanels()) {
                                    smallPanel.setBorder(null);
                                }
                                panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                                imageDisplay.getSelectedImagePaths().clear();
                                imageDisplay.getSelectedImagePaths().add(panel.getClientProperty("imagePath").toString());
                                imageDisplay.setSelectedImages(1);
                                imageDisplay.getBottomPane().updateInfo(imageDisplay.getNumOfImages(), imageDisplay.getTotalSize(), imageDisplay.getSelectedImages());
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageDisplay.setTotalSize(imageDisplay.getTotalSize() + totalSize);
        imageDisplay.setNumOfImages(imageDisplay.getNumOfImages() + copiedImagePaths.size());
        imageDisplay.getBottomPane().updateInfo(imageDisplay.getNumOfImages(), imageDisplay.getTotalSize(), imageDisplay.getSelectedImagePaths().size());
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
                //System.out.println(newPath);
                //System.out.println(oldPath);
                String oldPath1 = oldPath.toString();
                for(JPanel panel : imageDisplay.getSmallPanels()) {
                    //System.out.println(panel.getClientProperty("imagePath"));
                    if (oldPath1.equals(panel.getClientProperty("imagePath"))) {
                        //System.out.println(newName);
                        panel.putClientProperty("imagePath", newPath.toString());
                        JTextField textField = (JTextField) panel.getComponent(1);
                        panel.remove(1);
                        textField.setText(newName);
                        if (newName.length() > 10) {
                            textField.setText(newName.substring(0, 10) + "...");
                        }
                        textField.setBorder(null);
                        textField.setEditable(false);
                        textField.setHorizontalAlignment(SwingConstants.CENTER);
                        panel.add(textField, BorderLayout.PAGE_END);
                        break;
                    }
                }
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
