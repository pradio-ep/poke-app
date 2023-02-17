package id.pradio.pokeapp.ui.detail

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View.OnTouchListener
import kotlin.math.abs

abstract class OnSwipeTouchListener(ctx: Context?) : OnTouchListener {
	val gestureDetector: GestureDetector

	init {
		gestureDetector = GestureDetector(ctx, GestureListener())
	}

	companion object {
		private const val SWIPE_THRESHOLD = 100
		private const val SWIPE_VELOCITY_THRESHOLD = 100
	}

	private inner class GestureListener : SimpleOnGestureListener() {
		override fun onDown(e: MotionEvent): Boolean {
			return true
		}

		override fun onFling(
			e1: MotionEvent,
			e2: MotionEvent,
			velocityX: Float,
			velocityY: Float
		): Boolean {
			var result = false
			try {
				val diffY = e2.y - e1.y
				val diffX = e2.x - e1.x
				if (abs(diffX) > abs(diffY)) {
					if (abs(diffX) > Companion.SWIPE_THRESHOLD && abs(velocityX) > Companion.SWIPE_VELOCITY_THRESHOLD) {
						if (diffX > 0) {
							onSwipeLeft()
						} else {
							onSwipeRight()
						}
					}
				} else if (abs(diffY) > Companion.SWIPE_THRESHOLD && abs(velocityY) > Companion.SWIPE_VELOCITY_THRESHOLD) {
					if (diffY > 0) {
						onSwipeTop()
					} else {
						onSwipeBottom()
					}
				}
				result = true
			} catch (exception: Exception) {
				exception.printStackTrace()
			}
			return result
		}
	}

	abstract fun onSwipeRight()
	abstract fun onSwipeLeft()
	abstract fun onSwipeTop()
	abstract fun onSwipeBottom()
}