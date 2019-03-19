package rasterops;

import imagedata.Image;
import org.jetbrains.annotations.NotNull;

public interface TriangleRenderer<PixelType> {
    /**
     * Represents a triangle rasterization algorithm.
     * The triangle coordinates are considered normalized to a [-1;1] square
     * with the image bottom left corner being at (-1;-1) and the top right at (1;1)
     * @param image background image
     * @param x1 the x coordinate of the first point in [-1;1] range
     * @param y1 the y coordinate of the first point in [-1;1] range
     * @param x2 the x coordinate of the second point in [-1;1] range
     * @param y2 the y coordinate of the second point in [-1;1] range
     * @param x3 the x coordinate of the third point in [-1;1] range
     * @param y3 the y coordinate of the third point in [-1;1] range
     * @param value1 the value of the first point
     * @param value2 the value of the second point
     * @param value3 the value of the third point
     * @return a new image with pixels corresponding to the triangle
     */
    @NotNull Image<PixelType> render(
            @NotNull Image<PixelType> image,
            double x1, double y1,
            double x2, double y2,
            double x3, double y3,
            @NotNull PixelType value1,
            @NotNull PixelType value2,
            @NotNull PixelType value3
    );
}
