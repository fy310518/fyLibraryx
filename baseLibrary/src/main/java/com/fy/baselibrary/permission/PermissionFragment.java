package com.fy.baselibrary.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.Gravity;
import android.widget.ListView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.fy.baselibrary.R;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.application.mvvm.BaseViewModel;
import com.fy.baselibrary.base.ViewHolder;
import com.fy.baselibrary.base.dialog.CommonDialog;
import com.fy.baselibrary.base.dialog.DialogConvertListener;
import com.fy.baselibrary.base.dialog.NiceDialog;
import com.fy.baselibrary.base.fragment.BaseFragment;
import com.fy.baselibrary.utils.AppUtils;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.notify.T;
import com.fy.baselibrary.utils.os.OSUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 动态权限管理 fragment
 * Created by fangs on 2018/8/27 15:36.
 */
public class PermissionFragment extends BaseFragment<BaseViewModel, ViewDataBinding> {

    public final static String KEY_PERMISSIONS_ARRAY = "key_permission_array";
    public final static String TipsDialogGravity = "TipsDialogGravity";
    public final static String KEY_ShowCancelBtn = "isShowCancelBtn";
    public final static String KEY_isRun = "isRun";
    public final static String KEY_FIRST_MESSAGE = "key_first_message";
    public final static String KEY_ALWAYS_MESSAGE = "key_always_message";

    /** 权限请求 状态码 */
    public final static int PERMISSION_REQUEST_CODE = 0x01;
    /** 权限请求成功 状态码 */
    public final static int CALL_BACK_RESULT_CODE_SUCCESS = 0x02;
    /** 权限请求失败 状态码*/
    public final static int CALL_BACK_RESULE_CODE_FAILURE = 0x03;

    private String appName;
    /** 第一次拒绝该权限的提示信息。 */
    private String mFirstRefuseMessage;
    /** 永久拒绝权限提醒的提示信息 */
    private String mAlwaysRefuseMessage;
    private String[] mPermissions;
    private int gravity = Gravity.BOTTOM; // 权限请求失败，提示弹窗位置
    private boolean isShowCancelBtn = true; //特殊权限被拒绝后弹窗 是否显示 取消按钮
    private boolean isRun = false; //存在被拒绝的权限时，是否继续执行


    private boolean mIsSpecialPermissionStatus = true;//特殊权限是否请求成功
    private String mSpecialPermission;//特殊权限

    private boolean isToSettingPermission;

    private FragmentManager manager;
    private OnPermission call;

    private MutableLiveData<Boolean> isLoad = new MutableLiveData<>();
    private boolean isRunComm = false;


    private ActivityResultLauncher<String[]> launcher = null;

    @Override
    public int setContentLayout() {
        return -1;
    }

