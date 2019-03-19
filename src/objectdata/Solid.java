package objectdata;

import io.vavr.collection.IndexedSeq;
import io.vavr.collection.Seq;
import org.jetbrains.annotations.NotNull;

public interface Solid<VertexType, TopologyT> {
    class Part<TopologyType> {
        private final int startIndex;
        private final int primitiveCount;
        private final @NotNull TopologyType topology;
        Part(final int startIndex,
             final int primitiveCount,
             final @NotNull TopologyType topology) {
            this.startIndex = startIndex;
            this.primitiveCount = primitiveCount;
            this.topology = topology;
        }
        public int getStartIndex() {
            return startIndex;
        }
        public int getPrimitiveCount() {
            return primitiveCount;
        }
        public @NotNull TopologyType getTopology() {
            return topology;
        }
    }
    @NotNull IndexedSeq<VertexType> getVertices();
    @NotNull IndexedSeq<Integer> getIndices();
    @NotNull Seq<Part<TopologyT>> getParts();
}
