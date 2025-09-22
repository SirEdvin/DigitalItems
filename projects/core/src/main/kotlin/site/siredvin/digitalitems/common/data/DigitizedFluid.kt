package site.siredvin.digitalitems.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack

class DigitizedFluid : DigitizedSomething<AgnosticFluidStack> {

    constructor(id: ByteArrayWrapper, item: AgnosticFluidStack, currentTime: Long, player: ServerPlayer?) : super(id, item, currentTime, player)

    constructor(compoundTag: CompoundTag) : super(compoundTag)

    override fun fromCompound(tag: CompoundTag): AgnosticFluidStack = AgnosticFluidStack.of(tag)

    override fun toCompound(): CompoundTag = something.save(CompoundTag())

    override fun shrink(amount: Int) {
        something.shrink(amount)
    }

    override fun grow(amount: Int) {
        something.grow(amount)
    }

    override val somethingTagName: String
        get() = "fluidStack"
    override val isEmpty: Boolean
        get() = something.isEmpty
    override val amount: Long
        get() = something.amount
}
