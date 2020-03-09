package com.yisan.minaclient;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.yisan.minaclient.event.MinaMessageFormServerEvent;
import com.yisan.minaclient.mina.MinaService;
import com.yisan.minaclient.mina.SessionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Mina-Android客户端
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvDesc;
    private EditText etInput;

    /**
     * 接受Mina服务端的消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMinaMessageFormServerEvent(MinaMessageFormServerEvent event) {
        String message = event.message;
        tvDesc.append(message + "\n");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        etInput = findViewById(R.id.et_input);
        tvDesc = findViewById(R.id.tv_desc);
        findViewById(R.id.btn_connect_mina).setOnClickListener(this);
        findViewById(R.id.btn_send).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send) {
            String input = etInput.getText().toString().trim();
            if (!TextUtils.isEmpty(input)) {
                //发送数据
                SessionManager.getInstance().writeToServer(input);
            } else {
                Toast.makeText(this, "输入的不能为空", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btn_connect_mina) {
            //连接mina服务器
            Intent intent = new Intent(this, MinaService.class);
            startService(intent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(new Intent(this, MinaService.class));
    }

}
