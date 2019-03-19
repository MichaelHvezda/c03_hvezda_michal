package objectdata;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.IndexedSeq;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;
import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;

public class Cube implements Solid<Tuple2<Point3D, Col>, Topology> {
    final @NotNull IndexedSeq<Tuple2<Point3D, Col>> vertices;
    final @NotNull IndexedSeq<Integer> indices;
    final @NotNull Seq<Part<Topology>> parts;
    private final Col black = new Col(0,0,0);
    private final Col white = new Col(255,255,255);
    private final Col red = new Col(255,0,0);
    private final Col green = new Col(0,255,0);
    private final Col blue = new Col(0,0,255);
    private final Col purple = new Col(255,0,255);
    private final Col yellow = new Col(255,255,0);
    private final Col orange = new Col(255,165,0);
    Mat4 rotace;

    public Cube() {
        vertices = Array.of(
                Tuple.of(new Point3D(0,0,0),new Col(255,0,0)),
                Tuple.of(new Point3D(1,0,0),new Col(255,255,0)),
                Tuple.of(new Point3D(1,1,0),new Col(255,0,255)),
                Tuple.of(new Point3D(0,1,0),new Col(255,255,255)),
                Tuple.of(new Point3D(0,0,1),new Col(255,255,255)),
                Tuple.of(new Point3D(1,0,1),new Col(0,255,0)),
                Tuple.of(new Point3D(1,1,1),new Col(255,0,0)),
                Tuple.of(new Point3D(0,1,1),new Col(255,0,0))
        );
        indices = Stream.rangeClosed(0, 3).flatMap(
                i -> Array.of(i, (i + 1) % 4, i, i + 4, i + 4, (i + 1) % 4 + 4)
        ).appendAll(
                Stream.rangeClosed(0, 3).flatMap(
                        i -> Array.of(
                                i, (i + 1) % 4, (i + 1) % 4 + 4,
                                i, (i + 1) % 4 + 4, i + 4)
                )
        ).appendAll(
                Stream.rangeClosed(0, 1).flatMap(
                        i -> Array.of(
                                i * 4, i * 4 + 1, i * 4 + 3,
                                i * 4 + 1, i * 4 + 2, i * 4 + 3)
                )
        ).toArray();
        parts = Array.of(
                new Part(0, 12, Topology.LINE_LIST),
                new Part(24, 12, Topology.TRIANGLE_LIST));
    }

    //konstruktor ktery se stara aj o rotaci telesa podle bodu X:0 Y:0 Z:0
    public Cube(double rotaceKostky) {
        //vytvoreni matice
        rotace = rotace(rotaceKostky);
        vertices = Array.of(
                //vytvoreni bodu pro vykresleni dratoveho modelu
                Tuple.of(new Point3D(0,0,0).mul(rotace),white),
                Tuple.of(new Point3D(1,0,0).mul(rotace),red),
                Tuple.of(new Point3D(1,1,0).mul(rotace),green),
                Tuple.of(new Point3D(0,1,0).mul(rotace),blue),
                Tuple.of(new Point3D(0,0,1).mul(rotace),purple),
                Tuple.of(new Point3D(1,0,1).mul(rotace),yellow),
                Tuple.of(new Point3D(1,1,1).mul(rotace),white),
                Tuple.of(new Point3D(0,1,1).mul(rotace),purple),

                //vytvoreni bodu pro vykresleni vybarveneho modelu, usporadane podle hrany/ctverce a vykreslovane jako dva trouhelniky
                Tuple.of(new Point3D(1,0,0).mul(rotace),red),       //8
                Tuple.of(new Point3D(0,1,0).mul(rotace),red),
                Tuple.of(new Point3D(1,1,0).mul(rotace),red),
                Tuple.of(new Point3D(0,0,0).mul(rotace),red),

                Tuple.of(new Point3D(1,0,1).mul(rotace),blue),       //12
                Tuple.of(new Point3D(0,1,1).mul(rotace),blue),
                Tuple.of(new Point3D(1,1,1).mul(rotace),blue),
                Tuple.of(new Point3D(0,0,1).mul(rotace),blue),

                Tuple.of(new Point3D(1,0,1).mul(rotace),yellow),    //16
                Tuple.of(new Point3D(0,0,0).mul(rotace),yellow),
                Tuple.of(new Point3D(1,0,0).mul(rotace),yellow),
                Tuple.of(new Point3D(0,0,1).mul(rotace),yellow),

                Tuple.of(new Point3D(1,0,0).mul(rotace),green),       //20
                Tuple.of(new Point3D(1,1,1).mul(rotace),green),
                Tuple.of(new Point3D(1,1,0).mul(rotace),green),
                Tuple.of(new Point3D(1,0,1).mul(rotace),green),

                Tuple.of(new Point3D(1,1,0).mul(rotace),purple),     //24
                Tuple.of(new Point3D(0,1,1).mul(rotace),purple),
                Tuple.of(new Point3D(0,1,0).mul(rotace),purple),
                Tuple.of(new Point3D(1,1,1).mul(rotace),purple),

                Tuple.of(new Point3D(0,0,0).mul(rotace),orange),     //28
                Tuple.of(new Point3D(0,1,1).mul(rotace),orange),
                Tuple.of(new Point3D(0,1,0).mul(rotace),orange),
                Tuple.of(new Point3D(0,0,1).mul(rotace),orange)



        );
        indices = Stream.rangeClosed(0, 3).flatMap(
                i -> Array.of(
                        i, (i + 1) % 4,
                        i, i + 4, i + 4,
                        (i + 1) % 4 + 4
                )
        ).appendAll(
                Stream.rangeClosed(0, 5).flatMap(
                        i -> Array.of(
                                8+i*4, 9+i*4, 10+i*4,
                                8+i*4, 9+i*4, 11+i*4)
                )
        ).toArray();
        parts = Array.of(
                new Part(0, 12, Topology.LINE_LIST),
                new Part(24, 12, Topology.TRIANGLE_LIST));
    }
    @NotNull
    @Override
    public IndexedSeq<Tuple2<Point3D, Col>> getVertices() {
        return vertices;
    }

    @NotNull
    @Override
    public IndexedSeq<Integer> getIndices() {
        return indices;
    }

    @NotNull
    @Override
    public Seq<Part<Topology>> getParts() {
        return parts;
    }

    //vytvoreni matice pro rotaci
    private Mat4 rotace(double d){
        return new Mat4(new double[] {
                Math.cos(d), Math.sin(d), 0, 0,
                -Math.sin(d), Math.cos(d), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });
    }
}
