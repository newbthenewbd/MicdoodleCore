package micdoodle8.mods.miccore;

import net.minecraft.launchwrapper.Launch;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@IFMLLoadingPlugin.TransformerExclusions(value = { "micdoodle8.mods.miccore" })
public class MicdoodleTransformer implements net.minecraft.launchwrapper.IClassTransformer
{
	HashMap<String, ObfuscationEntry> nodemap = new HashMap<String, ObfuscationEntry>();
	private boolean deobfuscated = true;
	private boolean optifinePresent;
	private boolean isServer;
    private boolean playerApiActive;
    private DefaultArtifactVersion mcVersion;

    private String nameForgeHooksClient;
	private String nameConfManager;
	private String namePlayerController;
	private String nameEntityLiving;
	private String nameEntityItem;
	private String nameEntityRenderer;
	private String nameItemRenderer;
	private String nameGuiSleep;
	private String nameEffectRenderer;
	private String nameNetHandlerPlay;
	private String nameVertexBuffer;
	private String nameRenderGlobal;
	private String nameRenderManager;
	private String nameTileEntityRenderer;
	private String nameEntity;
	private String nameChunkProviderServer;
	private String nameEntityArrow;
	private String nameEntityGolem;
	private String nameWorld;

	private static final String KEY_CLASS_PLAYER_MP = "PlayerMP";
	private static final String KEY_CLASS_WORLD = "worldClass";
	private static final String KEY_CLASS_CONF_MANAGER = "confManagerClass";
	private static final String KEY_CLASS_GAME_PROFILE = "gameProfileClass";
	private static final String KEY_CLASS_ITEM_IN_WORLD_MANAGER = "itemInWorldManagerClass";
	private static final String KEY_CLASS_PLAYER_CONTROLLER = "playerControllerClass";
	private static final String KEY_CLASS_PLAYER_SP = "playerClient";
	private static final String KEY_CLASS_STAT_FILE_WRITER = "statFileWriterClass";
	private static final String KEY_CLASS_NET_HANDLER_PLAY = "netHandlerPlayClientClass";
	private static final String KEY_CLASS_ENTITY_LIVING = "entityLivingClass";
	private static final String KEY_CLASS_ENTITY_ITEM = "entityItemClass";
	private static final String KEY_CLASS_ENTITY_RENDERER = "entityRendererClass";
	private static final String KEY_CLASS_WORLD_RENDERER = "worldRendererClass";
	private static final String KEY_CLASS_RENDER_GLOBAL = "renderGlobalClass";
	private static final String KEY_CLASS_RENDER_MANAGER = "renderManagerClass";
	private static final String KEY_CLASS_TESSELLATOR = "tessellatorClass";
	private static final String KEY_CLASS_TILEENTITY_RENDERER = "tileEntityRendererClass";
	private static final String KEY_CLASS_CONTAINER_PLAYER = "containerPlayer";
	private static final String KEY_CLASS_MINECRAFT = "minecraft";
	private static final String KEY_CLASS_SESSION = "session";
	private static final String KEY_CLASS_GUI_SCREEN = "guiScreen";
	private static final String KEY_CLASS_ITEM_RENDERER = "itemRendererClass";
	private static final String KEY_CLASS_VEC3 = "vecClass";
	private static final String KEY_CLASS_ENTITY = "entityClass";
	private static final String KEY_CLASS_TILEENTITY = "tileEntityClass";
	private static final String KEY_CLASS_GUI_SLEEP = "guiSleepClass";
	private static final String KEY_CLASS_EFFECT_RENDERER = "effectRendererClass";
	private static final String KEY_CLASS_FORGE_HOOKS_CLIENT = "forgeHooks";
	private static final String KEY_CLASS_CUSTOM_PLAYER_MP = "customPlayerMP";
	private static final String KEY_CLASS_CUSTOM_PLAYER_SP = "customPlayerSP";
	private static final String KEY_CLASS_CUSTOM_OTHER_PLAYER = "customEntityOtherPlayer";
	private static final String KEY_CLASS_PACKET_SPAWN_PLAYER = "packetSpawnPlayer";
	private static final String KEY_CLASS_ENTITY_OTHER_PLAYER = "entityOtherPlayer";
	private static final String KEY_CLASS_SERVER = "minecraftServer";
	private static final String KEY_CLASS_WORLD_SERVER = "worldServer";
	private static final String KEY_CLASS_WORLD_CLIENT = "worldClient";
    private static final String KEY_CLASS_CHUNK_PROVIDER_SERVER = "chunkProviderServer";
	private static final String KEY_CLASS_ICHUNKPROVIDER = "IChunkProvider";
    private static final String KEY_NET_HANDLER_LOGIN_SERVER = "netHandlerLoginServer";
    private static final String KEY_CLASS_ENTITY_ARROW = "entityArrow";
    private static final String KEY_CLASS_RENDERER_LIVING_ENTITY = "rendererLivingEntity";
    private static final String KEY_CLASS_ENTITYGOLEM = "entityGolem";
    private static final String KEY_CLASS_IBLOCKACCESS = "iBlockAccess";
    private static final String KEY_CLASS_BLOCKPOS = "blockPos";
    private static final String KEY_CLASS_IBLOCKSTATE = "blockState";
    private static final String KEY_CLASS_ICAMERA = "icameraClass";

	private static final String KEY_FIELD_THE_PLAYER = "thePlayer";
//	private static final String KEY_FIELD_WORLDRENDERER_GLRENDERLIST = "glRenderList";
	private static final String KEY_FIELD_CPS_WORLDOBJ = "cps_worldObj";
	private static final String KEY_FIELD_CPS_SERVER_CHUNK_GEN = "serverChunkGenerator";

	private static final String KEY_METHOD_CREATE_PLAYER = "createPlayerMethod";
	private static final String KEY_METHOD_RESPAWN_PLAYER = "respawnPlayerMethod";
	private static final String KEY_METHOD_CREATE_CLIENT_PLAYER = "createClientPlayerMethod";
	private static final String KEY_METHOD_MOVE_ENTITY = "moveEntityMethod";
	private static final String KEY_METHOD_ON_UPDATE = "onUpdateMethod";
	private static final String KEY_METHOD_UPDATE_LIGHTMAP = "updateLightmapMethod";
	private static final String KEY_METHOD_RENDER_OVERLAYS = "renderOverlaysMethod";
	private static final String KEY_METHOD_UPDATE_FOG_COLOR = "updateFogColorMethod";
	private static final String KEY_METHOD_GET_FOG_COLOR = "getFogColorMethod";
	private static final String KEY_METHOD_GET_SKY_COLOR = "getSkyColorMethod";
	private static final String KEY_METHOD_WAKE_ENTITY = "wakeEntityMethod";
	private static final String KEY_METHOD_BED_ORIENT_CAMERA = "orientBedCamera";
	private static final String KEY_METHOD_RENDER_PARTICLES = "renderParticlesMethod";
	private static final String KEY_METHOD_CUSTOM_PLAYER_MP = "customPlayerMPConstructor";
	private static final String KEY_METHOD_CUSTOM_PLAYER_SP = "customPlayerSPConstructor";
	private static final String KEY_METHOD_ATTEMPT_LOGIN_BUKKIT = "attemptLoginMethodBukkit";
	private static final String KEY_METHOD_HANDLE_SPAWN_PLAYER = "handleSpawnPlayerMethod";
	private static final String KEY_METHOD_ORIENT_CAMERA = "orientCamera";
	private static final String KEY_METHOD_DO_RENDER_ENTITY = "doRenderEntityMethod";
	private static final String KEY_METHOD_PRERENDER_BLOCKS = "preRenderBlocksMethod"; //VertexBuffer.preRenderBlocks(int)
	private static final String KEY_METHOD_SETUP_GL = "setupGLTranslationMethod"; //VertexBuffer.setupGLTranslation()
	private static final String KEY_METHOD_SET_POSITION = "setPositionMethod"; //VertexBuffer.setPosition()
	private static final String KEY_METHOD_LOAD_RENDERERS = "loadRenderersMethod"; //RenderGlobal.loadRenderers()
	private static final String KEY_METHOD_RENDERGLOBAL_INIT = "renderGlobalInitMethod"; //RenderGlobal.RenderGlobal()
	private static final String KEY_METHOD_RENDERGLOBAL_SORTANDRENDER = "sortAndRenderMethod"; //RenderGlobal.sortAndRender()
	private static final String KEY_METHOD_TILERENDERER_RENDERTILEAT = "renderTileAtMethod"; //TileEntityRendererDispatcher.renderTileEntityAt()
	private static final String KEY_METHOD_START_GAME = "startGame";
	private static final String KEY_METHOD_CAN_RENDER_FIRE = "canRenderOnFire";
	private static final String KEY_METHOD_CGS_POPULATE = "CGSpopulate";
	private static final String KEY_METHOD_RENDER_MODEL = "renderModel";
	private static final String KEY_METHOD_RAIN_STRENGTH = "getRainStrength";
	private static final String KEY_METHOD_REGISTEROF = "registerOF";
	private static final String KEY_METHOD_SETUP_TERRAIN = "setupTerrain";
	private static final String KEY_METHOD_GET_EYE_HEIGHT = "getEyeHeight";
	private static final String KEY_METHOD_ENABLE_ALPHA = "enableAlphaMethod";

