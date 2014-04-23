package com.example.tut02_actionbarnavigationdrawer;



import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

  @SuppressLint("NewApi")
   public class GP_Fragment extends Fragment {

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
      {
              View rootView = inflater .inflate(R.layout.gp_fragment, container, false);  
              return rootView;
      }
 }