    @Override
    public void initData(@Nullable BaseViewModel viewModel, @Nullable ViewDataBinding dataBinding, @Nullable Bundle savedInstanceState) {
        appName = AppUtils.getAppName(getContext(), AppUtils.getLocalPackageName());

        Bundle bundle = getArguments();
        if (null != bundle) {
            mPermissions = bundle.getStringArray(KEY_PERMISSIONS_ARRAY);
            gravity = bundle.getInt(TipsDialogGravity, Gravity.BOTTOM);
            isShowCancelBtn = bundle.getBoolean(KEY_ShowCancelBtn, false);
            isRun = bundle.getBoolean(KEY_isRun, false);

            mFirstRefuseMessage = bundle.getString(KEY_FIRST_MESSAGE);
            mAlwaysRefuseMessage = bundle.getString(KEY_ALWAYS_MESSAGE);
        }

        if (TextUtils.isEmpty(mFirstRefuseMessage)) {
            mFirstRefuseMessage = ResUtils.getReplaceStr(R.string.default_always_message, appName);
        }

        if (TextUtils.isEmpty(mAlwaysRefuseMessage)) {
            mAlwaysRefuseMessage = ResUtils.getReplaceStr(R.string.default_always_message, appName);
        }

        isLoad.observe(this, aBoolean -> {
            if(aBoolean && !isRunComm) {
                isRunComm = true;
                checkPermission(mPermissions); // 只执行一次
            }
        });

        launcher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>(){
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                ArrayMap<String, Boolean> tempMap = new ArrayMap<>();
                tempMap.putAll(result);

                if (OSUtils.isAndroid14()) {
                    if (requestPermission.contains(Manifest.permission.READ_MEDIA_IMAGES) ||
                            requestPermission.contains(Manifest.permission.READ_MEDIA_VIDEO) ||
                            requestPermission.contains(Manifest.permission.READ_MEDIA_AUDIO)) {

                        if(PermissionUtils.isPermissionGranted(getContext(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)){
                            requestPermission.remove(Permission.READ_MEDIA_IMAGES);
                            requestPermission.remove(Permission.READ_MEDIA_VIDEO);
                            requestPermission.remove(Permission.READ_MEDIA_AUDIO);

                            tempMap.remove(Permission.READ_MEDIA_IMAGES);
                            tempMap.remove(Permission.READ_MEDIA_VIDEO);
                            tempMap.remove(Permission.READ_MEDIA_AUDIO);
                        }
                    }
                }

                int failNum = 0;
                for(String key : tempMap.keySet()){
                    if(Boolean.FALSE.equals(tempMap.get(key))){
                        failNum++;
                    }
                }

                resultCallback(requestPermission.toArray(new String[0]), failNum);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Fragment niceDialog1 = getChildFragmentManager().findFragmentByTag("NiceDialogPermission1");
        Fragment niceDialog2 = getChildFragmentManager().findFragmentByTag("NiceDialogPermission2");
        if(null != niceDialog1 || null != niceDialog2){ // 避免重复
            return;
        }


        //如果是从权限设置界面回来
        if (!TextUtils.isEmpty(mSpecialPermission) && !PermissionUtils.isAppSpecialPermission(getContext(), mSpecialPermission)){
            // 特殊权限不为空
            mIsSpecialPermissionStatus = false; //特殊权限开启失败
            showSpecialPermissionDialog(mSpecialPermission);
        } else if (isToSettingPermission) {
            // 重新检查权限
            isToSettingPermission = false;
            checkPermission(mPermissions);
        }

        if(!isRunComm) isLoad.setValue(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(!mIsSpecialPermissionStatus) return; // 特殊权限开启失败 不执行 后面的逻辑
        if (requestCode == PERMISSION_REQUEST_CODE) {
            List<Integer> failurePermissionCount = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    failurePermissionCount.add(grantResults[i]);
                }
            }

            resultCallback(permissions, failurePermissionCount.size());
        }
    }

    private void resultCallback(@NonNull String[] permissions, int failNum){
            if (failNum == 0) {//权限请求失败数为0，则全部成功
                permissionEnd(CALL_BACK_RESULT_CODE_SUCCESS, mIsSpecialPermissionStatus);
            } else { //失败
                List<String> rationaleList = PermissionUtils.getShouldRationaleList(getActivity(), permissions);
                if (rationaleList.size() > 0) {
                    if (rationaleList.size() < permissions.length){
                        showPermissionDialog(rationaleList, true, false);//部分永久拒绝
                    } else {
                        showPermissionDialog(rationaleList, true, true);//全部永久拒绝
                    }
                } else {
                    List<String> requestPermission = PermissionUtils.getRequestPermissionList(getContext(), permissions);
                    for(String permiss : requestPermission){ // 特殊权限 给与用户说明
                        if(Permission.specialPermission.containsKey(permiss)){
                            showSpecialPermissionDialog(permiss);
                            return;
                        }
                    }

                    if(!isRun) { // 必须授权的危险权限，弹窗 给与用户说明
                        showPermissionDialog(requestPermission, true, false);
                    } else {
                        closePermissionFragment();
                    }
                }
            }
    }

    List<String> requestPermission = new ArrayList<>();
    /** 请求多个权限 */
    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission(String... permissions) {
        if (null != permissions) {
            if(ConfigUtils.isDEBUG()) PermissionUtils.checkPermissions(getActivity(), permissions);

            requestPermission.clear();
            // 需要申请权限的权限列表
            requestPermission.addAll(PermissionUtils.getRequestPermissionList(getContext(), permissions));

            // 是否需要申请特殊权限
            boolean requestSpecialPermission = false;
            // 判断当前是否包含特殊权限
            if (PermissionUtils.containsSpecialPermission(requestPermission)) {
                // 需要申请的 存储权限 以 Android 13 为基准
                List<String> storagePermission = new ArrayList<>();
                for(String permission : requestPermission) {
                    if(permission.equals(Manifest.permission.READ_MEDIA_IMAGES)
                            || permission.equals(Manifest.permission.READ_MEDIA_VIDEO)
                            || permission.equals(Manifest.permission.READ_MEDIA_AUDIO)
                            || permission.equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                    ){
                        storagePermission.add(permission);
                    }
                }
                String[] array = storagePermission.toArray(new String[storagePermission.size()]);

                if(!PermissionUtils.hasStoragePermission(getActivity(), array) && storagePermission.size() > 0){ // 是否需要申请 存储权限
                    if (OSUtils.isAndroid13()) {

                    } else if (OSUtils.isAndroid10()) {
                        if (requestPermission.contains(Permission.MANAGE_EXTERNAL_STORAGE)){
                            requestSpecialPermission = true;
                            mSpecialPermission = Permission.MANAGE_EXTERNAL_STORAGE;
                            // 存储权限设置界面
                        } else {
                            requestPermission.remove(Permission.READ_MEDIA_AUDIO);
                            requestPermission.remove(Permission.READ_MEDIA_IMAGES);
                            requestPermission.remove(Permission.READ_MEDIA_VIDEO);
                            requestPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE); // 读权限
                        }
                    } else {
                        requestPermission.remove(Permission.READ_MEDIA_AUDIO);
                        requestPermission.remove(Permission.READ_MEDIA_IMAGES);
                        requestPermission.remove(Permission.READ_MEDIA_VIDEO);
                        requestPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE); // 写包含读权限
                    }
                }

                else if (requestPermission.contains(Permission.REQUEST_INSTALL_PACKAGES) && !PermissionUtils.hasInstallPermission(getActivity())) {
                    requestSpecialPermission = true;
                    mSpecialPermission = Permission.REQUEST_INSTALL_PACKAGES;
                    // 跳转到安装权限设置界面
                } else if (requestPermission.contains(Permission.SYSTEM_ALERT_WINDOW) && !PermissionUtils.hasWindowPermission(getActivity())) {
                    requestSpecialPermission = true;
                    mSpecialPermission = Permission.SYSTEM_ALERT_WINDOW;
                    // 跳转到悬浮窗设置页面
                } else if (requestPermission.contains(Permission.NOTIFICATION_SERVICE) && !PermissionUtils.hasNotifyPermission(getActivity())) {
                    requestSpecialPermission = true;
                    mSpecialPermission = Permission.NOTIFICATION_SERVICE;
                    // 跳转到通知栏权限设置页面
                } else if (requestPermission.contains(Permission.WRITE_SETTINGS) && !PermissionUtils.hasSettingPermission(getActivity())) {
                    requestSpecialPermission = true;
                    mSpecialPermission = Permission.WRITE_SETTINGS;
                    // 跳转到系统设置权限设置页面
                } else if (requestPermission.contains(Permission.VPN_SERVICE) && !PermissionUtils.hasVPNPermission(getActivity())) {
                    requestSpecialPermission = true;
                    mSpecialPermission = Permission.VPN_SERVICE;
                    // vpn 权限
                }
            }

            // 当前必须没有跳转到悬浮窗或者安装权限界面
            if(requestSpecialPermission){
                List<String> rationaleList = new ArrayList<>();
                rationaleList.add(mSpecialPermission);
                isToSettingPermission = true;
                removePermission(mSpecialPermission);

                PermissionUtils.startPermissionActivity(PermissionFragment.this, rationaleList);
            } else {
                if (requestPermission.size() > 0) {
                    mIsSpecialPermissionStatus = true;
                    mSpecialPermission = "";
//                    requestPermissions(requestPermission.toArray(new String[0]), PERMISSION_REQUEST_CODE);
                    launcher.launch(requestPermission.toArray(new String[0]));
                } else {
                    permissionEnd(CALL_BACK_RESULT_CODE_SUCCESS, mIsSpecialPermissionStatus);
                }
            }
        } else {
            permissionEnd(CALL_BACK_RESULT_CODE_SUCCESS, mIsSpecialPermissionStatus);
        }
    }


    /**
     * 调用系统弹窗请求权限
     * @param isRefuse    是否勾选了（“不在提示”多选框）
     */
    public void onSurePermission(boolean isRefuse) {
        if (isRefuse) {
            isToSettingPermission = true;
            List<String> rationaleList = PermissionUtils.getShouldRationaleList(getActivity(), mPermissions);
            PermissionUtils.startPermissionActivity(this, rationaleList);
        } else {
            checkPermission(mPermissions);
        }
    }

    /**
     * 权限请求结束
     * @param resultCode
     * @param isStatus   是否全部成功或者是否全部失败（根据第一个参数判断：如参数1 表示“成功”状态码，则参数2表示 是否全部成功）
     */
    public void permissionEnd(int resultCode, boolean isStatus) {
        if (null != call){
            int permission = isStatus ? R.string.permissionSuccess : R.string.permissionAllSuccess;

            if (resultCode == CALL_BACK_RESULT_CODE_SUCCESS && isStatus) {
                T.show(permission, -1);
                call.hasPermission(Arrays.asList(mPermissions), isStatus);
            } else if (resultCode == CALL_BACK_RESULE_CODE_FAILURE && isStatus){
                T.show(R.string.permissionFail, -1);
                call.noPermission(Arrays.asList(mPermissions));
            } else {
                List<String> list = PermissionUtils.getRequestPermissionList(getContext(), mPermissions);
                int permissionTwo = list.isEmpty() ? R.string.permissionSuccess : R.string.permissionAllSuccess;
                T.show(permissionTwo, -1);
                call.hasPermission(list, list.isEmpty());
            }
        }

        closePermissionFragment();
    }


    /**
     * 危险权限请求失败 给予用户提示 自定义弹窗
     * @param isAlwaysRefuse    是否勾选了（“不在提示”多选框）
     * @param isAllSuccess      权限请求是否 全部成功
     */
    public void showPermissionDialog(List<String> rationaleList, final boolean isAlwaysRefuse, boolean isAllSuccess) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_permission)
                .setDialogConvertListener(new DialogConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, CommonDialog dialog) {
                        //生成 权限组 列表，然后传给 listView 适配器
                        List<String> listData = new ArrayList<>();
                        for (String permission : rationaleList){
                            String group = PermissionUtils.getPermissionGroup(permission);
                            if (!TextUtils.isEmpty(group) && !listData.contains(group)) listData.add(group);
                        }

                        ListView lvRefusePermission = holder.getView(R.id.lvRefusePermission);
                        PermissionTipsListAdapter permissionTipsListAdapter = new PermissionTipsListAdapter(getContext(), listData);
                        lvRefusePermission.setAdapter(permissionTipsListAdapter);

                        holder.setText(R.id.tvPermissionDescribe, isAlwaysRefuse ? mAlwaysRefuseMessage : mFirstRefuseMessage);

                        holder.setText(R.id.tvpermissionConfirm, isAlwaysRefuse ? R.string.deauthorization : R.string.ok);
                        holder.setOnClickListener(R.id.tvpermissionConfirm, v -> {
                            onSurePermission(isAlwaysRefuse);// todo 需要验证
                            dialog.dismiss(false);
                        });

                        holder.setText(R.id.tvPermissionCancel, R.string.cancel);
                        holder.setOnClickListener(R.id.tvPermissionCancel, v -> {
                            permissionEnd(CALL_BACK_RESULE_CODE_FAILURE, isAllSuccess);
                            dialog.dismiss(false);
                        });
                    }
                })
                .setWidthPixels(-2)
                .setGravity(gravity)
                .setAnim(android.R.style.Animation_Dialog)
                .setKeyBack(true)
                .show(getChildFragmentManager(), "NiceDialogPermission1");
    }

    /**
     * 申请特殊权限 弹窗
     * @param specialPermission
     */
    public void showSpecialPermissionDialog(String specialPermission) {
        mSpecialPermission = specialPermission;

        String[] info = Permission.specialPermission.get(specialPermission);
        if (null == info) {
            mIsSpecialPermissionStatus = true;
            removePermission(specialPermission);
            checkPermission(mPermissions);
            return;
        }

        NiceDialog.init()
                .setLayoutId(R.layout.dialog_permission)
                .setDialogConvertListener(new DialogConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, CommonDialog dialog) {
                        holder.setVisibility(R.id.lvRefusePermission, false);
                        holder.setVisibility(R.id.txtSpecialPermission, true);

//                        String title = ResUtils.getReplaceStr(R.string.default_special_permission, appName, info[0]);
                        holder.setText(R.id.tvPermissionDescribe, mFirstRefuseMessage);//标题

//                        String content = ResUtils.getReplaceStr(R.string.default_special_permission_content, info[0], appName, info[0]);
                        holder.setText(R.id.txtSpecialPermission, info[0]);//特殊权限用途 说明

                        holder.setText(R.id.tvpermissionConfirm, R.string.deauthorization);
                        holder.setOnClickListener(R.id.tvpermissionConfirm, v -> {
                            List<String> rationaleList = new ArrayList<>();
                            rationaleList.add(specialPermission);
                            PermissionUtils.startPermissionActivity(PermissionFragment.this, rationaleList);
                            isToSettingPermission = true;

                            removePermission(specialPermission);
                            dialog.dismiss(false);
                        });


                        holder.setVisibility(R.id.tvPermissionCancel, isShowCancelBtn);
                        holder.setText(R.id.tvPermissionCancel, R.string.cancel);
                        holder.setOnClickListener(R.id.tvPermissionCancel, v -> {
                            mIsSpecialPermissionStatus = false;
                            //请求的权限列表中有特殊权限，如果取消，移除这个特殊权限，继续请求 其它权限
                            removePermission(specialPermission);
                            permissionEnd(CALL_BACK_RESULE_CODE_FAILURE, true);
                            dialog.dismiss(false);
                        });
                    }
                })
                .setWidthPixels(-2)
                .setGravity(gravity)
                .setAnim(android.R.style.Animation_Dialog)
                .setKeyBack(true)
                .show(getChildFragmentManager(), "NiceDialogPermission2");
    }

    //从全局 mPermissions 中移除 特殊权限
    private void removePermission(String permission){
        List<String> tempList = new ArrayList<>(Arrays.asList(mPermissions));
        tempList.remove(permission);
        String[] tempStrArray = new String[tempList.size()];
        tempList.toArray(tempStrArray);
        mPermissions = tempStrArray;
    }

    private void closePermissionFragment(){
        try {
            if(null != manager){
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(this);
                transaction.detach(this);
                transaction.commit();
            }
        } catch (Exception e) {
            L.e(TAG, e.getMessage());
        }
    }

    /**
     * 准备请求权限
     * @param object
     * @param needPermission
     */
    public static void newInstant(Object object, PermissionBean needPermission, OnPermission callListener) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(KEY_PERMISSIONS_ARRAY, needPermission.getValue());
        bundle.putInt(TipsDialogGravity, needPermission.getGravity());
        bundle.putBoolean(KEY_ShowCancelBtn, needPermission.isShowCancelBtn());
        bundle.putBoolean(KEY_isRun, needPermission.isRun());

        if (!TextUtils.isEmpty(needPermission.getFirstRefuseMsg())) bundle.putString(KEY_FIRST_MESSAGE, needPermission.getFirstRefuseMsg());
        if (!TextUtils.isEmpty(needPermission.getAlwaysRefuseMsg())) bundle.putString(KEY_ALWAYS_MESSAGE, needPermission.getAlwaysRefuseMsg());

        PermissionFragment fragment = new PermissionFragment();
        fragment.call = callListener;
        fragment.setArguments(bundle);

        FragmentManager manager = null;
        if (object instanceof AppCompatActivity) {
            AppCompatActivity act = ((AppCompatActivity)object);
            manager = act.getSupportFragmentManager();

        } else if (object instanceof Fragment) {
            Fragment fm = ((Fragment)object);
            manager = fm.getChildFragmentManager();
        }

        assert manager != null;
        fragment.manager = manager;
        manager.beginTransaction().add(fragment, "PermissionFragment").commitAllowingStateLoss();
    }

    /**
     * 权限请求 调用入口
     * @param object
     * @param needPermission  对标 needPermission 注解
     * @param callListener
     */
    public static void runPermissionRequest(Object object, PermissionBean needPermission, OnPermission callListener) {
        Context context = null;
        if (object instanceof Activity) {
            context = ((Activity) object);
        } else if (object instanceof Fragment) {
            context = ((Fragment) object).getActivity();
        }
        if (null == context) return;

        //获取需要申请的权限，如果返回的权限列表为空 则 已经获取了对应的权限列表
        List<String> requestPermission = PermissionUtils.getRequestPermissionList(context, needPermission.getValue());
        if (requestPermission.size() == 0) {
            callListener.hasPermission(new ArrayList<>(), true);
        } else {
            PermissionFragment.newInstant(object, needPermission, callListener);
        }
    }

}
