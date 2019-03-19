package objectdata;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.Array;
import io.vavr.collection.IndexedSeq;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import objectdata.Solid;
import objectdata.Topology;
import org.jetbrains.annotations.NotNull;
import transforms.Col;
import transforms.Point3D;

public class Xyz implements Solid<Tuple2<Point3D, Col>, Topology> {
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


    public Xyz(){
        verices = Array.of(
                //vytvoreni bodu pro usecky znazornujici osy XYZ
                Tuple.of(new Point3D(0,0,0),red),
                Tuple.of(new Point3D(0,0,0),green),
                Tuple.of(new Point3D(0,0,0),blue),
                Tuple.of(new Point3D(1,0,0),red),
                Tuple.of(new Point3D(0,1,0),green),
                Tuple.of(new Point3D(0,0,1),blue)

        );

        indices = Stream.rangeClosed(0,2).flatMap(
                i -> Array.of(
                        i, (3+i)
                )
        ).toArray();


        parts = Array.of(
                new Part(0, 3, Topology.LINE_LIST));

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
