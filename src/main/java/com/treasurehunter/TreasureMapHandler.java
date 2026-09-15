package com.treasurehunter;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.Random;

public class TreasureMapHandler {

    private final Random random = new Random();

    @SubscribeEvent
    public void onMapUse(PlayerInteractEvent event) {
        EntityPlayer player = event.entityPlayer;
        World world = player.worldObj;

        if (world.isRemote || event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            return;
        }

        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null || heldItem.getItem() == null) {
            return;
        }

        String itemName = Block.blockRegistry.getNameForObject(heldItem.getItem());
        if (itemName == null || !itemName.startsWith("quadrum:")) {
            return; 
        }

        String lootTable = "";
        int trapChance = 0;

        if (itemName.equals("quadrum:treasure_map_easy")) {
            lootTable = "recurrentcomplex:chests/easy_treasure"; 
            trapChance = TreasureHunterMod.easyTrapChance;
        } else if (itemName.equals("quadrum:treasure_map_hard")) {
            lootTable = "lootpp:chests/hard_treasure";
            trapChance = TreasureHunterMod.hardTrapChance;
        } else {
            return;
        }

        int randomX = (int) player.posX + (random.nextInt(101) - 50);
        int randomZ = (int) player.posZ + (random.nextInt(101) - 50);
        
        int surfaceY = world.getTopSolidOrLiquidBlock(randomX, randomZ);

        Block surfaceBlock = world.getBlock(randomX, surfaceY, randomZ);
        Material material = surfaceBlock.getMaterial();

        if (material == Material.water || material == Material.lava || surfaceBlock == Blocks.bedrock || surfaceY <= 5) {
            player.addChatComponentMessage(new ChatComponentText(
                EnumChatFormatting.RED + "The shifting terrain here is too unstable to hide treasure. Find solid ground!"));
            return; 
        }

        int buryDepth = 2 + random.nextInt(4); 
        int targetY = surfaceY - buryDepth;
        if (targetY < 5) targetY = 5;

        boolean isTrapped = random.nextInt(100) < trapChance;
        Block chestBlock = isTrapped ? Blocks.trapped_chest : Blocks.chest;

        world.setBlock(randomX, targetY, randomZ, chestBlock);

        if (isTrapped) {
            world.setBlock(randomX, targetY - 1, randomZ, Blocks.tnt);
            player.addChatComponentMessage(new ChatComponentText(
                EnumChatFormatting.RED + "The hair on your neck stands up... this hunt feels dangerous!"));
        }

        TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(randomX, targetY, randomZ);
        if (tileChest != null && !lootTable.isEmpty()) {
            String command = String.format("setblockloot %d %d %d %s", randomX, targetY, randomZ, lootTable);
            MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), command);
        }

        world.playSoundEffect(player.posX, player.posY, player.posZ, "ambient.weather.thunder", 0.6F, 1.2F);

        String coordMessage = String.format(
            EnumChatFormatting.GOLD + "🏹 The hunt is on! Target located: " + 
            EnumChatFormatting.YELLOW + "Dig at: " + 
            EnumChatFormatting.GREEN + "X: %d, Y: %d, Z: %d " + 
            EnumChatFormatting.AQUA + "(Bury depth: %d blocks)", 
            randomX, targetY, randomZ, buryDepth
        );
        player.addChatComponentMessage(new ChatComponentText(coordMessage));

        if (!player.capabilities.isCreativeMode) {
            heldItem.stackSize--;
            if (heldItem.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
        }
        
        event.setCanceled(true);
    }
}