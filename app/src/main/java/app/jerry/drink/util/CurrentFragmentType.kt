package app.jerry.drink.util

import app.jerry.drink.R
import app.jerry.drink.util.Util.getString

enum class CurrentFragmentType(val value: String) {
    HOME(""),
    RADAR(getString(R.string.label_radar)),
    ORDER(getString(R.string.label_order)),
    PROFILE(getString(R.string.label_profile)),
}