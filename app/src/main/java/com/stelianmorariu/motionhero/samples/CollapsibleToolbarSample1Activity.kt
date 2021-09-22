package com.stelianmorariu.motionhero.samples

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.stelianmorariu.motionhero.R
import com.stelianmorariu.motionhero.databinding.ActivityCollapsibleToolbarSample1Binding
import com.stelianmorariu.motionhero.utils.viewBinding
import timber.log.Timber
import kotlin.properties.Delegates

class CollapsibleToolbarSample1Activity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context) = Intent(context, CollapsibleToolbarSample1Activity::class.java)
    }

    private val binding: ActivityCollapsibleToolbarSample1Binding by viewBinding { ActivityCollapsibleToolbarSample1Binding.inflate(it) }

    private var windowTopInsets: Int = 0
    private var translucentStatusBarColour by Delegates.notNull<Int>()
    private var solidtStatusBarColour by Delegates.notNull<Int>()
    private val fullScreenFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        translucentStatusBarColour = ContextCompat.getColor(this@CollapsibleToolbarSample1Activity, R.color.black_40)
        solidtStatusBarColour = ContextCompat.getColor(this@CollapsibleToolbarSample1Activity, R.color.starlingBackgroundPrimary)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            this.decorView.systemUiVisibility = fullScreenFlags
            statusBarColor = translucentStatusBarColour
        }

        binding.root.setOnApplyWindowInsetsListener() { view, insets ->
            Timber.e("Top insets value: ${insets.systemWindowInsetTop}")
            windowTopInsets = insets.systemWindowInsetTop

            with(binding.contentView.getConstraintSet(R.id.partnerDetailsExpanded)) {
                setMargin(R.id.backButton, ConstraintSet.TOP, windowTopInsets)
            }

            with(binding.contentView.getConstraintSet(R.id.partnerDetailsCollapsed)) {
                setMargin(R.id.backButton, ConstraintSet.TOP, windowTopInsets)
                constrainHeight(R.id.fakeToolbar, binding.fakeToolbar.height + windowTopInsets)
            }

            return@setOnApplyWindowInsetsListener view.onApplyWindowInsets(insets)
        }


        setContentView(binding.root)

        binding.contentView.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, startId: Int, endId: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, startId: Int, endId: Int, progress: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
                val isUsingNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                val useLightStatusBar: Boolean

                if (currentId == R.id.partnerDetailsExpanded) {
                    window.statusBarColor = translucentStatusBarColour
                    useLightStatusBar = false
                } else {
                    window.statusBarColor = solidtStatusBarColour
                    useLightStatusBar = !isUsingNightMode
                }

                if (useLightStatusBar) {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = fullScreenFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.decorView.systemUiVisibility = fullScreenFlags
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
            }
        })
    }
}
