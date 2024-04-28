package function;

import ui.ImageDisplay;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImageSlideshowWindow extends JFrame {
    private ImageDisplay imageDisplay;
    // 记录arraylist下标
    private int index;
    // 存放图片
    private JLabel imageLabel;

    public ImageSlideshowWindow(int index, ImageDisplay imageDisplay) {
        this.imageDisplay = imageDisplay;
        this.index = index;
        this.imageLabel = new JLabel();
        // 前进、后退、放大、缩小、自动播放按钮
        JToolBar toolBar = initToolBar();
        add(imageLabel, "Center");
        add(toolBar, "South");
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
                    System.out.println(index);
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
                    System.out.println(index);
                    showImage();
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
        Icon imageIcon = imageDisplay.getSmallLabels().get(index).getIcon();
        imageLabel.setIcon(imageIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER); // 设置水平居中
        imageLabel.setVerticalAlignment(SwingConstants.CENTER); // 设置垂直居中
    }
}
