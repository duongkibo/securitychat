package com.kma.securechatapp.ui.groupconversation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.ContactAdapter;
import com.kma.securechatapp.adapter.DashboardAdapter;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.ApiInterface;
import com.kma.securechatapp.core.api.ApiUtil;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.core.api.model.UserConversation;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.api.model.UserKey;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.core.security.RSAUtil;
import com.kma.securechatapp.ui.contact.ContactAddViewModel;
import com.kma.securechatapp.ui.contact.ContactViewModel;
import com.kma.securechatapp.ui.control.suggestview.SuggestView;
import com.kma.securechatapp.ui.dashboard.DashboardViewModel;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import butterknife.BindView;

public class MutilSelectUserToGroup extends AppCompatActivity {
    private ContactViewModel contactViewModel;
    private ContactAdapter contactAdapter = new ContactAdapter();
    private DashboardViewModel dashboardViewModel;
    private  Button btnCreateGroup;
    DashboardAdapter dashboardAdapter;
    SuggestView suggestView;
    EventBus.EvenBusAction evenBus;
    ContactAddViewModel contactAddViewModel;
    List<UserInfo> userInfoList = new ArrayList<>();
    ApiInterface api = ApiUtil.getChatApi();



    RecyclerView rc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutil_select_user_to_group);
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        dashboardAdapter =  new DashboardAdapter();
        contactAdapter = new ContactAdapter();
        contactAddViewModel = ViewModelProviders.of(this).get(ContactAddViewModel.class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MutilSelectUserToGroup.this,LinearLayoutManager.VERTICAL,false);
        rc = findViewById(R.id.rc_list_user);
        rc.setAdapter(contactAdapter);
        rc.setLayoutManager(linearLayoutManager);
        loadData();
        btnCreateGroup = findViewById(R.id.btn_create_group);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contactAdapter.getSelected().size()>0 )
                {
                    ArrayList<UserInfo> infor = new ArrayList<UserInfo>();
                    for(int i=0;i<contactAdapter.getSelected().size();i++)
                    {
                        Contact contact = contactAdapter.getSelected().get(i);

                        if (contactAdapter.getSelected().get(i).isChecked())
                        {
                            try {
                                UserInfo contactInfo =  api.userInfo(contact.contactUuid).execute().body().data;
                                infor.add(contactInfo);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    infor.add(AppData.getInstance().currentUser);
                    Log.d("sssssss",infor.toString());
                    // noi dung goi len

                  List<UserConversation> userCons = makeKey(infor);
                  Intent intent = getIntent();
                  String uuidCon = intent.getStringExtra("uiid");
                    try {
                        api.addUser(uuidCon,userCons).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //   Toast.makeText(MutilSelectUserToGroup.this,stringBuilder.toString().trim(),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MutilSelectUserToGroup.this,"please select user",Toast.LENGTH_SHORT).show();
                }
                onBackPressed();
            }
        });



//        dashboardViewModel.getSuggest().observe(this,userInfos -> {
//            suggestView.upDateList(userInfos);
//        });
//
//
//
//        if (AppData.getInstance().currenUser != null){
////            dashboardViewModel.triggerLoadStuggest();
//        }
//
//        evenBus = new EventBus.EvenBusAction() {
//            @Override
//            public void onLogin(UserInfo u){
//                dashboardViewModel.triggerLoadSuggest();
//            }
//        };
//
//        EventBus.getInstance().addEvent(evenBus);
    }
    void loadData(){
        contactViewModel.getContact(0).observe(this,contacts -> {
            contactAdapter.setContacts(contacts);
            contactAdapter.notifyDataSetChanged();

        });
    }


    byte[] generatorKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = null;
        keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        byte[] buffKey = secretKey.getEncoded();
        return buffKey;
    }
    List<UserConversation> makeKey(List<UserInfo> users){
        ArrayList<UserConversation> userConversation = new ArrayList<>();
        try {
            byte [] buffKey = generatorKey();
            for (UserInfo u : users){
                UserConversation ucon = new UserConversation();
                ucon.key = RSAUtil.base64Encode(RSAUtil.RSAEncrypt(buffKey,u.getPublicKey()));
                ucon.userUuid = u.uuid;
                ucon.lastSeen = (long)0;
                userConversation.add(ucon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userConversation;
    }
}