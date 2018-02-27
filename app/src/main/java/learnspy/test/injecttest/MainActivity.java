package learnspy.test.injecttest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import learnspy.test.injecttest.inject.ContentView;
import learnspy.test.injecttest.inject.OnClick;
import learnspy.test.injecttest.inject.ViewById;
import learnspy.test.injecttest.inject.ViewUtils;
@ContentView(R.layout.activity_main)

public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.button)
    private Button button;
    @ViewById(R.id.tv)
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
    }


    @OnClick({R.id.tv,R.id.button})
    private void onClick(View view){
       // Toast.makeText(MainActivity.this,"tv",Toast.LENGTH_SHORT).show();

        switch (view.getId()){
            case R.id.tv:
                Toast.makeText(MainActivity.this,"tv",Toast.LENGTH_SHORT).show();

                break;
            case R.id.button:
                Toast.makeText(MainActivity.this,"button",Toast.LENGTH_SHORT).show();

                break;
        }
    }
}
