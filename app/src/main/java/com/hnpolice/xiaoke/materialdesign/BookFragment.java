package com.hnpolice.xiaoke.materialdesign;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * create by luoxiaoke on 2016/5/31 9:25.
 * use for 列表展示界面
 */
public class BookFragment extends Fragment {

    private FloatingActionButton mFab;
    private List<String> mBooks = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, null);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFab = (FloatingActionButton) view.findViewById(R.id.fab_normal);

        initData();
        mRecyclerView.setAdapter(new MyAdapter());

        setUpFAB(view);

        return view;
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            mBooks.add(String.valueOf(i));
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFab.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
    }

    @Override
    public void onResume() {
        super.onResume();
        startFABAnimation();
    }

    private void setUpFAB(View view) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.search_book, null);
        final EditText edSeach = (EditText) v.findViewById(R.id.et_search);
        edSeach.setHint("input what you want");
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Snackbar snackbar = Snackbar.make(view, "search what you want", Snackbar.LENGTH_INDEFINITE)
                        .setAction("搜索", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(getActivity())
                                        .setMessage(R.string.search)
                                        .setView(edSeach)
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        });
                snackbar.getView().setBackgroundColor(0xFF607d8b);
                snackbar.show();

            }
        });
    }

    private void startFABAnimation() {
        mFab.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(500)
                .setDuration(500)
                .start();
    }

    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Toast.makeText(getActivity(), position + "", Toast.LENGTH_SHORT).show();
        }
    };

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private static final int ANIMATED_ITEMS_COUNT = 4;

        private boolean animateItems = true;
        private int lastAnimatedPosition = -1;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            runEnterAnimation(holder.itemView, position);
        }

        private void runEnterAnimation(View view, int position) {
            if (!animateItems || position >= ANIMATED_ITEMS_COUNT - 1) {
                return;
            }

            if (position > lastAnimatedPosition) {
                lastAnimatedPosition = position;
                view.setTranslationY(getResources().getDisplayMetrics().heightPixels);
                view.animate()
                        .translationY(0)
                        .setStartDelay(200 * position)
                        .setInterpolator(new DecelerateInterpolator(3.f))
                        .setDuration(700)
                        .start();
            }
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView ivBook;
            public TextView tvTitle;
            public TextView tvDesc;


            public ViewHolder(View v) {
                super(v);
                ivBook = (ImageView) v.findViewById(R.id.iv_book);
                tvTitle = (TextView) v.findViewById(R.id.tv_title);
                tvDesc = (TextView) v.findViewById(R.id.tv_description);
            }
        }

    }
}
