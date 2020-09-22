package com.kma.securechatapp.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.securechatapp.BuildConfig;
import com.kma.securechatapp.R;
import com.kma.securechatapp.core.api.model.Contact;
import com.kma.securechatapp.helper.ImageLoadTask;
import com.kma.securechatapp.utils.common.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactViewHolder extends RecyclerView.ViewHolder {
    @Nullable

    @BindView(R.id.contact_item_avatar)
    public ImageView avatar;
    @Nullable
    @BindView(R.id.contact_item_name)
    public TextView title;
    @Nullable
    @BindView(R.id.contact_item_subname)
    public TextView subname;

    @BindView(R.id.contact_online)
    public View online;
    private TextView textView;
    private ImageView imageView;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
        imageView = itemView.findViewById(R.id.imageView);
        ButterKnife.bind(this ,itemView);
    }
    public void bind(final Contact contact) {
        imageView.setVisibility(contact.isChecked() ? View.VISIBLE : View.GONE);
//        textView.setText("abcs");

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contact.setChecked(!contact.isChecked());
                imageView.setVisibility(contact.isChecked() ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void setContactAvatar(String url){
        if (this.avatar == null){
            ButterKnife.bind(this,itemView);
        }
        if (avatar!=null)
            ImageLoader.getInstance().DisplayImage(url,avatar);


       // new ImageLoadTask(url,avatar).execute();
    }
    public void setContactName(String name){
        if (this.title == null){
            ButterKnife.bind(this,itemView);
        }
        if (name!= null && this.title != null)
            this.title.setText(name);

    }

    public void setSubName(String name){
        if (this.subname == null){
            ButterKnife.bind(this,itemView);
        }
        if (name!= null && this.subname != null)
            this.subname.setText(name);

    }
    public void setOnline(boolean isOnline){
        if (isOnline){
            online.setVisibility(View.VISIBLE);
        }else{
            online.setVisibility(View.GONE);
        }
    }
}
