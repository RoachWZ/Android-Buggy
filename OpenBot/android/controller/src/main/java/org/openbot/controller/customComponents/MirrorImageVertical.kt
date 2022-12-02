/*
 * Developed for the OpenBot project (https://openbot.org) by:
 *
 * Ivo Zivkov
 * izivkov@gmail.com
 *
 * Date: 2020-12-27, 10:57 p.m.
 */
// add by wz 2022-12-02
package org.openbot.controller.customComponents

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import org.openbot.controller.R
import org.openbot.controller.utils.LocalEventBus

class MirrorImageVertical @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    enum class State { MIRRORED, UN_MIRRORED }
    var state: State = State.UN_MIRRORED

    init {
        setOnTouchListener(OnTouchListener())
        offState()
    }

    inner class OnTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                if (state == MirrorImageVertical.State.UN_MIRRORED) {
                    state = MirrorImageVertical.State.MIRRORED
                    onState()
                } else {
                    state = MirrorImageVertical.State.UN_MIRRORED
                    offState()
                }
            }
            return true
        }
    }

    override fun offState() {
        animate().rotation(90F).start() // modify by wz 2022-12-02
        val event: LocalEventBus.ProgressEvents = LocalEventBus.ProgressEvents.UnmirrorVertical
        LocalEventBus.onNext(event)
    }

    override fun onState() {
        animate().rotation(270F).start()    // modify by wz 2022-12-02

        val event: LocalEventBus.ProgressEvents = LocalEventBus.ProgressEvents.MirrorVertical
        LocalEventBus.onNext(event)
    }
}
