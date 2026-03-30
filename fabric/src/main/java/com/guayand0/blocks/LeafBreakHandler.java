package com.guayand0.blocks;

import com.guayand0.blocks.utils.DropUtils;
import com.guayand0.blocks.utils.TreeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class LeafBreakHandler {

    /**
     * Rompe todas las hojas conectadas a los troncos dados,
     * sin tocar árboles vecinos aunque se toquen las hojas.
     */
    public static void breakLeavesFromLogs(ServerLevel world, Player player, Set<BlockPos> logsRoto) {

        // 1️⃣ Detectar posibles troncos vecinos dentro de un radio seguro
        Set<BlockPos> logsVecinos = new HashSet<>();
        int vecinoRadio = 10; // radio de búsqueda para troncos cercanos

        for (BlockPos log : logsRoto) {
            for (int dx = -vecinoRadio; dx <= vecinoRadio; dx++) {
                for (int dy = -vecinoRadio; dy <= vecinoRadio; dy++) {
                    for (int dz = -vecinoRadio; dz <= vecinoRadio; dz++) {
                        BlockPos check = log.offset(dx, dy, dz);
                        BlockState state = world.getBlockState(check);
                        if (TreeUtils.isLog(state) && !logsRoto.contains(check)) {
                            logsVecinos.add(check);
                        }
                    }
                }
            }
        }

        // 2️⃣ BFS para encontrar todas las hojas conectadas
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        for (BlockPos log : logsRoto) {
            for (BlockPos adj : getAdjacent(log)) {
                if (!visited.contains(adj)) queue.add(adj);
            }
        }

        // 3️⃣ Lista de hojas que podrían romperse
        List<BlockPos> hojasCandidatas = new ArrayList<>();

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos)) continue;

            BlockState state = world.getBlockState(pos);
            if (!TreeUtils.isLeave(state)) continue;

            hojasCandidatas.add(pos);

            // expandir solo a hojas adyacentes
            for (BlockPos adj : getAdjacent(pos)) {
                if (!visited.contains(adj)) queue.add(adj);
            }
        }

        // 4️⃣ Clasificar hojas según tronco más cercano
        for (BlockPos hoja : hojasCandidatas) {
            int minDistRoto = logsRoto.stream().mapToInt(log -> manhattan(hoja, log)).min().orElse(Integer.MAX_VALUE);
            int minDistVecino = logsVecinos.stream().mapToInt(log -> manhattan(hoja, log)).min().orElse(Integer.MAX_VALUE);

            if (minDistRoto < minDistVecino) {
                // Romper hoja
                BlockState state = world.getBlockState(hoja);
                DropTracker.mark(hoja);
                DropUtils.breakBlockToInventory(world, player, hoja, state, null);
            }
            // si minDistVecino <= minDistRoto → hoja pertenece a otro árbol → no romper
        }
    }

    /** Devuelve bloques adyacentes (arriba, abajo, norte, sur, este, oeste) */
    private static List<BlockPos> getAdjacent(BlockPos pos) {
        return List.of(pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west());
    }

    /** Distancia Manhattan */
    private static int manhattan(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }
}
