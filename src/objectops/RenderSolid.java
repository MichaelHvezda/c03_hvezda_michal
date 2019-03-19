package objectops;

import imagedata.Image;
import io.vavr.Function3;
import io.vavr.collection.IndexedSeq;
import io.vavr.collection.Stream;
import objectdata.Topology;
import org.jetbrains.annotations.NotNull;
import rasterops.LineRenderer;
import rasterops.LineRendererLerp;
import rasterops.TriangleRenderer;
import transforms.Mat4;
import transforms.Point3D;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RenderSolid<PixelT, VertexT>
        implements Renderer<PixelT, VertexT, Topology> {
    private final @NotNull Function<VertexT, Point3D> getPoint3D;
    private final @NotNull BiFunction<VertexT, Double, PixelT> getPixel;
    private final @NotNull Function3<VertexT, VertexT, Double, VertexT> lerp;
    private final @NotNull LineRendererLerp<PixelT> liner;
    private final @NotNull TriangleRenderer<PixelT> triangler;


    public RenderSolid(@NotNull Function<VertexT, Point3D> getPoint3D, @NotNull BiFunction<VertexT, Double, PixelT> getPixel, @NotNull Function3<VertexT, VertexT, Double, VertexT> lerp, @NotNull LineRendererLerp<PixelT> liner, @NotNull TriangleRenderer<PixelT> triangler) {
        this.getPoint3D = getPoint3D;
        this.getPixel = getPixel;
        this.lerp = lerp;
        this.liner = liner;
        this.triangler = triangler;
    }

    @NotNull
    @Override
    public Image<PixelT> render(
            @NotNull Image<PixelT> background,
            @NotNull IndexedSeq<VertexT> vertices,
            @NotNull IndexedSeq<Integer> indices,
            int startIndex, int primitiveCount,
            @NotNull Topology topology,
            @NotNull Mat4 transform, @NotNull PixelT value) {
        switch (topology) {
            case LINE_LIST :
                return Stream.rangeClosed(0, primitiveCount -1)
                    .foldLeft(background,
                        (currentImage, i) ->
                            transformEdge(
                                currentImage,
                                vertices.get(indices.get(
                                        startIndex + 2 * i
                                )),
                                vertices.get(indices.get(
                                        startIndex + 2 * i + 1
                                )),
                                transform
                            )
                    );
            case TRIANGLE_LIST:
                return Stream.rangeClosed(0, primitiveCount -1)
                    .foldLeft(background,
                        (currentImage, i) ->
                            transformTriangle(
                                currentImage,
                                vertices.get(indices.get(
                                        startIndex + 3 * i
                                )),
                                vertices.get(indices.get(
                                        startIndex + 3 * i + 1
                                )),
                                vertices.get(indices.get(
                                        startIndex + 3 * i + 2
                                )),
                                transform
                            )
                    );
        }
        return background;
    }


    private @NotNull Image<PixelT> transformEdge(
            final @NotNull Image<PixelT> backImage,
            final @NotNull VertexT p1, final @NotNull VertexT p2,
            final @NotNull Mat4 transform
    ) {
        final Point3D p1BeforeDehomog =
                getPoint3D.apply(p1).mul(transform);
        final Point3D p2BeforeDehomog =
                getPoint3D.apply(p2).mul(transform);
        return clipEdge(backImage, p1, p2, p1BeforeDehomog, p2BeforeDehomog);
    }

    private @NotNull Image<PixelT> clipEdge(
            final @NotNull Image<PixelT> backImage,
            final @NotNull VertexT p1, final @NotNull VertexT p2,
            final @NotNull Point3D p1BeforeDehomog,
            final @NotNull Point3D p2BeforeDehomog
    ) {
        //zaridit, ze p1BeforeDehomog.z >= p2BeforeDehomog.z
        if (p1BeforeDehomog.getZ() < p2BeforeDehomog.getZ())
            return clipEdge(backImage, p2, p1, p2BeforeDehomog, p1BeforeDehomog);
        // oba nejsou videt
        if (p1BeforeDehomog.getZ() < 0)
            return backImage;
        // p2 neni videt
        if (p2BeforeDehomog.getZ() < 0) {
            final double t = p1BeforeDehomog.getZ()
                    / (p1BeforeDehomog.getZ() - p2BeforeDehomog.getZ());
            final VertexT p = lerp.apply(p1, p2, t);
            final Point3D pBeforeDehomog =
                    p1BeforeDehomog.mul(1 - t).add(p2BeforeDehomog.mul(t));
            return renderEdge(backImage, p1, p, p1BeforeDehomog, pBeforeDehomog);
        }
        // oba jsou videt
        return renderEdge(backImage, p1, p2, p1BeforeDehomog, p2BeforeDehomog);
    }

    private @NotNull Image<PixelT> renderEdge(
            final @NotNull Image<PixelT> backImage,
            final @NotNull VertexT p1, final @NotNull VertexT p2,
            final @NotNull Point3D p1BeforeDehomog,
            final @NotNull Point3D p2BeforeDehomog
    ) {
        return p1BeforeDehomog.dehomog().flatMap(
                p1AfterDehomog -> p2BeforeDehomog.dehomog().flatMap(
                        p2AfterDehomog ->
                                Optional.of(liner.render(
                                        backImage,
                                        p1AfterDehomog.getX(),
                                        p1AfterDehomog.getY(),
                                        p2AfterDehomog.getX(),
                                        p2AfterDehomog.getY(),
                                        getPixel.apply(p1, p1AfterDehomog.getZ()),
                                        getPixel.apply(p2, p2AfterDehomog.getZ())))
                )
        ).orElse(backImage);
    }
//===============================================================================
//===============================================================================
//===============================================================================
    private @NotNull Image<PixelT> transformTriangle(
            final @NotNull Image<PixelT> backImage,
            final @NotNull VertexT p1,
            final @NotNull VertexT p2,
            final @NotNull VertexT p3,
            final @NotNull Mat4 transform
    ) {
        final Point3D p1BeforeDehomog =
                getPoint3D.apply(p1).mul(transform);
        final Point3D p2BeforeDehomog =
                getPoint3D.apply(p2).mul(transform);
        final Point3D p3BeforeDehomog =
                getPoint3D.apply(p3).mul(transform);
        return clipTriangle(backImage, p1, p2, p3,
                p1BeforeDehomog, p2BeforeDehomog, p3BeforeDehomog);
    }

    private @NotNull Image<PixelT> clipTriangle(
            final @NotNull Image<PixelT> backImage,
            final @NotNull VertexT p1,
            final @NotNull VertexT p2,
            final @NotNull VertexT p3,
            final @NotNull Point3D p1BeforeDehomog,
            final @NotNull Point3D p2BeforeDehomog,
            final @NotNull Point3D p3BeforeDehomog
    ) {
        //zaridit, ze p1BeforeDehomog.z >= p2BeforeDehomog.z >= p3BeforeDehomog.z
        if (p1BeforeDehomog.getZ() < p2BeforeDehomog.getZ())
            return clipTriangle(backImage, p2, p1, p3,
                    p2BeforeDehomog, p1BeforeDehomog, p3BeforeDehomog);
        if (p2BeforeDehomog.getZ() < p3BeforeDehomog.getZ())
            return clipTriangle(backImage, p1, p3, p2,
                    p1BeforeDehomog, p3BeforeDehomog, p2BeforeDehomog);
        // vsechny nejsou videt
        if (p1BeforeDehomog.getZ() < 0)
            return backImage;
        // p1 je videt
        if (p2BeforeDehomog.getZ() < 0) {
            final double ta = p1BeforeDehomog.getZ()
                    / (p1BeforeDehomog.getZ() - p2BeforeDehomog.getZ());
            final VertexT pa = lerp.apply(p1, p2, ta);
            final Point3D paBeforeDehomog =
                    p1BeforeDehomog.mul(1 - ta).add(p2BeforeDehomog.mul(ta));
            final double tb = p1BeforeDehomog.getZ()
                    / (p1BeforeDehomog.getZ() - p3BeforeDehomog.getZ());
            final VertexT pb = lerp.apply(p1, p3, tb);
            final Point3D pbBeforeDehomog =
                    p1BeforeDehomog.mul(1 - tb).add(p3BeforeDehomog.mul(tb));
            return renderTriangle(backImage, p1, pa, pb,
                    p1BeforeDehomog, paBeforeDehomog, pbBeforeDehomog);
        }
        // p1, p2 jsou videt
        if (p3BeforeDehomog.getZ() < 0) {
            final double ta = p2BeforeDehomog.getZ()
                    / (p2BeforeDehomog.getZ() - p3BeforeDehomog.getZ());
            final VertexT pa = lerp.apply(p2, p3, ta);
            final Point3D paBeforeDehomog =
                    p2BeforeDehomog.mul(1 - ta).add(p3BeforeDehomog.mul(ta));
            final double tb = p1BeforeDehomog.getZ()
                    / (p1BeforeDehomog.getZ() - p3BeforeDehomog.getZ());
            final VertexT pb = lerp.apply(p1, p3, tb);
            final Point3D pbBeforeDehomog =
                    p1BeforeDehomog.mul(1 - tb).add(p3BeforeDehomog.mul(tb));
            return renderTriangle(
                    renderTriangle(backImage, p1, p2, pa,
                        p1BeforeDehomog, p2BeforeDehomog, paBeforeDehomog),
                    p1, pa, pb,
                    p1BeforeDehomog, paBeforeDehomog, pbBeforeDehomog
                );
        }
        // vsechny jsou videt
        return renderTriangle(backImage, p1, p2, p3,
                p1BeforeDehomog, p2BeforeDehomog, p3BeforeDehomog);
    }

    private @NotNull Image<PixelT> renderTriangle(
            final @NotNull Image<PixelT> backImage,
            final @NotNull VertexT p1,
            final @NotNull VertexT p2,
            final @NotNull VertexT p3,
            final @NotNull Point3D p1BeforeDehomog,
            final @NotNull Point3D p2BeforeDehomog,
            final @NotNull Point3D p3BeforeDehomog
    ) {
        return p1BeforeDehomog.dehomog().flatMap(
            p1AfterDehomog -> p2BeforeDehomog.dehomog().flatMap(
                p2AfterDehomog -> p3BeforeDehomog.dehomog().flatMap(
                    p3AfterDehomog -> Optional.of(triangler.render(
                        backImage,
                        p1AfterDehomog.getX(),
                        p1AfterDehomog.getY(),
                        p2AfterDehomog.getX(),
                        p2AfterDehomog.getY(),
                        p3AfterDehomog.getX(),
                        p3AfterDehomog.getY(),
                        getPixel.apply(p1, p1AfterDehomog.getZ()),
                        getPixel.apply(p2, p2AfterDehomog.getZ()),
                        getPixel.apply(p3, p3AfterDehomog.getZ()))))
            )
        ).orElse(backImage);
    }
}
