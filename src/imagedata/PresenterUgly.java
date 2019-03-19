package imagedata;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PresenterUgly<SomeType> implements Presenter<SomeType, Graphics> {
    @NotNull
    @Override
    public Graphics present(@NotNull Image<SomeType> img,
                            @NotNull Graphics device) {
        if (img instanceof ImageUgly) {
            BufferedImage bufImg = ((ImageUgly) img).getBufferedImage();
            device.drawImage(bufImg, 0, 0, null);
        } else {
            System.err.println("Cannot present image of " + img.getClass());
        }
        return device;
    }
}

