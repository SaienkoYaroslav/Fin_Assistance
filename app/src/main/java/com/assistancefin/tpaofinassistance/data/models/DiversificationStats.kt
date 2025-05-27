package com.assistancefin.tpaofinassistance.data.models

data class DiversificationStats(
    val shortRatio: Float, // доля короткострокових [0f..1f]
    val longRatio: Float   // доля довгострокових [0f..1f]
)