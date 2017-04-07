package com.tcl.myapplication.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.myapplication.util.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lishiwei
 */
public class FragmentIndicator extends LinearLayout implements OnClickListener {

    private int mDefaultIndicator = 0;
    private static int mCurIndicator;
    private static int mCurIndicator1;
    private static View[] mIndicators;
    private OnIndicateListener mOnIndicateListener;

    private List<String> indicatorTagTexts = new ArrayList<>();

    private List<String> indicatorTexts = new ArrayList<>();



    private int indicaterItemSize;
    private static final String TAG_ICON_0 = "icon_tag_0";
    private static final String TAG_ICON_1 = "icon_tag_1";
    private static final String TAG_ICON_2 = "icon_tag_2";
    private static final String TAG_ICON_3 = "icon_tag_3";
    private static final String TAG_ICON_4 = "icon_tag_4";

    private static final String TAG_TEXT_0 = "text_tag_0";
    private static final String TAG_TEXT_1 = "text_tag_1";
    private static final String TAG_TEXT_2 = "text_tag_2";
    private static final String TAG_TEXT_3 = "text_tag_3";
    private static final String TAG_TEXT_4 = "text_tag_4";
    private List<String> indicatorTagIcons = new ArrayList<>();

    private static final int COLOR_UNSELECT = Color.argb(100, 0x88, 0x88, 0x88);
    private static final int COLOR_SELECT = Color.parseColor("#3598dc");

    private static final int COLOR_CLICKED = Color.parseColor("#f7f7f7");
    private static final int COLOR_UNCLICKED = Color.parseColor("#e4e4e4");

    private FragmentIndicator(Context context) {
        super(context);
    }

    public FragmentIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCurIndicator = mDefaultIndicator;
        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundColor(Color.parseColor("#73b24e"));
        init();
    }

    public void removeIndicators() {
        for (int i = 0; i < mIndicators.length; i++) {
            mIndicators[i] = null;
        }
    }

    private View createIndicator( String stringResID, int stringColor,
                                  String textTag) {
        LinearLayout view = new LinearLayout(getContext());
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(new LayoutParams(
                0, DensityUtils.dp2px(44), 1));
        view.setGravity(Gravity.BOTTOM | Gravity.CENTER);
//        ImageView iconView = new ImageView(getContext());
//        iconView.setTag(iconTag);
//
//        iconView.setLayoutParams(new LayoutParams(
//                DensityUtils.dp2px(22), DensityUtils.dp2px( 22), 1));
//        iconView.setImageResource(iconResID);

//        iconView.setPadding(0, DensityUtil.dp2px( 0), 0, DensityUtil.dp2px( 0));

        //LinearLayout linearLayout = new LinearLayout(getContext());
        //linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, DensityUtil.dp2px(getContext(), 52)) );
        //linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        //linearLayout.setGravity(Gravity.CENTER);
        //ImageView imageView = new ImageView(getContext());
        //imageView.setLayoutParams(new LayoutParams(
        //DensityUtil.dp2px(getContext(), 2),DensityUtil.dp2px(getContext(),36)));
        //imageView.setScaleType(ScaleType.MATRIX);
        //imageView.setBackgroundColor(Color.rgb(0x66, 0x66, 0x66));
        //imageView.setBackgroundResource(R.drawable.verticalline);

        TextView textView = new TextView(getContext());
        textView.setTag(textTag);
        textView.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
        textView.setTextColor(stringColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        textView.setPadding(0, 0, 0, DensityUtils.dp2px(4));
        textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        textView.setText(stringResID);
        textView.setVisibility(View.VISIBLE);

        //view.addView(textView);
        //linearLayout.addView(imageView);
        //view.addView(linearLayout);

//		linearLayout.addView(view);
//		linearLayout.addView(imageView);
//		return linearLayout;		

        return view;

    }

    private void init() {

            indicatorTexts.add("常用");
            indicatorTexts.add("客厅");
            indicatorTexts.add("主卧");
            indicatorTexts.add("次卧");

        for (int i = 0; i < indicatorTexts.size(); i++) {
            indicatorTagTexts.add("text_tag_" + i);
        }

        mIndicators = new View[indicatorTexts.size()];
        for (int i = 0; i < indicatorTexts.size(); i++) {
            if (i == 0) {
                mIndicators[i] = createIndicator( indicatorTexts.get(i),
                        COLOR_SELECT,  indicatorTagTexts.get(i));
                //mIndicators[i].setBackgroundColor(COLOR_CLICKED);
            } else {
                mIndicators[i] = createIndicator( indicatorTexts.get(i),
                        COLOR_UNSELECT, indicatorTagTexts.get(i));
                //mIndicators[i].setBackgroundColor(COLOR_UNCLICKED);

            }

            mIndicators[i].setTag(Integer.valueOf(i));
            mIndicators[i].setOnClickListener(this);
            addView(mIndicators[i]);
        }
//
    }

    public void setIndicator(int which) {

        mIndicators[mCurIndicator].setBackgroundColor(COLOR_UNCLICKED);
        ImageView prevIcon;
        TextView prevText;
        for (int i = 0; i < mIndicators.length; i++) {

            if (i == mCurIndicator) {
//                prevIcon = (ImageView) mIndicators[mCurIndicator].findViewWithTag(indicatorTagIcons.get(mCurIndicator));
//                prevIcon.setImageResource(indicatorNormalIcons.get(i));
                prevText = (TextView) mIndicators[mCurIndicator].findViewWithTag(indicatorTagTexts.get(mCurIndicator));
                prevText.setTextColor(COLOR_UNSELECT);

            }
        }
        mIndicators[which].setBackgroundColor(COLOR_CLICKED);
        ImageView currIcon;
        TextView currText;
        for (int j = 0; j < mIndicators.length; j++) {

            if (j == which) {

//                currIcon = (ImageView) mIndicators[j].findViewWithTag(indicatorTagIcons.get(j));
//                currIcon.setImageResource(indicatorClickedIcons.get(j));
                currText = (TextView) mIndicators[j].findViewWithTag(indicatorTagTexts.get(j));
                currText.setTextColor(COLOR_SELECT);
                break;
            }
        }
        mCurIndicator = which;
    }

    public interface OnIndicateListener {

        void onIndicate(View v, int which);

    }

    public void setOnIndicateListener(OnIndicateListener listener) {
        mOnIndicateListener = listener;

    }

    public OnIndicateListener getmOnIndicateListener() {
        return mOnIndicateListener;
    }

    @Override
    public void onClick(View v) {
        if (mOnIndicateListener != null) {
            int tag = (Integer) v.getTag();
            for (int i = 0; i < mIndicators.length; i++) {
                if (tag == i && mCurIndicator != i) {
                    mOnIndicateListener.onIndicate(v, i);

                    setIndicator(i);
                    break;
                }
            }
        }
    }
}
