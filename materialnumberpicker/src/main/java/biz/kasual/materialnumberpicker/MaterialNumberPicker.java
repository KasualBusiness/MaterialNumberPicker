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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

/**
 * Stock {@link NumberPicker} with hooks for customization
 */
public class MaterialNumberPicker extends NumberPicker {

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 10;
    private static final int DEFAULT_VALUE = 1;
    private static final float TEXT_SIZE = 20.f;
    private static final int TEXT_COLOR = Color.BLACK;
    private static final int BACKGROUND_COLOR = Color.WHITE;
    private static final int SEPARATOR_COLOR = Color.TRANSPARENT;

    private static float pixelsToSp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }

    private static float spToPixels(Context context, float sp) {
        return sp * context.getResources().getDisplayMetrics().scaledDensity;
    }

    private Builder mBuilder;
    private int mTextColor;
    private float mTextSize;
    private int mSeparatorColor;
    private boolean mEnableFocusability;

    //Cache fields since reflection is kinda slow
    private Field mPickerDividerField;
    private Paint mSelectorWheelPaint;

    public MaterialNumberPicker(Context context) {
        super(context);
        initView();
    }

    public MaterialNumberPicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();

        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.MaterialNumberPicker);

        for (int i = 0; i < a.getIndexCount(); ++i) {

            int attr = a.getIndex(i);
            if (attr == R.styleable.MaterialNumberPicker_npMinValue) {
                setMinValue(a.getInt(attr, MIN_VALUE));
            } else if (attr == R.styleable.MaterialNumberPicker_npMaxValue) {
                setMaxValue(a.getInt(attr, MAX_VALUE));
            } else if (attr == R.styleable.MaterialNumberPicker_npDefaultValue) {
                setValue(a.getInt(attr, DEFAULT_VALUE));
            } else if (attr == R.styleable.MaterialNumberPicker_npTextSize) {
                setTextSize(a.getDimension(attr, TEXT_SIZE));
            } else if (attr == R.styleable.MaterialNumberPicker_npTextColor) {
                setTextColor(a.getColor(attr, TEXT_COLOR));
            } else if (attr == R.styleable.MaterialNumberPicker_npSeparatorColor) {
                setSeparatorColor(a.getColor(attr, SEPARATOR_COLOR));
            } else if (attr == R.styleable.MaterialNumberPicker_npBackgroundColor) {
                setBackgroundColor(a.getColor(attr, BACKGROUND_COLOR));
            } else if (attr == R.styleable.MaterialNumberPicker_npFocusValue) {
                setFocusability(a.getBoolean(attr, false));
            } else if (attr == R.styleable.MaterialNumberPicker_npWrapValue) {
                setWrapSelectorWheel(a.getBoolean(attr, false));
            }
        }

        a.recycle();
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
        setTextSize(spToPixels(getContext(), builder.textSize));
        setWrapSelectorWheel(builder.wrapSelectorWheel);
        setFocusability(builder.enableFocusability);
    }

    @Nullable
    public final Builder getBuilder() {
        return this.mBuilder;
    }

    @ColorInt
    public int getTextColor() {
        return mTextColor;
    }

    @ColorInt
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
        setMinValue(MIN_VALUE);
        setMaxValue(MAX_VALUE);
        setValue(DEFAULT_VALUE);
        setBackgroundColor(BACKGROUND_COLOR);
        setSeparatorColor(SEPARATOR_COLOR);
        setTextColor(TEXT_COLOR);
        setTextSize(TEXT_SIZE);
        setWrapSelectorWheel(false);
        setFocusability(false);

        try {
            Field f = NumberPicker.class.getDeclaredField("mInputText");
            f.setAccessible(true);
            EditText inputText = (EditText) f.get(this);
            inputText.setFilters(new InputFilter[0]);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            //We do not really want to display the errors, since we are a lib
        }
    }

    /**
     * Uses reflection to access divider private attribute and override its color
     * Use Color.Transparent if you wish to hide them
     *
     * @return true if separator set, false if field was not accessible
     */
    public boolean setSeparatorColor(int separatorColor) {
        mSeparatorColor = separatorColor;
        if (mPickerDividerField == null) {
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    mPickerDividerField = pf;
                    break;
                }
            }
        }
        if (mPickerDividerField != null) {
            try {
                mPickerDividerField.set(this, new ColorDrawable(separatorColor));
            } catch (IllegalAccessException | IllegalArgumentException e) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Uses reflection to access text color private attribute for both wheel and edit text inside the number picker.
     */
    public void setTextColor(int textColor) {
        mTextColor = textColor;
        updateTextAttributes();
    }

    /**
     * Uses reflection to access text size private attribute for both wheel and edit text inside the number picker.
     */
    public void setTextSize(float textSize) {
        mTextSize = textSize;
        updateTextAttributes();
    }

    private void updateTextAttributes() {

        updateSelectorWheelPaint();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof EditText) {
                EditText editText = ((EditText) child);
                editText.setTextColor(mTextColor);
                editText.setTextSize(pixelsToSp(getContext(), mTextSize));
                editText.invalidate();
            }
        }
    }

    private boolean updateSelectorWheelPaint() {
        if (mSelectorWheelPaint == null) {
            try {
                Field selectorWheelPaintField = NumberPicker.class.getDeclaredField("mSelectorWheelPaint");
                selectorWheelPaintField.setAccessible(true);

                mSelectorWheelPaint = ((Paint) selectorWheelPaintField.get(this));
            } catch (Exception e) {
                return false;
            }

        }
        if (mSelectorWheelPaint != null) {
            mSelectorWheelPaint.setColor(mTextColor);
            mSelectorWheelPaint.setTextSize(mTextSize);
            invalidate();
            return true;
        }
        return false;
    }

    private void setFocusability(boolean isFocusable) {
        mEnableFocusability = isFocusable;
        setDescendantFocusability(isFocusable ? FOCUS_AFTER_DESCENDANTS : FOCUS_BLOCK_DESCENDANTS);
    }

    public static class Builder {
        private Context context;
        private Formatter formatter;
        private int backgroundColor = BACKGROUND_COLOR;
        private int separatorColor = SEPARATOR_COLOR;
        private int textColor = TEXT_COLOR;
        private float textSize = TEXT_SIZE;
        private int minValue = MIN_VALUE;
        private int maxValue = MAX_VALUE;
        private int defaultValue = DEFAULT_VALUE;
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

        public Builder separatorColor(int separatorColor) {
            this.separatorColor = separatorColor;
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder textSize(float textSize) {
            this.textSize = textSize;
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
