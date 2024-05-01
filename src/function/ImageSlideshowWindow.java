package function;

import ui.ImageDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

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
        JButton previousButton = new JButton("<--");
        JButton nextButton = new JButton("-->");
        JButton zoomInButton = new JButton("放大");
        JButton zoomOutButton = new JButton("缩小");
        JButton autoPlayButton = new JButton("自动播放");
        JToolBar toolB = new JToolBar();
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index == 0) {
                    JOptionPane.showMessageDialog(null, "已经是第一张图片了");
                } else {
                    index--;
                    showImage();
                }
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index == imageDisplay.getSmallPanels().size() - 1) {
                    JOptionPane.showMessageDialog(null, "已经是最后一张图片了");
                } else {
                    index++;
                    showImage();
                }
            }
        });
        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon originalIcon = (ImageIcon)imageLabel.getIcon();
                ImageIcon zoomedIcon = zoomIn(originalIcon, 1.2);
                imageLabel.setIcon(zoomedIcon);
            }
        });
        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon originalIcon = (ImageIcon)imageLabel.getIcon();
                ImageIcon zoomedIcon = zoomOut(originalIcon, 1.2);
                imageLabel.setIcon(zoomedIcon);
            }
        });
        autoPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("自动播放".equals(autoPlayButton.getText())) {
                    autoPlayButton.setText("  ||  ");
                    autoPlay();
                } else {
                    autoPlayButton.setText("自动播放");
                    stopAutoPlay();
                }
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
        BufferedImage image = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        imageIcon.paintIcon(null, g, 0, 0);
        g.dispose();
        BufferedImage resizedImage = ImageResizer.resize(image, radio);
        return new ImageIcon(resizedImage);
    }

    public ImageIcon zoomOut(ImageIcon imageIcon, double radio) {
        BufferedImage image = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        imageIcon.paintIcon(null, g, 0, 0);
        g.dispose();
        BufferedImage resizedImage = ImageResizer.resize(image, 1 / radio);
        return new ImageIcon(resizedImage);
    }

    public void stopAutoPlay() {
        isAutoPlaying = false;
        if (autoPlayThread != null) {
            autoPlayThread.interrupt();
        }
    }
}
