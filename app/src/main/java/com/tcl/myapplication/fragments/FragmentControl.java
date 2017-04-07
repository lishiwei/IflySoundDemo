package com.tcl.myapplication.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dinuscxj.itemdecoration.GridOffsetsItemDecoration;
import com.tcl.myapplication.R;
import com.tcl.myapplication.adapter.ControlRecyclerAdapter;
import com.tcl.myapplication.util.DensityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentControl#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentControl extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.recyclerview_control)
    RecyclerView mRecyclerviewControl;
//    @Bind(R.id.listview)
//    ListView mListview;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentControl() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentControl.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentControl newInstance(String param1, String param2) {
        FragmentControl fragment = new FragmentControl();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerview();
    }



    private void initRecyclerview() {
        GridOffsetsItemDecoration offsetsItemDecoration = new GridOffsetsItemDecoration(
                GridOffsetsItemDecoration.GRID_OFFSETS_VERTICAL);
        offsetsItemDecoration.setVerticalItemOffsets(DensityUtils.dp2px(12));
        offsetsItemDecoration.setHorizontalItemOffsets(DensityUtils.dp2px(12));
        mRecyclerviewControl.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mRecyclerviewControl.setAdapter(new ControlRecyclerAdapter(getActivity()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
