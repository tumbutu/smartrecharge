package aaronichie.smartrecharge;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by aaronichie on 6/15/2017.
 */

public class FocusBoxView extends View {

    private static final int MIN_FOCUS_BOX_WIDTH = 50;
    private static final int MIN_FOCUS_BOX_HEIGHT = 20;

    private final Paint paint;
    private final int maskColor;
    private final int frameColor;
    private final int cornerColor;

    public FocusBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();

        maskColor = resources.getColor(R.color.focus_box_mask);
        frameColor = resources.getColor(R.color.focus_box_frame);
        cornerColor = resources.getColor(R.color.focus_box_corner);

        this.setOnTouchListener(getTouchListener());
    }

    private Rect box;

    private static Point ScrRes;

    private  Rect getBoxRect() {

        if (box == null) {

            ScrRes = FocusBoxUtils.getScreenResolution(getContext());

            int width = ScrRes.x * 6 / 7;
            int height = ScrRes.y / 9;

            width = width == 0
                    ? MIN_FOCUS_BOX_WIDTH
                    : width < MIN_FOCUS_BOX_WIDTH ? MIN_FOCUS_BOX_WIDTH : width;

            height = height == 0
                    ? MIN_FOCUS_BOX_HEIGHT
                    : height < MIN_FOCUS_BOX_HEIGHT ? MIN_FOCUS_BOX_HEIGHT : height;

            int left = (ScrRes.x - width) / 2;
            int top = (ScrRes.y - height) / 2;

            box = new Rect(left, top, left + width, top + height);
        }

        return box;
    }

    public Rect getBox() {
        return box;
    }

    private void updateBoxRect(int dW, int dH) {

        int newWidth = (box.width() + dW > ScrRes.x - 4 || box.width() + dW < MIN_FOCUS_BOX_WIDTH)
                ? 0
                : box.width() + dW;

        int newHeight = (box.height() + dH > ScrRes.y - 4 || box.height() + dH < MIN_FOCUS_BOX_HEIGHT)
                ? 0
                : box.height() + dH;

        int leftOffset = (ScrRes.x - newWidth) / 2;

        int topOffset = (ScrRes.y - newHeight) / 2;

        if (newWidth < MIN_FOCUS_BOX_WIDTH || newHeight < MIN_FOCUS_BOX_HEIGHT)
            return;

        box = new Rect(leftOffset, topOffset, leftOffset + newWidth, topOffset + newHeight);
    }

    private OnTouchListener touchListener;

    private OnTouchListener getTouchListener() {

        if (touchListener == null)
            touchListener = new OnTouchListener() {

                int lastX = -1;
                int lastY = -1;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastX = -1;
                            lastY = -1;
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            int currentX = (int) event.getX();
                            int currentY = (int) event.getY();
                            try {
                                Rect rect = getBoxRect();
                                final int BUFFER = 50;
                                final int BIG_BUFFER = 60;
                                if (lastX >= 0) {
                                    if (((currentX >= rect.left - BIG_BUFFER
                                            && currentX <= rect.left + BIG_BUFFER)
                                            || (lastX >= rect.left - BIG_BUFFER
                                            && lastX <= rect.left + BIG_BUFFER))
                                            && ((currentY <= rect.top + BIG_BUFFER
                                            && currentY >= rect.top - BIG_BUFFER)
                                            || (lastY <= rect.top + BIG_BUFFER
                                            && lastY >= rect.top - BIG_BUFFER))) {
                                        updateBoxRect(2 * (lastX - currentX),
                                                2 * (lastY - currentY));
                                    } else if (((currentX >= rect.right - BIG_BUFFER
                                            && currentX <= rect.right + BIG_BUFFER)
                                            || (lastX >= rect.right - BIG_BUFFER
                                            && lastX <= rect.right + BIG_BUFFER))
                                            && ((currentY <= rect.top + BIG_BUFFER
                                            && currentY >= rect.top - BIG_BUFFER)
                                            || (lastY <= rect.top + BIG_BUFFER
                                            && lastY >= rect.top - BIG_BUFFER))) {
                                        // Top right corner: adjust both top and right sides
                                        updateBoxRect(2 * (currentX - lastX),
                                                2 * (lastY - currentY));
                                    } else if (((currentX >= rect.left - BIG_BUFFER
                                            && currentX <= rect.left + BIG_BUFFER)
                                            || (lastX >= rect.left - BIG_BUFFER
                                            && lastX <= rect.left + BIG_BUFFER))
                                            && ((currentY <= rect.bottom + BIG_BUFFER
                                            && currentY >= rect.bottom - BIG_BUFFER)
                                            || (lastY <= rect.bottom + BIG_BUFFER
                                            && lastY >= rect.bottom - BIG_BUFFER))) {
                                        // Bottom left corner: adjust both bottom and left sides
                                        updateBoxRect(2 * (lastX - currentX),
                                                2 * (currentY - lastY));
                                    } else if (((currentX >= rect.right - BIG_BUFFER
                                            && currentX <= rect.right + BIG_BUFFER)
                                            || (lastX >= rect.right - BIG_BUFFER
                                            && lastX <= rect.right + BIG_BUFFER))
                                            && ((currentY <= rect.bottom + BIG_BUFFER
                                            && currentY >= rect.bottom - BIG_BUFFER)
                                            || (lastY <= rect.bottom + BIG_BUFFER
                                            && lastY >= rect.bottom - BIG_BUFFER))) {
                                        // Bottom right corner: adjust both bottom and right sides
                                        updateBoxRect(2 * (currentX - lastX),
                                                2 * (currentY - lastY));
                                    } else if (((currentX >= rect.left - BUFFER
                                            && currentX <= rect.left + BUFFER)
                                            || (lastX >= rect.left - BUFFER
                                            && lastX <= rect.left + BUFFER))
                                            && ((currentY <= rect.bottom
                                            && currentY >= rect.top)
                                            || (lastY <= rect.bottom
                                            && lastY >= rect.top))) {
                                        // Adjusting left side: event falls within BUFFER pixels of
                                        // left side, and between top and bottom side limits
                                        updateBoxRect(2 * (lastX - currentX), 0);
                                    } else if (((currentX >= rect.right - BUFFER
                                            && currentX <= rect.right + BUFFER)
                                            || (lastX >= rect.right - BUFFER
                                            && lastX <= rect.right + BUFFER))
                                            && ((currentY <= rect.bottom
                                            && currentY >= rect.top)
                                            || (lastY <= rect.bottom
                                            && lastY >= rect.top))) {
                                        // Adjusting right side: event falls within BUFFER pixels of
                                        // right side, and between top and bottom side limits
                                        updateBoxRect(2 * (currentX - lastX), 0);
                                    } else if (((currentY <= rect.top + BUFFER
                                            && currentY >= rect.top - BUFFER)
                                            || (lastY <= rect.top + BUFFER
                                            && lastY >= rect.top - BUFFER))
                                            && ((currentX <= rect.right
                                            && currentX >= rect.left)
                                            || (lastX <= rect.right
                                            && lastX >= rect.left))) {
                                        // Adjusting top side: event falls within BUFFER pixels of
                                        // top side, and between left and right side limits
                                        updateBoxRect(0, 2 * (lastY - currentY));
                                    } else if (((currentY <= rect.bottom + BUFFER
                                            && currentY >= rect.bottom - BUFFER)
                                            || (lastY <= rect.bottom + BUFFER
                                            && lastY >= rect.bottom - BUFFER))
                                            && ((currentX <= rect.right
                                            && currentX >= rect.left)
                                            || (lastX <= rect.right
                                            && lastX >= rect.left))) {
                                        updateBoxRect(0, 2 * (currentY - lastY));
                                    }
                                }
                            } catch (NullPointerException e) {
                            }
                            v.invalidate();
                            lastX = currentX;
                            lastY = currentY;
                            return true;
                        case MotionEvent.ACTION_UP:
                            lastX = -1;
                            lastY = -1;
                            return true;
                    }
                    return false;
                }
            };

        return touchListener;
    }

    @Override
    public void onDraw(Canvas canvas) {

        Rect frame = getBoxRect();

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        paint.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        paint.setAlpha(0);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(frameColor);
        canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
        canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
        canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
        canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

        paint.setColor(cornerColor);
        canvas.drawCircle(frame.left - 32, frame.top - 32, 32, paint);
        canvas.drawCircle(frame.right + 32, frame.top - 32, 32, paint);
        canvas.drawCircle(frame.left - 32, frame.bottom + 32, 32, paint);
        canvas.drawCircle(frame.right + 32, frame.bottom + 32, 32, paint);

    }
}
