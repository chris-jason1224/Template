package com.cj.ui.editable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.cj.ui.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;


/**
 * 类说明：可以清除内容的ExidText
 * <p>
 * 作  者：小蜗（Richard） on 2017/6/5
 */
public class ClearEditText extends AppCompatEditText implements View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {

    private Drawable mClearTextIcon;
    //是否显示密码的icon
    private Drawable mShowPwdTextIcon;
    private OnFocusChangeListener mOnFocusChangeListener;
    private OnTouchListener mOnTouchListener;
    private OnTextWatcherListener listener;
    public static final int TYPE_PHONE = 0;
    public static final int TYPE_CARD = 1;
    public static final int TYPE_IDCARD = 2;
    public static final int TYPE_PASSWORD = 3;
    public static final int TYPE_VISIBLE_PASSWORD = 4;//可显示的密码框
    public static final int TYPE_IDCARD_OTHER = 5;//港澳台的身份编号
    public static final int TYPE_POSTCODE = 6;//邮政编码 十位以内，数字加字母
    private int maxLength = 100;
    private int contentType;
    private int rightIconType;
    private int start, count, before;
    private String digits;
    //一直显示
    private boolean isshowclear;
    //默认图片
    private Drawable defaultDrawable;

    public boolean isIsshowclear() {
        return isshowclear;
    }

    public void setIsshowclear(boolean isshowclear) {
        this.isshowclear = isshowclear;
    }

    public ClearEditText(final Context context) {
        super(context);
        init(context, null);
    }

    public ClearEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClearEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        parseAttributeSet(context, attrs);
        setPadding(dp2px(5), 0, dp2px(5), 0);


        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }


    private void parseAttributeSet(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.core_ui_clear_editText, 0, 0);
        contentType = ta.getInt(R.styleable.core_ui_clear_editText_core_ui_type, -1);
        int anInt = ta.getInt(R.styleable.core_ui_clear_editText_core_ui_maxLength, -1);
        if (anInt != -1) {
            maxLength = anInt;
        }
        defaultDrawable = ta.getDrawable(R.styleable.core_ui_clear_editText_core_ui_clearDrawable);

        ta.recycle();
        setSingleLine();
        initType();
        initRightIcon(context);

        addTextChangedListener(this);
    }

    private void initType() {
        if (contentType == TYPE_PHONE) {
            maxLength = 11;
            digits = "0123456789 ";
            setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (contentType == TYPE_CARD) {
            maxLength = 23;
            digits = "0123456789 ";
            setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (contentType == TYPE_IDCARD) {
            maxLength = 21;
            digits = "0123456789xX ";
            setInputType(TYPE_CLASS_TEXT);
        } else if (contentType == TYPE_PASSWORD) {
            //支付密码
            maxLength = 20;
//            digits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+|<>,.?/:;'\"[]{}";
//            digits = "^\\u4e00-\\u9fa5";
            setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength), typeFilter});
            return;
        } else if (contentType == TYPE_IDCARD_OTHER) {

            maxLength = 50;
            digits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            setInputType(TYPE_CLASS_TEXT);
        } else if (contentType == TYPE_VISIBLE_PASSWORD) {
            maxLength = 20;
//            digits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+|<>,.?/:;'\"[]{}";
//            digits = "^\\u4e00-\\u9fa5";
            setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength), typeFilter});
            return;
        }else if(contentType == TYPE_POSTCODE){
            maxLength =10;
            setInputType(TYPE_CLASS_TEXT );
            digits ="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        // 输入长度限制
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    /**
     * 限制中文 空格输入
     */
    InputFilter typeFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//            Pattern p = Pattern.compile("[^\\u4e00-\\u9fa5^\\s*$]+");
            Pattern p = Pattern.compile("^[a-zA-Z0-9\\x21-\\x7e]+");
//            Pattern p = Pattern.compile("^[a-zA-Z0-9\\x21-\\x7e]{6,20}$");
            Matcher m = p.matcher(source.toString());
            if (!m.matches()) return "";
            return null;
        }

    };

    private void initRightIcon(Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.core_ui_icon_clean);
        if (defaultDrawable != null) {
            drawable = defaultDrawable;
        }

        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable); //Wrap the drawable so that it can be tinted pre Lollipop
        DrawableCompat.setTint(wrappedDrawable, getCurrentHintTextColor());
        mClearTextIcon = wrappedDrawable;
        mClearTextIcon.setBounds(0, 0, mClearTextIcon.getIntrinsicHeight(), mClearTextIcon.getIntrinsicHeight());
        setClearIconVisible(false);

