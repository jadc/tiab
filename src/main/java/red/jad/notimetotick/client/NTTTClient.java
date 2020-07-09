package red.jad.notimetotick.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import red.jad.notimetotick.NTTT;
import red.jad.notimetotick.backend.SpawnPacketHelper;
import red.jad.notimetotick.objects.entities.render.TickerEntityRenderer;

@Environment(EnvType.CLIENT)
public class NTTTClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(NTTT.TICKER, ((dispatcher, context) -> {
            return new TickerEntityRenderer(dispatcher);
        }));
        ClientSidePacketRegistry.INSTANCE.register(SpawnPacketHelper.SPAWN_PACKET, ClientNetworking::spawnNonLivingEntity);
    }
}
