package webit.android.shanti.main.groups;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import webit.android.shanti.R;
import webit.android.shanti.entities.Group;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;

import java.lang.reflect.Type;
import java.util.List;

public class ApprovalGroupFragment extends GroupMainFragment {

    ApprovalGroupAdapter approvalGroupAdapter;
    public static ApprovalGroupFragment instance = null;


    public static ApprovalGroupFragment getInstance() {
        if (instance == null)
            instance = new ApprovalGroupFragment();
        return instance;
    }

    View rootView;
    List<Group> groups;

    public ApprovalGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_approval_group, container, false);
        changeActionBar(rootView, getString(R.string.approvalJoinRequests), "", View.INVISIBLE);
//        getGroupsList();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getGroupsList();
    }

    public void refresh() {
        final android.support.v4.app.Fragment fragment = this;
        // getGroupsList();
        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                Type programsListType = new TypeToken<List<Group>>() {
                }.getType();
                groups = new Gson().fromJson(result, programsListType);
                if (groups != null) {
                    //approvalGroupAdapter.setData(groups);
                    approvalGroupAdapter = new ApprovalGroupAdapter(groups, fragment);
                    ListView listView = (ListView) rootView.findViewById(R.id.approvalGroupsList);
                    listView.setEmptyView(rootView.findViewById((R.id.approvalGroupEmptyList)));
                    listView.setAdapter(approvalGroupAdapter);
                }
            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + Common.user.getiUserId() + "}", ConnectionUtil.GetUserPendingGroups);
    }

    private void getGroupsList() {//רשימת בקשות הצטרפות
        final android.support.v4.app.Fragment fragment = this;
        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                Type programsListType = new TypeToken<List<Group>>() {
                }.getType();
                groups = new Gson().fromJson(result, programsListType);
                if (groups == null)
                    return;
                approvalGroupAdapter = new ApprovalGroupAdapter(groups, fragment);
                ListView listView = (ListView) rootView.findViewById(R.id.approvalGroupsList);
                listView.setEmptyView(rootView.findViewById((R.id.approvalGroupEmptyList)));
                listView.setAdapter(approvalGroupAdapter);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + Common.user.getiUserId() + "}", ConnectionUtil.GetUserPendingGroups);
    }


}
