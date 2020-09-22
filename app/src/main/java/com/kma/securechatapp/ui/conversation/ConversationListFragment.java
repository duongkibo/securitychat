package com.kma.securechatapp.ui.conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kma.securechatapp.R;
import com.kma.securechatapp.adapter.ConversationAdapter;
import com.kma.securechatapp.core.AppData;
import com.kma.securechatapp.core.api.model.Conversation;
import com.kma.securechatapp.core.api.model.CreateGroup;
import com.kma.securechatapp.core.api.model.UserInfo;
import com.kma.securechatapp.core.event.EventBus;
import com.kma.securechatapp.ui.control.suggestview.OnlineView;
import com.kma.securechatapp.ui.control.suggestview.SuggestView;
import com.kma.securechatapp.ui.groupconversation.MutilSelectUserToGroup;
import com.kma.securechatapp.utils.misc.EndlessRecyclerOnScrollListener;
import com.kma.securechatapp.utils.misc.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private ConversationListViewModel conversationListViewModel;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    ConversationAdapter conversationAdapter = new ConversationAdapter();
    @BindView(R.id.conversation_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.conversation_swipcontainer)
    SwipeRefreshLayout swipeRefreshLayout;

     @BindView(R.id.btn_add_action)
     ImageButton buttonAddAction;

    EventBus.EvenBusAction evenBus;
    OnlineView onlineView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        conversationListViewModel = ViewModelProviders.of(this).get(ConversationListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_conversation, container, false);
        ButterKnife.bind(this,root);

        buttonAddAction.setOnClickListener(this);


        onlineView =  new OnlineView(this.getActivity(),root.findViewById(R.id.online_view));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setAdapter(conversationAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                conversationListViewModel.trigerLoadData(0);
            }
        });


        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(this.getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Conversation conversation = conversationAdapter.getConversationList().get(position);
                        Intent intent1 = new Intent(ConversationListFragment.this.getContext(), InboxActivity.class);
                        intent1.putExtra("uuid", conversation.UUID);
                        startActivity(intent1);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );

        conversationListViewModel.getConversations().observe(this,conversations -> {
            conversationAdapter.setConversationList(conversations);
            conversationAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });

        conversationListViewModel.getListOnline().observe( this , users->{
            onlineView.upDateList(users);
        });
        conversationListViewModel.trigerLoadOnline();

        registerEvent();
        return root;
    }
    void registerEvent(){
        evenBus = new EventBus.EvenBusAction() {
            @Override
            public void onRefreshConversation() {
                conversationListViewModel.trigerLoadData(0);
                conversationListViewModel.trigerLoadOnline();
            }
        };
        EventBus.getInstance().addEvent(evenBus);

    }

    @Override
    public void onRefresh() {
        conversationListViewModel.trigerLoadOnline();
        conversationListViewModel.trigerLoadData(0);
    }

    @Override
    public void onDetach() {
        if (evenBus != null);
        EventBus.getInstance().removeEvent(evenBus);

        super.onDetach();
    }

    @Override
    public void onClick(View view) {

        PopupMenu popupMenu = new PopupMenu(getContext(), buttonAddAction);
        popupMenu.getMenuInflater().inflate(R.menu.menu_add_group, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.item_add_group:
                    {


                        conversationListViewModel.getResultConvers().observe(ConversationListFragment.this,
                        conversation -> {

                            Intent intent = new Intent();
                            intent.putExtra("uiid",conversation.UUID);
                            intent.setClass(getContext(), MutilSelectUserToGroup.class);

                            getActivity().startActivity(intent);
                        });
                        CreateGroup group = new CreateGroup(AppData.getInstance().currentUser.uuid);
                        ArrayList<UserInfo> users = new ArrayList<>();
                        users.add(AppData.getInstance().currentUser);
                        group.setUsers(users);
                        conversationListViewModel.CreateGroup(group);

                        break;
                    }
                    case R.id.item_add_friend:
                    {

                        break;
                    }
                    case R.id.item_scan_qr:
                    {
                        break;
                    }
                    case R.id.item_history_login:
                    {
                        break;
                    }
                    case R.id.item_calandar_zalo:
                    {
                        break;
                    }
                    case R.id.item_tranfer_file:
                    {
                        break;
                    }
                    default:
                        break;
                }
                return  true;
            }
        });
        popupMenu.setForceShowIcon(true);
        popupMenu.show();






    }
}