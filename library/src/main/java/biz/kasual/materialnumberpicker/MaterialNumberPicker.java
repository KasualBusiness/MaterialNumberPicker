/*
 * Copyright (C) 2015 Kasual Business
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

package biz.kasual.materialnumberpicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

/**
 * Created by Stephen Vinouze on 22/09/2015.
 */
public class MaterialNumberPicker extends NumberPicker {

    private Builder mBuilder;
    private int mTextColor;
    private int mSeparatorColor;
    private boolean mEnableFocusability;

    public MaterialNumberPicker(Context context) {
        super(context);
        initView();
    }

    public MaterialNumberPicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public MaterialNumberPicker(Builder builder) {
        super(builder.context);
        initView();

        mBuilder = builder;

        setMinValue(builder.minValue);
        setMaxValue(builder.maxValue);
        setValue(builder.defaultValue);
        setFormatter(builder.formatter);
        setBackgroundColor(builder.backgroundColor);
        setSeparatorColor(builder.separatorColor);
        setTextColor(builder.textColor);
        setWrapSelectorWheel(builder.wrapSelectorWheel);
        setFocusability(builder.enableFocusability);
    }

    public final Builder getBuilder() {
        return this.mBuilder;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public int getSeparatorColor() {
        return mSeparatorColor;
    }

    public boolean isFocusabilityEnabled() {
        return mEnableFocusability;
    }

    /**
     * Init number picker by disabling focusability of edit text embedded inside the number picker
     * We also override the edit text filter private attribute by using reflection as the formatter is still buggy while attempting to display the default value
     * This is still an open Google @see <a href="https://code.google.com/p/android/issues/detail?id=35482#c9">issue</a> from 2012
     */
    private void initView() {
        try {
            Field f = NumberPicker.class.getDeclaredField("mInputText");
            f.setAccessible(true);
            EditText inputText = (EditText)f.get(this);
            inputText.setFilters(new InputFilter[0]);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses reflection to access divider private attribute and override its color
     * Use Color.Transparent if you wish to hide them
     */
    public void setSeparatorColor(int separatorColor) {
        mSeparatorColor = separatorColor;

        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(this, new ColorDrawable(separatorColor));
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * Uses reflection to access text color private attribute for both wheel and edit text inside the number picker.
     * By default uses the colorPrimaryText but it's a shame to have to override the value whereas simple accessors would have been perfect
     */
    public void setTextColor(int textColor) {
        mTextColor = textColor;

        for (int i = 0; i < getChildCount(); i++){
            View child = getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = NumberPicker.class.getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(this)).setColor(textColor);
                    ((EditText)child).setTextColor(textColor);
                    invalidate();
                    break;
                }
                catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setFocusability(boolean isFocusable) {
        mEnableFocusability = isFocusable;
        setDescendantFocusability(isFocusable ? FOCUS_AFTER_DESCENDANTS : FOCUS_BLOCK_DESCENDANTS);
    }

    public static class Builder {
        private Context context;
        private Formatter formatter;
        private int backgroundColor = Color.WHITE;
        private int textColor = Color.BLACK;
        private int separatorColor = Color.TRANSPARENT;
        private int minValue = 1;
        private int maxValue = 10;
        private int defaultValue = 1;
        private boolean enableFocusability = false;
        private boolean wrapSelectorWheel = false;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder formatter(Formatter formatter) {
            this.formatter = formatter;
            return this;
        }

        public Builder backgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder separatorColor(int separatorColor) {
            this.separatorColor = separatorColor;
            return this;
        }

        public Builder minValue(int minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder maxValue(int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder defaultValue(int defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder wrapSelectorWheel(boolean wrapSelectorWheel) {
            this.wrapSelectorWheel = wrapSelectorWheel;
            return this;
        }

        public Builder enableFocusability(boolean enableFocusability) {
            this.enableFocusability = enableFocusability;
            return this;
        }

        public MaterialNumberPicker build() {
            return new MaterialNumberPicker(this);
        }

    }

}
