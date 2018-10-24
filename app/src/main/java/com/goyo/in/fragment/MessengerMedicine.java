package com.goyo.in.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goyo.in.AddMedicineImage;
import com.goyo.in.R;
import com.goyo.in.StatusMedicine;
import com.goyo.in.Utils.CustomDialog;

import java.io.File;
import java.util.ArrayList;

public class MessengerMedicine extends Fragment {


    private CustomDialog customDialog;
    private FloatingActionButton _add_image,_status;
    private View view;
    private String[] arrFilePaths = new String[4];
    ArrayList<File> CompressedImage = new ArrayList<>();
    public MessengerMedicine() {
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
       view= inflater.inflate(R.layout.fragment_messenger_medicine, container, false);
        _add_image=(FloatingActionButton)view.findViewById(R.id.add_image);
        _status=(FloatingActionButton)view.findViewById(R.id.status);

        _add_image.setOnClickListener(new View.OnClickListener() {
                @Override
        public void onClick(View v) {
            Intent intent=new Intent(getActivity(), AddMedicineImage.class);
            startActivity(intent);
//                pickImages();
        }
    });

        _status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), StatusMedicine.class);
                startActivity(intent);
//                pickImages();
            }
        });

        return view;
    }
}
