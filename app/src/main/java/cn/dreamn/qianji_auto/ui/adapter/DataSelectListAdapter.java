package cn.dreamn.qianji_auto.ui.adapter;

/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.views.IconView;
import cn.dreamn.qianji_auto.utils.pictures.MyBitmapUtils;


public class DataSelectListAdapter extends ArrayAdapter {

    private Context mContext;
    public DataSelectListAdapter(Context context, Bundle[] bundles) {
        super(context, R.layout.data_item, bundles);
        mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = (Bundle) getItem(position);

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.data_item, null, false);
        ImageView icon_header = (ImageView) view.findViewById(R.id.icon_header);
        TextView item_title = (TextView) view.findViewById(R.id.item_title);

        item_title.setText(bundle.getString("name"));
        final Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                MyBitmapUtils.setImage(mContext,icon_header,(Bitmap) msg.obj);
            }
        };
        MyBitmapUtils myBitmapUtils=new MyBitmapUtils(mContext,mHandler);
        myBitmapUtils.disPlay(icon_header,bundle.getString("icon"));
        return view;


    }
}

