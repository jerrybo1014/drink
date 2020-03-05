package app.jerry.drink.util

enum class PermissionCode(val requestCode:Int) {
    CAMERA(10),
    READ_EXTERNAL_STORAGE(11),
    LOCATION(12)
}

enum class RequestCode(val requestCode:Int) {
    GOOGLE_SIGN_IN(100),
    FB_SIGN_IN(101),
    PICK_IMAGE(102),
    TAKE_PHOTO(103)
}
