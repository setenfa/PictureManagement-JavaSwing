package ui;

import java.awt.*;

public class CustomFlowLayout extends FlowLayout {

    // 用途：
    @Override
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            int gap = this.getHgap();
            int vga = this.getVgap();
            Insets insets = target.getInsets();
            Rectangle bounds = target.getBounds();
            int visibleWidth = bounds.width - (insets.left + insets.right + gap * 2);
            int maxWidth = visibleWidth - (insets.left + insets.right + gap * 2);
            //System.out.println(visibleWidth);
            int nMembers = target.getComponentCount();
            int x = 0;
            int y = insets.top + vga;
            int rowMaxHeight = 0;

            for (int i = 0; i < nMembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    m.setSize(d.width, d.height);

                    if ((x == 0) || ((x + d.width) <= maxWidth)) {
                        if (x > 0) {
                            x += gap;
                        }
                        x += d.width;
                        rowMaxHeight = Math.max(rowMaxHeight, d.height);
                    } else {
                        x = d.width;
                        y += vga + rowMaxHeight;
                        rowMaxHeight = d.height;
                    }
                }
            }
            return new Dimension(insets.left + insets.right + x + gap * 2,
                    insets.top + insets.bottom + y + rowMaxHeight + vga * 2);
        }
    }
    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            int gap = this.getHgap();
            int vga = this.getVgap();
            Insets insets = target.getInsets();
            int maxWidth = target.getWidth() - (insets.left + insets.right + gap * 2);
            int nMembers = target.getComponentCount();
            int x = insets.left + gap;
            int y = insets.top + vga;
            int rowMaxHeight = 0;

            for (int i = 0; i < nMembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();

                    // Check if the new bounds would exceed the container's bounds
                    if ((x + d.width) <= maxWidth) {
                        m.setBounds(x, y, d.width, d.height);
                        x += d.width + gap;
                        rowMaxHeight = Math.max(rowMaxHeight, d.height);
                    } else {
                        // If it would, start a new row
                        x = insets.left + gap;
                        y += vga + rowMaxHeight;
                        rowMaxHeight = d.height;
                        m.setBounds(x, y, d.width, d.height);
                        x += d.width + gap;
                    }
                }
            }
        }
    }
}