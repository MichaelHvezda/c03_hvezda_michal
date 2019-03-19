package rasterops;


import imagedata.Image;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;

public class FloodFill4<T> implements FloodFill<T> {

    @NotNull
    @Override
    public Image<T> fill(
            @NotNull Image<T> image,
            int c, int r,
            @NotNull T value,
            @NotNull Predicate<T> shouldBeFilled) {
        return image.getValue(c, r).flatMap(
            pixel -> {
                if (shouldBeFilled.test(pixel)) {
                    return Optional.of(
                        fill(
                            fill(
                                fill(
                                    fill(
                                        image.withValue(c, r, value),
                                    c + 1, r, value, shouldBeFilled),
                                c, r + 1, value, shouldBeFilled),
                            c - 1, r, value, shouldBeFilled),
                        c, r - 1, value, shouldBeFilled)
                    );
                }
                return Optional.empty();
            }
        ).orElse(image);
    }
}
