package ui;

import javax.swing.*;
public class BottomPane extends JPanel{
    private JLabel infoLabel;

    public BottomPane(){
        infoLabel = new JLabel();
        infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(infoLabel);
    }

    public void updateInfo(int totalImages, long totalSize, int selectedImages){
        double sizeInMB = totalSize / 1024.0 / 1024.0;
        infoLabel.setText(totalImages + "张图片(" + String.format("%.2f", sizeInMB) + "MB) - 选中" + selectedImages + "张图片");
    }
}
