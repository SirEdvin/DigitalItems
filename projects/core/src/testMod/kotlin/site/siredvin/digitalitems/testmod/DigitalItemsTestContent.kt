package site.siredvin.digitalitems.testmod

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.broccolium.modules.base.block.GenericBlockEntityBlock
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.digitalitems.xplat.ModPlatform
import java.util.function.Supplier

object DigitalItemsTestContent {
    val BLOCK: Supplier<Block> = ModPlatform.registerBlock(
        "test_storage",
        { GenericBlockEntityBlock({ BLOCK_ENTITY.get() }, false) },
    )
    val BLOCK_ENTITY: Supplier<BlockEntityType<TestStorageBlockEntity>> = ModPlatform.registerBlockEntity(
        "test_storage",
    ) { PlatformToolkit.get().createBlockEntityType(::TestStorageBlockEntity, BLOCK.get()) }

    fun register() = Unit
}
