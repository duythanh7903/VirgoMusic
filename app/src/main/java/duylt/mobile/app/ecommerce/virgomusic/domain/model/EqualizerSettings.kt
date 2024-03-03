package duylt.mobile.app.ecommerce.virgomusic.domain.model

data class EqualizerSettings(
    var seekbarpos: IntArray = IntArray(5),
    var presetPos: Int = 0,
    var reverbPreset: Short = 0,
    var bassStrength: Short = 0
) {

}