package zhq.com.itemmovedelete;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Author:  张慧强
 * Version:  1.0
 * Date:    2018/1/28 0028
 * Modify:
 * Description: //TODO
 * Copyright notice:
 */
public class ItemMoveDeleteLayout extends FrameLayout {
    public ItemMoveDeleteLayout(@NonNull Context context) {
        this(context,null);
    }

    public ItemMoveDeleteLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    View content,delete;
    ViewDragHelper dragHelper;
    public ItemMoveDeleteLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper=ViewDragHelper.create(ItemMoveDeleteLayout.this,callBack);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        content=getChildAt(0);
        delete=getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        content.layout(0,0,content.getMeasuredWidth(),content.getMeasuredHeight());
        delete.layout(content.getMeasuredWidth(),0,content.getMeasuredWidth()+delete.getMeasuredWidth(),delete.getMeasuredHeight());

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让ViewGragHelper来帮助判断是否要拦截
        boolean result = dragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    float downX, downY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - downX;
                float dy = event.getY() - downY;
                //通过判断用户手指滑动的方向来猜测到底是要滑动ListView还是ItemMoveDeleteLayout
                //如果是偏向于垂直，就是滑动ListView，如果偏向于水平，就是滑动条目
                if(Math.abs(dx) > Math.abs(dy)){
                    //说明是横向滑动，那么就要请求父View不要拦截
                    //直接让父View不在拦截
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        //让dragHelper帮助我们处理触摸事件
        dragHelper.processTouchEvent(event);
        return true;
    }

    ViewDragHelper.Callback callBack=new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==content||child==delete;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }



        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //限制content的范围
            if(child==content){
                if(left>0){
                    left = 0;
                }else if(left<-delete.getMeasuredWidth()){
                    left = -delete.getMeasuredWidth();
                }
            }else if(child==delete){
                //限制delete的范围
                if(left < (content.getMeasuredWidth()-delete.getMeasuredWidth())){
                    left = content.getMeasuredWidth()-delete.getMeasuredWidth();
                }else if(left > content.getMeasuredWidth()){
                    left = content.getMeasuredWidth();
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //当content位置改变了，手动让delete也移动
            if(changedView==content){
                delete.offsetLeftAndRight(dx);
            }else if(changedView==delete){
                //让content也移动
                content.offsetLeftAndRight(dx);
            }

            //回调接口的方法
            if(listener!=null){
                //说明ItemMoveDeleteLayout打开了
                if(content.getLeft()==-delete.getMeasuredWidth()){
                    listener.onOpen(ItemMoveDeleteLayout.this);
                }else if(content.getLeft()==0){
                    //说明ItemMoveDeleteLayout关闭了
                    listener.onClose(ItemMoveDeleteLayout.this);
                }
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if(content.getLeft() < -delete.getMeasuredWidth()/2){
                //open
                open();
            }else {
                //close
                close();
            }
        }
    };
    //关闭
    public void close() {
        dragHelper.smoothSlideViewTo(content, 0, 0);
        ViewCompat.postInvalidateOnAnimation(ItemMoveDeleteLayout.this);
    }
     //打开
    public void open() {
        dragHelper.smoothSlideViewTo(content, -delete.getMeasuredWidth(), 0);
        ViewCompat.postInvalidateOnAnimation(ItemMoveDeleteLayout.this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(dragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(ItemMoveDeleteLayout.this);
        }
    }

    OnSwipeListener listener;
    public void setOnSwipeListener(OnSwipeListener listener){
        this.listener = listener;
    }
    public interface OnSwipeListener{
        void onOpen(ItemMoveDeleteLayout openLayout);
        void onClose(ItemMoveDeleteLayout closeLayout);
    }

}
