package rasterops;

import imagedata.Image;
import org.jetbrains.annotations.NotNull;

public interface LineRenderer<LinePixelType> {
    /**
     * Represents a line rasterization algorithm.
     * The line coordinates are considered normalized to a [-1;1] square
     * with the image bottom left corner being at (-1;-1) and the top right at (1;1)
     * @param image background image
     * @param x1 the x coordinate of the start-point in [-1;1] range
     * @param y1 the y coordinate of the start-point in [-1;1] range
     * @param x2 the x coordinate of the end-point in [-1;1] range
     * @param y2 the y coordinate of the end-point in [-1;1] range
     * @param value the value all line pixels should have
     * @return a new image with pixels corresponding to the line having the given value
     */
    @NotNull Image<LinePixelType> render(
            @NotNull Image<LinePixelType> image,
            double x1, double y1, double x2, double y2,
            @NotNull LinePixelType value
    );
}
