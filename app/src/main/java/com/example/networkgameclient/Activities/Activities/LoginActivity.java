package com.example.networkgameclient.Activities.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networkgameclient.Activities.Classes.Item;
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

public class LoginActivity extends AppCompatActivity {

    Button login_btn, usedCheck;
    EditText  nickName;
    Socket client;
    private int count=0;
    private FrameLayout not_logged;
    private String ip = "1.224.133.48"; // IP
    private int login_port = 29999; // PORT번호
    public InputStream is;
    public OutputStream os;
    public DataInputStream dis;
    public DataOutputStream dos;
    private boolean status, check;
    private Thread thread;
    ArrayList<String> users = new ArrayList<>();
    String login_id=null;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_btn = (Button)findViewById(R.id.login_btn);
        usedCheck = (Button)findViewById(R.id.usedCheck);
        nickName = (EditText)findViewById(R.id.loginNick);

        mContext = getApplicationContext();

        check=false;
        connect();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check==true){
                    login_id = nickName.getText().toString();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("nickname", login_id);
                    nickName.setText("");

                    try {
                        //if(os!=null)
                            os.close();
                       // if(is!=null)
                            is.close();
                        //if(dos!=null)
                            dos.close();
                        //if(dis!=null)
                            dis.close();

                        client.close();

                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    startActivity(intent);
                }else{
                    Toast.makeText(mContext, "중복체크를 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        usedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_id = nickName.getText().toString();

                for(int i=0; i<users.size(); i++){
                    if(users.get(i).equals(login_id)){
                        Toast.makeText(mContext, users.get(i), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if(!login_id.equals("")){
                    check=true;
                    Toast.makeText(mContext, "가입이 가능한 아이디입니다", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    client = new Socket(ip, login_port);
                    is = client.getInputStream();
                    dis = new DataInputStream(is);
                    os = client.getOutputStream();
                    dos = new DataOutputStream(os);

                    thread = new Thread(new LoginActivity.ReceiveMsg());
                    thread.setDaemon(true);
                    thread.start();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

    }

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
                    users.add(msg);

                }
                catch(IOException e) {
                    //e.printStackTrace();
                    //status = false;
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