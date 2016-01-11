package io.rong.app.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import io.rong.app.R;

/**
 * Created by Bob on 15/11/13.
 */
public class UpdateGroupUserInfoActivity extends BaseActionBarActivity {

    private EditText mNewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_update_group_info);

        getSupportActionBar().setTitle(R.string.de_actionbar_update_discussion);
        mNewName = (EditText) findViewById(R.id.et_new_name);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.de_fix_username, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon:
//                if (RongIM.getInstance() == null || DemoContext.getInstance() == null )
//
//                    return true;
//
//                    RongIM.setGroupUserInfoProvider(new RongIM.GroupUserInfoProvider() {
//                        @Override
//                        public GroupUserInfo getGroupUserInfo(String userId) {
//
//                            String currentUserId = DemoContext.getInstance().getSharedPreferences().getString(Constants.APP_USER_ID,Constants.DEFAULT);
//
//                            if(userId.equals(currentUserId)) {
//
//                                WinToast.toast(UpdateGroupUserInfoActivity.this, "修改成功");
//
//                                Intent intent = new Intent();
//                                intent.putExtra("UPDATA_GROPU_INFO", mNewName.getText().toString());
//                                setResult(Constants.FIX_GROUP_INFO, intent);
//                                finish();
//                                return new GroupUserInfo(userId, mNewName.getText().toString());
//
//                            }else {
//                                return null;
//                            }
//                        }
//                    },true);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
