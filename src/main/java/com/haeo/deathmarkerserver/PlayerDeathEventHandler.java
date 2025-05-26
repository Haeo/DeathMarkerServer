package com.haeo.deathmarkerserver; // 패키지명 변경

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = DeathMarkerServerMod.MOD_ID) // 변경된 MOD_ID 사용
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

            DeathMarkerServerMod.LOGGER.info(chatMessage); // 변경된 LOGGER 사용
            if (player.getServer() != null) {
                player.getServer().getPlayerList().broadcastSystemMessage(Component.literal(chatMessage), false);
            }

            createDeathMarker(player.level(), deathPos, playerName, player.getUUID());
        }
    }

    private static void createDeathMarker(Level world, BlockPos pos, String playerName, UUID playerUUID) {
        DeathMarkerServerMod.LOGGER.info("[DEBUG] createDeathMarker 호출됨. 플레이어: " + playerName + ", 위치: " + pos.toString() + ", 차원: " + world.dimension().location().toString());
        if (world.isClientSide()) {
            DeathMarkerServerMod.LOGGER.warn("[DEBUG] 클라이언트 사이드에서 createDeathMarker 호출되어 반환됨.");
            return;
        }

        ArmorStand marker = new ArmorStand(EntityType.ARMOR_STAND, world);
        // Y좌표를 블록의 중앙으로 설정 (원래 요청하신 "그 자리"에 가깝게)
        marker.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        marker.setInvisible(true);
        marker.setNoGravity(true);
        marker.setCustomName(Component.literal(playerName));
        marker.setCustomNameVisible(true);

        marker.addTag("death_marker");
        marker.addTag(DeathMarkerServerMod.MOD_ID + "_marker");
        marker.addTag("player_uuid_" + playerUUID.toString());

        DeathMarkerServerMod.LOGGER.info("[DEBUG] 아머 스탠드 엔티티 생성 및 설정 완료 (" + playerName + "). 월드에 추가 시도...");
        boolean added = world.addFreshEntity(marker);

        if (added) {
            DeathMarkerServerMod.LOGGER.info("[SUCCESS] " + playerName + "의 사망 표식 엔티티 월드에 추가 성공! UUID: " + marker.getUUID());
            DeathMarkerManager.addMarker(marker);
            DeathMarkerServerMod.LOGGER.info("[DEBUG] DeathMarkerManager에 표식 추가 완료: " + marker.getUUID());
        } else {
            DeathMarkerServerMod.LOGGER.error("[FAILURE] " + playerName + "의 사망 표식 엔티티 월드에 추가 실패!");
        }
    }
}