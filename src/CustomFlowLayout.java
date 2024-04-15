import java.awt.*;

public class CustomFlowLayout extends FlowLayout {
    private int maxComponentsPerRow;

    public CustomFlowLayout(int maxComponentsPerRow) {
        this.maxComponentsPerRow = maxComponentsPerRow;
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            int hgap = this.getHgap();
            int vgap = this.getVgap();
            Insets insets = target.getInsets();
            int maxwidth = target.getWidth() - (insets.left + insets.right + hgap * 2);
            int nmembers = target.getComponentCount();
            int x = 0;
            int y = insets.top + vgap;
            int rowh = 0;
            int start = 0;

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    m.setSize(d.width, d.height);

                    if ((x == 0) || ((x + d.width) <= maxwidth)) {
                        if (x > 0) {
                            x += hgap;
                        }
                        x += d.width;
                        rowh = Math.max(rowh, d.height);
                    } else {
                        x = d.width;
                        y += vgap + rowh;
                        rowh = d.height;
                    }
                }
            }
            return new Dimension(insets.left + insets.right + x + hgap * 2,
                    insets.top + insets.bottom + y + rowh + vgap * 2);
        }
    }
    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            int hgap = this.getHgap();
            int vgap = this.getVgap();
            Insets insets = target.getInsets();
            int maxwidth = target.getWidth() - (insets.left + insets.right + hgap * 2);
            int nmembers = target.getComponentCount();
            int x = 0;
            int y = insets.top + vgap;
            int rowh = 0;
            int start = 0;

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();

                    // Reset x to 0 (left alignment) if this component is the first in a row
                    if ((x == 0) || ((x + d.width) > maxwidth)) {
                        if (i > 0) {
                            y += vgap + rowh;
                        }
                        x = insets.left;
                        rowh = d.height;
                    }

                    // Set component's bounds
                    m.setBounds(x, y, d.width, d.height);

                    x += d.width + hgap;
                    rowh = Math.max(rowh, d.height);
                }
            }
        }
    }
}