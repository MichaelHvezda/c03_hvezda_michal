package imagedata;

import org.jetbrains.annotations.NotNull;

public interface Presenter<PixelType, DeviceType> {
    /**
     * Presents the given image to the given device
     * @param img image to be presented
     * @param device device to be presented into
     * @return New device reflecting the presented image
     */
    @NotNull DeviceType present(@NotNull Image<PixelType> img, @NotNull DeviceType device);
}
