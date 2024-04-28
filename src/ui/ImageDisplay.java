package ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import function.ImageSlideshowWindow;
public class ImageDisplay {
    JPanel imagePanel;
    JScrollPane scrollPane;
    JPanel infoPanel;
    JLabel infoLabel;
    ArrayList<JLabel> smallLabels = new ArrayList<>();
    ArrayList<JTextField> smallTextFields = new ArrayList<>();
    ArrayList<JPanel> smallPanels = new ArrayList<>();
    // 记录当前面板中的图片数量
    private int numOfImages;
    private int selectedImages = 0;
    private BottomPane bottomPane;
    private long totalSize = 0;
    ArrayList<String> selectedImagePaths = new ArrayList<>();
    private String currentDirectory;
    private ImageSlideshowWindow slideshowWindow;

    public ImageDisplay() {
        this.infoLabel = new JLabel();
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoPanel = new JPanel(new GridBagLayout());
        infoPanel.add(infoLabel);
        this.bottomPane = new BottomPane();
        imagePanel = new JPanel();
        imagePanel.setLayout(new CustomFlowLayout(5));
        // 用于放大或缩小imagePanel刷新组件的显示
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                imagePanel.revalidate();
                imagePanel.repaint();
            }
        });
        scrollPane = new JScrollPane(imagePanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        scrollPane.setColumnHeaderView(infoPanel);
    }

    public void addImageOnPane(File[] files, String folderName) {
        // 清空原有的图片
        numOfImages = 0;
        selectedImages = 0;
        totalSize = 0;

        smallLabels.clear();
        smallTextFields.clear();
        smallPanels.clear();
        imagePanel.removeAll();
        if (files == null) {
            return;
        }
        //folderNameLabel.setText("当前文件夹：" + files[0].getParentFile().getName());
        if (files.length == 0) {
            infoLabel.setText("当前文件夹：" + folderName + "，图片数量：" + numOfImages);
            return;
        } else {
            currentDirectory = files[0].getParent();
        }
        for (File f : files) {
            if (f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg")
                    || f.getName().endsWith(".png") || f.getName().endsWith(".gif")
                    || f.getName().endsWith(".bmp")) {
                totalSize += f.length();
                ImageIcon icon = new ImageIcon(f.getPath());
                // 缩放图片(gif格式的图片会失去动画效果的情况未解决)
                Image image = icon.getImage();
                Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaledImage);

                JLabel label = new JLabel(icon);
                smallLabels.add(label);
                // 避免文件名过长导致图片变形
                String fileName = f.getName();
                if (fileName.length() > 10) {
                    fileName = fileName.substring(0, 10) + "...";
                }
                JTextField textField = new JTextField(fileName);
                // 取消文本框的边框，设置为不可编辑，居中对齐
                textField.setBorder(null);
                textField.setEditable(false);
                textField.setHorizontalAlignment(JTextField.LEFT);
                JPanel panel = new JPanel();
                // 设定panel为箱式布局，让textField和label垂直排列
                panel.setLayout(new BorderLayout());
                panel.add(label, BorderLayout.CENTER);
                panel.add(textField, BorderLayout.SOUTH);
                panel.putClientProperty("imagePath", f.getPath());
                // 添加鼠标监听器，图片选中(暂时搁置，后续添加图片选中功能)
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ImageDisplay thisImageDisplay = ImageDisplay.this;
                        if (e.getClickCount() == 2) {
                            // 双击打开图片
                            // System.out.println("点击了两次");
                            if (slideshowWindow != null) {
                                slideshowWindow.dispose();
                            }
                            slideshowWindow = new ImageSlideshowWindow(smallPanels.indexOf(panel), thisImageDisplay);
                            slideshowWindow.showImage();
                        } else if (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1) {
                            // 如果按下了Ctrl键和鼠标左键，检查面板是否已经被选中
                            if (panel.getBorder() != null) {
                                // 如果面板已经被选中，取消选中
                                panel.setBorder(null);
                                selectedImages--;
                                bottomPane.updateInfo(numOfImages, totalSize, selectedImages);
                                selectedImagePaths.remove(panel.getClientProperty("imagePath").toString());
                            } else {
                                // 如果面板没有被选中，选中面板
                                panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                                selectedImages++;
                                bottomPane.updateInfo(numOfImages, totalSize, selectedImages);
                                selectedImagePaths.add(panel.getClientProperty("imagePath").toString());
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON1) {
                            // 如果没有按下Ctrl键或鼠标左键，将所有面板的边框设为null，然后只为被点击的面板设置边框
                            // 检查面板是否已经被选中
                            if (panel.getBorder() == null) {
                                // 如果面板没有被选中，选中面板
                                for (JPanel smallPanel : smallPanels) {
                                    smallPanel.setBorder(null);
                                }
                                panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                                selectedImages = 1;
                                bottomPane.updateInfo(numOfImages, totalSize, selectedImages);
                                selectedImagePaths.clear();
                                selectedImagePaths.add(panel.getClientProperty("imagePath").toString());
                            }
                        }
                    }
                });
                imagePanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            // 当用户点击imagePanel时，取消所有面板的选中状态
                            for(JPanel smallPanel : smallPanels) {
                                smallPanel.setBorder(null);
                            }
                            selectedImages = 0;
                            selectedImagePaths.clear();
                            bottomPane.updateInfo(numOfImages, totalSize, selectedImages);
                        }
                    }
                });

                smallPanels.add(panel);
            }
        }
        // 将小面板添加到主面板，如果没有则显示没有图片
        if (!smallPanels.isEmpty()) {
            for (JPanel smallPanel : smallPanels) {
                imagePanel.add(smallPanel);
                numOfImages++;
            }
        }
        infoLabel.setText("当前文件夹：" + folderName + "，图片数量：" + numOfImages);
        bottomPane.updateInfo(numOfImages, totalSize, selectedImages);
        // 用于动态添加或删除组件后更新面板布局和刷新显示
        imagePanel.revalidate();
        imagePanel.repaint();
    }
    // 删除图片,并更新面板


    public void refreshImages() {
        // 清除当前显示的所有图片
        File directory = new File(currentDirectory);
        File[] files = directory.listFiles(file -> file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")
                || file.getName().endsWith(".png") || file.getName().endsWith(".gif")
                || file.getName().endsWith(".bmp"));
        addImageOnPane(files, directory.getName());

    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public BottomPane getBottomPane() {
        return bottomPane;
    }
    public ArrayList<String> getSelectedImagePaths() {
        return selectedImagePaths;
    }
    public JPanel getImagePanel() {
        return imagePanel;
    }
    public ArrayList<JPanel> getSmallPanels() {
        return smallPanels;
    }
    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public ArrayList<JLabel> getSmallLabels() {
        return smallLabels;
    }

}