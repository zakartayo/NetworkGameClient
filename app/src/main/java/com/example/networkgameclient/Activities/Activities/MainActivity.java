package com.example.networkgameclient.Activities.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.networkgameclient.Activities.Adapter.MyAdapter;
import com.example.networkgameclient.Activities.Classes.Item;
import com.example.networkgameclient.Activities.Classes.UserInfo;
import com.example.networkgameclient.R;

public class MainActivity extends AppCompatActivity  implements OnClickListener{

    Button btn;
    TextView textView, userView;
    EditText editText, nickName;
    Button btn_send;
    Socket client;
    private int count=0;
    private FrameLayout logged;
    private String ip = "192.168.43.176"; // IP
    private int main_port = 30000; // PORT번호
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean status;
    private Thread thread;
    ArrayList<Item> items = new ArrayList<>();
    ArrayList<UserInfo> users = new ArrayList<>();
    String r_msg;
    String u_msg;

    final String LOBBY_COMMAND = "0";
    final String INGAME_COMMAND = "1";
    final String USER_LIST_ADD = "/USER_LIST_ADD";
    final String USER_LIST_REMOVE = "/USER_LIST_REMOVE";
    final String USER_CHAT = "/USER_CHAT";
    final String ROOM_LIST_ADD = "/ROOM_LIST_ADD";
    final String ROOM_LIST_REMOVE = "/ROOM_LIST_REMOVE";
    final String MAKE_ROOM = "/MAKE_ROOM";

    String login_id;

    Context mContext;
    RecyclerView recyclerView;
    RecyclerView.Adapter Adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        userView = (TextView)findViewById(R.id.users);
        editText = (EditText)findViewById(R.id.editText);
        btn_send = (Button)findViewById(R.id.btn_send);
        logged = (FrameLayout)findViewById(R.id.logged);

        login_id = getIntent().getStringExtra("nickname");
        connect();

        mContext = getApplicationContext();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,2);

        recyclerView.setLayoutManager(layoutManager);


        btn_send.setOnClickListener(this);
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
    public void getRoomInfo(){
        send_Message("getRoomInfo");
    }
    public void connect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = new Socket(ip, main_port);
                    is = client.getInputStream();
                    dis = new DataInputStream(is);
                    os = client.getOutputStream();
                    dos = new DataOutputStream(os);

                    if(count==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                send_Message(login_id);
                                count=1;

                            }
                        });
                    }
                    thread = new Thread(new MainActivity.ReceiveMsg());
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
        if(v.getId()==R.id.btn_send){
            if (!(editText.getText().equals(""))) {
                String send = null;
                send = "0" + "$" + "/USER_CHAT" + "$" + "[" +login_id+ "] "
                        + editText.getText().toString();
                send_Message(send);
                editText.setText("");
            }
        }

    }
    public void findUser(String name){
        for(int i=0; i<users.size(); i++){
            if(users.get(i).getNickname().equals(name)){
                users.remove(i);
            }
        }
    }
    Handler mHandler = new Handler() {
        @SuppressWarnings("null")
        public void handleMessage(android.os.Message msg) {
            //System.out.println("##"+r_msg);
            textView.append(r_msg + "\n");
            //if(!m.getId().equals(login_id))
            //if (!r_msg.contains(login_id))
            //newNotification(r_msg);

        }
    };
    Handler uHandler = new Handler() {
        @SuppressWarnings("null")
        public void handleMessage(android.os.Message msg) {
            for(int i=0; i<users.size(); i++){
                userView.append(users.get(i).getNickname() + "\n");
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

                    if (cursor.equals(LOBBY_COMMAND)) {
                        String command = st.nextToken();
                        if (command.equals(USER_LIST_ADD)) {
                            String name = st.nextToken();
                            UserInfo user = new UserInfo(name);
                            users.add(user);
                            uHandler.sendEmptyMessage(0);
                        } else if (command.equals(USER_LIST_REMOVE)) {
                            findUser(st.nextToken());
                            uHandler.sendEmptyMessage(0);
                        } else if (command.equals(USER_CHAT)) {
                            r_msg = st.nextToken();
                            mHandler.sendEmptyMessage(0);
                        } else if (command.equals(ROOM_LIST_ADD)) {

                        }
                    }

                    /*if (token.equals("/first")) {

                        for(int i=0; i<10; i++){
                            if(token!=null){
                                token = st.nextToken();
                                Item item = new Item();
                                item.setRoomState(token);

                                token = st.nextToken();
                                item.setState(token);

                                token = st.nextToken();
                                item.setRoomNum(token);

                                token = st.nextToken();
                                item.setNumCount(token);

                                items.add(item);
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Adapter = new MyAdapter(items,mContext);
                                recyclerView.setAdapter(Adapter);
                            }
                        });

                        continue;
                    }*/

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