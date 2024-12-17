package com.pyco.app.navigation

object Routes {
    // auth
    const val LOGIN = "login"
    const val SIGNUP = "signup"

    // basic navigation
    const val HOME = "home"
    const val CLOSET = "closet"
    const val UPLOAD = "upload"
    const val OUTFITS = "outfits"
    const val ACCOUNT = "account"

    // outfit related navigation
    const val CREATE_OUTFIT = "create_outfit"
    const val OUTFIT_DETAIL = "outfit_detail"

    // wardrobe related navigation
    const val ADD_WARDROBE_ITEM = "add_wardrobe_item"

    // account related navigation
    const val UPDATE_PROFILE = "update_profile"

    // request related navigation
    const val MAKE_REQUEST = "make_request"

    // response related navigation
    const val CREATE_RESPONSE = "create_response"
    const val RESPONSES_LIST = "responses_list"

    // related to accounts that arent yours
    const val USER_PROFILE = "user_profile"
    const val FOLLOW_OR_FOLLOWING = "follow_or_following"
}
