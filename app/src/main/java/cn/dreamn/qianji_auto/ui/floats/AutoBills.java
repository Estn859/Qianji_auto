/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
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

package cn.dreamn.qianji_auto.ui.floats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.hjq.toast.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.bills.SendDataToApp;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Table.AutoBill;
import cn.dreamn.qianji_auto.ui.adapter.BillListAdapter;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;


/**
 * 应用切换悬浮窗
 *
 * @author xuexiang
 * @since 2019/1/21 上午11:53
 */
public class AutoBills {

    private Context context;
    private BillListAdapter billListAdapter;
    private List<AutoBill> autoBills;
    private View mView;
    private MaterialDialog dialog;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (autoBills == null || autoBills.size() == 0) {
                ToastUtils.show("没有待记录的账单！");
                dismiss();
            } else {
                if (dialog != null)
                    dialog.show();
                if (billListAdapter != null)
                    billListAdapter.addAll(autoBills);

            }
        }
    };

    /**
     * 构造器
     *
     * @param context
     */
    public AutoBills(Context context) {

        // super(context);
        this.context = context;
        initView();
        initData();
    }

    private void initView() {
        ContextThemeWrapper ctx = new ContextThemeWrapper(context, R.style.Theme_AppCompat_Light_NoActionBar);
        context = ctx;
        LayoutInflater factory = LayoutInflater.from(ctx);
        mView = factory.inflate(R.layout.float_autobill, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        dialog = new MaterialDialog(context, bottomSheet);
        dialog.cancelable(false);
        ListView listView = mView.findViewById(R.id.list_view);
        TextView tv_close = mView.findViewById(R.id.tv_close);
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        billListAdapter = new BillListAdapter(context, R.layout.float_autobill, null);
        listView.setAdapter(billListAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                AutoBill autoBill = autoBills.get(position);
                BillInfo billInfo = BillInfo.parse(autoBill.billInfo);
                billInfo.setId(autoBill.id);
                SendDataToApp.notice(context, billInfo);
                autoBills.remove(position);
                TaskThread.onThread(() -> {
                    //设置为已记录
                    Db.db.AutoBillDao().update(billInfo.getId());
                });
                if (autoBills.size() <= 0) {
                    dismiss();
                } else {
                    billListAdapter.remove(autoBill);
                }

            } catch (Throwable e) {
                Log.i("发生了错误" + e.toString());
            }
        });
    }

    private Context getContext() {
        return context;
    }


    private void initData() {
        initListener();
    }


    public void setData() {
        TaskThread.onThread(() -> {
            AutoBill[] autoBill = Db.db.AutoBillDao().getNoRecord();
            autoBills = new ArrayList<>();
            autoBills.addAll(Arrays.asList(autoBill));
            HandlerUtil.send(mMainHandler, 0);
        });
    }

    @SuppressLint("CheckResult")
    protected void initListener() {


    }


    public void dismiss() {
        mMainHandler.removeCallbacksAndMessages(null);
        if (dialog != null)
            dialog.dismiss();
    }

    public void show() {
        DialogCustomViewExtKt.customView(dialog, null, mView,
                false, true, false, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
        } else {
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        }
        dialog.cornerRadius(15f, null);

        setData();

    }


}
