package com.haeo.deathmarkerserver;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
// UUID 클래스는 이제 이 파일에서 직접 사용하지 않으므로 임포트 제거 가능 (선택 사항)
// import java.util.UUID;

@Mod.EventBusSubscriber(modid = DeathMarkerServerMod.MOD_ID)
public class ServerTickHandler {

    // 감지 반경을 1칸으로 수정 (제곱값)
    private static final double DETECTION_RADIUS_SQUARED = 1.0 * 1.0; // 1.0으로 해도 동일

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            MinecraftServer server = event.getServer();
            if (server == null) return;

            Iterator<ArmorStand> markerIterator = DeathMarkerManager.getActiveMarkers().iterator();
            while (markerIterator.hasNext()) {
                ArmorStand marker = markerIterator.next();

                if (marker == null || marker.isRemoved()) {
                    markerIterator.remove(); // 이미 제거된 마커는 목록에서도 정리
                    continue;
                }

                // 소유자 UUID를 확인하고 소유자를 제외하는 로직을 제거합니다.
                // 이제 모든 플레이어에 대해 동일하게 작동합니다.

                // DeathMarkerServerMod.LOGGER.info("[DEBUG] 검사 중인 표식: " + marker.getCustomName().getString()); // 필요시 디버깅 로그

                boolean removedThisTick = false;
                for (ServerPlayer inspectingPlayer : server.getPlayerList().getPlayers()) {
                    if (!inspectingPlayer.level().dimension().equals(marker.level().dimension())) {
                        continue; // 다른 차원의 플레이어는 무시
                    }

                    double distanceSquared = marker.position().distanceToSqr(inspectingPlayer.position());
                    // DeathMarkerServerMod.LOGGER.info("[DEBUG] " + inspectingPlayer.getName().getString() + "와 표식(" + marker.getCustomName().getString() + ") 간 거리 제곱: " + distanceSquared);

                    if (distanceSquared <= DETECTION_RADIUS_SQUARED) {
                        DeathMarkerServerMod.LOGGER.info("[ACTION] " + inspectingPlayer.getName().getString() +
                                " (UUID: " + inspectingPlayer.getStringUUID() + ")가 " +
                                marker.getCustomName().getString() + // 표식의 이름 (원래 사망자 닉네임)
                                " 표식 근처(" + String.format("%.2f", Math.sqrt(distanceSquared)) +"m)에 접근하여 제거합니다.");

                        if (!marker.isRemoved()) {
                            marker.discard(); // 월드에서 엔티티 제거
                        }
                        markerIterator.remove(); // 관리 목록에서 제거 (Iterator 사용으로 안전)
                        removedThisTick = true; // 이번 틱에서 제거됨을 표시
                        break; // 이 마커는 이미 제거되었으므로, 다른 플레이어에 대한 검사는 불필요
                    }
                }
            }
        }
    }
}