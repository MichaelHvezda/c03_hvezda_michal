package imagedata;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface Image<PixelType> {
    /**
     * Returns value of the pixel at the given column and row address
     * @param c column address
     * @param r row address
     * @return empty {@link Optional} if pixel address invalid
     */
    @NotNull
    Optional<PixelType> getValue(int c, int r);

    /**
     * Creates a new image with the pixel at the given address
     * set to the given value
     * @param c column address
     * @param r row address
     * @param value new pixel value
     * @return new image if address valid, this otherwise
     */
    @NotNull
    Image<PixelType> withValue(int c, int r, @NotNull PixelType value);

    /**
     * Returns a new image filled with the given value
     * @param value a value to fill the image with
     * @return new image
     */
    @NotNull Image<PixelType> cleared(@NotNull PixelType value);

    /**
     * Returns the number of columns
     * @return column count
     */
    int getWidth();

    /**
     * Returns the number of rows
     * @return row count
     */
    int getHeight();
}
