/*
 * Lunar Launcher
 * Copyright (C) 2022 Md Rasel Hossain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rasel.lunar.launcher.helpers;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.google.android.material.textview.MaterialTextView;

public class TypewriterTexts extends MaterialTextView {

    private CharSequence text;
    private int index;
    private long delay = 150;
    private final Handler handler = new Handler();

    public TypewriterTexts(Context context) {
        super(context);
    }

    public TypewriterTexts(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private final Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(text.subSequence(0, index++));
            if(index <= text.length()) {
                handler.postDelayed(characterAdder, delay);
            }
        }
    };

    public void animateText(CharSequence sequence) {
        text = sequence;
        index = 0;
        setText("");
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, delay);
    }

    private void setCharacterDelay(long characterDelay) {
        delay = characterDelay;
    }
}
