package com.haeo.deathmarkerserver;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid = DeathMarkerServerMod.MOD_ID)
public class ServerTickHandler {

    // 감지 반경을 2칸으로 수정 (제곱값)
    private static final double DETECTION_RADIUS_SQUARED = 2.0 * 2.0;
    private static int tickCounter = 0;
    private static final int CHECK_INTERVAL_IN_TICKS = 20; // 1초 (20틱)
    private static final long MARKER_EXPIRATION_TIME_MS = 1000 * 60 * 60; // 1시간 (밀리초 단위)
    // 표식 생성 후 근접 삭제 검사 시작까지의 유예 시간 (밀리초 단위)
    private static final long PROXIMITY_CHECK_GRACE_PERIOD_MS = 2000; // 2초 유예

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;
            if (tickCounter >= CHECK_INTERVAL_IN_TICKS) {
                tickCounter = 0;

                MinecraftServer server = event.getServer();
                if (server == null) return;

                long currentTimeMillis = System.currentTimeMillis();
                Iterator<DeathMarkerInfo> markerInfoIterator = DeathMarkerManager.getActiveMarkersInfo().iterator();

                while (markerInfoIterator.hasNext()) {
                    DeathMarkerInfo markerInfo = markerInfoIterator.next();
                    ArmorStand markerEntity = markerInfo.getMarkerEntity();
                    long creationTime = markerInfo.getCreationTimeMillis(); // 표식 생성 시간

                    if (markerEntity == null || markerEntity.isRemoved()) {
                        // DeathMarkerServerMod.LOGGER.info("[DEBUG] 이미 제거된 엔티티를 가진 표식 정보를 목록에서 제거: " + markerInfo.getMarkerUUID());
                        markerInfoIterator.remove();
                        continue;
                    }

                    // 1. 자동 소멸 검사 (1시간 경과)
                    if (currentTimeMillis - creationTime > MARKER_EXPIRATION_TIME_MS) {
                        DeathMarkerServerMod.LOGGER.info("[AUTO-REMOVE] 1시간이 경과한 표식(" + markerEntity.getCustomName().getString() + ", UUID: " + markerEntity.getUUID() + ")을 자동으로 제거합니다.");
                        if (!markerEntity.isRemoved()) {
                            markerEntity.discard();
                        }
                        markerInfoIterator.remove();
                        continue;
                    }

                    // 2. 근접 플레이어에 의한 삭제 검사 (유예 시간 확인 후)
                    // 유예 시간이 지나야만 근접 삭제 로직을 실행
                    if (currentTimeMillis - creationTime > PROXIMITY_CHECK_GRACE_PERIOD_MS) {
                        boolean removedByProximity = false;
                        for (ServerPlayer inspectingPlayer : server.getPlayerList().getPlayers()) {
                            if (!inspectingPlayer.level().dimension().equals(markerEntity.level().dimension())) {
                                continue;
                            }

                            double distanceSquared = markerEntity.position().distanceToSqr(inspectingPlayer.position());

                            if (distanceSquared <= DETECTION_RADIUS_SQUARED) { // 반경 2칸으로 수정됨
                                DeathMarkerServerMod.LOGGER.info("[PROXIMITY-REMOVE] " + inspectingPlayer.getName().getString() +
                                        " (UUID: " + inspectingPlayer.getStringUUID() + ")가 " +
                                        markerEntity.getCustomName().getString() +
                                        " 표식 근처(" + String.format("%.2f", Math.sqrt(distanceSquared)) +"m)에 접근하여 제거합니다. (유예시간 경과)");

                                if (!markerEntity.isRemoved()) {
                                    markerEntity.discard();
                                }
                                markerInfoIterator.remove();
                                removedByProximity = true;
                                break;
                            }
                        }
                        // 만약 근접 삭제로 제거되었다면, 이 표식에 대한 처리는 끝.
                    } else {
                        // 아직 유예 기간 중임을 디버깅 시 로그로 확인할 수 있습니다.
                        // DeathMarkerServerMod.LOGGER.info("[DEBUG] 표식 " + markerEntity.getUUID() + "은(는) 아직 근접 삭제 유예 기간 중입니다. (생성 후 " + (currentTimeMillis - creationTime) + "ms 경과)");
                    }
                }
            }
        }
    }
}