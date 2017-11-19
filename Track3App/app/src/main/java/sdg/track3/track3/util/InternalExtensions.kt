package sdg.track3.track3.util

import android.view.View

/**
 * @author <@Po10cio> on 11/19/17 for Track3App
 *
 */
fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.remove() {
    this.visibility = View.GONE
}

fun View.enable() {
    this.isEnabled = true
}

fun View.disAble() {
    this.isEnabled = false
}

fun View.toggle(show: Boolean) {
    if (show) this.show() else this.hide()
}