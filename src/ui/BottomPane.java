package ui;

import function.ImageSlideshowWindow;

import javax.swing.*;
import java.awt.*;

public class BottomPane extends JPanel{
    private JLabel infoLabel;
    private final ImageDisplay imageDisplay;
    private JButton ImageSlideshowWindowButton;

    public BottomPane(ImageDisplay imageDisplay) {
        this.imageDisplay = imageDisplay;
        setLayout(new BorderLayout());
        infoLabel = new JLabel();
        ImageSlideshowWindowButton = new JButton("\ue768");
        ImageSlideshowWindowButton.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 18));
        ImageSlideshowWindowButton.addActionListener(e -> {
            if (imageDisplay.getOriginalIcons().size() == 0) {
                JOptionPane.showMessageDialog(null, "该文件夹暂无图片，无法播放");
            } else {
                new ImageSlideshowWindow(0, imageDisplay).showImage();
            }
        });
        infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(infoLabel, BorderLayout.WEST);
        add(ImageSlideshowWindowButton, BorderLayout.EAST);
        infoLabel.setText("0张图片(0MB) - 选中0张图片");
    }

    public void updateInfo(int totalImages, long totalSize, int selectedImages){
        double sizeInMB = totalSize / 1024.0 / 1024.0;
        infoLabel.setText(totalImages + "张图片(" + String.format("%.2f", sizeInMB) + "MB) - 选中" + selectedImages + "张图片");
    }
}
