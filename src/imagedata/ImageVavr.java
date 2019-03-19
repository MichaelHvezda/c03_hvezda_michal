package imagedata;

import io.vavr.collection.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ImageVavr<T> implements Image<T> {
    private final @NotNull Vector<T> data;
    private final int width, height;

    public ImageVavr(final int width, final int height,
                     final @NotNull T value) {
        this.width = width;
        this.height = height;
        data = Vector.fill(width * height, () -> value);
    }

    private ImageVavr(final @NotNull Vector<T> data,
                      final int width, final int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    @NotNull
    @Override
    public Optional<T> getValue(int c, int r) {
        if (c >= 0 && c < getWidth() && r >= 0 && r < getHeight()) {
            return Optional.of(data.get(r * width + c));
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Image<T> withValue(int c, int r, @NotNull T value) {
        if (c >= 0 && c < getWidth() && r >= 0 && r < getHeight()) {
            return new ImageVavr<>(
                    data.update(r * width + c, value), width, height);
        }
        return this;
    }

    @NotNull
    @Override
    public Image<T> cleared(@NotNull T value) {
        return new ImageVavr<>(width, height, value);
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
