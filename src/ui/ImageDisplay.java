package ui;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import function.ImageSlideshowWindow;
import util.ui.CustomFlowLayout;

public class ImageDisplay {
    JPanel imagePanel;
    JScrollPane scrollPane;
    JPanel infoPanel;
    JLabel infoLabel;
    ArrayList<JLabel> smallLabels = new ArrayList<>();
    ArrayList<JTextField> smallTextFields = new ArrayList<>();
    ArrayList<JPanel> smallPanels = new ArrayList<>();
    ArrayList<ImageIcon> originalIcons = new ArrayList<>();
    // 记录当前面板中的图片数量
    private int numOfImages;
    private int selectedImages = 0;
    private final BottomPane bottomPane;
    private long totalSize = 0;
    private static final int IMAGES_PER_PAGE = 20;
    private int currentPage = 0;
    ArrayList<String> selectedImagePaths = new ArrayList<>();
    private String currentDirectory;
    private ImageSlideshowWindow slideshowWindow;

    public ImageDisplay() {
        this.infoLabel = new JLabel();
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoPanel = new JPanel(new GridBagLayout());
        infoPanel.add(infoLabel);
        this.bottomPane = new BottomPane(ImageDisplay.this);
        imagePanel = new JPanel();
        imagePanel.setLayout(new CustomFlowLayout());
        // 用于放大或缩小imagePanel刷新组件的显示
        SwingUtilities.invokeLater(() -> {
            imagePanel.revalidate();
            imagePanel.repaint();
        });
        scrollPane = new JScrollPane(imagePanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        scrollPane.setColumnHeaderView(infoPanel);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
                int extent = scrollBar.getModel().getExtent();
                if (e.getValue() + extent == scrollBar.getMaximum()) {
                    loadNextPage();
                }
            }
        });
    }

    private static boolean isGifImage(File file) throws IOException {
        return file.getName().toLowerCase().endsWith(".gif");
    }

    public void refreshImages() {
        // 清除当前显示的所有图片
        imagePanel.removeAll();
        currentPage = 0;
        // 重新加载图片
        if (!smallPanels.isEmpty()) {
            loadNextPage();
        }
        File directory = new File(currentDirectory);
        String folderName = directory.getName();
        infoLabel.setText("当前文件夹：" + folderName + "，图片数量：" + numOfImages);
        bottomPane.updateInfo(numOfImages, totalSize, selectedImages);
    }

    private static ArrayList<ImageIcon> readGifFrames(File file) throws IOException {
        ArrayList<ImageIcon> frames = new ArrayList<>();
        ImageReader reader = null;
        try (ImageInputStream input = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (readers.hasNext()) {
                reader = readers.next();
                reader.setInput(input);
                int numFrames = reader.getNumImages(true);
                for (int i = 0; i < numFrames; i++) {
                    Image frame = reader.read(i);
                    frames.add(new ImageIcon(frame));
                }
            }
        } finally {
            if (reader != null) {
                reader.dispose();
            }
        }
        return frames;
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
        originalIcons.clear();
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
                ImageIcon icon;
                ImageIcon icon1;
                try {
                    if (isGifImage(f)) {
                        ArrayList<ImageIcon> frames = readGifFrames(f);
                        icon = frames.get(0);
                        icon1 = new ImageIcon(f.getPath());
                    } else {
                        icon = new ImageIcon(f.getPath());
                        icon1 = icon;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                originalIcons.add(icon1);
                numOfImages++;
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();

                // 设置最大的宽度和高度
                int maxWidth = 100;
                int maxHeight = 100;

                int scaledWidth;
                int scaledHeight;

                // 计算缩放后的宽度和高度，保持纵横比不变

                if (originalWidth > originalHeight) {
                    scaledWidth = maxWidth;
                    scaledHeight = (int) (originalHeight * ((double) maxWidth / originalWidth));
                } else {
                    scaledHeight = maxHeight;
                    scaledWidth = (int) (originalWidth * ((double) maxHeight / originalHeight));
                }

                // 缩放图片
                Image image = icon.getImage();
                Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaledImage);

                JLabel label = new JLabel(icon);
                // 设置label的大小
                label.setPreferredSize(new Dimension(maxWidth, maxHeight));
                label.setOpaque(true);
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
                textField.setHorizontalAlignment(SwingConstants.CENTER);
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                panel.add(label, BorderLayout.CENTER);
                panel.add(textField, BorderLayout.PAGE_END);
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
            loadNextPage();
        } else {
            currentPage = 0;
        }
        infoLabel.setText("当前文件夹：" + folderName + "，图片数量：" + numOfImages);
        bottomPane.updateInfo(numOfImages, totalSize, selectedImages);
        // 用于动态添加或删除组件后更新面板布局和刷新显示
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private void loadNextPage() {
        int start = currentPage * IMAGES_PER_PAGE;
        int end = Math.min(start + IMAGES_PER_PAGE, smallPanels.size());
        for (int i = start; i < end; i++) {
            imagePanel.add(smallPanels.get(i));
        }
        imagePanel.revalidate();
        imagePanel.repaint();
        currentPage++;
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
    public int getNumOfImages() {
        return numOfImages;
    }
    public void setNumOfImages(int numOfImages) {
        this.numOfImages = numOfImages;
    }
    public long getTotalSize() {
        return totalSize;
    }
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public ArrayList<ImageIcon> getOriginalIcons() {
        return originalIcons;
    }
    public void setOriginalIcons(ArrayList<ImageIcon> originalIcons) {
        this.originalIcons = originalIcons;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    public int getSelectedImages() {
        return selectedImages;
    }
    public void setSelectedImages(int selectedImages) {
        this.selectedImages = selectedImages;
    }
}