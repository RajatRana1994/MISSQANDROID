package com.msq.util

import androidx.fragment.app.FragmentManager
import com.msq.R

/**
 * Created by jyotidubey on 18/01/18.
 */
internal fun androidx.fragment.app.FragmentManager.removeFragment(
    tag: String,
    slideIn: Int = R.anim.slide_left,
    slideOut: Int = R.anim.slide_right
) {
    this.beginTransaction()
//        .disallowAddToBackStack()
        .setCustomAnimations(slideIn, slideOut)
        .remove(this.findFragmentByTag(tag)!!)
        .commitNow()
}

internal fun androidx.fragment.app.FragmentManager.replaceFragment(
    containerViewId: Int,
    fragment: androidx.fragment.app.Fragment
) {
    this.beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(containerViewId, fragment,fragment.tag)
        .commit()
}

internal fun androidx.fragment.app.FragmentManager.addFragment(containerViewId: Int, fragment: androidx.fragment.app.Fragment) {
    this.popBackStack(fragment.tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    this.beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(containerViewId, fragment,fragment.tag)
        .commit()
}

internal fun androidx.fragment.app.FragmentManager.replaceFragmentAnim(
    containerViewId: Int,
    fragment: androidx.fragment.app.Fragment,
    tag: String,
    slideIn: Int = R.anim.slide_left,
    slideOut: Int = R.anim.slide_right
) {
    this.beginTransaction()
//        .disallowAddToBackStack()
        .setCustomAnimations(slideIn, slideOut)
        .replace(containerViewId, fragment)
        .commit()
}