	private static final String CLASS_RUNTIME_INTERFACE = "micdoodle8/mods/miccore/Annotations$RuntimeInterface";
	private static final String CLASS_ALT_FORVERSION = "micdoodle8/mods/miccore/Annotations$AltForVersion";
	private static final String CLASS_VERSION_SPECIFIC = "micdoodle8/mods/miccore/Annotations$VersionSpecific";
	private static final String CLASS_MICDOODLE_PLUGIN = "micdoodle8/mods/miccore/MicdoodlePlugin";
//	private static final String CLASS_CLIENT_PROXY_MAIN = "micdoodle8/mods/galacticraft/core/proxy/ClientProxyCore";
//	private static final String CLASS_WORLD_UTIL = "micdoodle8/mods/galacticraft/core/util/WorldUtil";
	private static final String CLASS_TRANSFORMER_HOOKS = "micdoodle8/mods/galacticraft/core/TransformerHooks";
	private static final String CLASS_GL11 = "org/lwjgl/opengl/GL11";
//	private static final String CLASS_RENDER_PLAYER_GC = "micdoodle8/mods/galacticraft/core/client/render/entities/RenderPlayerGC";
	private static final String CLASS_IENTITYBREATHABLE = "micdoodle8/mods/galacticraft/api/entity/IEntityBreathable";
    private static final String CLASS_SYNCMOD_CLONEPLAYER = "sync/common/tileentity/TileEntityDualVertical";
    private static final String CLASS_RENDERPLAYEROF = "RenderPlayerOF";
    
	private static int operationCount = 0;
	private static int injectionCount = 0;

