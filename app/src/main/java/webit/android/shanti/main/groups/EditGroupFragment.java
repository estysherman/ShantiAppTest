package webit.android.shanti.main.groups;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.AddMembersCustomView;
import webit.android.shanti.customViews.CustomViewsInitializer;
import webit.android.shanti.customViews.ICallBackCustomViewsData;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.Group;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.general.photo.ImageHelper;
import webit.android.shanti.general.photo.getimage.CallbackContext;
import webit.android.shanti.general.photo.getimage.CameraLauncher;
import webit.android.shanti.general.photo.getimage.CameraLauncherArguments;
import webit.android.shanti.main.MainBaseFragment;

public class EditGroupFragment extends MainBaseFragment {

    private static final String ARG_GROUP = "group";

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    public List<Integer> mUserToDelete;
    CameraLauncher cameraLauncher;
    CameraLauncherArguments arguments;
    AddMembersCustomView mSddMembersView;
    Bitmap mGroupBitmap;
    private View mRootView;
    private Group mGroup;
    private ImageView mGroupImage;
    private ListView membersListView;
    private RadioGroup mGroupType;
    private EditText mGroupName, mGroupComments;
    CallbackContext callbackContext = new CallbackContext() {

        @Override
        public void success(Bitmap bitmap) {
            super.success(bitmap);
            mGroupBitmap = bitmap;
            mGroupImage.setImageBitmap(bitmap);
        }

    };


    public EditGroupFragment() {
        // Required empty public constructor
    }

    public static EditGroupFragment getInstance(String groupAsJson) {
        EditGroupFragment fragment = new EditGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP, groupAsJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroup = new Gson().fromJson(getArguments().getString(ARG_GROUP), Group.class);
        }
    }

    @Override
    public void changeActionBar(View view, String title) {
        super.changeActionBar(view, title);
        setActionBarEvents(view);
        setBackView(view.findViewById(R.id.profileGroupActionBack));
        changeVisibility(view.findViewById(R.id.profileGroupSaveChanges), View.VISIBLE);
        changeVisibility(view.findViewById(R.id.profileGroupEdit), View.GONE);
    }

    @Override
    public void setActionBarEvents(View view) {
        super.setActionBarEvents(view);
        view.findViewById(R.id.profileGroupSaveChanges).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChange();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_edit_group, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //mRootView  = inflater.inflate(R.layout.fragment_edit_group, null);
        changeActionBar(mRootView, "");
        setViews();
        cameraLauncher = new CameraLauncher(this);
        if (savedInstanceState != null) {
            mGroupName.setText(savedInstanceState.get("GroupName").toString());
            mGroupComments.setText(savedInstanceState.get("GroupComments").toString());
        }
        return mRootView;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("GroupName", mGroupName.getText().toString());
        outState.putString("GroupComments", mGroupComments.getText().toString());
    }

    private void initRadioGroup() {
        CustomViewsInitializer initializer = new CustomViewsInitializer(getActivity());
        initializer.getCodeTable(CodeValue.groupType, new ICallBackCustomViewsData() {
            @Override
            public void doCallBack(ArrayList<CodeValue> objects) {
                RadioButton radioButton;
                if (objects == null || objects.size() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.groupEditLoadViewsError), Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().onBackPressed();
                        }
                    }, 200);
                } else
                    for (CodeValue codeValue : objects) {
                        radioButton = new RadioButton(getActivity());
                        radioButton.setText(codeValue.getNvValue());
                        radioButton.setTag(codeValue.getiKeyId());
                        mGroupType.addView(radioButton);
                        if (codeValue.getiKeyId() == mGroup.getiGroupType())
                            radioButton.setChecked(true);
                        else radioButton.setChecked(false);
                    }
            }
        });
    }

    private void setViews() {
        mUserToDelete = new ArrayList<>();
        mGroupName = (EditText) mRootView.findViewById(R.id.groupEditGroupName);
        mGroupComments = (EditText) mRootView.findViewById(R.id.groupEditComments);
        mGroupImage = (ImageView) mRootView.findViewById(R.id.groupEditImage);
        membersListView = (ListView) mRootView.findViewById(R.id.groupEditFriendsList);
        mGroupType = (RadioGroup) mRootView.findViewById(R.id.groupEditGroupType);

        mGroupName.setText(mGroup.getNvGroupName());
        mGroupComments.setText(mGroup.getNvComment());
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGroupBitmap = ImageHelper.getBitmapFromURL(mGroup.getNvImage(), getActivity());
            }
        }).start();
        new Utils.DownloadImageTask(mGroupImage).execute(mGroup.getNvImage());

        membersListView.setAdapter(new GroupVerticalMembersAdapter(mGroup.getUsersList(), this, true, mGroup.getiMainUserId()));
