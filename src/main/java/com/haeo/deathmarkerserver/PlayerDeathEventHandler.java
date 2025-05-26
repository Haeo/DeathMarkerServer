package com.haeo.deathmarkerserver;

import net.minecraft.server.level.ServerPlayer; // 서버 플레이어
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component; // 채팅 메시지용
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "deathmarkerserver") // 메인 모드 클래스의 modId
public class PlayerDeathEventHandler {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        // 사망한 엔티티가 서버의 플레이어인지 확인합니다.
        if (entity instanceof ServerPlayer player) {
            // 서버 사이드 모드이므로, 클라이언트 관련 로직은 여기에 포함하지 않습니다.
            if (player.level().isClientSide) { // 혹시 모를 클라이언트 사이드 호출 방지
                return;
            }

            BlockPos deathPos = player.blockPosition(); // 사망 위치의 블록 좌표
            String dimension = player.level().dimension().location().toString(); // 차원 정보

            String message = String.format("%s 님이 %s (X: %d, Y: %d, Z: %d) 에서 사망했습니다.",
                    player.getName().getString(),
                    dimension,
                    deathPos.getX(),
                    deathPos.getY(),
                    deathPos.getZ());

            DeathMarkerServerMod.LOGGER.info(message); // 서버 로그에도 기록

            // 모든 플레이어에게 시스템 메시지로 알림
            // Component.literal()을 사용하여 텍스트 컴포넌트 생성
            if (player.getServer() != null) {
                player.getServer().getPlayerList().broadcastSystemMessage(Component.literal(message), false);
            }
        }
    }
}