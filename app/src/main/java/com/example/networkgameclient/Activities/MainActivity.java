package com.example.networkgameclient.Activities;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networkgameclient.Classes.ClientThread;
import com.example.networkgameclient.R;

public class MainActivity extends AppCompatActivity  implements OnClickListener{

    Button btn;
    TextView textView;
    EditText editText, nickName;
    Button btn_send;
    Socket client;
    private int count=0;
    private FrameLayout logged, not_logged;
    private String ip = "192.168.219.105"; // IP
    private int port = 30000; // PORT번호

    Thread thread;
    ClientThread clientThread;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button)findViewById(R.id.btn_login);

        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
        nickName = (EditText)findViewById(R.id.nickName);
        btn_send = (Button)findViewById(R.id.btn_send);
        logged = (FrameLayout)findViewById(R.id.logged);
        not_logged = (FrameLayout)findViewById(R.id.not_logged);

        logged.setVisibility(View.GONE);

        handler = new Handler(){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                textView.append(bundle.getString("msg")+"\n");
            }
        };
        btn.setOnClickListener(this);
        btn_send.setOnClickListener(this);
    }
    public void connect(){
        thread = new Thread(){
            public void run() {
                super.run();
                try {
                    client = new Socket(ip, port);
                    clientThread = new ClientThread(client, handler);
                    clientThread.start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    if(count==0){
                                        clientThread.send(nickName.getText().toString());
                                        not_logged.setVisibility(View.GONE);
                                        logged.setVisibility(View.VISIBLE);
                                        count=1;
                                    }
                                }
                            });
                        }
                    }).start();

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
    @Override

    public void onClick(View v) {
        if(v.getId()==R.id.btn_login){
            connect();
        }
        if(v.getId()==R.id.btn_send){
            clientThread.send(editText.getText().toString());
            editText.setText("");
        }

    }

}