//        membersListView.findViewById(R.id.itemGroupProfileMemberRemove).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mGroup.getUsersList();
//                mUserToDelete.add(mGroup.getUsersList().get(GroupVerticalMembersAdapter.deletePosition).getiUserId());
//            }
//        });
        mSddMembersView = ((AddMembersCustomView) mRootView.findViewById(R.id.groupEditAddMembers));
        mSddMembersView.setmUsersExistInGroup(User.getUsersId(mGroup.getUsersList()));
        mSddMembersView.setGroupId(mGroup.getiGroupId());
        final Fragment fragment = this;
        mGroupImage.post(new Runnable() {
            @Override
            public void run() {
                arguments = new CameraLauncherArguments(mGroupImage.getWidth(), mGroupImage.getHeight(), true, null);
            }
        });

        initRadioGroup();

        mRootView.findViewById(R.id.groupEditRemoveGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeGroup();
            }
        });

        mRootView.findViewById(R.id.groupEditImageReplace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arguments.eSourceType = CameraLauncherArguments.ESourceType.GALLERY;
                cameraLauncher.execute(arguments, callbackContext, getActivity().getApplicationContext());
            }
        });
        mRootView.findViewById(R.id.groupEditImageRetake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arguments.eSourceType = CameraLauncherArguments.ESourceType.CAMERA;
                cameraLauncher.execute(arguments, callbackContext, getActivity().getApplicationContext());
            }
        });
        mRootView.findViewById(R.id.groupEditImageDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Utils.DownloadImageTask(mGroupImage).execute(Group.defultGroupImageUrl);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mGroupBitmap = ImageHelper.getBitmapFromURL(Group.defultGroupImageUrl, getActivity());
                    }
                }).start();

            }
        });
        mSddMembersView.setmPageScrollView((ScrollView) mRootView.findViewById(R.id.groupEditPageScroller));
    }

    private void saveChange() {
        if (checkGroupField()) {
            List<Integer> usersToAdd = new ArrayList<>();
            for (User user : mSddMembersView.getmUsersGroup())
                usersToAdd.add(user.getiUserId());
            mUserToDelete = GroupVerticalMembersAdapter.usersToDelete;
            UpdateGroupToSend groupToSend = new UpdateGroupToSend(mGroup, usersToAdd, mUserToDelete);
            GroupVerticalMembersAdapter.usersToDelete = null;
            new GeneralTask(getActivity(), new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    Group group = new Gson().fromJson(result, Group.class);
                    if (group == null || group.getiGroupId() == -1)
                        Toast.makeText(getActivity(), getString(R.string.groupEditUpdateError), Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(getActivity(), getString(R.string.groupEditUpdateSuccess), Toast.LENGTH_SHORT).show();
                        group.setUsersList(mGroup.getUsersList());
                        GroupChatFragment.getInstance(getActivity(), new Gson().toJson(group)).updateGroup(group, mGroupBitmap);
                        GroupProfileFragment.getInstance(new Gson().toJson(group)).updateGroup(group, mGroupBitmap);
                        GroupsListFragment.getInstance().updateGroup(group, mGroupBitmap);

//                        FragmentManager fm = getActivity().getSupportFragmentManager();
//                        fm.popBackStack(EditGroupFragment.class.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        fm.popBackStack(GroupProfileFragment.class.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                        fm.popBackStack(GroupChatFragment.class.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                }
            }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), groupToSend.getJson(), ConnectionUtil.UpdateGroup);
        }
    }

    private boolean checkGroupField() {
        Group newGroup = Group.copy(mGroup);
        mGroup.setNvImage(ImageHelper.getBase64FromBitmap(mGroupBitmap));
        mGroup.setUsersList(((GroupVerticalMembersAdapter) membersListView.getAdapter()).getmUsers());
        mGroup.setNvGroupName(mGroupName.getText().toString());
        mGroup.setNvComment(mGroupComments.getText().toString());
        int radioButtonID = mGroupType.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) mGroupType.findViewById(radioButtonID);
        if (radioButton != null)
            mGroup.setiGroupType(Integer.parseInt(radioButton.getTag().toString()));
        if (mGroup.getNvGroupName().equals("") || radioButtonID == -1) {
            Toast.makeText(getActivity(), getString(R.string.error_field_required), Toast.LENGTH_LONG).show();
            mGroup = newGroup;
            return false;
        }
        return true;
    }

    public void removeGroup() {

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("");
        alertDialog.setMessage(getString(R.string.groupEditRemoveDialogMessage));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.groupEditRemoveDialogOK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        QBChatService.getInstance().getGroupChatManager().deleteDialog(mGroup.getNvQBDialogId(), new QBEntityCallbackImpl<Void>() {
                            @Override
                            public void onSuccess() {
                                new GeneralTask(getActivity(), new UseTask() {
                                    @Override
                                    public void doAfterTask(String result) {
                                        if (result.equals("false"))
                                            Toast.makeText(getActivity(), R.string.groupEditRemoveError, Toast.LENGTH_SHORT).show();
                                        else {
                                            // groupsAdapter.notifyDataSetChanged();
                                            //GroupsListFragment.getInstance().removeGroup(mGroup.getiGroupId());
                                            FragmentManager fm = getActivity().getSupportFragmentManager();
                                            fm.popBackStack(GroupChatFragment.class.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);//מוציא את הפרגמנט של הקבוצה מהמחסנית - המחסנית מיועדת שיוכלו לבור ממסך למסך באמצעות הכפתור חזור
                                            fm.popBackStack(GroupProfileFragment.class.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                            fm.popBackStack(EditGroupFragment.class.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                            GroupsListFragment.getInstance().removeGroup(mGroup.getiGroupId());
                                        }
                                    }
                                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"iGroupId\":" + mGroup.getiGroupId() + "}", ConnectionUtil.DeleteGroup);

                            }

                            @Override
                            public void onError(List<String> errors) {
                                if (errors != null && errors.get(0) != null)
                                    Utils.sendToLog(getActivity(), "removeGroup", errors.get(0));
                                Toast.makeText(getActivity(), getActivity().getString(R.string.errorRemoveGroupFromQB), Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.groupEditRemoveDialogCancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            cameraLauncher.onActivityResult(requestCode, resultCode, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
