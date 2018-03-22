package com.example.opopkk.embedded;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by opopkk on 2017-11-19.
 */

public class UserId  extends AppCompatActivity {

    void finish_this_acitivity(int resutl_value){
        Intent result_intent = new Intent();
        result_intent.putExtra("id", resutl_value);
        result_intent.putExtra("index", now_index);
        setResult(1, result_intent);
        finish();
    }

    EditText et_id;
    int now_index;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_id);

        Intent intent = new Intent(this.getIntent());
        int get_index = intent.getIntExtra("index", -1);
//        Log.e("qweqwe", String.valueOf(get_index));
        now_index = get_index;
        et_id = (EditText) findViewById(R.id.et_id) ;

        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        Button btn_cancle = (Button) findViewById(R.id.btn_cancle);

        btn_ok.setOnClickListener(new btn_listener());
        btn_cancle.setOnClickListener(new btn_listener());
    }

    class btn_listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.btn_ok){
                finish_this_acitivity(Integer.valueOf(et_id.getText().toString()));
            }else if(view.getId() == R.id.btn_cancle){
                finish_this_acitivity(-1);
            }
        }
    }
}
