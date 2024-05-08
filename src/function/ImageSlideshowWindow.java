package function;

import ui.ImageDisplay;

import javax.swing.*;
import java.awt.*;

public class ImageSlideshowWindow extends JFrame {
    private ImageDisplay imageDisplay;
    // 记录arraylist下标
    private int index;
    // 存放图片
    private JLabel imageLabel;
    private boolean isAutoPlaying = false;
    private Thread autoPlayThread;

    public ImageSlideshowWindow(int index, ImageDisplay imageDisplay) {
        this.imageDisplay = imageDisplay;
        this.index = index;
        this.imageLabel = new JLabel();
        // 前进、后退、放大、缩小、自动播放按钮
        JToolBar toolBar = initToolBar();
        add(imageLabel, "Center");
        add(toolBar, "South");
        setTitle("幻灯片播放");
        // 全屏
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public JToolBar initToolBar() {
        JButton previousButton = new JButton("\ue72b");
        previousButton.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 24));
        JButton nextButton = new JButton("\ue72a");
        nextButton.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 24));
        JButton zoomInButton = new JButton("\ue71e");
        zoomInButton.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 24));
        JButton zoomOutButton = new JButton("\ue71f");
        zoomOutButton.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 24));
        JButton autoPlayButton = new JButton("\ue768");
        autoPlayButton.setFont(new Font("Segoe MDL2 Assets", Font.PLAIN, 24));
        JToolBar toolB = new JToolBar();
        previousButton.addActionListener(e -> {
            if (index == 0) {
                JOptionPane.showMessageDialog(null, "已经是第一张图片了");
            } else {
                index--;
                showImage();
            }
        });
        nextButton.addActionListener(e -> {
            if (index == imageDisplay.getSmallPanels().size() - 1) {
                JOptionPane.showMessageDialog(null, "已经是最后一张图片了");
            } else {
                index++;
                showImage();
            }
        });
        zoomInButton.addActionListener(e -> {
            ImageIcon originalIcon = (ImageIcon)imageLabel.getIcon();
            ImageIcon zoomedIcon = zoomIn(originalIcon, 1.2);
            imageLabel.setIcon(zoomedIcon);
        });
        zoomOutButton.addActionListener(e -> {
            ImageIcon originalIcon = (ImageIcon)imageLabel.getIcon();
            ImageIcon zoomedIcon = zoomOut(originalIcon, 1.2);
            imageLabel.setIcon(zoomedIcon);
        });
        autoPlayButton.addActionListener(e -> {
            if ("\ue768".equals(autoPlayButton.getText())) {
                autoPlayButton.setText("\ue769");
                autoPlay();
            } else {
                autoPlayButton.setText("\ue768");
                stopAutoPlay();
            }
        });
        toolB.add(previousButton);
        toolB.add(nextButton);
        toolB.add(zoomInButton);
        toolB.add(zoomOutButton);
        toolB.add(autoPlayButton);
        return toolB;
    }

    public void showImage() {
        Icon imageIcon = imageDisplay.getOriginalIcons().get(index);
        imageLabel.setIcon(imageIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER); // 设置水平居中
        imageLabel.setVerticalAlignment(SwingConstants.CENTER); // 设置垂直居中
    }

    // 自动播放
    public void autoPlay() {
        isAutoPlaying = true;
        autoPlayThread = new Thread(() -> {
            while (isAutoPlaying && !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (index == imageDisplay.getSmallPanels().size() - 1) {
                    index = 0;
                } else {
                    index++;
                }
                showImage();
            }
        });
        autoPlayThread.start();
    }

    public ImageIcon zoomIn(ImageIcon imageIcon, double radio) {
        Image originalImage = imageIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance((int)(originalImage.getWidth(null) * radio),
                (int)(originalImage.getHeight(null) * radio), Image.SCALE_DEFAULT);
        return new ImageIcon(scaledImage);
    }

    public ImageIcon zoomOut(ImageIcon imageIcon, double radio) {
        Image originalImage = imageIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance((int)(originalImage.getWidth(null) / radio),
                (int)(originalImage.getHeight(null) / radio), Image.SCALE_DEFAULT);
        return new ImageIcon(scaledImage);
    }

    public void stopAutoPlay() {
        isAutoPlaying = false;
        if (autoPlayThread != null) {
            autoPlayThread.interrupt();
        }
    }
}
