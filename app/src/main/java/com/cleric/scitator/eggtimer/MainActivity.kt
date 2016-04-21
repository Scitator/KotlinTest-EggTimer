package com.cleric.scitator.eggtimer

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.*

import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar

fun Int.format(digits: Int) = java.lang.String.format("%${digits}d", this)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // old java style
        //setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)
        MainActivityUi().setContentView(this)

    }

    fun startTimer(howLong: Int, step: Int) {

    }
}

class MainActivityUi : AnkoComponent<MainActivity> {

    var label: TextView? = null
    var progressBar: SeekBar? = null
    private var _isBusy = false
    var isBusy: Boolean
        get() = _isBusy
        set(value) {
            progressBar?.isEnabled = !value
            _isBusy = value
        }

    val labelId: Int = 1

    var timer: CountDownTimer? = null

    private val customStyle = { v: Any ->
        when (v) {
            is Button -> v.textSize = 26f
            is EditText -> v.textSize = 30f
            is TextView -> v.textSize = 60f
        }
    }

    fun updateTimer(secondsLeft: Int) {
        val minutes: Int = secondsLeft / 60
        val seconds: Int = secondsLeft % 60
        label?.text = minutes.format(2) + ":" + seconds.format(2)
    }

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        verticalLayout {
            toolbar {
                title = "Egg timer"
                setTitleTextColor(Color.BLUE)
            }
            progressBar = seekBar {
                max = 600
                progress = 60
                onSeekBarChangeListener {
                    onProgressChanged { seekBar, i, b ->
                        updateTimer(progress)
                    }
                }
            }
            relativeLayout {
                imageView {
                    setImageResource(R.drawable.egg)
                }
                label = textView {
                    id = labelId
                    text = "01:00"
                    gravity = Gravity.CENTER
                }.lparams {
                    centerHorizontally()
                    centerVertically()
                }
                button("Start") {
                    gravity = Gravity.CENTER_HORIZONTAL
                    onClick {
                        if (!isBusy) {
                            isBusy = true
                            text = "Stop"
                            timer = object : CountDownTimer((progressBar!!.progress * 1000 + 100).toLong(), 1000) {
                                override fun onFinish() {
                                    //this@button.animate().alpha(1f).setDuration(1000).start()
                                    updateTimer(0)
                                    val mlplayer = MediaPlayer.create(context, R.raw.airhorn)
                                    mlplayer.start()
                                    isBusy = false
                                    text = "Start"
                                }

                                override fun onTick(millisUntilFinished: Long) {
                                    updateTimer((millisUntilFinished / 1000).toInt())
                                    progressBar?.progress = (millisUntilFinished / 1000).toInt()
                                }

                            }.start()
                            //this.animate().alpha(0f).setDuration(1000).start()
                        } else {
                            timer?.cancel()
                            isBusy = false
                            text = "Start"
                        }
                    }
                }.lparams {
                    below(labelId)
                    centerHorizontally()

                }
            }.style(customStyle)

        }.style(customStyle)
    }
}
