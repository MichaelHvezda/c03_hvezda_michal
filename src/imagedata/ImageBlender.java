package imagedata;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;

public class ImageBlender<T> implements Image<T> {
    private final @NotNull Image<T> data;
    private final @NotNull BiFunction<T, T, T> blendFunc;

    public ImageBlender(@NotNull Image<T> data, @NotNull BiFunction<T, T, T> blendFunc) {
        this.data = data;
        this.blendFunc = blendFunc;
    }

    @NotNull
    @Override
    public Optional<T> getValue(int c, int r) {
        return data.getValue(c, r);
    }

    @NotNull
    @Override
    public Image<T> withValue(int c, int r, @NotNull T value) {
        return data.getValue(c, r).flatMap(
            oldValue -> Optional.of(new ImageBlender<>(
                    data.withValue(c, r,
                        blendFunc.apply(value, oldValue)),
                    blendFunc))
        ).orElse(this);
    }

    @NotNull
    @Override
    public Image<T> cleared(@NotNull T value) {
        return new ImageBlender<>(data.cleared(value), blendFunc);
    }

    @Override
    public int getWidth() {
        return data.getWidth();
    }

    @Override
    public int getHeight() {
        return data.getHeight();
    }
}
