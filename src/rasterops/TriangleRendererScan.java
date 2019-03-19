package rasterops;

import imagedata.Image;
import io.vavr.Function3;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class TriangleRendererScan<T> implements TriangleRenderer<T> {
    private final @NotNull Function3<T, T, Double, T> lerp;

    public TriangleRendererScan(@NotNull Function3<T, T, Double, T> lerp) {
        this.lerp = lerp;
    }

    @NotNull
    @Override
    public Image<T> render(
            final @NotNull Image<T> image,
            final double x1, final double y1,
            final double x2, final double y2,
            final double x3, final double y3,
            final @NotNull T value1,
            final @NotNull T value2,
            final @NotNull T value3
    ) {
        // zaridit, ze y1 >= y2 >= y3
        if (y1 < y2)
            return render(image,
                    x2, y2, x1, y1, x3, y3,
                    value2, value1, value3);
        if (y2 < y3)
            return render(image,
                    x1, y1, x3, y3, x2, y2,
                    value1, value3, value2);
        final double ix1 = (x1 + 1) * image.getWidth() / 2;
        final double iy1 = (-y1 + 1) * image.getHeight() / 2;
        final double ix2 = (x2 + 1) * image.getWidth() / 2;
        final double iy2 = (-y2 + 1) * image.getHeight() / 2;
        final double ix3 = (x3 + 1) * image.getWidth() / 2;
        final double iy3 = (-y3 + 1) * image.getHeight() / 2;
        
        final Image<T> firstHalf = render(image,
                (int) iy1 + 1, (int) iy2,
                ix1, ix1,
                ix2, ix3,
                iy1, iy1,
                iy2, iy3,
                value1, value1,
                value2, value3
                );
        return render(firstHalf,
                (int) iy2 + 1, (int) iy3,
                ix2, ix1,
                ix3, ix3,
                iy2, iy1,
                iy3, iy3,
                value2, value1,
                value3, value3
                );
    }
    
    private @NotNull Image<T> render(
            final @NotNull Image<T> image,
            final int rStart, final int rEnd,
            final double ixUL, final double ixUR,
            final double ixLL, final double ixLR,
            final double iyUL, final double iyUR,
            final double iyLL, final double iyLR,
            final T vUL, final T vUR,
            final T vLL, final T vLR) {
        return Stream
                .rangeClosed(rStart, rEnd)
                .foldLeft(image,
            (curImage, r) -> {
                final double ta = (r - iyUL) / (iyLL - iyUL);
                final double tb = (r - iyUR) / (iyLR - iyUR);
                final double ixa = ixUL * (1 - ta) + ixLL * ta;
                final double ixb = ixUR * (1 - tb) + ixLR * tb;
                final T va = lerp.apply(vUL, vLL, ta);
                final T vb = lerp.apply(vUR, vLR, tb);
                return render(curImage, r, ixa, ixb, va, vb);
            }
        );
    }

    private @NotNull Image<T> render(
            final @NotNull Image<T> image,
            final int r,
            final double ixa, final double ixb,
            final @NotNull T va, final @NotNull T vb) {
        // zaridit, ixa <= ixb
        if (ixa > ixb)
            return render(image, r, ixb, ixa, vb, va);
        return Stream
                .rangeClosed((int) ixa + 1, (int) ixb)
                .foldLeft(image,
                    (curImage, c) -> {
                        final double t = (c - ixa) / (ixb - ixa);
                        final T v = lerp.apply(va, vb, t);
                        return curImage.withValue(c, r, v);
                    }
        );
    }
}
