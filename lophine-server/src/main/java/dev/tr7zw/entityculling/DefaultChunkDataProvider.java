package dev.tr7zw.entityculling;

import com.logisticscraft.occlusionculling.DataProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DefaultChunkDataProvider implements DataProvider {
    private final Level level;

    public DefaultChunkDataProvider(Level level) {
        this.level = level;
    }

    @Override
    public boolean prepareChunk(int chunkX, int chunkZ) {
        return this.level.getChunkIfLoaded(chunkX, chunkZ) != null;
    }

    @Override
    public boolean isOpaqueFullCube(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);

        final ChunkAccess access = this.level.getChunkIfLoaded(pos);
        if (access == null) {
            return false;
        }

        if (this.level.isOutsideBuildHeight(pos)) {
            return Blocks.VOID_AIR.defaultBlockState().isSolidRender();
        } else {
            return access.getBlockState(pos).isSolidRender();// 好孩子不要学坏叔叔这样绕过异步拦截()
        }
    }

    @Override
    public void cleanup() {
        DataProvider.super.cleanup();
    }

}