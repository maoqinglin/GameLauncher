/*
 * Copyright (C) 2012 The Android Open Source Project
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
 */

package com.ireadygo.app.gamelauncher.mygame.utils;

import android.view.View;

public class CheckLongPressHelper {
    private View mView;
    private boolean mHasPerformedLongPress;
    private CheckForLongPress mPendingCheckForLongPress;

    class CheckForLongPress implements Runnable {
        public void run() {
            if ((mView.getParent() != null) && mView.hasWindowFocus()
                    && !mHasPerformedLongPress) {
                if (mView.performLongClick()) {
                    mView.setPressed(false);
                    mHasPerformedLongPress = true;
                }
            }
        }
    }

    public CheckLongPressHelper(View v) {
        mView = v;
    }

    public void postCheckForLongPress() {
        mHasPerformedLongPress = false;

        if (mPendingCheckForLongPress == null) {
            mPendingCheckForLongPress = new CheckForLongPress();
        }
        mView.postDelayed(mPendingCheckForLongPress,300);
    }
    
    /**
     * add by linmaoqing 2014-5-14
     * @param pressTime
     * 抖动时页面触摸延时
     */
    public void postCheckForLongPress(int pressTime) {
        mHasPerformedLongPress = false;

        if (mPendingCheckForLongPress == null) {
            mPendingCheckForLongPress = new CheckForLongPress();
        }
        mView.postDelayed(mPendingCheckForLongPress, pressTime);
    }//end by linmaoqing

    public void cancelLongPress() {
        mHasPerformedLongPress = false;
        if (mPendingCheckForLongPress != null) {
            mView.removeCallbacks(mPendingCheckForLongPress);
            mPendingCheckForLongPress = null;
        }
    }

    public boolean hasPerformedLongPress() {
        return mHasPerformedLongPress;
    }
}