	public MicdoodleTransformer() {
        this.mcVersion = new DefaultArtifactVersion((String) FMLInjectionData.data()[4]);

        try {
        	deobfuscated = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
            optifinePresent = Launch.classLoader.getClassBytes("CustomColorizer") != null;
            playerApiActive = Launch.classLoader.getClassBytes("api.player.forge.PlayerAPITransformer") != null;
        } catch (final Exception e) { }

    	Launch.classLoader.addTransformerExclusion(CLASS_IENTITYBREATHABLE.replace('/', '.'));

        if (this.mcVersionMatches("[1.10.2]"))
        {
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_PLAYER_MP, new ObfuscationEntry("net/minecraft/entity/player/EntityPlayerMP", "lu"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_WORLD, new ObfuscationEntry("net/minecraft/world/World", "aid"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_CONF_MANAGER, new ObfuscationEntry("net.minecraft.server.management.PlayerList", "mp"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_GAME_PROFILE, new ObfuscationEntry("com/mojang/authlib/GameProfile"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_ITEM_IN_WORLD_MANAGER, new ObfuscationEntry("net.minecraft.server.management.PlayerInteractionManager", "lv"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_PLAYER_CONTROLLER, new ObfuscationEntry("net/minecraft/client/multiplayer/PlayerControllerMP", "blm"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_PLAYER_SP, new ObfuscationEntry("net/minecraft/client/entity/EntityPlayerSP", "bnn"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_STAT_FILE_WRITER, new ObfuscationEntry("net.minecraft.stats.StatisticsManager", "nx"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_NET_HANDLER_PLAY, new ObfuscationEntry("net/minecraft/client/network/NetHandlerPlayClient", "bll"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_ENTITY_LIVING, new ObfuscationEntry("net/minecraft/entity/EntityLivingBase", "sf"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_ENTITY_ITEM, new ObfuscationEntry("net/minecraft/entity/item/EntityItem", "yk"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_ENTITY_RENDERER, new ObfuscationEntry("net/minecraft/client/renderer/EntityRenderer", "bnz"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_WORLD_RENDERER, new ObfuscationEntry("net.minecraft.client.renderer.VertexBuffer", "bnt"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_RENDER_GLOBAL, new ObfuscationEntry("net/minecraft/client/renderer/RenderGlobal", "boh"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_TESSELLATOR, new ObfuscationEntry("net/minecraft/client/renderer/Tessellator", "bon"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_RENDER_MANAGER, new ObfuscationEntry("net/minecraft/client/renderer/entity/RenderManager", "bsh"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_TILEENTITY_RENDERER, new ObfuscationEntry("net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher", "bqf"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_CONTAINER_PLAYER, new ObfuscationEntry("net/minecraft/inventory/ContainerPlayer", "abu"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_MINECRAFT, new ObfuscationEntry("net/minecraft/client/Minecraft", "bcx"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_SESSION, new ObfuscationEntry("net/minecraft/util/Session", "bde"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_GUI_SCREEN, new ObfuscationEntry("net/minecraft/client/gui/GuiScreen", "bft"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_ITEM_RENDERER, new ObfuscationEntry("net/minecraft/client/renderer/ItemRenderer", "bod"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_VEC3, new ObfuscationEntry("net.minecraft.util.math.Vec3d", "bcb"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_ENTITY, new ObfuscationEntry("net/minecraft/entity/Entity", "rw"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_GUI_SLEEP, new ObfuscationEntry("net/minecraft/client/gui/GuiSleepMP", "bfk"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_EFFECT_RENDERER, new ObfuscationEntry("net.minecraft.client.particle.ParticleManager", "bms"));

            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_FORGE_HOOKS_CLIENT, new ObfuscationEntry("net/minecraftforge/client/ForgeHooksClient"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_MP, new ObfuscationEntry("micdoodle8/mods/galacticraft/core/entities/player/GCEntityPlayerMP"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_SP, new ObfuscationEntry("micdoodle8/mods/galacticraft/core/entities/player/GCEntityClientPlayerMP"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_CUSTOM_OTHER_PLAYER, new ObfuscationEntry("micdoodle8/mods/galacticraft/core/entities/player/GCEntityOtherPlayerMP"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_PACKET_SPAWN_PLAYER, new ObfuscationEntry("net.minecraft.network.play.server.SPacketSpawnPlayer", "fs"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_ENTITY_OTHER_PLAYER, new ObfuscationEntry("net/minecraft/client/entity/EntityOtherPlayerMP", "bno"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_SERVER, new ObfuscationEntry("net/minecraft/server/MinecraftServer"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_WORLD_SERVER, new ObfuscationEntry("net/minecraft/world/WorldServer", "ls"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_WORLD_CLIENT, new ObfuscationEntry("net/minecraft/client/multiplayer/WorldClient", "bln"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_TILEENTITY, new ObfuscationEntry("net/minecraft/tileentity/TileEntity", "aqk"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_CHUNK_PROVIDER_SERVER, new ObfuscationEntry("net/minecraft/world/gen/ChunkProviderServer", "lr"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_ICHUNKPROVIDER, new ObfuscationEntry("net/minecraft/world/chunk/IChunkProvider", "asq"));
            this.nodemap.put(MicdoodleTransformer.KEY_NET_HANDLER_LOGIN_SERVER, new ObfuscationEntry("net/minecraft/server/network/NetHandlerLoginServer", "mg"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_ENTITY_ARROW, new ObfuscationEntry("net/minecraft/entity/projectile/EntityArrow", "zv"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_RENDERER_LIVING_ENTITY, new ObfuscationEntry("net.minecraft.client.renderer.entity.RenderLivingBase", "bsy"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_ENTITYGOLEM, new ObfuscationEntry("net/minecraft/entity/monster/EntityGolem", "wf"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_IBLOCKACCESS, new ObfuscationEntry("net/minecraft/world/IBlockAccess", "aih"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_BLOCKPOS, new ObfuscationEntry("net.minecraft.util.math.BlockPos", "cm"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_IBLOCKSTATE, new ObfuscationEntry("net/minecraft/block/state/IBlockState", "ars"));
            this.nodemap.put(MicdoodleTransformer.KEY_CLASS_ICAMERA, new ObfuscationEntry("net/minecraft/client/renderer/culling/ICamera", "brf"));

            this.nodemap.put(MicdoodleTransformer.KEY_FIELD_THE_PLAYER, new FieldObfuscationEntry("thePlayer", "h"));
//            this.nodemap.put(MicdoodleTransformer.KEY_FIELD_WORLDRENDERER_GLRENDERLIST, new FieldObfuscationEntry("glRenderList", "z"));
            this.nodemap.put(MicdoodleTransformer.KEY_FIELD_CPS_WORLDOBJ, new FieldObfuscationEntry("worldObj", "i"));
            this.nodemap.put(MicdoodleTransformer.KEY_FIELD_CPS_SERVER_CHUNK_GEN, new FieldObfuscationEntry("serverChunkGenerator", "e"));

            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_CREATE_PLAYER, new MethodObfuscationEntry("createPlayerForUser", "g", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_GAME_PROFILE) + ";)L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_MP) + ";"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_RESPAWN_PLAYER, new MethodObfuscationEntry("recreatePlayerEntity", "a", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_MP) + ";IZ)L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_MP) + ";"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_CREATE_CLIENT_PLAYER, new MethodObfuscationEntry("func_178892_a", "a", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_STAT_FILE_WRITER) + ";)L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_SP) + ";"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_MOVE_ENTITY, new MethodObfuscationEntry("moveEntityWithHeading", "g", "(FF)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_ON_UPDATE, new MethodObfuscationEntry("onUpdate", "m", "()V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_UPDATE_LIGHTMAP, new MethodObfuscationEntry("updateLightmap", "g", "(F)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_RENDER_OVERLAYS, new MethodObfuscationEntry("renderOverlays", "b", "(F)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_UPDATE_FOG_COLOR, new MethodObfuscationEntry("updateFogColor", "h", "(F)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_GET_FOG_COLOR, new MethodObfuscationEntry("getFogColor", "f", "(FF)L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_VEC3) + ";"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_GET_SKY_COLOR, new MethodObfuscationEntry("getSkyColor", "a", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY) + ";F)L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_VEC3) + ";"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_WAKE_ENTITY, new MethodObfuscationEntry("wakeFromSleep", "a", "()V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_BED_ORIENT_CAMERA, new MethodObfuscationEntry("orientBedCamera", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_IBLOCKACCESS) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_BLOCKPOS) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_IBLOCKSTATE) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY) + ";)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_RENDER_PARTICLES, new MethodObfuscationEntry("renderParticles", "a", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY) + ";F)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_MP, new MethodObfuscationEntry("<init>", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_SERVER) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD_SERVER) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_GAME_PROFILE) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ITEM_IN_WORLD_MANAGER) + ";)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_SP, new MethodObfuscationEntry("<init>", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_MINECRAFT) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_NET_HANDLER_PLAY) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_STAT_FILE_WRITER) + ";)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_HANDLE_SPAWN_PLAYER, new MethodObfuscationEntry("handleSpawnPlayer", "a", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PACKET_SPAWN_PLAYER) + ";)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_ORIENT_CAMERA, new MethodObfuscationEntry("orientCamera", "f", "(F)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_DO_RENDER_ENTITY, new MethodObfuscationEntry("doRenderEntity", "a", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY) + ";DDDFFZ)Z"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_SETUP_GL, new MethodObfuscationEntry("setupGLTranslation", "f", "()V")); //func_78905_g
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_PRERENDER_BLOCKS, new MethodObfuscationEntry("preRenderBlocks", "b", "(I)V")); //func_147890_b
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_SET_POSITION, new MethodObfuscationEntry("setPosition", "a", "(III)V")); //func_78913_a
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_LOAD_RENDERERS, new MethodObfuscationEntry("loadRenderers", "a", "()V")); //func_72712_a
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_RENDERGLOBAL_INIT, new MethodObfuscationEntry("<init>", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_MINECRAFT) + ";)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_RENDERGLOBAL_SORTANDRENDER, new MethodObfuscationEntry("sortAndRender", "a", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY_LIVING) + ";ID)I")); //func_72719_a
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_TILERENDERER_RENDERTILEAT, new MethodObfuscationEntry("renderTileEntityAt", "a", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_TILEENTITY) + ";DDDF)V")); //bmc/a (Land;DDDF)V net/minecraft/client/renderer/tileentity/TileEntityRendererDispatcher/func_147549_a (Lnet/minecraft/tileentity/TileEntity;DDDF)V
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_START_GAME, new MethodObfuscationEntry("startGame", "am", "()V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_CAN_RENDER_FIRE, new MethodObfuscationEntry("canRenderOnFire", "bd", "()Z"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_CGS_POPULATE, new MethodObfuscationEntry("populate", "b", "(II)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_ATTEMPT_LOGIN_BUKKIT, new MethodObfuscationEntry("attemptLogin", "attemptLogin", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_NET_HANDLER_LOGIN_SERVER) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_GAME_PROFILE) + ";Ljava/lang/String;)L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_MP) + ";"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_RENDER_MODEL, new MethodObfuscationEntry("renderModel", "a", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY_LIVING) + ";FFFFFF)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_RAIN_STRENGTH, new MethodObfuscationEntry("getRainStrength", "j", "(F)F"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_REGISTEROF, new MethodObfuscationEntry("register", "register", "()V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_SETUP_TERRAIN, new MethodObfuscationEntry("setupTerrain", "a", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY) + ";DL" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ICAMERA) + ";IZ)V"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_GET_EYE_HEIGHT, new MethodObfuscationEntry("getEyeHeight", "bq", "()F"));
            this.nodemap.put(MicdoodleTransformer.KEY_METHOD_ENABLE_ALPHA, new MethodObfuscationEntry("enableAlpha", "d", "()V"));
        }

        try
        {
            isServer = Launch.classLoader.getClassBytes(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_RENDER_GLOBAL)) == null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (name.contains("galacticraft"))
		{
			return this.transformCustomAnnotations(bytes);
		}
		else
		{
			if (this.nameForgeHooksClient == null)
			{
				this.nameForgeHooksClient = this.getName(MicdoodleTransformer.KEY_CLASS_FORGE_HOOKS_CLIENT);
				if (this.deobfuscated)
					this.populateNamesDeObf();
				else
					this.populateNamesObf();
			}
			String testName = name.replace('.', '/');
			if (testName.equals(this.nameForgeHooksClient))
			{
				return this.transformForgeHooks(bytes);
			}

			if (testName.equals(MicdoodleTransformer.CLASS_SYNCMOD_CLONEPLAYER))
			{
				return this.transformSyncMod(bytes);
			}

			if (testName.equals(MicdoodleTransformer.CLASS_RENDERPLAYEROF))
			{
				return this.transformOptifine(bytes);
			}

			if (testName.length() <= 3 || this.deobfuscated)
			{
				return this.transformVanilla(testName, bytes);
			}
		}
		
		return bytes;
	}

	private void populateNamesDeObf()
	{
		this.nameConfManager  = this.getName(MicdoodleTransformer.KEY_CLASS_CONF_MANAGER);
		this.namePlayerController  = this.getName(MicdoodleTransformer.KEY_CLASS_PLAYER_CONTROLLER);
		this.nameEntityLiving  = this.getName(MicdoodleTransformer.KEY_CLASS_ENTITY_LIVING);
		this.nameEntityItem  = this.getName(MicdoodleTransformer.KEY_CLASS_ENTITY_ITEM);
		this.nameEntityRenderer  = this.getName(MicdoodleTransformer.KEY_CLASS_ENTITY_RENDERER);
		this.nameItemRenderer  = this.getName(MicdoodleTransformer.KEY_CLASS_ITEM_RENDERER);
		this.nameGuiSleep  = this.getName(MicdoodleTransformer.KEY_CLASS_GUI_SLEEP);
		this.nameEffectRenderer  = this.getName(MicdoodleTransformer.KEY_CLASS_EFFECT_RENDERER);
		this.nameNetHandlerPlay  = this.getName(MicdoodleTransformer.KEY_CLASS_NET_HANDLER_PLAY);
		this.nameVertexBuffer  = this.getName(MicdoodleTransformer.KEY_CLASS_WORLD_RENDERER);
		this.nameRenderGlobal  = this.getName(MicdoodleTransformer.KEY_CLASS_RENDER_GLOBAL);
		this.nameRenderManager  = this.getName(MicdoodleTransformer.KEY_CLASS_RENDER_MANAGER);
		this.nameTileEntityRenderer  = this.getName(MicdoodleTransformer.KEY_CLASS_TILEENTITY_RENDERER);
		this.nameEntity  = this.getName(MicdoodleTransformer.KEY_CLASS_ENTITY);
		this.nameChunkProviderServer  = this.getName(MicdoodleTransformer.KEY_CLASS_CHUNK_PROVIDER_SERVER);
		this.nameEntityArrow  = this.getName(MicdoodleTransformer.KEY_CLASS_ENTITY_ARROW);
		this.nameEntityGolem  = this.getName(MicdoodleTransformer.KEY_CLASS_ENTITYGOLEM);
		this.nameWorld  = this.getName(MicdoodleTransformer.KEY_CLASS_WORLD);
	}

	private void populateNamesObf()
	{
		this.nameConfManager  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_CONF_MANAGER).obfuscatedName;
		this.namePlayerController  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_PLAYER_CONTROLLER).obfuscatedName;
		this.nameEntityLiving  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_ENTITY_LIVING).obfuscatedName;
		this.nameEntityItem  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_ENTITY_ITEM).obfuscatedName;
		this.nameEntityRenderer  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_ENTITY_RENDERER).obfuscatedName;
		this.nameItemRenderer  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_ITEM_RENDERER).obfuscatedName;
		this.nameGuiSleep  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_GUI_SLEEP).obfuscatedName;
		this.nameEffectRenderer  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_EFFECT_RENDERER).obfuscatedName;
		this.nameNetHandlerPlay  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_NET_HANDLER_PLAY).obfuscatedName;
		this.nameVertexBuffer  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_WORLD_RENDERER).obfuscatedName;
		this.nameRenderGlobal  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_RENDER_GLOBAL).obfuscatedName;
		this.nameRenderManager  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_RENDER_MANAGER).obfuscatedName;
		this.nameTileEntityRenderer  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_TILEENTITY_RENDERER).obfuscatedName;
		this.nameEntity  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_ENTITY).obfuscatedName;
		this.nameChunkProviderServer  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_CHUNK_PROVIDER_SERVER).obfuscatedName;
		this.nameEntityArrow  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_ENTITY_ARROW).obfuscatedName;
		this.nameEntityGolem  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_ENTITYGOLEM).obfuscatedName;
		this.nameWorld  = this.nodemap.get(MicdoodleTransformer.KEY_CLASS_WORLD).obfuscatedName;
	}
	
	private byte[] transformVanilla(String testName, byte[] bytes)
	{
		if (testName.equals(this.nameConfManager))
		{
			return this.transformConfigManager(bytes);
		}
		else if (testName.equals(this.namePlayerController))
		{
			return this.transformPlayerController(bytes);
		}
		else if (testName.equals(this.nameEntityLiving))
		{
			return this.transformEntityLiving(bytes);
		}
		else if (testName.equals(this.nameEntityItem))
		{
			return this.transformEntityItem(bytes);
		}
		else if (testName.equals(this.nameEntityRenderer))
		{
			return this.transformEntityRenderer(bytes);
		}
		else if (testName.equals(this.nameItemRenderer))
		{
			return this.transformItemRenderer(bytes);
		}
		else if (testName.equals(this.nameGuiSleep))
		{
			return this.transformGuiSleep(bytes);
		}
		else if (testName.equals(this.nameEffectRenderer))
		{
			return this.transformEffectRenderer(bytes);
		}
		else if (testName.equals(this.nameNetHandlerPlay))
		{
			return this.transformNetHandlerPlay(bytes);
		}
		else if (testName.equals(this.nameRenderGlobal))
		{
			return this.transformRenderGlobal(bytes);
		}
		else if (testName.equals(this.nameEntity))
		{
			return this.transformEntityClass(bytes);
		}
		else if (testName.equals(this.nameChunkProviderServer))
		{
			return this.transformChunkProviderServerClass(bytes);
		}
		else if (testName.equals(this.nameEntityArrow))
		{
			return this.transformEntityArrow(bytes);
		}
		else if (testName.equals(this.nameEntityGolem))
		{
			return this.transformEntityGolem(bytes);			
		}
		else if (testName.equals(this.nameWorld))
		{
			return this.transformWorld(bytes);
		}
		
		return bytes;
	}

	/**
	 * replaces EntityPlayerMP initialization with custom ones
	 */
	public byte[] transformChunkProviderServerClass(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 1;

		MethodNode populateMethod = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_CGS_POPULATE);

		if (populateMethod != null)
		{
			LabelNode skipLabel = new LabelNode();
			for (int count = 0; count < populateMethod.instructions.size(); count++)
			{
				final AbstractInsnNode list = populateMethod.instructions.get(count);
				
				if (list instanceof MethodInsnNode)
				{
					final MethodInsnNode nodeAt = (MethodInsnNode) list;

					if (nodeAt.getOpcode() == Opcodes.INVOKEINTERFACE && nodeAt.desc.equals(populateMethod.desc))
					{
						final InsnList nodesToAdd = new InsnList();

						//(p_73153_2_, p_73153_3_, worldObj, serverChunkGenerator, p_73153_1_)
						nodesToAdd.add(new VarInsnNode(Opcodes.ILOAD, 2));
						nodesToAdd.add(new VarInsnNode(Opcodes.ILOAD, 3));
						nodesToAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
						nodesToAdd.add(new FieldInsnNode(Opcodes.GETFIELD, this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_CHUNK_PROVIDER_SERVER), this.getNameDynamic(MicdoodleTransformer.KEY_FIELD_CPS_WORLDOBJ), "L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD_SERVER) + ";"));
						nodesToAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
						nodesToAdd.add(new FieldInsnNode(Opcodes.GETFIELD, this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_CHUNK_PROVIDER_SERVER), this.getNameDynamic(MicdoodleTransformer.KEY_FIELD_CPS_SERVER_CHUNK_GEN), "L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ICHUNKPROVIDER) + ";"));
						nodesToAdd.add(new VarInsnNode(Opcodes.ALOAD, 1));
						nodesToAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "otherModPreventGenerate", "(IIL" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ICHUNKPROVIDER) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ICHUNKPROVIDER) + ";)Z", false));
						nodesToAdd.add(new JumpInsnNode(Opcodes.IFNE, skipLabel)); 
						populateMethod.instructions.insert(nodeAt, nodesToAdd);
						MicdoodleTransformer.injectionCount++;
					} else
						if (nodeAt.getOpcode() == Opcodes.INVOKESTATIC && nodeAt.owner.contains("GameRegistry"))
						{
							populateMethod.instructions.insert(nodeAt, skipLabel);
						}
				
				}
			}
		}
		return this.finishInjection(node);
	}
	
	/**
	 * replaces EntityPlayerMP initialization with custom ones
	 */
	public byte[] transformConfigManager(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

        boolean playerAPI = this.isPlayerApiActive();
        MethodNode attemptLoginMethod = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_ATTEMPT_LOGIN_BUKKIT);
		MicdoodleTransformer.operationCount = playerAPI ? 0 : (attemptLoginMethod == null ? 4 : 6);

        if (!playerAPI)
        {
            MethodNode createPlayerMethod = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_CREATE_PLAYER);
            MethodNode respawnPlayerMethod = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_RESPAWN_PLAYER);

            if (createPlayerMethod != null)
            {
                for (int count = 0; count < createPlayerMethod.instructions.size(); count++)
                {
                    final AbstractInsnNode list = createPlayerMethod.instructions.get(count);

                    if (list instanceof TypeInsnNode)
                    {
                        final TypeInsnNode nodeAt = (TypeInsnNode) list;

                        if (nodeAt.getOpcode() != Opcodes.CHECKCAST && nodeAt.desc.contains(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_MP)))
                        {
                            final TypeInsnNode overwriteNode = new TypeInsnNode(Opcodes.NEW, this.getName(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_MP));

                            createPlayerMethod.instructions.set(nodeAt, overwriteNode);
                            MicdoodleTransformer.injectionCount++;
                        }
                    }
                    else if (list instanceof MethodInsnNode)
                    {
                        final MethodInsnNode nodeAt = (MethodInsnNode) list;

                        if (nodeAt.owner.contains(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_MP)) && nodeAt.getOpcode() == Opcodes.INVOKESPECIAL)
                        {
                            createPlayerMethod.instructions.set(nodeAt, new MethodInsnNode(Opcodes.INVOKESPECIAL, this.getName(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_MP), this.getName(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_MP), this.getDescDynamic(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_MP), false));
                            MicdoodleTransformer.injectionCount++;
                        }
                    }
                }
            }

            if (respawnPlayerMethod != null)
            {
                for (int count = 0; count < respawnPlayerMethod.instructions.size(); count++)
                {
                    final AbstractInsnNode list = respawnPlayerMethod.instructions.get(count);

                    if (list instanceof TypeInsnNode)
                    {
                        final TypeInsnNode nodeAt = (TypeInsnNode) list;

                        if (nodeAt.getOpcode() != Opcodes.CHECKCAST && nodeAt.desc.contains(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_MP)))
                        {
                            final TypeInsnNode overwriteNode = new TypeInsnNode(Opcodes.NEW, this.getName(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_MP));

                            respawnPlayerMethod.instructions.set(nodeAt, overwriteNode);
                            MicdoodleTransformer.injectionCount++;
                        }
                    }
                    else if (list instanceof MethodInsnNode)
                    {
                        final MethodInsnNode nodeAt = (MethodInsnNode) list;

                        if (nodeAt.name.equals("<init>") && nodeAt.owner.equals(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_MP)))
                        {
                            respawnPlayerMethod.instructions.set(nodeAt, new MethodInsnNode(Opcodes.INVOKESPECIAL, this.getName(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_MP), this.getName(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_MP), this.getDescDynamic(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_MP), false));

                            MicdoodleTransformer.injectionCount++;
                        }
                    }
                }
            }

            if (attemptLoginMethod != null)
            {
                for (int count = 0; count < attemptLoginMethod.instructions.size(); count++)
                {
                    final AbstractInsnNode list = attemptLoginMethod.instructions.get(count);

                    if (list instanceof TypeInsnNode)
                    {
                        final TypeInsnNode nodeAt = (TypeInsnNode) list;

                        if (nodeAt.getOpcode() == Opcodes.NEW && nodeAt.desc.contains(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_MP)))
                        {
                            final TypeInsnNode overwriteNode = new TypeInsnNode(Opcodes.NEW, this.getName(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_MP));

                            attemptLoginMethod.instructions.set(nodeAt, overwriteNode);
                            MicdoodleTransformer.injectionCount++;
                        }
                    }
                    else if (list instanceof MethodInsnNode)
                    {
                        final MethodInsnNode nodeAt = (MethodInsnNode) list;

                        if (nodeAt.getOpcode() == Opcodes.INVOKESPECIAL && nodeAt.name.equals("<init>") && nodeAt.owner.equals(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_MP)))
                        {
                            String initDesc = "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_SERVER) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD_SERVER) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_GAME_PROFILE) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ITEM_IN_WORLD_MANAGER) + ";)V";
                            attemptLoginMethod.instructions.set(nodeAt, new MethodInsnNode(Opcodes.INVOKESPECIAL, this.getName(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_MP), this.getName(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_MP), initDesc, false));

                            MicdoodleTransformer.injectionCount++;
                        }
                    }
                }
            }
        }

