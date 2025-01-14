package expo.modules.av.video

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.callbacks.callback

/**
 * We need the wrapper to be able to remove the view from the React-managed tree
 * into the FullscreenVideoPlayer and not have to fight with the React styles
 * overriding our native layout.
 */
@SuppressLint("ViewConstructor")
class VideoViewWrapper(context: Context, appContext: AppContext) : FrameLayout(context) {
  val videoViewInstance: VideoView
  private val mLayoutRunnable = Runnable {
    measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    )
    layout(left, top, right, bottom)
  }

  override fun requestLayout() {
    super.requestLayout()

    // Code borrowed from:
    // https://github.com/facebook/react-native/blob/d19afc73f5048f81656d0b4424232ce6d69a6368/ReactAndroid/src/main/java/com/facebook/react/views/toolbar/ReactToolbar.java#L166

    // It fixes two bugs:
    // - ExpoMediaController's height = 0 when initialized (until relayout)
    // - blank VideoView (until relayout) after dismissing FullscreenVideoPlayer
    post(mLayoutRunnable)
  }

  init {
    videoViewInstance = VideoView(context, this, appContext)
    addView(videoViewInstance, generateDefaultLayoutParams())
  }

  //region view callbacks

  val onStatusUpdate by callback<Bundle>()
  val onLoadStart by callback<Unit>()
  val onLoad by callback<Bundle>()
  val onError by callback<Bundle>()
  val onReadyForDisplay by callback<Bundle>()
  val onFullscreenUpdate by callback<Bundle>()

  //endregion
}
