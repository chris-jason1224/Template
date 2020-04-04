package com.cj.login;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.lifecycle.Observer;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.base.BaseActivity;
import com.cj.common.bus.ModuleBus;
import com.gyf.barlibrary.ImmersionBar;
import butterknife.BindView;
import gen.com.cj.bus.Gen$biz_login$Interface;

@Route(path="/biz_login/ACT/com.cj.login.LoginActivity")
public class LoginActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       ModuleBus.getInstance().of(Gen$biz_login$Interface.class).Gen$LoginEvent$Method().observeSticky(this, new Observer<String>() {
           @Override
           public void onChanged(String s) {
               Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
           }
       });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int resourceLayout() {
        return R.layout.activity_login;
    }

    @Override
    public View initStatusLayout() {
        return null;
    }

    @Override
    protected void initData() {
        String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572609968392&di=c313712ce7768b428f6bd41f042f74f9&imgtype=0&src=http%3A%2F%2Fphotocdn.sohu.com%2F20130302%2FImg367606372.jpg";

    }

    @Override
    protected void initView() {

    }

    @Override
    protected boolean useImmersionBar() {
        return true;
    }

    @Override
    protected void initImmersionBar() {
        //super.initImmersionBar();
        immersionBar = ImmersionBar.with(this);
        immersionBar.
                transparentStatusBar().
                statusBarDarkFont(false).//顶部状态栏是否为深色字体
                keyboardEnable(true).//解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                navigationBarWithKitkatEnable(false).//是否可以修改安卓4.4和emui3.1手机导航栏颜色，默认为true
                init();
    }

    @Override
    public void onClick(View v) {

    }

}
