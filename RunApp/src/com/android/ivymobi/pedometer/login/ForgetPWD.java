package com.android.ivymobi.pedometer.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Program.TextureType;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.ivymobi.pedometer.BaseActivity;
import com.android.ivymobi.pedometer.Config;
import com.android.ivymobi.pedometer.SyncMetaData;
import com.android.ivymobi.pedometer.SyncMetaData.MetaData;
import com.android.ivymobi.pedometer.data.BaseModel;
import com.android.ivymobi.pedometer.util.RegUtil;
import com.android.ivymobi.pedometer.util.ToastUtil;
import com.android.ivymobi.pedometer.util.UserUtil;
import com.android.ivymobi.runapp.R;
import com.google.gson.Gson;
import com.msx7.annotations.Inject;
import com.msx7.annotations.InjectActivity;
import com.msx7.annotations.InjectView;
import com.msx7.core.command.ErrorCode;
import com.msx7.core.command.IResponseListener;
import com.msx7.core.command.model.DefaultMapRequest;
import com.msx7.core.command.model.Request;
import com.msx7.core.command.model.Response;

@InjectActivity(id = R.layout.activity_forgetpwd)
public class ForgetPWD extends BaseActivity implements View.OnClickListener, IResponseListener {

    @InjectView(id = R.id.email)
    TextView mViewEmail;
    @InjectView(id = R.id.sendEmail)
    Button mBtnLogin;
    @InjectView(id = R.id.regist)
    View mForgetPWD;
    @InjectView(id = R.id.login)
    View mRegist;
    @InjectView(id = R.id.suffix)
    EditText emailSuffix;
    @InjectView(id = R.id.down)
    ImageView down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Inject.inject(this);
        mRegist.setOnClickListener(this);
        mForgetPWD.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mBtnLogin.setEnabled(false);
        mViewEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(mViewEmail.getText().toString())) {
                    mBtnLogin.setEnabled(false);
                } else
                    mBtnLogin.setEnabled(true);
            }

        });
        SyncMetaData.SyncMetaData(null);
        down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showSpinner();
            }

        });
    }

    String[] mItems = null;
    PopupWindow popupWindow;

    void showSpinner() {
        MetaData data = UserUtil.getMetaData();
        if (data == null || data.domain == null || data.domain.size() < 1) {
            mItems = getResources().getStringArray(R.array.domain);
        } else {
            // 建立数据源
            mItems = data.domain.toArray(new String[data.domain.size()]);
        }

        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1, mItems);
        popupWindow = new PopupWindow();
        ListView mListView = new ListView(this);
        mListView.setBackgroundColor(Color.WHITE);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setAdapter(_Adapter);
        mListView.measure(0, 0);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                emailSuffix.setText(mItems[position]);
                popupWindow.dismiss();
            }

        });
        popupWindow.setWidth(getResources().getDisplayMetrics().widthPixels);
        popupWindow.setHeight(mListView.getMeasuredHeight() * 3);
        popupWindow.setContentView(mListView);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        Rect rect = new Rect();
        emailSuffix.getGlobalVisibleRect(rect);
        popupWindow.showAtLocation(down, Gravity.TOP | Gravity.LEFT, 0, rect.bottom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.regist:
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
            break;

        case R.id.login:
            onBackPressed();
            break;
        case R.id.sendEmail:
            String email = mViewEmail.getText().toString() + emailSuffix.getText().toString();
            if (email == null || "".equals(email) || "".equals(email.trim())) {
                ToastUtil.showLongToast("邮箱不能为空");
                return;
            }
            // if (!RegUtil.isEmail(email)) {
            // ToastUtil.showLongToast("邮箱格式不正确");
            // return;
            // }
            showLoadingDialog(R.string.loadingData);
            Request request = new DefaultMapRequest(Config.SEVER_FORGET_PAW, "email", email);
            goPost(request, this);
            break;
        default:
            break;
        }
    }

    @Override
    public void onSuccess(Response response) {
        dismissLoadingDialog();
        String dataString = response.getData().toString();
        BaseModel model = new Gson().fromJson(dataString, BaseModel.class);
        ToastUtil.showLongToast(model.message);

    }

    @Override
    public void onError(Response response) {
        dismissLoadingDialog();
        ToastUtil.showLongToast(ErrorCode.getErrorCodeString(response.errorCode));
    }

}
