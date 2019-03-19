package imagedata;

import io.vavr.Tuple2;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ImageSplitter<T, U, V> implements Image<T> {
    private final @NotNull Image<U> imageU;
    private final @NotNull Image<V> imageV;
    private final @NotNull BiFunction<U, V, T> combine;
    private final @NotNull Function<T, Tuple2<U, V>> split;
    private final int width, height;

    public ImageSplitter(@NotNull Image<U> imageU, @NotNull Image<V> imageV, @NotNull BiFunction<U, V, T> combine, @NotNull Function<T, Tuple2<U, V>> split) {
        this.imageU = imageU;
        this.imageV = imageV;
        this.combine = combine;
        this.split = split;
        if (imageU.getWidth() != imageV.getWidth() ||
                imageU.getHeight() != imageV.getHeight()) {
            System.err.println("Images do not have the same size");
        }
        width = Math.min(imageU.getWidth(), imageV.getWidth());
        height = Math.min(imageU.getHeight(), imageV.getHeight());
    }

    @NotNull
    @Override
    public Optional<T> getValue(int c, int r) {
        if (c >= 0 && c < getWidth() && r >= 0 && r < getHeight()) {
            return imageU.getValue(c, r).flatMap(
                    uPixel -> imageV.getValue(c, r).flatMap(
                            vPixel -> Optional.of(combine.apply(
                                    uPixel, vPixel
                            ))
                    )
            );
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Image<T> withValue(int c, int r, @NotNull T value) {
        if (c >= 0 && c < getWidth() && r >= 0 && r < getHeight()) {
            return split.apply(value).apply(
                    (uPixel, vPixel) ->
                            new ImageSplitter<>(
                                    imageU.withValue(c, r, uPixel),
                                    imageV.withValue(c, r, vPixel),
                                    combine,
                                    split
                            )
            );
        }
        return this;
    }

    @NotNull
    @Override
    public Image<T> cleared(@NotNull T value) {
        return split.apply(value).apply(
                (uPixel, vPixel) ->
                        new ImageSplitter<>(
                                imageU.cleared(uPixel),
                                imageV.cleared(vPixel),
                                combine,
                                split
                        )
        );
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
