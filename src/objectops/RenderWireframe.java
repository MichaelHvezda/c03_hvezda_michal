package objectops;

import imagedata.Image;
import io.vavr.collection.IndexedSeq;
import io.vavr.collection.Stream;
import objectdata.Topology;
import org.jetbrains.annotations.NotNull;
import rasterops.LineRenderer;
import transforms.Mat4;
import transforms.Point3D;

import java.util.Optional;

public class RenderWireframe<PixType> implements
        Renderer<PixType, Point3D, Topology> {
    final @NotNull LineRenderer<PixType> liner;

    public RenderWireframe(@NotNull LineRenderer<PixType> liner) {
        this.liner = liner;
    }

    @NotNull
    @Override
    public Image<PixType> render(
            @NotNull Image<PixType> background,
            @NotNull IndexedSeq<Point3D> vertices,
            @NotNull IndexedSeq<Integer> indices,
            int startIndex, int primitiveCount,
            @NotNull Topology topology,
            @NotNull Mat4 transform, @NotNull PixType value) {
        switch (topology) {
            case LINE_LIST :
                return Stream.rangeClosed(0, primitiveCount -1)
                    .foldLeft(background,
                        (currentImage, i) ->
                            renderEdge(
                                currentImage,
                                vertices.get(indices.get(
                                    startIndex + 2 * i
                                )),
                                vertices.get(indices.get(
                                    startIndex + 2 * i + 1
                                )),
                                transform,
                                value
                        )
                    );
        }
        return background;
    }

    private @NotNull Image<PixType> renderEdge(
        final @NotNull Image<PixType> backImage,
        final @NotNull Point3D p1, final @NotNull Point3D p2,
        final @NotNull Mat4 transform, final @NotNull PixType value
    ) {
        final Point3D p1BeforeDehomog = p1.mul(transform);
        final Point3D p2BeforeDehomog = p2.mul(transform);
        if (p1BeforeDehomog.getW() <= 0 || p2BeforeDehomog.getW() <= 0)
            return backImage;
        return p1BeforeDehomog.dehomog().flatMap(
            p1AfterDehomog -> p2BeforeDehomog.dehomog().flatMap(
                p2AfterDehomog ->
                    Optional.of(liner.render(
                            backImage,
                            p1AfterDehomog.getX(),
                            p1AfterDehomog.getY(),
                            p2AfterDehomog.getX(),
                            p2AfterDehomog.getY(), value))
            )
        ).orElse(backImage);
    }
}
