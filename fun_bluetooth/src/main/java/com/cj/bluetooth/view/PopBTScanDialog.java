package com.cj.bluetooth.view;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.cj.bluetooth.R;
import com.cj.bluetooth.entity.NotPairedEntity;
import com.cj.bluetooth.entity.PairedEntity;
import com.cj.common.base.BaseApp;
import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.cj.common.multitype.Items;
import com.cj.common.multitype.MultiTypeAdapter;
import com.cj.common.multitype.MultiTypeViewBinder;
import com.cj.common.multitype.ViewHolder;
import com.cj.ui.tip.UITipDialog;
import com.cj.ui.util.ScreenUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Author:chris - jason
 * Date:2019/3/16.
 * Package:com.sctjsj.lazyhost.widget
 * 用于非蓝牙搜索页面的蓝牙搜索功能
 */
public class PopBTScanDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private TextView mTVCancel;
    private RecyclerView mRV;
    private LinearLayout mLLEmpty;
    private Items data;
    private MultiTypeAdapter adapter;
    private List<BluetoothDevice> pairedList = new ArrayList<>();
    private List<BluetoothDevice> unPairedList = new ArrayList<>();

    private ProgressBar mPB;


    public PopBTScanDialog(@NonNull Context context) {
        super(context, R.style.fun_bluetooth_full_width_dialog);
        this.mContext = context;
        //设置不可取消
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        initAttr();
    }

    //初始化属性
    private void initAttr() {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View rootView = inflater.inflate(R.layout.fun_bluetooth_pop_bt_scan_dialog_layout, null);

        //set view
        setContentView(rootView);

        setCancelable(false);//点击外部区域可取消
        setCanceledOnTouchOutside(false);//点击返回键可取消
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);//显示在底部

        //设置宽高
        WindowManager.LayoutParams params = window.getAttributes();

        Display display = getWindow().getWindowManager().getDefaultDisplay();

        params.width = display.getWidth();
        //设置高度为屏幕高度的80%
        params.height = ScreenUtil.getScreenHeight(mContext) * 8 / 10;
        window.setAttributes(params);

        //初始化控件
        bindView(rootView);
    }

    //绑定控件
    private void bindView(View root) {
        mTVCancel = root.findViewById(R.id.tv_cancel);
        mTVCancel.setOnClickListener(this);
        mRV = root.findViewById(R.id.rv);
        mPB = root.findViewById(R.id.pb);
        mLLEmpty = root.findViewById(R.id.ll_empty);
        initRV();

    }

    private void initRV() {

        data = new Items();
        data.add(new PairedEntity());

        adapter = new MultiTypeAdapter(data);

        MultiTypeViewBinder<NotPairedEntity> binder_notPaired = new MultiTypeViewBinder<NotPairedEntity>(mContext, R.layout.fun_bluetooth_item_not_paired_layout) {
            @Override
            protected void convert(ViewHolder holder, NotPairedEntity notPairedEntity, int position) {

            }
        };

        MultiTypeViewBinder<PairedEntity> binder_paired = new MultiTypeViewBinder<PairedEntity>(mContext, R.layout.fun_bluetooth_item_paired_layout) {
            @Override
            protected void convert(ViewHolder holder, PairedEntity pairedEntity, int position) {

            }
        };

        MultiTypeViewBinder<BluetoothDevice> binder_device = new MultiTypeViewBinder<BluetoothDevice>(mContext, R.layout.fun_bluetooth_item_bt_device_layout) {
            @Override
            protected void convert(ViewHolder holder, final BluetoothDevice bluetoothDevice, int position) {

                TextView mTVName = holder.getView(R.id.tv_device_name);
                TextView mTVMacAddress = holder.getView(R.id.tv_mac_address);
                TextView mTVState = holder.getView(R.id.tv_state);
                RelativeLayout mItem = holder.getView(R.id.rl_item);

                mTVName.setText(bluetoothDevice.getName());
                mTVMacAddress.setText(bluetoothDevice.getAddress());

                final int state = bluetoothDevice.getBondState();

                if (state == BluetoothDevice.BOND_BONDED) {
                    mTVState.setText("已配对");
                } else if (state == BluetoothDevice.BOND_NONE) {
                    mTVState.setText("未配对");
                } else {
                    mTVState.setText("蓝牙配对中...");
                }

                //点击ITEM
                mItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //已经配对的设备，去连接
                        if (state == BluetoothDevice.BOND_BONDED) {
                            //pop tip
                            //发送连接事件
                            Intent intent = new Intent();
                            intent.setAction("quest_for_bt_connect");
                            intent.putExtra("remote_device", bluetoothDevice);
                            DataBus.get().with(DataBusKey.BluetoothEvent.getKey(), DataBusKey.BluetoothEvent.getT()).setValue(intent);

                            if (BaseApp.getInstance().getCurrentActivity() != null) {
                                final UITipDialog dialog = new UITipDialog.Builder(BaseApp.getInstance().getCurrentActivity()).
                                        setIconType(UITipDialog.Builder.ICON_TYPE_INFO).
                                        setTipWord("蓝牙连接中...").create();
                                if (dialog != null) {
                                    dialog.show();
                                }
                                //延迟两秒关闭PopBTScanDialog，避免BTCenter.btEventObserver无法接收到连接请求的Action
                                dialog.setOnDismissListener(new OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        //Tip关闭后关闭Dialog
                                        dismiss();
                                    }
                                });
                                //2s后关闭Tip
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                }, 2000);

                            }

                        } else {
                            //点击未配对设备去配对
                            String address = bluetoothDevice.getAddress();
                            if (BluetoothAdapter.getDefaultAdapter() != null && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                                final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                                if (device == null) {
                                    return;
                                }
                                if (BaseApp.getInstance().getCurrentActivity() != null) {
                                    final UITipDialog dialog = new UITipDialog.Builder(BaseApp.getInstance().getCurrentActivity()).
                                            setIconType(UITipDialog.Builder.ICON_TYPE_INFO).
                                            setTipWord("蓝牙配对中...").create();
                                    if (dialog != null) {
                                        dialog.show();
                                    }

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    }, 2000);

                                    dialog.setOnDismissListener(new OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            bondDevice(device);
                                        }
                                    });

                                }

                            }

                        }
                    }
                });


            }
        };

        adapter.register(PairedEntity.class, binder_paired);
        adapter.register(NotPairedEntity.class, binder_notPaired);
        adapter.register(BluetoothDevice.class, binder_device);
        mRV.setLayoutManager(new LinearLayoutManager(mContext));
        mRV.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        int vid = v.getId();

        //关闭扫描框
        if (vid == R.id.tv_cancel) {
            dismiss();
        }

    }


    public void setDeviceList(List<BluetoothDevice> pairedList, List<BluetoothDevice> unPairedList) {

        this.pairedList.clear();
        this.unPairedList.clear();

        if (pairedList != null && pairedList.size() > 0) {
            this.pairedList.addAll(pairedList);
        }

        if (unPairedList != null && unPairedList.size() > 0) {
            this.unPairedList.addAll(unPairedList);
        }

        //展示空数据
        if (pairedList.size() == 0 && unPairedList.size() == 0) {
            mLLEmpty.setVisibility(View.VISIBLE);
            mRV.setVisibility(View.GONE);
            return;
        }

        mLLEmpty.setVisibility(View.GONE);
        mRV.setVisibility(View.VISIBLE);

        data.clear();
        data.add(new PairedEntity());
        data.addAll(this.pairedList);
        data.add(new NotPairedEntity());
        data.addAll(this.unPairedList);

        adapter.notifyDataSetChanged();


    }


    public void setScanProgress(int progress) {

        switch (progress) {
            //开始扫描
            case 1:
                mPB.setVisibility(View.VISIBLE);
                mTVCancel.setVisibility(View.GONE);
                break;

            //扫描结束
            case 2:
                mPB.setVisibility(View.GONE);
                mTVCancel.setVisibility(View.VISIBLE);
                break;
        }

    }

    //配对设备
    private boolean bondDevice(BluetoothDevice device) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return device.createBond();
        }

        //api 19以下反射调用配对
        boolean res = false;
        try {
            Method method = device.getClass().getMethod("createBond", null);
            res = (boolean) method.invoke(device, (Object[]) null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return res;
    }

}
