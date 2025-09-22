package site.siredvin.digitalitems.common.data

data class ByteArrayWrapper(val byteArray: ByteArray) {
    override fun equals(other: Any?): Boolean {
        val trueOther = other as? ByteArrayWrapper ?: return false
        return byteArray.contentEquals(trueOther.byteArray)
    }

    override fun hashCode(): Int = byteArray.contentHashCode()
}
