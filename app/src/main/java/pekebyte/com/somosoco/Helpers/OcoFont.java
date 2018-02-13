package pekebyte.com.somosoco.Helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by pedromolina on 2/12/18.
 */

public class OcoFont extends AppCompatTextView {

    private static Typeface sMaterialDesignIcons;

    public OcoFont(Context context) {
        this(context, null);
    }

    public OcoFont(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OcoFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) return;//Won't work in Eclipse graphical layout
        setTypeface();
    }

    private void setTypeface() {
        if (sMaterialDesignIcons == null) {
            sMaterialDesignIcons = Typeface.createFromAsset(getContext().getAssets(), "fonts/GillSansUltraBold.ttf");
        }
        setTypeface(sMaterialDesignIcons);
    }
}