//        Drawable showPwdDrawable = ContextCompat.getDrawable(context,R.drawable.icon_biyan);
//        final Drawable pwdWrappedDrawable = DrawableCompat.wrap(showPwdDrawable); //Wrap the drawable so that it can be tinted pre Lollipop
//        DrawableCompat.setTint(wrappedDrawable, getCurrentHintTextColor());
//        if (mShowPwdTextIcon==null){
//            mShowPwdTextIcon= pwdWrappedDrawable;
//        }
//        mShowPwdTextIcon.setBounds(0, 0, mShowPwdTextIcon.getIntrinsicHeight(), mShowPwdTextIcon.getIntrinsicHeight());
    }

    @Override
    public void setInputType(int type) {
        super.setInputType(type);
        // setKeyListener要在setInputType后面调用，否则无效
        if (!TextUtils.isEmpty(digits)) {
            setKeyListener(DigitsKeyListener.getInstance(digits));
        }
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mOnFocusChangeListener = l;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mOnTouchListener = l;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            if (isshowclear) {
                setClearIconVisible(getText().length() > 0);
            } else {
                setClearIconVisible(false);
            }
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int x = (int) motionEvent.getX();
        if (mClearTextIcon.isVisible() && x > getWidth() - getPaddingRight() - mClearTextIcon.getIntrinsicWidth()) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                setError(null);
                setText("");
                if (listener != null) {
                    listener.onClear();
                }
            }
            return true;
        }
        return mOnTouchListener != null && mOnTouchListener.onTouch(view, motionEvent);
    }

    @Override
    public final void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (isFocused()) {
            setClearIconVisible(text.length() > 0);
        } else {
            if (isshowclear) {
                setClearIconVisible(text.length() > 0);
            }
        }

        this.start = start;
        this.before = lengthBefore;
        this.count = lengthAfter;
        if (listener != null) {
            listener.onTextChanged(text, start, lengthBefore, lengthAfter);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == null) {
            return;
        }
        //判断是否是在中间输入，需要重新计算
        boolean isMiddle = (start + count) < (s.length());
        //在末尾输入时，是否需要加入空格
        boolean isNeedSpace = false;
        if (!isMiddle && isSpace(s.length())) {
            isNeedSpace = true;
        }

    }

    private boolean isSpace(int length) {
        if (contentType == TYPE_PHONE) {
            return isSpacePhone(length);
        } else if (contentType == TYPE_CARD) {
            return isSpaceCard(length);
        } else if (contentType == TYPE_IDCARD) {
            return isSpaceIDCard(length);
        }
        return false;
    }

    private boolean isSpacePhone(int length) {
        return length >= 4 && (length == 4 || (length + 1) % 5 == 0);
    }

    private boolean isSpaceCard(int length) {
        return length % 5 == 0;
    }

    private boolean isSpaceIDCard(int length) {
        return length > 6 && (length == 7 || (length - 2) % 5 == 0);
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
        initType();
    }

    public String getTextWithoutSpace() {
        return super.getText()
                .toString()
                .replace(" ", "");
    }



    private void setClearIconVisible( boolean visible) {

        if(!isEnabled()){
            visible = false;
        }

        mClearTextIcon.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], visible ? mClearTextIcon : null, compoundDrawables[3]);
        setCompoundDrawablePadding(dp2px(10));
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    public void setOnTextWatcherListener(OnTextWatcherListener listener) {
        this.listener = listener;
    }

    public interface OnTextWatcherListener {
        void onClear();

        void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter);
    }

}