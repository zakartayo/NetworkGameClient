package com.example.networkgameclient.Activities.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.example.networkgameclient.Activities.Classes.UserInfo;
import com.example.networkgameclient.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class IngameActivity extends AppCompatActivity implements View.OnClickListener {
    TextView ingame_textView, ingame_user, ingame_titletext;
    EditText ingame_editText;
    Button ingame_btn_send, out, ready;
    Boolean ready_state = false;
    Socket client;
    private int count=0;
    private FrameLayout logged;
    private String ip = "1.224.133.48"; // IP
   //private String ip = "1.224.133.48"
    public int ingame_port; // PORT번호
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean status;
    private Thread thread;
    ArrayList<UserInfo> ingame_users = new ArrayList<>();
    String r_msg;
    String u_msg;

    final String LOBBY_COMMAND = "0";
    final String INGAME_COMMAND = "1";
    final String USER_LIST_ADD = "/USER_LIST_ADD";
    final String USER_LIST_REMOVE = "/USER_LIST_REMOVE";
    final String USER_CHAT = "/USER_CHAT";
    final String USER_OUT = "/USER_OUT";
    final String READY = "/READY";
    final String UNREADY = "/UNREADY";

    String login_id;
    String ingame_title;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingame);

        ingame_textView = (TextView)findViewById(R.id.ingame_textView);
        ingame_titletext = (TextView)findViewById(R.id.ingame_title);
        ingame_user = (TextView)findViewById(R.id.ingame_user);
        ingame_editText = (EditText)findViewById(R.id.ingame_editText);
        ingame_btn_send = (Button)findViewById(R.id.ingame_btn_send);
        logged = (FrameLayout)findViewById(R.id.ingameFrame);
        out = (Button)findViewById(R.id.out) ;
        ready = (Button)findViewById(R.id.ready);

        ingame_port =getIntent().getIntExtra("ingame_port", -1);
        login_id = getIntent().getStringExtra("nickname");
        ingame_title = getIntent().getStringExtra("ingame_title");

        ingame_titletext.setText(ingame_title);

        connect();

        mContext = getApplicationContext();

        ingame_btn_send.setOnClickListener(this);
        out.setOnClickListener(this);
        ready.setOnClickListener(this);
    }
    public void send_Message(String str) {
        try {
            byte[] bb;
            String s = String.format("%-128s", str);
            bb = s.getBytes("euc-kr");
            dos.write(bb, 0, 128);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void connect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = new Socket(ip, ingame_port);
                    is = client.getInputStream();
                    dis = new DataInputStream(is);
                    os = client.getOutputStream();
                    dos = new DataOutputStream(os);

                    if(count==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                send_Message(login_id+"$");
                                count=1;

                            }
                        });
                    }
                    thread = new Thread(new IngameActivity.ReceiveMsg());
                    thread.setDaemon(true);
                    thread.start();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ingame_btn_send){
            if (!(ingame_editText.getText().equals(""))) {
                String send = null;
                send = "1" + "$" + "/USER_CHAT" + "$" + "[" +login_id+ "] "
                        + ingame_editText.getText().toString();
                send_Message(send);
                ingame_editText.setText("");
            }
        }else if(v.getId()==R.id.out){
            String send = null;
            send = "1" + "$" + USER_OUT + "$";
            send_Message(send);
            //finish();
        }else if(v.getId()==R.id.ready){
            if(ready_state==false){
                ready_state = true;
                ready.setText("준비완료");
                String send = null;
                send = "1" + "$" + READY + "$";
                send_Message(send);
            }else{
                ready.setText("준비");
                ready_state = false;
                String send = null;
                send = "1" + "$" + UNREADY + "$";
                send_Message(send);
            }
        }

    }
    public void findUser(String name){
        for(int i=0; i<ingame_users.size(); i++){
            if(ingame_users.get(i).getNickname().equals(name)){
                ingame_users.remove(i);
            }
        }
    }
    Handler mHandler = new Handler() {
        @SuppressWarnings("null")
        public void handleMessage(android.os.Message msg) {
            ingame_textView.append(r_msg + "\n");
        }
    };
    Handler iuHandler = new Handler() {
        @SuppressWarnings("null")
        public void handleMessage(android.os.Message msg) {
            for(int i=0; i<ingame_users.size(); i++){
                ingame_user.append(ingame_users.get(i).getNickname() + "\n");
            }
        }
    };


    class ReceiveMsg implements Runnable {
        @SuppressWarnings("null")
        @Override
        public void run() {
            status=true;
            while(status) {
                try{

                    byte[] b = new byte[128];
                    dis.read(b);
                    String msg = new String(b, "euc-kr"); //+ "\n";

                    StringTokenizer st = new StringTokenizer(msg, "$");
                    String cursor = st.nextToken();

                    if (cursor.equals(INGAME_COMMAND)) {
                        String command = st.nextToken();
                        if (command.equals(USER_LIST_ADD)) {
                            String name = st.nextToken();
                            UserInfo user = new UserInfo(name);
                            ingame_users.add(user);
                            iuHandler.sendEmptyMessage(0);
                        } else if (command.equals(USER_LIST_REMOVE)) {
                            String name = st.nextToken();

                            if(name.equals(login_id)){
                                try {
                                    os.close();
                                    is.close();
                                    dos.close();
                                    dis.close();
                                    client.close();
                                    finish();
                                    break;
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }else{
                                findUser(name);
                                iuHandler.sendEmptyMessage(0);
                            }

                        } else if (command.equals(USER_CHAT)) {
                            r_msg = st.nextToken();
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                }
                catch(IOException e) {
                    try {
                        os.close();
                        is.close();
                        dos.close();
                        dis.close();
                        client.close();
                        break;
                    } catch (IOException e1) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}