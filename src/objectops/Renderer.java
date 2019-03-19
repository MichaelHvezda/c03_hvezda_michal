package objectops;

import imagedata.Image;
import io.vavr.collection.IndexedSeq;
import objectdata.Solid;
import org.jetbrains.annotations.NotNull;
import transforms.Mat4;

public interface Renderer<PixelType, VertexType, TopologyType> {
    default @NotNull Image<PixelType> render(
            @NotNull Image<PixelType> background,
            @NotNull Solid<VertexType, TopologyType> solid,
            @NotNull Mat4 transform,
            @NotNull PixelType value
    ) {
        return solid.getParts().foldLeft(background,
            (currentImage, part) -> render(
                    currentImage,
                    solid,
                    part,
                    transform,
                    value
                )
        );
    }

    default @NotNull Image<PixelType> render(
            @NotNull Image<PixelType> background,
            @NotNull Solid<VertexType, TopologyType> solid,
            @NotNull Solid.Part<TopologyType> part,
            @NotNull Mat4 transform,
            @NotNull PixelType value
    ) {
        return render(
                background,
                solid.getVertices(),
                solid.getIndices(),
                part.getStartIndex(),
                part.getPrimitiveCount(),
                part.getTopology(),
                transform,
                value
        );
    }

    @NotNull Image<PixelType> render(
            @NotNull Image<PixelType> background,
            @NotNull IndexedSeq<VertexType> vertices,
            @NotNull IndexedSeq<Integer> indices,
            int startIndex,
            int primitiveCount,
            @NotNull TopologyType topology,
            @NotNull Mat4 transform,
            @NotNull PixelType value
    );
}
