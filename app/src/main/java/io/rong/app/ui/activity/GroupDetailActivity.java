package io.rong.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sea_monster.exception.BaseException;
import com.sea_monster.network.AbstractHttpRequest;

import java.util.HashMap;

import io.rong.app.DemoContext;
import io.rong.app.R;
import io.rong.app.model.ApiResult;
import io.rong.app.model.Status;
import io.rong.app.ui.fragment.GroupListFragment;
import io.rong.app.ui.widget.LoadingDialog;
import io.rong.app.ui.widget.WinToast;
import io.rong.app.utils.Constants;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;

/**
 * Created by Bob on 2015/3/28.
 */
public class GroupDetailActivity extends BaseApiActivity implements View.OnClickListener, Handler.Callback {

    private static final String TAG = GroupDetailActivity.class.getSimpleName();
    private static final int HAS_JOIN = 1;
    private static final int NO_JOIN = 2;
    private AsyncImageView mGroupImg;
    private TextView mGroupName;
    private TextView mGroupId;
    private RelativeLayout mRelGroupIntro;
    private TextView mGroupIntro;
    private RelativeLayout mRelGroupNum;
    private TextView mGroupNum;
    private RelativeLayout mRelGroupMyName;
    private TextView mGroupMyName;
    private RelativeLayout mRelGroupCleanMess;
    private Button mGroupJoin;
    private Button mGroupQuit;
    private Button mGroupChat;
    private ApiResult mApiResult;
    private AbstractHttpRequest<Status> mJoinRequest;
    private AbstractHttpRequest<Status> mQuitRequest;
    private Handler mHandler;
    private LoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_fr_group_intro);

        getSupportActionBar().setTitle(R.string.personal_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.de_actionbar_back);
        mGroupImg = (AsyncImageView) findViewById(R.id.group_portrait);
        mGroupName = (TextView) findViewById(R.id.group_name);
        mGroupId = (TextView) findViewById(R.id.group_id);
        mRelGroupIntro = (RelativeLayout) findViewById(R.id.rel_group_intro);
        mGroupIntro = (TextView) findViewById(R.id.group_intro);
        mRelGroupNum = (RelativeLayout) findViewById(R.id.rel_group_number);
        mGroupNum = (TextView) findViewById(R.id.group_number);
        mRelGroupMyName = (RelativeLayout) findViewById(R.id.rel_group_myname);
        mGroupMyName = (TextView) findViewById(R.id.group_myname);
        mRelGroupCleanMess = (RelativeLayout) findViewById(R.id.rel_group_clear_message);
        mGroupJoin = (Button) findViewById(R.id.join_group);
        mGroupQuit = (Button) findViewById(R.id.quit_group);
        mGroupChat = (Button) findViewById(R.id.chat_group);

        mHandler = new Handler(this);
        mDialog = new LoadingDialog(this);

        initData();
    }


    protected void initData() {

        mGroupJoin.setOnClickListener(this);
        mGroupChat.setOnClickListener(this);
        mGroupQuit.setOnClickListener(this);

        if (DemoContext.getInstance() != null) {
            HashMap<String, Group> groupHashMap = DemoContext.getInstance().getGroupMap();

            if (getIntent().hasExtra("INTENT_GROUP")) {
                mApiResult = (ApiResult) getIntent().getSerializableExtra("INTENT_GROUP");
            }
            if (mApiResult != null) {
                mGroupName.setText(mApiResult.getName().toString());
                mGroupId.setText("ID:" + mApiResult.getId().toString());
                mGroupIntro.setText(mApiResult.getIntroduce().toString());
                mGroupNum.setText(mApiResult.getNumber().toString());
                Message mess = Message.obtain();
                if (groupHashMap != null && groupHashMap.containsKey(mApiResult.getId().toString())) {
                    mess.what = HAS_JOIN;
                } else {
                    mess.what = NO_JOIN;
                }
                mHandler.sendMessage(mess);
            }

        }


    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HAS_JOIN:
                if (mDialog != null)
                    mDialog.dismiss();
                mGroupJoin.setVisibility(View.GONE);
                mGroupChat.setVisibility(View.VISIBLE);
                mGroupQuit.setVisibility(View.VISIBLE);
                break;
            case NO_JOIN:
                if (mDialog != null)
                    mDialog.dismiss();
                mGroupQuit.setVisibility(View.GONE);
                mGroupJoin.setVisibility(View.VISIBLE);
                mGroupChat.setVisibility(View.GONE);
                break;

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.join_group:
                if (DemoContext.getInstance() != null) {

                    if (mApiResult.getNumber().equals("500")) {
                        WinToast.toast(GroupDetailActivity.this, R.string.group_is_full);
                        return;
                    }

                    if (mDialog != null && !mDialog.isShowing())
                        mDialog.show();

                    mJoinRequest = DemoContext.getInstance().getDemoApi().joinGroup(mApiResult.getId(), this);

                }
                break;
            case R.id.quit_group:
                if (DemoContext.getInstance() != null) {
                    if (mDialog != null && !mDialog.isShowing())
                        mDialog.show();
                    mQuitRequest = DemoContext.getInstance().getDemoApi().quitGroup(mApiResult.getId(), this);
                }

                break;

            case R.id.chat_group:
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().getRongIMClient().joinGroup(mApiResult.getId(), mApiResult.getName(), new RongIMClient.OperationCallback() {
                        @Override
                        public void onSuccess() {
                            RongIM.getInstance().startGroupChat(GroupDetailActivity.this, mApiResult.getId(), mApiResult.getName());

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                        }
                    });

                break;
        }
    }


    @Override
    public void onCallApiSuccess(AbstractHttpRequest request, Object obj) {

        if (mJoinRequest == request) {
            if (obj instanceof Status) {
                final Status status = (Status) obj;
                if (status.getCode() == 200 && mApiResult != null) {
                    WinToast.toast(this, R.string.group_join_success);
                    Log.e(TAG, "-----------join success ----");
                    GroupListFragment.setGroupMap(mApiResult, 1);

                    if (RongIM.getInstance() != null)
                        RongIM.getInstance().getRongIMClient().joinGroup(mApiResult.getId(), mApiResult.getName(), new RongIMClient.OperationCallback() {
                            @Override
                            public void onSuccess() {

                                Message mess = Message.obtain();
                                mess.what = HAS_JOIN;
                                mHandler.sendMessage(mess);

                                RongIM.getInstance().startGroupChat(GroupDetailActivity.this, mApiResult.getId(), mApiResult.getName());

                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                            }
                        });

                    Intent intent = new Intent();
                    intent.putExtra("result", DemoContext.getInstance().getGroupMap());
                    this.setResult(Constants.GROUP_JOIN_REQUESTCODE, intent);
                }
            }

        } else if (mQuitRequest == request) {
            if (obj instanceof Status) {
                final Status status = (Status) obj;
                if (status.getCode() == 200) {
//                    WinToast.toast(this, "quit success ");
                    GroupListFragment.setGroupMap(mApiResult, 0);

                    Message mess = Message.obtain();
                    mess.what = NO_JOIN;
                    mHandler.sendMessage(mess);

                    if (RongIM.getInstance() != null) {
                        RongIM.getInstance().getRongIMClient().quitGroup(mApiResult.getId(), new RongIMClient.OperationCallback() {
                            @Override
                            public void onSuccess() {
                                WinToast.toast(GroupDetailActivity.this, R.string.group_quit_success);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                    }

                    Intent intent = new Intent();
                    intent.putExtra("result", DemoContext.getInstance().getGroupMap());
                    this.setResult(Constants.GROUP_QUIT_REQUESTCODE, intent);
                    Log.e(TAG, "-----------quit success ----");
                }
            }
        }

    }

    @Override
    public void onCallApiFailure(AbstractHttpRequest request, BaseException e) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            finish();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


}
