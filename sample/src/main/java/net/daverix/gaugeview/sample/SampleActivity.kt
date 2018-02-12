/*
    Copyright 2018 David Laurell

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package net.daverix.gaugeview.sample

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.DecelerateInterpolator
import net.daverix.gaugeview.GaugeView

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.sample)

        val gaugeView = findViewById<GaugeView>(R.id.gaugeView)

        animate(gaugeView)
    }

    private fun animate(gaugeView: GaugeView) {
        val animateTo = (Math.random() * 80f - 30f).toFloat()
        val animatorSet = AnimatorSet()
        animatorSet.play(ObjectAnimator.ofFloat(gaugeView, "value", animateTo))
        animatorSet.duration = 1500
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(p0: Animator?) {
                animate(gaugeView)
            }
        })
        animatorSet.start()
    }
}