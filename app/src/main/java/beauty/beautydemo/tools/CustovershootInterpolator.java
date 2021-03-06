package beauty.beautydemo.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import beauty.beautydemo.R;

/**
 * Created by LJW on 15/3/29.
 */
public class CustovershootInterpolator implements Interpolator {

    private final float mTension;

    public CustovershootInterpolator() {
        mTension = 2.0f;
    }

    /**
     * @param tension Amount of overshoot. When tension equals 0.0f, there is
     *                no overshoot and the interpolator becomes a simple
     *                deceleration interpolator.
     */
    public CustovershootInterpolator(float tension) {
        mTension = tension;
    }



    public float getInterpolation(float t) {
        // _o(t) = t * t * ((tension + 1) * t + tension)
        // o(t) = _o(t - 1) + 1
        t -= 1.0f;
        return t * t * ((mTension + 1) * t + mTension) + 1.0f;
    }
}
