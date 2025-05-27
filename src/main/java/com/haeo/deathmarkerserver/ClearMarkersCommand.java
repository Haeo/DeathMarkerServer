package com.haeo.deathmarkerserver;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.Command; // Command.SINGLE_SUCCESS를 위해

public class ClearMarkersCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // 명령어: /clearallmarkers
        // 권한: 레벨 2 이상 (보통 OP 권한)
        dispatcher.register(Commands.literal("clearallmarkers")
                .requires(source -> source.hasPermission(2))
                .executes(ClearMarkersCommand::executeClearAll)
        );
    }

    private static int executeClearAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int count = DeathMarkerManager.getActiveMarkerCount(); // 수정된 메소드 사용
        DeathMarkerManager.clearAllMarkers();

        context.getSource().sendSuccess(() -> Component.literal(count + "개의 모든 사망 표식을 제거했습니다."), true);

        DeathMarkerServerMod.LOGGER.info(context.getSource().getTextName() + " 사용자가 /clearallmarkers 명령으로 " + count + "개의 표식을 제거했습니다.");
        return Command.SINGLE_SUCCESS;
    }
}