/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.tweetui.internal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;

class AnimationUtils {
    public static ViewPropertyAnimator fadeOut(final View from, int duration) {
        if (from.getVisibility() == View.VISIBLE) {
            from.clearAnimation();
            final ViewPropertyAnimator animator = from.animate();
            animator.alpha(0f)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            from.setVisibility(View.INVISIBLE);
                            from.setAlpha(1f);
                        }
                    });
            return animator;
        }
        return null;
    }

    public static ViewPropertyAnimator fadeIn(View to, int duration) {
        if (to.getVisibility() != View.VISIBLE) {
            to.setAlpha(0f);
            to.setVisibility(View.VISIBLE);
        }
        to.clearAnimation();
        final ViewPropertyAnimator animator = to.animate();
        animator.alpha(1f)
                .setDuration(duration)
                .setListener(null);
        return animator;
    }
}
