package webit.android.shanti.main.groups;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.chat.chatManager.ApplicationSingleton;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.Group;
import webit.android.shanti.entities.UserQuickBlox;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.general.photo.ImageHelper;
import webit.android.shanti.general.photo.getimage.CallbackContext;
import webit.android.shanti.general.photo.getimage.CameraLauncher;
import webit.android.shanti.general.photo.getimage.CameraLauncherArguments;

public class NewGroupFragment extends GroupMainFragment implements RadioGroup.OnCheckedChangeListener {


    public static final int SELECT_FILE = 1;
    private View mRootView;
    private Group mGroup;
    private RadioGroup mRadioGroup;
    private Button mAddGroupBtn;
    private ImageView mDefaultImage;
    private LinearLayout l;
    private TextView mAddGroupName;
    private TextView mAddGroupMoreInfo;
    private AlertDialog.Builder getImageFrom;
    private DialogInterface d;
    CameraLauncher cameraLauncher;
    //    private AddMembersCustomView mAddMembersView;
    CameraLauncherArguments arguments;
    ImageView imageView;
    CallbackContext callbackContext = new CallbackContext() {

        @Override
        public void success(Bitmap bitmap) {
            super.success(bitmap);
            imageView.setImageBitmap(bitmap);
        }

    };

    //if is return instance al views initialized with last values
    public static NewGroupFragment getInstance() {
        return new NewGroupFragment();
    }

    public NewGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_new_group, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mGroup = new Group();
        //mScrollView = (ScrollView) mRootView.findViewById(R.id.addGroupScrollMembers);
        mAddGroupBtn = (Button) mRootView.findViewById(R.id.addGroupCreateGroup);
//        mAddMembersView = (AddMembersCustomView) mRootView.findViewById(R.id.addGroupAddmembers);
//        mAddMembersView.setmUsersExistInGroup(User.getUsersId(mGroup.getUsersList()));
        imageView = (ImageView) mRootView.findViewById(R.id.addGroupImage);
        Bitmap image = ImageHelper.getBitmapFromURL(Group.defultGroupImageUrl,getActivity());
        Drawable d = new BitmapDrawable(getResources(), image);
        imageView.setImageDrawable(d);


        cameraLauncher = new CameraLauncher(this);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                arguments = new CameraLauncherArguments(imageView.getWidth(), imageView.getHeight(), true, CameraLauncherArguments.ESourceType.CAMERA);
            }
        });
        mAddGroupName = (TextView) mRootView.findViewById(R.id.addGroupName);
        mAddGroupMoreInfo = (TextView) mRootView.findViewById(R.id.addGroupMoreInfo);
        if (savedInstanceState == null) {
            mAddGroupName.setText("");
            mAddGroupMoreInfo.setText("");
        } else {
            mAddGroupName.setText(savedInstanceState.get("AddGroupName").toString());
            mAddGroupMoreInfo.setText(savedInstanceState.get("AddGroupMoreInfo").toString());
        }
//        mDefaultImage = (ImageView) mRootView.findViewById(R.id.GroupDetailsBgImv);


        //getActionBarManager().changeToGroupActionBar(getString(R.string.groupsNewGroup), getString(R.string.groupsTitle), View.INVISIBLE);
        changeActionBar(mRootView, getString(R.string.groupsNewGroup), getString(R.string.groupsTitle), View.INVISIBLE, GroupsListFragment.class.toString());
        initRadioGroup();
