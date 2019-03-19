package objectdata;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.Array;
import io.vavr.collection.IndexedSeq;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;
import transforms.Col;
import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

public class MyPoligom implements Solid<Tuple2<Point3D, Col>, Topology> {
    private final @NotNull IndexedSeq<Tuple2<Point3D, Col>> verices;
    private final @NotNull IndexedSeq<Integer>  indices;
    private final @NotNull Seq<Solid.Part<Topology>> parts;
    private @NotNull Mat4 pyramidMatrix0 = new Mat4(new Point3D(0,0,2),new Point3D(1,1,1), new Point3D(2,1,0),new Point3D(0,1,-1));
    private @NotNull Mat4 pyramidMatrix1 = new Mat4(new Point3D(0,1,2),new Point3D(1,2,1), new Point3D(2,2,0),new Point3D(0,2,-1));
    private @NotNull Mat4 pyramidMatrix2 = new Mat4(new Point3D(0,2,2),new Point3D(1,3,1), new Point3D(2,3,0),new Point3D(0,3,-1));
    private @NotNull Mat4 pyramidMatrix3 = new Mat4(new Point3D(0,3,2),new Point3D(1,4,1), new Point3D(2,4,0),new Point3D(0,4,-1));
    private Cubic souradnice0 =new Cubic(Cubic.BEZIER,pyramidMatrix0.getRow(0),pyramidMatrix0.getRow(1),pyramidMatrix0.getRow(2),pyramidMatrix0.getRow(3));
    private Cubic souradnice1 =new Cubic(Cubic.BEZIER,pyramidMatrix1.getRow(0),pyramidMatrix1.getRow(1),pyramidMatrix1.getRow(2),pyramidMatrix1.getRow(3));
    private Cubic souradnice2 =new Cubic(Cubic.BEZIER,pyramidMatrix2.getRow(0),pyramidMatrix2.getRow(1),pyramidMatrix2.getRow(2),pyramidMatrix2.getRow(3));
    private Cubic souradnice3 =new Cubic(Cubic.BEZIER,pyramidMatrix3.getRow(0),pyramidMatrix3.getRow(1),pyramidMatrix3.getRow(2),pyramidMatrix3.getRow(3));
    private final Col black = new Col(0,0,0);
    private final Col white = new Col(255,255,255);
    private final Col red = new Col(255,0,0);
    private final Col green = new Col(0,255,0);
    private final Col blue = new Col(0,0,255);
    private final Col purple = new Col(255,0,255);
    private final Col yellow = new Col(255,255,0);

    public MyPoligom(){
        //vytvoreni usecek podle urcite cubiky
        verices = Stream.rangeClosed(0,10).flatMap(
                i ->  Array.of(
                        Tuple.of(souradnice0.compute(i/10.0),white)
                )
        ).appendAll(
                Stream.rangeClosed(0, 10).flatMap(
                        i -> Array.of(
                                Tuple.of(souradnice1.compute(i/10.0),red)
                        )
                )
        ).appendAll(
                Stream.rangeClosed(0, 10).flatMap(
                        i -> Array.of(
                                Tuple.of(souradnice2.compute(i/10.0),purple)
                        )
                )
        ).appendAll(
                Stream.rangeClosed(0, 10).flatMap(
                        i -> Array.of(
                                Tuple.of(souradnice3.compute(i/10.0),blue)
                        )
                )
        ).toArray();
                /*
                souradnice.compute(0.8),
                souradnice.compute(0.5),
                souradnice.compute(0.7),
                souradnice.compute(1)
//*/
                /*


                souradnice0.compute(0),
                souradnice0.compute(0.1),
                souradnice0.compute(0.2),
                souradnice0.compute(0.3),
                souradnice0.compute(0.4),
                souradnice0.compute(0.5),
                souradnice0.compute(0.6),
                souradnice0.compute(0.7),
                souradnice0.compute(0.8),
                souradnice0.compute(0.9),
                souradnice0.compute(1),

                souradnice1.compute(0),
                souradnice1.compute(0.1),
                souradnice1.compute(0.2),
                souradnice1.compute(0.3),
                souradnice1.compute(0.4),
                souradnice1.compute(0.5),
                souradnice1.compute(0.6),
                souradnice1.compute(0.7),
                souradnice1.compute(0.8),
                souradnice1.compute(0.9),
                souradnice1.compute(1),

                souradnice2.compute(0),
                souradnice2.compute(0.1),
                souradnice2.compute(0.2),
                souradnice2.compute(0.3),
                souradnice2.compute(0.4),
                souradnice2.compute(0.5),
                souradnice2.compute(0.6),
                souradnice2.compute(0.7),
                souradnice2.compute(0.8),
                souradnice2.compute(0.9),
                souradnice2.compute(1),

                souradnice3.compute(0),
                souradnice3.compute(0.1),
                souradnice3.compute(0.2),
                souradnice3.compute(0.3),
                souradnice3.compute(0.4),
                souradnice3.compute(0.5),
                souradnice3.compute(0.6),
                souradnice3.compute(0.7),
                souradnice3.compute(0.8),
                souradnice3.compute(0.9),
                souradnice3.compute(1);


//*/



        indices = Stream.rangeClosed(0,9).flatMap(
                i -> Array.of(
                        /*
                        i,(i+1)%4,
                        i,(i+2)%4,
                        i,(i+3)%4
                        //*/
                        //vykresleni vzniklych usecek
                        i,i+1,
                        i+11,i+12,
                        i+22,i+23,
                        i+33,i+34

                )
        ).appendAll(
                //vybarveni mezi useckama za pomoci vykreslovanani trouhelniku
                Stream.rangeClosed(0, 9).flatMap(
                        i -> Array.of(
                                i, i+11,i+12,
                                i+1, i,i+12
                        )
                )
        ).appendAll(
                //vybarveni mezi useckama za pomoci vykreslovanani trouhelniku
                Stream.rangeClosed(11, 20).flatMap(
                        i -> Array.of(
                                i, i+11,i+12,
                                i+1, i,i+12
                        )
                )
        ).appendAll(
                //vybarveni mezi useckama za pomoci vykreslovanani trouhelniku
                Stream.rangeClosed(22, 31).flatMap(
                        i -> Array.of(
                                i, i+11,i+12,
                                i+1, i,i+12
                        )
                )
        ).toArray();

        parts = Array.of(
                new Part(0, 40, Topology.LINE_LIST),
                new Part(80, 60, Topology.TRIANGLE_LIST));
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
    public Seq<Solid.Part<Topology>> getParts() {
        return parts;
    }
}
