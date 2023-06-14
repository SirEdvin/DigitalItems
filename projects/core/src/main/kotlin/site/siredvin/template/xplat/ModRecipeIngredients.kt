package site.siredvin.template.xplat

interface ModRecipeIngredients {

    companion object {
        private var _IMPL: ModRecipeIngredients? = null

        fun configure(impl: ModRecipeIngredients) {
            _IMPL = impl
        }

        fun get(): ModRecipeIngredients {
            if (_IMPL == null) {
                throw IllegalStateException("You should init PeripheralWorks Platform first")
            }
            return _IMPL!!
        }
    }
}
