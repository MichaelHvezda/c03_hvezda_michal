package objectdata;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.IndexedSeq;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;
import transforms.Col;
import transforms.Point3D;

public class Pyramid implements Solid<Tuple2<Point3D, Col>, Topology> {
    private final @NotNull IndexedSeq<Tuple2<Point3D, Col>> verices;
    private final @NotNull IndexedSeq<Integer>  indices;
    private final @NotNull Seq<Part<Topology>> parts;
    private final Col black = new Col(0,0,0);
    private final Col white = new Col(255,255,255);
    private final Col red = new Col(255,0,0);
    private final Col green = new Col(0,255,0);
    private final Col blue = new Col(0,0,255);
    private final Col purple = new Col(255,0,255);
    private final Col yellow = new Col(255,255,0);


    public Pyramid(){
        verices = Array.of(
                Tuple.of(new Point3D(0,0,0),blue),
                Tuple.of(new Point3D(1,0,0),red),
                Tuple.of(new Point3D(1,1,0),green),
                Tuple.of(new Point3D(0,1,0),yellow),
                Tuple.of(new Point3D(0.5,0.5,1),purple)

        );

        indices = Stream.rangeClosed(0,3).flatMap(
                i -> Array.of(
                        i,(i+1)%4,
                        i,4
                )
        ).appendAll(
                Stream.rangeClosed(0, 3).flatMap(
                        i -> Array.of(
                                4, i,(i+1)%4
                        )
                )
        ).appendAll(
                Stream.rangeClosed(0, 1).flatMap(
                        i -> Array.of(
                                i*2, 1, 3
                        )
                )
        ).toArray();
        parts = Array.of(
                new Part(0, 8, Topology.LINE_LIST),
                new Part(16, 6, Topology.TRIANGLE_LIST));


    }

    //konstruktor starajici se jiz o zvetseni a zmenseni telesa
    public Pyramid(double a){
        verices = Array.of(
                //vytvoreni bodu pro vykresleni dratoveho modelu
                Tuple.of(new Point3D(-0.5*a,-0.5*a,0),blue),
                Tuple.of(new Point3D(0.5*a,-0.5*a,0),yellow),
                Tuple.of(new Point3D(0.5*a,0.5*a,0),green),
                Tuple.of(new Point3D(-0.5*a,0.5*a,0),red),
                Tuple.of(new Point3D(0,0,1*a),purple),

                //body usporadane podle trouhelniku
                Tuple.of(new Point3D(0,0,1*a),blue),
                Tuple.of(new Point3D(0.5*a,-0.5*a,0),blue),
                Tuple.of(new Point3D(-0.5*a,-0.5*a,0),blue),

                Tuple.of(new Point3D(0,0,1*a),yellow),
                Tuple.of(new Point3D(0.5*a,0.5*a,0),yellow),
                Tuple.of(new Point3D(0.5*a,-0.5*a,0),yellow),

                Tuple.of(new Point3D(0,0,1*a),green),
                Tuple.of(new Point3D(-0.5*a,0.5*a,0),green),
                Tuple.of(new Point3D(0.5*a,0.5*a,0),green),

                Tuple.of(new Point3D(0,0,1*a),red),
                Tuple.of(new Point3D(-0.5*a,-0.5*a,0),red),
                Tuple.of(new Point3D(-0.5*a,0.5*a,0),red),


                Tuple.of(new Point3D(0.5*a,0.5*a,0),purple),
                Tuple.of(new Point3D(-0.5*a,-0.5*a,0),purple),
                Tuple.of(new Point3D(0.5*a,-0.5*a,0),purple),
                Tuple.of(new Point3D(-0.5*a,0.5*a,0),purple)


        );

        indices = Stream.rangeClosed(0,3).flatMap(
                i -> Array.of(
                        i,(i+1)%4,
                        i,4
                )
        ).appendAll(
                Stream.rangeClosed(0, 3).flatMap(
                        i -> Array.of(
                                5+i*3, (5+i*3)+1,(5+i*3)+2
                        )
                )
        ).appendAll(
                Stream.rangeClosed(0, 1).flatMap(
                        i -> Array.of(
                                17, 18, 19+i
                        )
                )
        ).toArray();
        parts = Array.of(
                new Part(0, 8, Topology.LINE_LIST),
                new Part(16, 6, Topology.TRIANGLE_LIST));


    }




    @NotNull
    @Override
    public IndexedSeq<Tuple2<Point3D, Col>> getVertices() {
        return verices;
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
}
