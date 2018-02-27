package learnspy.test.injecttest.inject;

import android.app.Activity;
import android.view.View;



public class ViewFinder {
    private View mView;
    private Activity mActivity;
    public ViewFinder(View view) {
        this.mView = view;
    }

    public ViewFinder(Activity activity) {
        this.mActivity = activity;
    }

    public View findById(int viewId){
        return mActivity!=null?mActivity.findViewById(viewId):mView.findViewById(viewId);
    }



}
