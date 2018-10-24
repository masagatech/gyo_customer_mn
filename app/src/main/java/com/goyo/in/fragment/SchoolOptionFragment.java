package com.goyo.in.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.goyo.in.AddMoneyDetail;
import com.goyo.in.ModelClasses.LoginTransfer;
import com.goyo.in.R;
import com.goyo.in.Utils.Global;
import com.goyo.in.Utils.Preferences;
import com.goyo.in.school.MyKids;

import static com.goyo.in.MainActivity.CURRENT_TAG;
import static com.goyo.in.MainActivity.TAG_MY_KIDS;
import static com.goyo.in.MainActivity.navItemIndex;

/**
 * A simple {@link Fragment} subclass.
 */
public class SchoolOptionFragment extends Fragment implements View.OnClickListener {


    private View view;
    private FrameLayout ParentApp, TeacherApp;
    private NavigationView navigationView;
    private Button Btn_track, btn_addmoney;
    private TextView actionbar_title;

    public SchoolOptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_school_option, container, false);

        //initialize all buttons
        Init();


        return view;
    }

    private Void Init() {

        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        actionbar_title = (TextView) getActivity().findViewById(R.id.actionbar_title);
        ParentApp = (FrameLayout) view.findViewById(R.id.btn_parentapp);
        TeacherApp = (FrameLayout) view.findViewById(R.id.btn_teacherapp);
        Btn_track = (Button) view.findViewById(R.id.btn_track);
        btn_addmoney = (Button) view.findViewById(R.id.btn_addmoney);

//        actionbar_title.setText("SCHOOL");
        ParentApp.setOnClickListener(this);
        TeacherApp.setOnClickListener(this);
        Btn_track.setOnClickListener(this);
        btn_addmoney.setOnClickListener(this);
        return null;
    }

//    public void startNewActivity(Context context, String packageName) {
//        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
//        if (intent != null) {
//            // We found the activity now start the activity
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        } else {
//            // Bring user to the market or let them choose an app?
//            intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setData(Uri.parse("market://details?id=" + packageName));
//            context.startActivity(intent);
//        }
//    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_parentapp:
                Global.OpenAppWithParams(getActivity(),"com.goyo.parent");
                break;
            case R.id.btn_teacherapp:
                Intent intent = getContext().getPackageManager().getLaunchIntentForPackage("com.goyo.teacher");
                Global.startNewActivity(getActivity(), intent,"com.goyo.teacher");
                break;

            case R.id.btn_track:

                navItemIndex = 3;
                actionbar_title.setText(R.string.nav_my_kids);
                CURRENT_TAG = TAG_MY_KIDS;
                SetCheckedNavItem(navItemIndex);
                com.goyo.in.school.MyKids MyKids2 = new MyKids();
                CallBack(MyKids2);
                break;
            case R.id.btn_addmoney:

                Intent intents = new Intent(this.getContext(), AddMoneyDetail.class);
                intents.putExtra("addMoneyAmount", "365");
                startActivity(intents);

                break;
        }
    }

    //opening new fragment
    private void CallBack(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    //highlighting selected nav item
    private void SetCheckedNavItem(int ItemCount) {
        if (navigationView != null) {
            navigationView.getMenu().getItem(ItemCount).setChecked(true);
        }
    }
}
