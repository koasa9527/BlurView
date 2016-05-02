package eightbitlab.com.blurview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * FrameLayout that blurs its underlying content.
 * Can have children and draw them over blurred background.
 *
 * Must have {@link BlurController} to be set to work properly
 */
public class BlurView extends FrameLayout {
    private static final String TAG = BlurView.class.getSimpleName();

    protected BlurController blurController;

    @ColorInt
    private int overlayColor;

    public BlurView(Context context) {
        super(context);
        init(null, 0);
    }

    public BlurView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BlurView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        createStubController();
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BlurView, defStyleAttr, 0);
        int defaultColor = ContextCompat.getColor(getContext(), android.R.color.transparent);
        overlayColor = a.getColor(R.styleable.BlurView_overlayColor, defaultColor);
        a.recycle();

        //we need to draw even without background set
        setWillNotDraw(false);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!blurController.isInternalCanvas(canvas)) {
            blurController.drawBlurredContent(canvas);
            drawColorOverlay(canvas);
            super.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        blurController.updateBlurViewSize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        blurController.onDrawEnd(canvas);
    }

    protected void drawColorOverlay(Canvas canvas) {
        canvas.drawColor(overlayColor);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        blurController.destroy();
    }

    public void setBlurController(@NonNull BlurController blurController) {
        this.blurController = blurController;
    }

    /**
     * Sets the color overlay to be drawn on top of blurred content
     * @param overlayColor int color
     */
    public void setOverlayColor(@ColorInt int overlayColor) {
        this.overlayColor = overlayColor;
        invalidate();
    }

    /**
     * Used in edit mode and in case if no BlurController was set
     */
    private void createStubController() {
        blurController = new BlurController() {
            @Override
            public boolean isInternalCanvas(Canvas canvas) {
                return false;
            }

            @Override
            public void drawBlurredContent(Canvas canvas) {}

            @Override
            public void updateBlurViewSize() {}

            @Override
            public void onDrawEnd(Canvas canvas) {}

            @Override
            public void updateBlur() {}

            @Override
            public void destroy() {}
        };
    }
}
