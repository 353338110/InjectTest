package learnspy.test.injecttest.inject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class ViewUtils {

    public static void inject(Activity activity) {
        inject(new ViewFinder(activity), activity);
    }

    public static void inject(View view) {
        inject(new ViewFinder(view), view);
    }

    public static void inject(View view, Object o) {
        inject(new ViewFinder(view), o);
    }


    private static void inject(ViewFinder finder, Object o) {
        injectLayout(o);
        injectFiled(finder, o);
        injectEvent(finder, o);
    }

    private static void injectLayout(Object o) {
        Class<?> clazz = o.getClass();
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView != null) {
            int layoutId = contentView.value();
            try {
                Method method = clazz.getMethod("setContentView", int.class);
                method.setAccessible(true);
                method.invoke(o, layoutId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void injectFiled(ViewFinder finder, Object o) {
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            ViewById viewById = field.getAnnotation(ViewById.class);
            if (viewById != null) {
                Log.d("tag", "1");
                int viewId = viewById.value();

                View view = finder.findById(viewId);

                field.setAccessible(true);
                try {
                    field.set(o, view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private static void injectEvent(ViewFinder finder, Object o) {
        Class<?> clazz = o.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            OnClick onClick = method.getAnnotation(OnClick.class);

            if (onClick != null) {
                int[] value = onClick.value();

                if (value != null) {
                    for (int i : value) {
                        View view = finder.findById(i);
                        boolean isCheckNet = method.getAnnotation(CheckNet.class) != null;
                        boolean isCheckRepeat = method.getAnnotation(CheckRepeat.class)!= null;
                        view.setOnClickListener(new DeclaredOnClickListener(method, o, isCheckNet,isCheckRepeat));

                    }
                }

            }
        }
    }

    private static class DeclaredOnClickListener implements View.OnClickListener {
        private Method mMethod;
        private Object mObject;
        private boolean mIsCheckNet;
        private boolean mIsCheckRepeat;
        private long mLastTime;
        private long time;
        private DeclaredOnClickListener(Method method, Object o, boolean isCheckNet, boolean isCheckRepeat) {
            this.mMethod = method;
            this.mObject = o;
            this.mIsCheckNet = isCheckNet;
            this.mIsCheckRepeat = isCheckRepeat;
        }

        @Override
        public void onClick(View v) {

            try {
                if (mIsCheckNet){
                    if (!isNetworkConnected(v.getContext())){
                        Toast.makeText(v.getContext(),"请检查网络状态",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (mIsCheckRepeat){
                    if ((System.currentTimeMillis()-mLastTime)<1000&&mLastTime!=0){
                        Toast.makeText(v.getContext(),"点击过快",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                mMethod.setAccessible(true);

                mMethod.invoke(mObject, v);

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (mIsCheckNet){
                        if (!isNetworkConnected(v.getContext())){
                            Toast.makeText(v.getContext(),"请检查网络状态",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (mIsCheckRepeat){

                        if ((System.currentTimeMillis()-mLastTime)<1000&&mLastTime!=0){
                            Toast.makeText(v.getContext(),"点击过快",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    mMethod.invoke(mObject, null);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            mLastTime = System.currentTimeMillis();
        }
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


}