		return this.finishInjection(node);
	}

	/**
	 * replaces EntityPlayerMP initialization with custom one in Sync Mod
	 */
	public byte[] transformSyncMod(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

        boolean playerAPI = this.isPlayerApiActive();
		MicdoodleTransformer.operationCount = playerAPI ? 0 : 2;

        if (!playerAPI)
        {
            MethodNode respawnPlayerMethod = this.getMethodNoDesc(node, "func_71004_bE");

            if (respawnPlayerMethod != null)
            {
                for (int count = 0; count < respawnPlayerMethod.instructions.size(); count++)
                {
                    final AbstractInsnNode list = respawnPlayerMethod.instructions.get(count);

                    if (list instanceof TypeInsnNode)
                    {
                        final TypeInsnNode nodeAt = (TypeInsnNode) list;

                        //Deobfuscated name for EntityPlayerMP, because this is in a mod
                        if (nodeAt.getOpcode() == Opcodes.NEW && nodeAt.desc.contains(this.getName(MicdoodleTransformer.KEY_CLASS_PLAYER_MP)))
                        {
                            final TypeInsnNode overwriteNode = new TypeInsnNode(Opcodes.NEW, this.getName(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_MP));

                            respawnPlayerMethod.instructions.set(nodeAt, overwriteNode);
                            MicdoodleTransformer.injectionCount++;
                        }
                    }
                    else if (list instanceof MethodInsnNode)
                    {
                        final MethodInsnNode nodeAt = (MethodInsnNode) list;

                        //Deobfuscated name for EntityPlayerMP, because this is in a mod
                        if (nodeAt.name.equals("<init>") && nodeAt.owner.equals(this.getName(MicdoodleTransformer.KEY_CLASS_PLAYER_MP)))
                        {
                            respawnPlayerMethod.instructions.set(nodeAt, new MethodInsnNode(Opcodes.INVOKESPECIAL, this.getName(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_MP), this.getName(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_MP), this.getDescDynamic(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_MP)));

                            MicdoodleTransformer.injectionCount++;
                        }
                    }
                }
            }
        }

		return this.finishInjection(node);
	}

	public byte[] transformPlayerController(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

        boolean playerAPI = this.isPlayerApiActive();
        MicdoodleTransformer.operationCount = playerAPI ? 0 : 2;

        if (!playerAPI)
        {
            MethodNode method = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_CREATE_CLIENT_PLAYER);

            if (method != null)
            {
                for (int count = 0; count < method.instructions.size(); count++)
                {
                    final AbstractInsnNode list = method.instructions.get(count);

                    if (list instanceof TypeInsnNode)
                    {
                        final TypeInsnNode nodeAt = (TypeInsnNode) list;

                        if (nodeAt.desc.contains(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_SP)))
                        {
                            System.err.println(nodeAt.getOpcode() + " " + nodeAt.desc);
                            System.err.println(Opcodes.NEW + " " + this.getName(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_SP));
                            final TypeInsnNode overwriteNode = new TypeInsnNode(Opcodes.NEW, this.getName(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_SP));

                            method.instructions.set(nodeAt, overwriteNode);
                            MicdoodleTransformer.injectionCount++;
                        }
                    }
                    else if (list instanceof MethodInsnNode)
                    {
                        final MethodInsnNode nodeAt = (MethodInsnNode) list;

                        if (nodeAt.name.equals("<init>") && nodeAt.owner.equals(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_PLAYER_SP)))
                        {
                            System.err.println(nodeAt.desc);
                            System.err.println(this.getDescDynamic(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_SP));
                            method.instructions.set(nodeAt, new MethodInsnNode(Opcodes.INVOKESPECIAL, this.getName(MicdoodleTransformer.KEY_CLASS_CUSTOM_PLAYER_SP), this.getName(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_SP), this.getDescDynamic(MicdoodleTransformer.KEY_METHOD_CUSTOM_PLAYER_SP)));
                            MicdoodleTransformer.injectionCount++;
                        }
                    }
                }
            }
        }

		return this.finishInjection(node);
	}

	public byte[] transformEntityLiving(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 1;

		MethodNode method = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_MOVE_ENTITY);

		if (method != null)
		{
			for (int count = 0; count < method.instructions.size(); count++)
			{
				final AbstractInsnNode list = method.instructions.get(count);

				if (list instanceof LdcInsnNode)
				{
					final LdcInsnNode nodeAt = (LdcInsnNode) list;

					if (nodeAt.cst.equals(0.08D))
					{
						final VarInsnNode beforeNode = new VarInsnNode(Opcodes.ALOAD, 0);
						final MethodInsnNode overwriteNode = new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "getGravityForEntity", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY) + ";)D");

						method.instructions.insertBefore(nodeAt, beforeNode);
						method.instructions.set(nodeAt, overwriteNode);
						MicdoodleTransformer.injectionCount++;
					}
				}
			}
		}

		return this.finishInjection(node);
	}

	public byte[] transformEntityItem(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 1;

		MethodNode method = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_ON_UPDATE);

		if (method != null)
		{
			for (int count = 0; count < method.instructions.size(); count++)
			{
				final AbstractInsnNode list = method.instructions.get(count);

				if (list instanceof LdcInsnNode)
				{
					final LdcInsnNode nodeAt = (LdcInsnNode) list;

					if (nodeAt.cst.equals(0.03999999910593033D))
					{
						final VarInsnNode beforeNode = new VarInsnNode(Opcodes.ALOAD, 0);
						final MethodInsnNode overwriteNode = new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "getItemGravity", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY_ITEM) + ";)D");

						method.instructions.insertBefore(nodeAt, beforeNode);
						method.instructions.set(nodeAt, overwriteNode);
						MicdoodleTransformer.injectionCount++;
					}
				}
			}
		}

		return this.finishInjection(node);
	}

	public byte[] transformEntityRenderer(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 5;

		MethodNode updateLightMapMethod = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_UPDATE_LIGHTMAP);
		MethodNode updateFogColorMethod = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_UPDATE_FOG_COLOR);
		MethodNode orientCameraMethod = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_ORIENT_CAMERA);

		if (orientCameraMethod != null)
		{
			final InsnList nodesToAdd = new InsnList();

			nodesToAdd.add(new VarInsnNode(Opcodes.FLOAD, 1));
			nodesToAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "orientCamera", "(F)V"));
			orientCameraMethod.instructions.insertBefore(orientCameraMethod.instructions.get(orientCameraMethod.instructions.size() - 3), nodesToAdd);
			MicdoodleTransformer.injectionCount++;
			if (ConfigManagerMicCore.enableDebug) System.out.println("bll.OrientCamera done");
		}

		if (updateLightMapMethod != null)
		{
			boolean worldBrightnessInjection = false;

			for (int count = 0; count < updateLightMapMethod.instructions.size(); count++)
			{
				final AbstractInsnNode list = updateLightMapMethod.instructions.get(count);

				if (list instanceof MethodInsnNode)
				{
					MethodInsnNode nodeAt = (MethodInsnNode) list;

					//Original code:  float f1 = worldclient.getSunBrightness(1.0F) * 0.95F + 0.05F;
					if (!worldBrightnessInjection && nodeAt.owner.equals(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD)))
					{
						updateLightMapMethod.instructions.remove(updateLightMapMethod.instructions.get(count - 1));
						updateLightMapMethod.instructions.remove(updateLightMapMethod.instructions.get(count - 1));
						updateLightMapMethod.instructions.insertBefore(updateLightMapMethod.instructions.get(count - 1), new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "getWorldBrightness", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD_CLIENT) + ";)F"));
						MicdoodleTransformer.injectionCount++;
						worldBrightnessInjection = true;
						if (ConfigManagerMicCore.enableDebug) System.out.println("bll.updateLightMap - worldBrightness done");
						continue;
					}
				}

				if (list instanceof IntInsnNode)
				{
					final IntInsnNode nodeAt = (IntInsnNode) list;

					if (nodeAt.operand == 255)
					{
						final InsnList nodesToAdd = new InsnList();

						nodesToAdd.add(new VarInsnNode(Opcodes.FLOAD, 12));
						nodesToAdd.add(new VarInsnNode(Opcodes.ALOAD, 2));
						nodesToAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "getColorRed", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD) + ";)F"));
						nodesToAdd.add(new InsnNode(Opcodes.FMUL));
						nodesToAdd.add(new VarInsnNode(Opcodes.FSTORE, 12));

						nodesToAdd.add(new VarInsnNode(Opcodes.FLOAD, 13));
						nodesToAdd.add(new VarInsnNode(Opcodes.ALOAD, 2));
						nodesToAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "getColorGreen", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD) + ";)F"));
						nodesToAdd.add(new InsnNode(Opcodes.FMUL));
						nodesToAdd.add(new VarInsnNode(Opcodes.FSTORE, 13));

						nodesToAdd.add(new VarInsnNode(Opcodes.FLOAD, 14));
						nodesToAdd.add(new VarInsnNode(Opcodes.ALOAD, 2));
						nodesToAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "getColorBlue", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD) + ";)F"));
						nodesToAdd.add(new InsnNode(Opcodes.FMUL));
						nodesToAdd.add(new VarInsnNode(Opcodes.FSTORE, 14));

						updateLightMapMethod.instructions.insertBefore(nodeAt, nodesToAdd);
						MicdoodleTransformer.injectionCount++;
						if (ConfigManagerMicCore.enableDebug) System.out.println("bll.updateLightMap - getColors done");
						break;
					}
				}
			}
		}

		if (updateFogColorMethod != null)
		{
			for (int count = 0; count < updateFogColorMethod.instructions.size(); count++)
			{
				final AbstractInsnNode list = updateFogColorMethod.instructions.get(count);

				if (list instanceof MethodInsnNode)
				{
					final MethodInsnNode nodeAt = (MethodInsnNode) list;

					if (!this.optifinePresent && this.methodMatches(MicdoodleTransformer.KEY_METHOD_GET_FOG_COLOR, nodeAt))
					{
						InsnList toAdd = new InsnList();

						toAdd.add(new VarInsnNode(Opcodes.ALOAD, 2));
						toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "getFogColorHook", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD) + ";)L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_VEC3) + ";"));
						toAdd.add(new VarInsnNode(Opcodes.ASTORE, 9));

						updateFogColorMethod.instructions.insertBefore(updateFogColorMethod.instructions.get(count + 2), toAdd);
						MicdoodleTransformer.injectionCount++;
						if (ConfigManagerMicCore.enableDebug) System.out.println("bll.updateFogColor - getFogColor (no Optifine) done");
					}
					else if (this.methodMatches(MicdoodleTransformer.KEY_METHOD_GET_SKY_COLOR, nodeAt))
					{
						InsnList toAdd = new InsnList();

						toAdd.add(new VarInsnNode(Opcodes.ALOAD, 2));
						toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "getSkyColorHook", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD) + ";)L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_VEC3) + ";"));
						toAdd.add(new VarInsnNode(Opcodes.ASTORE, 5));

						updateFogColorMethod.instructions.insertBefore(updateFogColorMethod.instructions.get(count + 2), toAdd);
						MicdoodleTransformer.injectionCount++;
						if (ConfigManagerMicCore.enableDebug) System.out.println("bll.updateFogColor - getSkyColor done");
					}
				}
			}
		}

		return this.finishInjection(node);
	}

	public byte[] transformGuiSleep(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 1;

		MethodNode method = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_WAKE_ENTITY);

		if (method != null)
		{
			method.instructions.insertBefore(method.instructions.get(method.instructions.size() - 3), new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_MICDOODLE_PLUGIN, "onSleepCancelled", "()V"));
			MicdoodleTransformer.injectionCount++;
		}

		return this.finishInjection(node);
	}

	public byte[] transformForgeHooks(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 1;

		MethodNode method = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_BED_ORIENT_CAMERA);

		if (method != null)
		{
			method.instructions.insertBefore(method.instructions.get(0), new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_MICDOODLE_PLUGIN, "orientCamera", "()V"));
			MicdoodleTransformer.injectionCount++;
		}

		return this.finishInjection(node);
	}

	public byte[] transformEntityGolem(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);
		MicdoodleTransformer.operationCount = 0;
		MicdoodleTransformer.injectionCount = 0;
		String inter = CLASS_IENTITYBREATHABLE;
		try
		{
			Class.forName(inter.replace("/", "."));
			if (!node.interfaces.contains(inter))
			{
				node.interfaces.add(inter);
				MicdoodleTransformer.injectionCount++;
			}
			MethodNode canBreathe = new MethodNode(Opcodes.ACC_PUBLIC, "canBreath", "()Z", null, null);
			canBreathe.instructions.add(new InsnNode(Opcodes.ICONST_1));
			canBreathe.instructions.add(new InsnNode(Opcodes.IRETURN));
			node.methods.add(canBreathe);
		}
		catch (Exception e) { }

		return this.finishInjection(node);
	}
	
	@SuppressWarnings("unchecked")
	public byte[] transformCustomAnnotations(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 0;
		MicdoodleTransformer.injectionCount = 0;

		final Iterator<MethodNode> methods = node.methods.iterator();
		List<String> ignoredMods = new ArrayList<String>();

		while (methods.hasNext())
		{
			MethodNode methodnode = methods.next();

			methodLabel:
			if (methodnode.visibleAnnotations != null && methodnode.visibleAnnotations.size() > 0)
			{
				for (AnnotationNode annotation : methodnode.visibleAnnotations)
				{
                    if (annotation.desc.equals("L" + MicdoodleTransformer.CLASS_VERSION_SPECIFIC + ";"))
                    {
                        String toMatch = null;

                        for (int i = 0; i < annotation.values.size(); i+=2)
                        {
                            if ("version".equals(annotation.values.get(i)))
                            {
                                toMatch = String.valueOf(annotation.values.get(i + 1));
                            }
                        }

                        if (toMatch != null)
                        {
                            boolean doRemove = true;
                            if (mcVersionMatches(toMatch))
                            {
                            	doRemove = false;
                            }

                            if (doRemove)
                            {
	                            methods.remove();
	                            break methodLabel;
                            }
                        }
                    }

                    if (annotation.desc.equals("L" + MicdoodleTransformer.CLASS_ALT_FORVERSION + ";"))
                    {
                        String toMatch = null;

                        for (int i = 0; i < annotation.values.size(); i+=2)
                        {
                            if ("version".equals(annotation.values.get(i)))
                            {
                                toMatch = String.valueOf(annotation.values.get(i + 1));
                            }
                        }

                        if (toMatch != null)
                        {
                            if (mcVersionMatches(toMatch))
                            {
                            	String existing = new String(methodnode.name);
                            	existing = existing.substring(0, existing.length() - 1);
                            	if (ConfigManagerMicCore.enableDebug) this.printLog("Renaming method "+existing+" for version "+toMatch);
                            	methodnode.name = new String(existing);
                            	break;
                            }
                        }
                    }

					if (annotation.desc.equals("L" + MicdoodleTransformer.CLASS_RUNTIME_INTERFACE + ";"))
					{
						List<String> desiredInterfaces = new ArrayList<String>();
						String modID = "";

						for (int i = 0; i < annotation.values.size(); i+=2)
						{
							Object value = annotation.values.get(i);

							if (value.equals("clazz"))
							{
								desiredInterfaces.add(String.valueOf(annotation.values.get(i + 1)));
							}
							else if (value.equals("modID"))
							{
								modID = String.valueOf(annotation.values.get(i + 1));
							}
							else if (value.equals("altClasses"))
							{
								desiredInterfaces.addAll((ArrayList<String>) annotation.values.get(i + 1));
							}
						}

						if (modID.isEmpty() || !ignoredMods.contains(modID))
						{
							boolean modFound = modID.isEmpty() || Loader.isModLoaded(modID);

							if (modFound)
							{
								for (String inter : desiredInterfaces)
								{
									try
									{
										Class.forName(inter);
									}
									catch (ClassNotFoundException e)
									{
										if (ConfigManagerMicCore.enableDebug) this.printLog("Galacticraft ignored missing interface \"" + inter + "\" from mod \"" + modID + "\".");
										continue;
									}

									inter = inter.replace(".", "/");

									if (!node.interfaces.contains(inter))
									{
										if (ConfigManagerMicCore.enableDebug) this.printLog("Galacticraft added interface \"" + inter + "\" dynamically from \"" + modID + "\" to class \"" + node.name + "\".");
										node.interfaces.add(inter);
										MicdoodleTransformer.injectionCount++;
									}

									break;
								}
							}
							else
							{
								ignoredMods.add(modID);
								if (ConfigManagerMicCore.enableDebug) this.printLog("Galacticraft ignored dynamic interface insertion since \"" + modID + "\" was not found.");
							}
						}

						break methodLabel;
					}
				}
			}
		}

		if (MicdoodleTransformer.injectionCount > 0)
		{
			if (ConfigManagerMicCore.enableDebug) this.printLog("Galacticraft successfully injected bytecode into: " + node.name + " (" + MicdoodleTransformer.injectionCount + ")");
		}

		return this.finishInjection(node, false);
	}

	public byte[] transformEffectRenderer(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 1;

		MethodNode renderParticlesMethod = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_RENDER_PARTICLES);

		if (renderParticlesMethod != null)
		{
			InsnList toAdd = new InsnList();
			toAdd.add(new VarInsnNode(Opcodes.FLOAD, 2));
			toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "renderFootprints", "(F)V", false));
			renderParticlesMethod.instructions.insert(renderParticlesMethod.instructions.get(0), toAdd);
			MicdoodleTransformer.injectionCount++;
		}

		return this.finishInjection(node);
	}

	public byte[] transformItemRenderer(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 1;

		MethodNode renderOverlaysMethod = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_RENDER_OVERLAYS);

		if (renderOverlaysMethod != null)
		{
			for (int count = 0; count < renderOverlaysMethod.instructions.size(); count++)
			{
				final AbstractInsnNode glEnable = renderOverlaysMethod.instructions.get(count);

				if (glEnable instanceof MethodInsnNode && ((MethodInsnNode) glEnable).name.equals(getNameDynamic(MicdoodleTransformer.KEY_METHOD_ENABLE_ALPHA)))
				{
					InsnList toAdd = new InsnList();

					toAdd.add(new VarInsnNode(Opcodes.FLOAD, 1));
					toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "renderLiquidOverlays", "(F)V", false));

					renderOverlaysMethod.instructions.insertBefore(glEnable, toAdd);
					MicdoodleTransformer.injectionCount++;
					break;
				}
			}
		}

		return this.finishInjection(node);
	}

	public byte[] transformNetHandlerPlay(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 2;

		MethodNode handleNamedSpawnMethod = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_HANDLE_SPAWN_PLAYER);

		if (handleNamedSpawnMethod != null)
		{
			for (int count = 0; count < handleNamedSpawnMethod.instructions.size(); count++)
			{
				final AbstractInsnNode list = handleNamedSpawnMethod.instructions.get(count);

				if (list instanceof TypeInsnNode)
				{
					final TypeInsnNode nodeAt = (TypeInsnNode) list;

					if (nodeAt.desc.contains(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY_OTHER_PLAYER)))
					{
						final TypeInsnNode overwriteNode = new TypeInsnNode(Opcodes.NEW, this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_CUSTOM_OTHER_PLAYER));

						handleNamedSpawnMethod.instructions.set(nodeAt, overwriteNode);
						MicdoodleTransformer.injectionCount++;
					}
				}
				else if (list instanceof MethodInsnNode)
				{
					final MethodInsnNode nodeAt = (MethodInsnNode) list;

					if (nodeAt.name.equals("<init>") && nodeAt.owner.equals(this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY_OTHER_PLAYER)))
					{
						handleNamedSpawnMethod.instructions.set(nodeAt, new MethodInsnNode(Opcodes.INVOKESPECIAL, this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_CUSTOM_OTHER_PLAYER), "<init>", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD) + ";L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_GAME_PROFILE) + ";)V"));
						MicdoodleTransformer.injectionCount++;
					}
				}
			}
		}

		return this.finishInjection(node);
	}

	public byte[] transformRenderGlobal(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 1;

		MethodNode setupTerrainMethod = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_SETUP_TERRAIN);

		if (setupTerrainMethod != null)
		{
			for (int count = 0; count < setupTerrainMethod.instructions.size(); count++)
			{
				final AbstractInsnNode nodeTest = setupTerrainMethod.instructions.get(count);

				if (nodeTest instanceof MethodInsnNode && ((MethodInsnNode) nodeTest).name.equals(this.getNameDynamic(MicdoodleTransformer.KEY_METHOD_GET_EYE_HEIGHT)))
				{
					/*
					 * The following (hacky) bytecode insertion will always let entities render above y=256
					 * This is necessary for rockets since the 1.8 update changed directional frustum culling
					 * See WorldUtil.getRenderPosY
					 */
					InsnList list = new InsnList();
					list.add(new VarInsnNode(Opcodes.ALOAD, 1));
					list.add(new VarInsnNode(Opcodes.DLOAD, 15));
					list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "getRenderPosY", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY) + ";D)D", false));
					setupTerrainMethod.instructions.remove(setupTerrainMethod.instructions.get(count - 2));
					setupTerrainMethod.instructions.remove(setupTerrainMethod.instructions.get(count - 2));
					setupTerrainMethod.instructions.remove(setupTerrainMethod.instructions.get(count - 2));
					setupTerrainMethod.instructions.remove(setupTerrainMethod.instructions.get(count - 2));
					setupTerrainMethod.instructions.remove(setupTerrainMethod.instructions.get(count - 2));
					setupTerrainMethod.instructions.insertBefore(setupTerrainMethod.instructions.get(count - 2), list);
					MicdoodleTransformer.injectionCount++;
					break;
				}
			}
		}

		return this.finishInjection(node);
	}

    public byte[] transformEntityClass(byte[] bytes)
    {
        if (isServer) return bytes;
        
    	ClassNode node = this.startInjection(bytes);

        MicdoodleTransformer.operationCount = 1;

        MethodNode method = this.getMethod(node, KEY_METHOD_CAN_RENDER_FIRE);

        if (method != null)
        {
            for (int i = 0; i < method.instructions.size(); i++)
            {
                AbstractInsnNode nodeAt = method.instructions.get(i);

                if (nodeAt instanceof MethodInsnNode && nodeAt.getOpcode() == Opcodes.INVOKEVIRTUAL)
                {
                    MethodInsnNode overwriteNode = new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "shouldRenderFire", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY) + ";)Z", false);

                    method.instructions.set(nodeAt, overwriteNode);
                    MicdoodleTransformer.injectionCount++;
                }
            }
        }

        return this.finishInjection(node);
    }

	public byte[] transformEntityArrow(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 1;

		MethodNode method = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_ON_UPDATE);

		if (method != null)
		{
			for (int count = 0; count < method.instructions.size(); count++)
			{
				final AbstractInsnNode list = method.instructions.get(count);

				if (list instanceof LdcInsnNode)
				{
					final LdcInsnNode nodeAt = (LdcInsnNode) list;

					if (nodeAt.cst.equals(0.05F))
					{
						final VarInsnNode beforeNode = new VarInsnNode(Opcodes.ALOAD, 0);
						final MethodInsnNode overwriteNode = new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "getArrowGravity", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_ENTITY_ARROW) + ";)F", false);

						method.instructions.insertBefore(nodeAt, beforeNode);
						method.instructions.set(nodeAt, overwriteNode);
						MicdoodleTransformer.injectionCount++;
					}
				}
			}
		}

		return this.finishInjection(node);
	}

	public byte[] transformWorld(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 1;

		MethodNode method = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_RAIN_STRENGTH);

		if (method != null)
		{
			for (int count = 0; count < method.instructions.size(); ++count)
			{
				final AbstractInsnNode list = method.instructions.get(count);

				if (list.getOpcode() == Opcodes.ALOAD)
				{
					// Remove ALOAD, GETFIELD, ALOAD, GETFIELD, ALOAD, GETFIELD, FSUB, FLOAD, FMUL, FADD, FRETURN
					for (int i = 0; i < 6; ++i)
					{
						method.instructions.remove(method.instructions.get(count + i));
					}

					InsnList toAdd = new InsnList();
					toAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
					toAdd.add(new VarInsnNode(Opcodes.FLOAD, 1));
					toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MicdoodleTransformer.CLASS_TRANSFORMER_HOOKS, "getRainStrength", "(L" + this.getNameDynamic(MicdoodleTransformer.KEY_CLASS_WORLD) + ";F)F"));
					toAdd.add(new InsnNode(Opcodes.FRETURN));
					method.instructions.insertBefore(method.instructions.get(count), toAdd);
					MicdoodleTransformer.injectionCount++;
					break;
				}
			}
		}

		return this.finishInjection(node);
	}
	
	
	public byte[] transformOptifine(byte[] bytes)
	{
		ClassNode node = this.startInjection(bytes);

		MicdoodleTransformer.operationCount = 1;

		MethodNode method = this.getMethod(node, MicdoodleTransformer.KEY_METHOD_REGISTEROF);

		if (method != null)
		{
			AbstractInsnNode toAdd = new InsnNode(Opcodes.RETURN);
			method.instructions.insertBefore(method.instructions.get(0), toAdd);
			MicdoodleTransformer.injectionCount++;
		}

		return this.finishInjection(node);
	}
	
    public static class ObfuscationEntry
	{
		public String name;
		public String obfuscatedName;

		public ObfuscationEntry(String name, String obfuscatedName)
		{
			this.name = name;
			this.obfuscatedName = obfuscatedName;
		}

		public ObfuscationEntry(String commonName)
		{
			this(commonName, commonName);
		}
	}

	public static class MethodObfuscationEntry extends ObfuscationEntry
	{
		public String methodDesc;

		public MethodObfuscationEntry(String name, String obfuscatedName, String methodDesc)
		{
			super(name, obfuscatedName);
			this.methodDesc = methodDesc;
		}

		public MethodObfuscationEntry(String commonName, String methodDesc)
		{
			this(commonName, commonName, methodDesc);
		}
	}

    public static class FieldObfuscationEntry extends ObfuscationEntry
	{
		public FieldObfuscationEntry(String name, String obfuscatedName)
		{
			super(name, obfuscatedName);
		}
	}

	private void printResultsAndReset(String nodeName)
	{
		if (MicdoodleTransformer.operationCount > 0)
		{
			if (MicdoodleTransformer.injectionCount >= MicdoodleTransformer.operationCount)
			{
				this.printLog("Galacticraft successfully injected bytecode into: " + nodeName + " (" + MicdoodleTransformer.injectionCount + " / " + MicdoodleTransformer.operationCount + ")");
			}
			else
			{
				System.err.println("Potential problem: Galacticraft did not complete injection of bytecode into: " + nodeName + " (" + MicdoodleTransformer.injectionCount + " / " + MicdoodleTransformer.operationCount + ")");
			}
		}
	}

	private MethodNode getMethod(ClassNode node, String keyName)
	{
		for (MethodNode methodNode : node.methods)
		{
			if (this.methodMatches(keyName, methodNode))
			{
				return methodNode;
			}
		}

		return null;
	}

	private MethodNode getMethodNoDesc(ClassNode node, String methodName)
	{
		for (MethodNode methodNode : node.methods)
		{
			if (methodNode.name.equals(methodName))
			{
				return methodNode;
			}
		}

		return null;
	}

	private boolean methodMatches(String keyName, MethodInsnNode node)
	{
		return node.name.equals(this.getNameDynamic(keyName)) && node.desc.equals(this.getDescDynamic(keyName));
	}

	private boolean methodMatches(String keyName, MethodNode node)
	{
		return node.name.equals(this.getNameDynamic(keyName)) && node.desc.equals(this.getDescDynamic(keyName));
	}

	public String getName(String keyName)
	{
		return this.nodemap.get(keyName).name;
	}

	public String getObfName(String keyName)
	{
		return this.nodemap.get(keyName).obfuscatedName;
	}

	public String getNameDynamic(String keyName)
	{
		try
		{
			if (this.deobfuscated)
			{
				return this.getName(keyName);
			}
			else
			{
				return this.getObfName(keyName);
			}
		}
		catch (NullPointerException e)
		{
			System.err.println("Could not find key: " + keyName);
			throw e;
		}
	}

	public String getDescDynamic(String keyName)
	{
		return ((MethodObfuscationEntry) this.nodemap.get(keyName)).methodDesc;
	}

	private void printLog(String message)
	{
		// TODO: Add custom log file
		System.out.println(message);
	}

	private ClassNode startInjection(byte[] bytes)
	{
		final ClassNode node = new ClassNode();
		final ClassReader reader = new ClassReader(bytes);
		reader.accept(node, 0);
		MicdoodleTransformer.injectionCount = 0;
		MicdoodleTransformer.operationCount = 0;
		return node;
	}

	private byte[] finishInjection(ClassNode node)
	{
		return this.finishInjection(node, true);
	}

	private byte[] finishInjection(ClassNode node, boolean printToLog)
	{
		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		node.accept(writer);

		if (printToLog)
		{
			this.printResultsAndReset(node.name);
		}

		return writer.toByteArray();
	}

    private boolean isPlayerApiActive()
    {
        return playerApiActive;
    }

    private boolean mcVersionMatches(String testVersion)
    {
        return VersionParser.parseRange(testVersion).containsVersion(this.mcVersion);
    }
}
