package fun.bm.lophine.utils;

import fun.bm.lophine.config.modules.misc.ContainerExpansionConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ShulkerBoxesUtil {
    public static boolean shouldCheck() {
        return ContainerExpansionConfig.shulkerCount > 1 && ContainerExpansionConfig.shulkerCount <= 64;
    }

    public static boolean checkShulkerBox(ItemStack itemStack) {
        return shouldCheck() && checkIsShulkerBox(itemStack);
    }

    public static boolean checkIsShulkerBox(ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem b && b.getBlock() instanceof ShulkerBoxBlock;
    }

    public static int getItemMaxCount(ItemStack itemStack) {
        if (checkShulkerBox(itemStack)) {
            return Math.clamp(ContainerExpansionConfig.shulkerCount, 1, 64);
        }
        return itemStack.getMaxStackSize();
    }

    public static int getShulkerBoxesMaxCountUnsafe() {
        return Math.clamp(ContainerExpansionConfig.shulkerCount, 1, 64);
    }

    public static boolean emptyShulkerBoxCheck(@NotNull ItemStack stack) {
        return stack.getComponents().getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).stream().findAny().isEmpty();
    }

    public static boolean isStackable(ItemStack itemStack) {
        return getItemMaxCount(itemStack) > 1 && (!itemStack.isDamageableItem() || !itemStack.isDamaged());
    }

    public static int getItemStackMaxCountReal(ItemStack stack) {
        CompoundTag nbt = Optional.ofNullable(stack.get(DataComponents.CUSTOM_DATA)).orElse(CustomData.EMPTY).copyTag();
        return nbt.getInt("Lophine.RealStackSize").orElse(stack.getMaxStackSize());
    }

    public static ItemStack encodeMaxStackSize(ItemStack itemStack) {
        int realMaxStackSize = getItemStackMaxCountReal(itemStack);
        int modifiedMaxStackSize = getItemMaxCount(itemStack);
        if (itemStack.getMaxStackSize() != modifiedMaxStackSize) {
            itemStack.set(DataComponents.MAX_STACK_SIZE, modifiedMaxStackSize);
            CompoundTag nbt = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            nbt.putInt("Lophine.RealStackSize", realMaxStackSize);
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        }
        return itemStack;
    }

    public static ItemStack decodeMaxStackSize(ItemStack itemStack) {
        int realMaxStackSize = getItemStackMaxCountReal(itemStack);
        if (itemStack.getMaxStackSize() != realMaxStackSize) {
            itemStack.set(DataComponents.MAX_STACK_SIZE, realMaxStackSize);
            CompoundTag nbt = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            nbt.remove("Lophine.RealStackSize");
            if (nbt.isEmpty()) {
                itemStack.remove(DataComponents.CUSTOM_DATA);
            } else {
                itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            }
        }
        return itemStack;
    }
}