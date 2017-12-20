package com.example.networkgameclient.Activities.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networkgameclient.Activities.Classes.Item;
import com.example.networkgameclient.Activities.Classes.UserInfo;
import com.example.networkgameclient.R;

public class MainActivity extends AppCompatActivity  implements OnClickListener{

    Button btn, make_room, exit, btn_send;
    TextView textView, userView;
    EditText editText;
    ImageView renew;
    Socket client;
    private int count=0;
    private String ip = "1.224.133.48"; // IP
    private int main_port = 30000; // PORT번호
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean status;
    private Thread thread;
    public  ArrayList<Item> items = new ArrayList<>();
    public ArrayList<UserInfo> users = new ArrayList<>();
    public String r_msg, login_id, makeRoomMSG, passwordCheck, stateCongif, getRoomInfo;
    public int clickPosition;


    final String LOBBY_COMMAND = "0";
    final String USER_LIST_ADD = "/USER_LIST_ADD";
    final String USER_LIST_REMOVE = "/USER_LIST_REMOVE";
    final String USER_CHAT = "/USER_CHAT";
    final String ROOM_INFO = "/ROOM_INFO";
    final String MAKE_ROOM = "/MAKE_ROOM";
    final String PW_CHECK = "/PW_CHECK";
    final String STATE_CHECK = "/STATE_CHECK";
    final String GET_ROOMINFO = "/GET_ROOMINFO";
    final String USER_OUT = "/USER_OUT";


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
        make_room = (Button)findViewById(R.id.make_room);
        exit = (Button)findViewById(R.id.exit);
        renew = (ImageView)findViewById(R.id.renewal);

        login_id = getIntent().getStringExtra("nickname");
        connect();

        mContext = getApplicationContext();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        for(int i=0; i<10; i++){
            Item item = new Item(i, "", false, 0);
            items.add(item);
        }


        layoutManager = new GridLayoutManager(this,2);

        recyclerView.setLayoutManager(layoutManager);

