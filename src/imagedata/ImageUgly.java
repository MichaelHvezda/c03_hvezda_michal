package imagedata;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Function;

public class ImageUgly<UglyPixelType> implements Image<UglyPixelType> {
    private final @NotNull BufferedImage image;
    private final @NotNull Function<UglyPixelType, Integer> toRGB;
    private final @NotNull Function<Integer, UglyPixelType> toUgly;

    public ImageUgly(final @NotNull BufferedImage image,
                     final @NotNull Function<UglyPixelType, Integer> toRGB,
                     final @NotNull Function<Integer, UglyPixelType> toUgly){
        this.image = image;
        this.toRGB = toRGB;
        this.toUgly = toUgly;
    }

    @NotNull
    @Override
    public Optional<UglyPixelType> getValue(int c, int r) {
        if (c >= 0 && c < image.getWidth() && r >= 0 && r < image.getHeight())
            return Optional.of(toUgly.apply(image.getRGB(c, r)));
        return Optional.empty();
    }

    @NotNull
    @Override
    public Image<UglyPixelType> withValue(int c, int r, @NotNull UglyPixelType value) {
        if (c >= 0 && c < image.getWidth() && r >= 0 && r < image.getHeight()) {
            image.setRGB(c, r, toRGB.apply(value));
        }
        return this;
    }

    @NotNull
    @Override
    public Image<UglyPixelType> cleared(@NotNull UglyPixelType value) {
        Graphics gr = image.getGraphics();
        gr.setColor(new Color(toRGB.apply(value)));
        gr.fillRect(0, 0, image.getWidth(), image.getHeight());
        return this;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    public @NotNull BufferedImage getBufferedImage() {
        return image;
    }
}
