package top.kmar.mi.api.regedits.machines;

import top.kmar.mi.api.exception.TransferException;
import top.kmar.mi.api.net.messages.player.IPlayerHandler;
import top.kmar.mi.api.net.messages.player.PlayerHandlerRegedit;
import top.kmar.mi.api.regedits.AutoRegisterMachine;
import top.kmar.mi.api.regedits.others.AutoPlayerHandler;

import javax.annotation.Nonnull;

/**
 * PlayerHandle注册机
 * @author EmptyDreams
 */
public class PlayerHandlerRegistryMachine extends AutoRegisterMachine<AutoPlayerHandler, Object> {
    
    @Nonnull
    @Override
    public Class<AutoPlayerHandler> getTargetClass() {
        return AutoPlayerHandler.class;
    }
    
    @Override
    public void registry(Class<?> clazz, AutoPlayerHandler annotation, Object data) {
        try {
            IPlayerHandler handler = (IPlayerHandler) clazz.getField(annotation.field()).get(null);
            PlayerHandlerRegedit.registry(annotation.value(), handler);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw TransferException.instance(e);
        }
    }
    
}