        btn_send.setOnClickListener(this);
        make_room.setOnClickListener(this);
        renew.setOnClickListener(this);
        exit.setOnClickListener(this);
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
                                send_Message(login_id+"$");
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
        }else if(v.getId()==R.id.make_room){

            LayoutInflater inflater=getLayoutInflater();
            final View dialogView= inflater.inflate(R.layout.dialog_view, null);

            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);

            ad.setTitle("방제목을 입력하세요");     // 내용 설정
            ad.setView(dialogView);

            final EditText title = dialogView.findViewById(R.id.title);
            final EditText password = dialogView.findViewById(R.id.password);
            final CheckBox checkBox = dialogView.findViewById(R.id.check);

            password.setEnabled(false);
            password.setClickable(false);

            checkBox.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox)v).isChecked()) {
                        password.setEnabled(true);
                        password.setClickable(true);
                    } else {
                        password.setEnabled(false);
                        password.setClickable(false);
                    }
                }
            }) ;

            ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (checkBox.isChecked()) {
                        if(password.getText().toString().equals(null)){
                            Toast.makeText(MainActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                        }
                        else if(!password.getText().toString().equals(null)){
                            makeRoomMSG = "0" + "$" + MAKE_ROOM + "$" + title.getText().toString() + "$" + password.getText().toString() + "$" + true ;
                            send_Message(makeRoomMSG);
                            dialog.dismiss();
                        }
                    } else {
                        if(title.getText().toString().equals("")){
                            Toast.makeText(MainActivity.this, "방제목을 입력하세요", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            makeRoomMSG = "0" + "$" + MAKE_ROOM + "$" + title.getText().toString() + "$" + "nopass" + "$";
                            send_Message(makeRoomMSG);
                            dialog.dismiss();
                        }
                    }
                }
            });
            ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();     //닫기
                }
            });
            ad.show();

        }
        else if(v.getId()==R.id.exit){
            String send = null;
            send = "0" + "$" + USER_OUT + "$";
            send_Message(send);

            /*
            try {
                os.close();
                is.close();
                dos.close();
                dis.close();
                client.close();
                finish();
            } catch (IOException e3) {
                e3.printStackTrace();
            }*/
        }
        else if(v.getId()==R.id.renewal){
            getRoomInfo = "0" + "$" +GET_ROOMINFO+ "$";
            send_Message(getRoomInfo);
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
    public Item refactorItem(StringTokenizer st, int roomNum){
        String token = "";

        Item item = new Item();
        item.setRoomNum(roomNum);

        token = st.nextToken();
        item.setRoomState(token);

        token = st.nextToken();
        item.setAvailable(Boolean.parseBoolean(token));

        token = st.nextToken();
        item.setNumCount(Integer.parseInt(token));

        token = st.nextToken();
        item.setPort(Integer.parseInt(token));

        token = st.nextToken();
        item.setTitle(token);

        token = st.nextToken();
        item.setLock(Boolean.parseBoolean(token));

        return item;
    }
    public void setRoom(String roomInfo){
        StringTokenizer st = new StringTokenizer(roomInfo, "@");
        int roomToken = Integer.parseInt(st.nextToken());

        switch (roomToken){
            case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:

                    Item imsi = refactorItem(st, roomToken);
                    items.set(roomToken,imsi);
                break;
            default:
                break;
        }
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
                            String name = st.nextToken();
                            findUser(name);
                            uHandler.sendEmptyMessage(0);

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
                            }
                        } else if (command.equals(USER_CHAT)) {
                            r_msg = st.nextToken();
                            mHandler.sendEmptyMessage(0);
                        } else if (command.equals(ROOM_INFO)) {

                            setRoom(st.nextToken());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Adapter = new MyAdapter(items,mContext);
                                    recyclerView.setAdapter(Adapter);

                                }
                            });
                        }else if (command.equals(GET_ROOMINFO)) {
                            setRoom(st.nextToken());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for(int i=0; i<10; i++){
                                        Adapter.notifyItemChanged(i);
                                    }

                                }
                            });
                        }
                        else if (command.equals(MAKE_ROOM)){
                            Intent intent = new Intent(MainActivity.this, IngameActivity.class);
                            int make_game_port = Integer.parseInt(st.nextToken());
                            intent.putExtra("ingame_port", make_game_port);

                            String ingame_title = st.nextToken();
                            intent.putExtra("ingame_title", ingame_title);

                            intent.putExtra("nickname", login_id);

                            startActivity(intent);
                        } else if(command.equals(PW_CHECK)){
                            String result = st.nextToken();
                            Log.d("result", result);
                            if(result.equals("ok")){
                                stateCongif = "0" + "$" +STATE_CHECK + "$" + clickPosition + "$";
                                send_Message(stateCongif);
                            }else if(result.equals("nok")){
                                Handler mHandler = new Handler(Looper.getMainLooper());
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }, 0);
                            }
                        }
                        else if(command.equals(STATE_CHECK)){
                            String result = st.nextToken();
                            Log.d("result", result);
                            if(result.equals("ok")){
                                Intent intent = new Intent(MainActivity.this, IngameActivity.class);
                                int ingame_port = items.get(clickPosition).getPort();
                                intent.putExtra("ingame_port", ingame_port);

                                String ingame_title = items.get(clickPosition).getTitle();
                                intent.putExtra("ingame_title", ingame_title);

                                intent.putExtra("nickname", login_id);

                                startActivity(intent);
                            }else if(result.equals("nok")){
                                Handler mHandler = new Handler(Looper.getMainLooper());
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "방이 꽉 찼습니다", Toast.LENGTH_SHORT).show();
                                    }
                                }, 0);
                            }
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
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context context;
        private ArrayList<Item> mItems;
        private int lastPosition = -1;

        public MyAdapter(ArrayList items, Context mContext)
        {
            mItems = items;
            context = mContext;
        }
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
            MyAdapter.ViewHolder holder = new MyAdapter.ViewHolder(v); return holder;
        }
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {

            if(mItems.get(position).getRoomState().equals("")){
                holder.roomState.setText("");
            }else{
                holder.roomState.setText(mItems.get(position).getRoomState());
            }

            if(mItems.get(position).getAvailable()==false){
                holder.state.setText("입장 불가");
            }else{
                holder.state.setText("입장 가능합니다");
            }

            holder.numCount.setText("현재 " +  String.valueOf(mItems.get(position).getNumCount()) + "명");
            holder.room_title.setText(mItems.get(position).getTitle());

            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickPosition = position;

                    if(mItems.get(position).getLock()==true){
                        LayoutInflater inflater= LayoutInflater.from(MainActivity.this);
                        final View dialog_password= inflater.inflate(R.layout.dialog_password, null);

                        AlertDialog.Builder adp = new AlertDialog.Builder(MainActivity.this);

                        adp.setTitle("비밀번호를 입력하세요");     // 내용 설정
                        adp.setView(dialog_password);

                        final EditText ingame_password = dialog_password.findViewById(R.id.ingame_password);

                        adp.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(ingame_password.getText().toString().equals(null)){
                                    Toast.makeText(context, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                                }
                                else if(!ingame_password.getText().toString().equals(null)){
                                    passwordCheck = "0" + "$" +PW_CHECK + "$" + position + "$" + ingame_password.getText().toString() + "$";
                                    send_Message(passwordCheck);
                                }
                            }
                        });
                        adp.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        });
                        adp.show();
                    }else{
                        stateCongif = "0" + "$" +STATE_CHECK + "$" + position + "$";
                        send_Message(stateCongif);

                    }

                }
            });

        }
        @Override public int getItemCount() {
            return mItems.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView roomState;
            public TextView state;
            public TextView numCount;
            public TextView room_title;
            public CardView cardview;

            public ViewHolder(View view) {
                super(view);
                roomState = (TextView) view.findViewById(R.id.roomState);
                state = (TextView) view.findViewById(R.id.state);
                numCount = (TextView) view.findViewById(R.id.numCount);
                room_title = (TextView) view.findViewById(R.id.room_title);
                cardview=(CardView)itemView.findViewById(R.id.cardview);
            }
        }
    }
}