mRadioGroup.setOnCheckedChangeListener(this);
        setNewGroupEvents();

        return mRootView;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("AddGroupName", mAddGroupName.getText().toString());
        outState.putString("AddGroupMoreInfo", mAddGroupMoreInfo.getText().toString());
    }

    private void initRadioGroup() {
        mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.addGroupType);
        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                if (result != null && !result.equals("")) {
                    Type programsListType = new TypeToken<List<CodeValue>>() {
                    }.getType();
                    ArrayList<CodeValue> codeValues = new Gson().fromJson(result, programsListType);
                    RadioButton radioButton;
                    for (int i = 0; i < codeValues.size(); i++) {
                        radioButton = new RadioButton(getActivity());
                        radioButton.setText(codeValues.get(i).getNvValue());
                        radioButton.setTag(codeValues.get(i).getiKeyId());
                        mRadioGroup.addView(radioButton);
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.addGroupLoadViewsError), Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().onBackPressed();
                        }
                    }, 200);
                }
            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), CodeValue.getJsonToSent(CodeValue.groupType), ConnectionUtil.GetCodeTable);
    }


    private void setNewGroupEvents() {
        //members = new HashMap<>();
        final Fragment fragment = this;
        mRootView.findViewById(R.id.addGroupLoadGroupImage).setOnClickListener(onClickListener);


        mAddGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.HideKeyboard(getActivity());
                createNewGroup();

                //getActivity().getSupportFragmentManager().popBackStack();//שליפת הפרגמנט הקודם
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
Utils.HideKeyboard(getActivity());
            getImageFrom = new AlertDialog.Builder(getActivity(), R.style.myDialog);
            getImageFrom.setTitle(getActivity().getString(R.string.chooseImage));
            final CharSequence[] opsChars = {getActivity().getString(R.string.chooseImageCamera), getActivity().getString(R.string.chooseImageGallery), getActivity().getString(R.string.chooseImageCancel)};
            getImageFrom.setItems(opsChars, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    d = dialog;
                    switch (which) {
                        case 0:
                            arguments.eSourceType = CameraLauncherArguments.ESourceType.CAMERA;
                            cameraLauncher.execute(arguments, callbackContext, getActivity());
                            break;
                        case 1:
                            arguments.eSourceType = CameraLauncherArguments.ESourceType.GALLERY;
                            cameraLauncher.execute(arguments, callbackContext, getActivity());
                            break;
                    }
                    dialog.dismiss();
                }
            }).show();
        }
    };

    private void createNewGroup() {
        mRootView.findViewById(R.id.addGroupLoadGroupImage).setOnClickListener(null);
        if (d != null)
            d.dismiss();
        mGroup.setiMainUserId(Common.user.getiUserId());
        if (checkGroupField()) {
//            mGroup.setUsersList(mAddMembersView.getmUsersGroup());
            createChatDialog();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mRootView.findViewById(R.id.addGroupLoadGroupImage).setOnClickListener(onClickListener);
    }

    private void createChatDialog() {
        ((ApplicationSingleton) getActivity().getApplicationContext()).addDialogsUsers(UserQuickBlox.getQbUsersList(mGroup.getUsersList()));
        QBDialog dialogToCreate = new QBDialog();
        dialogToCreate.setName(mGroup.getNvGroupName());
        dialogToCreate.setType(QBDialogType.GROUP);
        dialogToCreate.setOccupantsIds(UserQuickBlox.getQbUsersIdsList(mGroup.getUsersList()));
        if ((QBChatService.getInstance().getGroupChatManager() == null)) {
            Toast.makeText(getActivity(), getString(R.string.addGroupCreateNewGroupFail), Toast.LENGTH_LONG).show();
            if (mRootView.findViewById(R.id.addGroupLoadGroupImage) != null)
                mRootView.findViewById(R.id.addGroupLoadGroupImage).setOnClickListener(onClickListener);
        } else {
            QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>() {
                @Override
                public void onSuccess(QBDialog dialog, Bundle args) {
                    mGroup.setNvQBDialogId(dialog.getDialogId());
                    mGroup.setNvQBRoomJid(dialog.getRoomJid());
                    registerGroupInServer();
                    if (mRootView.findViewById(R.id.addGroupLoadGroupImage) != null)
                        mRootView.findViewById(R.id.addGroupLoadGroupImage).setOnClickListener(onClickListener);
                }

                @Override
                public void onError(List<String> errors) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage("dialog creation errors: " + errors).create().show();
                    if (mRootView.findViewById(R.id.addGroupLoadGroupImage) != null)
                        mRootView.findViewById(R.id.addGroupLoadGroupImage).setOnClickListener(onClickListener);
                }
            });
        }
    }

    private void registerGroupInServer() {

        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                if (result != null && !result.equals("")) {
                    try {
                        Group newGroup = new Gson().fromJson(result, Group.class);
                        if (newGroup.getiGroupId() != 0 && newGroup.getiGroupId() != -1) {
                            // mGroup = newGroup;

                            GroupsListFragment.getInstance().addGroup(newGroup);
                            GroupsListFragment.isFromNewGroupFragment = true;
                            getActivity().onBackPressed();
                            //initFragment(GroupsListFragment.getInstance(), false);
                            Common.user.setbIsMainUser(true);
                        } else
                            Toast.makeText(getActivity(), getString(R.string.addGroupCreateNewGroupFail), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.addGroupCreateNewGroupFail), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), mGroup.toSend(), ConnectionUtil.CreateNewGroup);
    }

    private boolean checkGroupField() {//בדיקת תקינות לשדות
        ImageView imageView = (ImageView) mRootView.findViewById(R.id.addGroupImage);
        if (imageView.getDrawable() != null)
            mGroup.setNvImage(ImageHelper.getBase64FromImage(imageView));
        mGroup.setNvGroupName(((TextView) mRootView.findViewById(R.id.addGroupName)).getText().toString());
        mGroup.setNvComment(((TextView) mRootView.findViewById(R.id.addGroupMoreInfo)).getText().toString());//עוד על הקבוצה
        int radioButtonID = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) mRadioGroup.findViewById(radioButtonID);
        if (radioButton != null)
            mGroup.setiGroupType(Integer.parseInt(radioButton.getTag().toString()));
        if (mGroup.getNvGroupName().equals("")) {
            ((TextView) mRootView.findViewById(R.id.addGroupName)).setError(getResources().getString(R.string.groupErrorNameGroup));
            return false;
        } else
            ((TextView) mRootView.findViewById(R.id.addGroupName)).setError(null);
        if (radioButtonID == -1) {
            ((TextView) mRootView.findViewById(R.id.errorAddGroupType)).setError("");
            Toast.makeText(getActivity(), getString(R.string.error_field_required), Toast.LENGTH_LONG).show();
            return false;
        } else
            ((TextView) mRootView.findViewById(R.id.addGroupName)).setError(null);
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //mGetImage.onResult(requestCode, resultCode, data);
        try {
            cameraLauncher.onActivityResult(requestCode, resultCode, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

@Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Utils.HideKeyboard(getActivity());
    }
}
