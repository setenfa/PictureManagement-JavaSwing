import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class ImageDisplay {
    JPanel imagePanel;
    JScrollPane scrollPane;
    ArrayList<JLabel> smallLabels = new ArrayList<>();
    ArrayList<JTextField> smallTextFields = new ArrayList<>();
    ArrayList<JPanel> smallPanels = new ArrayList<>();

    public ImageDisplay() {
        imagePanel = new JPanel();
        imagePanel.setLayout(new CustomFlowLayout(5));
        scrollPane = new JScrollPane(imagePanel);
    }

    public void addImageOnPane(File[] files) {
        smallLabels.clear();
        smallTextFields.clear();
        smallPanels.clear();
        imagePanel.removeAll();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg")
                    || f.getName().endsWith(".png") || f.getName().endsWith(".gif")
                    || f.getName().endsWith(".bmp")) {
                ImageIcon icon = new ImageIcon(f.getPath());
                // 缩放图片
                Image image = icon.getImage();
                Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaledImage);

                JLabel label = new JLabel(icon);
                //label.setPreferredSize(new Dimension(100, 100)); // 设置JLabel的大小
                JTextField textField = new JTextField(f.getName());
                // 取消文本框的边框，设置为不可编辑，居中对齐
                textField.setBorder(null);
                textField.setEditable(false);
                textField.setHorizontalAlignment(JTextField.CENTER);
                JPanel panel = new JPanel();
                BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
                panel.setLayout(boxLayout);
                panel.add(label);
                panel.add(textField);

                smallLabels.add(label);
                smallTextFields.add(textField);
                smallPanels.add(panel);
            }
        }
        // 将小面板添加到主面板，如果没有则显示没有图片
        if (smallPanels.size() > 0) {
            for (JPanel smallPanel : smallPanels) {
                imagePanel.add(smallPanel);
            }
        } else {
            JLabel label = new JLabel("没有图片");
            imagePanel.add(label);
        }
        // 用于动态添加或删除组件后更新面板布局和刷新显示
        imagePanel.revalidate();
        imagePanel.repaint();
    }
    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}