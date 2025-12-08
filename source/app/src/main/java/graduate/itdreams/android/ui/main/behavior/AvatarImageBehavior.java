package graduate.itdreams.android.ui.main.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;

public class AvatarImageBehavior extends CoordinatorLayout.Behavior<View> {

    private float mStartYPosition;
    private float mStartHeight;
    private float mFinalYPosition;
    private float mStartXPosition;
    private float mFinalXPosition;
    private float mFinalHeight;

    public AvatarImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        initProperties(child, dependency);

        final float currentScrollPercentage = dependency.getY() / ((AppBarLayout) dependency).getTotalScrollRange();

        float distanceYToSubtract = (mStartYPosition - mFinalYPosition) * (1f - currentScrollPercentage);
        float distanceXToSubtract = (mStartXPosition - mFinalXPosition) * (1f - currentScrollPercentage);
        float heightToSubtract = (mStartHeight - mFinalHeight) * (1f - currentScrollPercentage);

        child.setY(mStartYPosition - distanceYToSubtract);
        child.setX(mStartXPosition - distanceXToSubtract);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.width = (int) (mStartHeight - heightToSubtract);
        lp.height = (int) (mStartHeight - heightToSubtract);
        child.setLayoutParams(lp);

        return true;
    }

    private void initProperties(final View child, final View dependency) {
        if (mStartYPosition == 0)
            mStartYPosition = child.getY();

        if (mFinalYPosition == 0)
            mFinalYPosition = dependency.getHeight() / 2f;

        if (mStartHeight == 0)
            mStartHeight = child.getHeight();

        if (mFinalHeight == 0)
            mFinalHeight = mStartHeight / 2;

        if (mStartXPosition == 0)
            mStartXPosition = child.getX();

        if (mFinalXPosition == 0)
            mFinalXPosition = child.getX() / 2;
    }
}
