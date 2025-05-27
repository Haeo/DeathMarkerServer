package com.haeo.deathmarkerserver;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.decoration.ArmorStand;
//import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
//import java.util.UUID;

@Mod.EventBusSubscriber(modid = DeathMarkerServerMod.MOD_ID)
public class PlayerDeathEventHandler {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof ServerPlayer player) {
            if (player.level().isClientSide) {
                return;
            }

            BlockPos deathPos = player.blockPosition();
            String dimension = player.level().dimension().location().toString();
            String playerName = player.getName().getString();

            String chatMessage = String.format("%s 님이 %s (X: %d, Y: %d, Z: %d) 에서 사망했습니다.",
                    playerName,
                    dimension,
                    deathPos.getX(),
                    deathPos.getY(),
                    deathPos.getZ());

            DeathMarkerServerMod.LOGGER.info(chatMessage);
            if (player.getServer() != null) {
                player.getServer().getPlayerList().broadcastSystemMessage(Component.literal(chatMessage), false);
            }

//            createDeathMarker(player.level(), deathPos, playerName, player.getUUID());
        }
    }

//    private static void createDeathMarker(Level world, BlockPos pos, String playerName, UUID playerUUID) {
//        DeathMarkerServerMod.LOGGER.info("[DEBUG] createDeathMarker 호출됨. 플레이어: " + playerName + ", 위치: " + pos.toString() + ", 차원: " + world.dimension().location().toString());
//        if (world.isClientSide()) {
//            DeathMarkerServerMod.LOGGER.warn("[DEBUG] 클라이언트 사이드에서 createDeathMarker 호출되어 반환됨.");
//            return;
//        }
//
//        ArmorStand marker = new ArmorStand(EntityType.ARMOR_STAND, world);
//        // Y좌표를 요청대로 다시 1.5로 수정
//        marker.setPos(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
//        marker.setInvisible(true);
//        marker.setNoGravity(true);
//        marker.setCustomName(Component.literal(playerName));
//        marker.setCustomNameVisible(true);
//
//        marker.addTag("death_marker");
//        marker.addTag(DeathMarkerServerMod.MOD_ID + "_marker");
//        marker.addTag("player_uuid_" + playerUUID.toString()); // 이 태그는 현재 소유자 구분에 사용하지 않지만, 디버깅이나 다른 기능 확장에 유용할 수 있음
//
//        DeathMarkerServerMod.LOGGER.info("[DEBUG] 아머 스탠드 엔티티 생성 및 설정 완료 (" + playerName + "). 월드에 추가 시도...");
//        boolean added = world.addFreshEntity(marker);
//
//        if (added) {
//            DeathMarkerServerMod.LOGGER.info("[SUCCESS] " + playerName + "의 사망 표식 엔티티 월드에 추가 성공! UUID: " + marker.getUUID());
//            // DeathMarkerManager가 ArmorStand를 받아 내부적으로 DeathMarkerInfo를 생성하고 시간 기록
//            DeathMarkerManager.addMarker(marker);
//        } else {
//            DeathMarkerServerMod.LOGGER.error("[FAILURE] " + playerName + "의 사망 표식 엔티티 월드에 추가 실패!");
//        }
//    }
}