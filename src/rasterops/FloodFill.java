package rasterops;

import imagedata.Image;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface FloodFill<T> {
    /**
     * Returns a new image with all pixels starting at c,r
     * that pass the shouldBeFilled test filled with value
     * @param image imge to fill
     * @param c starting pixel column address
     * @param r starting pixel row address
     * @param value value of the filled pixels
     * @param shouldBeFilled test wether pixel should be filled
     * @return new image
     */
    @NotNull Image<T> fill(
            @NotNull Image<T> image,
            int c, int r,
            @NotNull T value,
            @NotNull Predicate<T> shouldBeFilled);
}
