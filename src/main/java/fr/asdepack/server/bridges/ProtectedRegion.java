package fr.asdepack.server.bridges;

import net.minecraft.core.BlockPos;

public class ProtectedRegion {

    private final String id;
    protected BlockPos minPoint;
    protected BlockPos maxPoint;

    public ProtectedRegion(String id, BlockPos minPoint, BlockPos maxPoint) {
        this.id = id;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
    }

    public String getId() {
        return this.id;
    }

    public BlockPos getMinPoint() {
        return minPoint;
    }

    public BlockPos getMaxPoint() {
        return maxPoint;
    }

    @Override
    public String toString() {
        return String.format("%s - [%d, %d, %d] to [%d, %d, %d]", 
            id, minPoint.getX(), minPoint.getY(), minPoint.getZ(),
            maxPoint.getX(), maxPoint.getY(), maxPoint.getZ());
    }
}