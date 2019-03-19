package rasterops;

import imagedata.Image;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;
import static java.lang.Math.*;

public class LineRendererDDA<T> implements LineRenderer<T> {

    @NotNull
    @Override
    public Image<T> render(
            @NotNull Image<T> image,
            double x1, double y1, double x2, double y2,
            @NotNull T value) {
        final double ix1 = (x1 + 1) * image.getWidth() / 2;
        final double iy1 = (-y1 + 1) * image.getHeight() / 2;
        final double ix2 = (x2 + 1) * image.getWidth() / 2;
        final double iy2 = (-y2 + 1) * image.getHeight() / 2;
        final double dx = ix2 - ix1, dy = iy2 - iy1,
                max = max(abs(dx), abs(dy)),
                ddx = dx / max, ddy = dy / max;
        return Stream.rangeClosed(0, (int) max).foldLeft(image,
                (currentImage, i) -> {
                    return currentImage.withValue(
                            (int) (ix1 + i * ddx),
                            (int) (iy1 + i * ddy), value);
                }
        );
    }
}
