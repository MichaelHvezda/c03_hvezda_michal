package imagedata;

import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public class PresenterUniversal<SomeType> implements Presenter<SomeType, Graphics> {
    private final @NotNull Function<SomeType, Integer> toRGB;

    public PresenterUniversal(
            final @NotNull Function<SomeType, Integer> toRGB){
        this.toRGB = toRGB;
    }
    @NotNull
    @Override
    public Graphics present(@NotNull Image<SomeType> img,
                            @NotNull Graphics device) {
        BufferedImage bufImg = new BufferedImage(
                img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Stream.range(0, img.getHeight()).forEach(
            r -> Stream.range(0, img.getWidth()).forEach(
                c -> img.getValue(c, r).ifPresent(
                    value -> bufImg.setRGB(c, r, toRGB.apply(value))
                )
            )
        );
        device.drawImage(bufImg, 0, 0, null);
        return device;
    }
}

