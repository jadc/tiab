package red.jad.tiab.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import red.jad.tiab.TIAB;
import red.jad.tiab.backend.SpawnPacketHelper;
import red.jad.tiab.objects.entities.render.TickerEntityRenderer;

@Environment(EnvType.CLIENT)
public class TIABClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(TIAB.TICKER, ((dispatcher, context) -> {
            return new TickerEntityRenderer(dispatcher);
        }));
        ClientSidePacketRegistry.INSTANCE.register(SpawnPacketHelper.SPAWN_PACKET, ClientNetworking::spawnNonLivingEntity);
    }
}
