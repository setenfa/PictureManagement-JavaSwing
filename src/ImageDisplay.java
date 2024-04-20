import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ImageDisplay {
    JPanel imagePanel;
    JScrollPane scrollPane;
    ArrayList<JLabel> smallLabels = new ArrayList<>();
    ArrayList<JTextField> smallTextFields = new ArrayList<>();
    ArrayList<JPanel> smallPanels = new ArrayList<>();
    // 记录当前面板中的图片数量
    private int numOfImages;
    private JLabel folderNameLabel;
    private JLabel numOfImagesLabel;

    public ImageDisplay() {
        imagePanel = new JPanel();
        imagePanel.setLayout(new CustomFlowLayout(5));
        scrollPane = new JScrollPane(imagePanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        JPanel infoPanel = new JPanel();
        folderNameLabel = new JLabel();
        numOfImagesLabel = new JLabel();
        infoPanel.add(folderNameLabel);
        infoPanel.add(numOfImagesLabel);
        scrollPane.setColumnHeaderView(infoPanel);
    }

    public void addImageOnPane(File[] files) {
        // 清空原有的图片
        numOfImages = 0;
        smallLabels.clear();
        smallTextFields.clear();
        smallPanels.clear();
        imagePanel.removeAll();
        if (files == null) {
            return;
        }
        folderNameLabel.setText("当前文件夹：" + files[0].getParentFile().getName());
        for (File f : files) {
            if (f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg")
                    || f.getName().endsWith(".png") || f.getName().endsWith(".gif")
                    || f.getName().endsWith(".bmp")) {
                ImageIcon icon = new ImageIcon(f.getPath());
                // 缩放图片(gif格式的图片会失去动画效果的情况未解决)
                Image image = icon.getImage();
                Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaledImage);

                JLabel label = new JLabel(icon);
                //label.setPreferredSize(new Dimension(100, 100)); // 设置JLabel的大小
                JTextField textField = new JTextField(f.getName());
                // 取消文本框的边框，设置为不可编辑，居中对齐
                textField.setBorder(null);
                textField.setEditable(false);
                textField.setHorizontalAlignment(JTextField.LEFT);
                JPanel panel = new JPanel();
                // 设定panel为箱式布局，让textfield和label垂直排列
                BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
                panel.setLayout(boxLayout);
                panel.add(label);
                panel.add(textField);

                // 添加鼠标监听器，图片选中(暂时搁置，后续添加图片选中功能)
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (panel.getBorder() != null) {
                            panel.setBorder(null);
                        } else {
                            panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                        }
                    }
                });

                smallLabels.add(label);
                smallTextFields.add(textField);
                smallPanels.add(panel);
            }
        }
        // 将小面板添加到主面板，如果没有则显示没有图片
        if (smallPanels.size() > 0) {
            for (JPanel smallPanel : smallPanels) {
                imagePanel.add(smallPanel);
                numOfImages++;
            }
        } else {
            JLabel label = new JLabel("没有图片");
            imagePanel.add(label);
        }
        numOfImagesLabel.setText("图片数量：" + numOfImages);
        // 用于动态添加或删除组件后更新面板布局和刷新显示
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public int getNumOfImages() {
        return numOfImages;
    }
}