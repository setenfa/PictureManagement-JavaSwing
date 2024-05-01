package function;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageResizer {
    public static BufferedImage resize(BufferedImage image, double ratio) {
        int width = (int) (image.getWidth() * ratio);
        int height = (int) (image.getHeight() * ratio);
        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());
        AffineTransform transform = AffineTransform.getScaleInstance(ratio, ratio);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
        op.filter(image, resizedImage);
        return resizedImage;
    }
}