package com.hnpolice.xiaoke.materialdesign;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.agera.BaseObservable;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;
import com.hnpolice.xiaoke.materialdesign.book.Book;
import com.hnpolice.xiaoke.materialdesign.book.BooksSupplier;
import com.hnpolice.xiaoke.materialdesign.book.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * create by luoxiaoke on 2016/5/31 9:25.
 * use for 书籍列表界面
 */
public class BookFragment extends Fragment implements Updatable {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private ProgressBar mProbar;
    private MyAdapter mAdapter;

    private static final int ANIM_DURATION_FAB = 400;
    private ExecutorService networkExecutor;
    private Repository<Result<List<Book>>> booksRepository;
    private SearchObservable searchObservable;
    private BooksSupplier booksSupplier;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, null);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFab = (FloatingActionButton) view.findViewById(R.id.fab_normal);

        mProbar = (ProgressBar) view.findViewById(R.id.progress_bar);

        mAdapter = new MyAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        setUpFAB(view);

        setUpRepository();

        return view;
    }


    public class SearchObservable extends BaseObservable {

        public void doSearch(String key) {
            booksSupplier.setKey(key);
            dispatchUpdate();
        }

    }


    private void setUpRepository() {
        // Set up background executor
        networkExecutor = newSingleThreadExecutor();

        searchObservable = new SearchObservable();

        booksSupplier = new BooksSupplier(getString(R.string.default_search_keyword));

        // Set up books repository
        booksRepository = Repositories
                .repositoryWithInitialValue(Result.<List<Book>>absent())
                .observe(searchObservable)
                .onUpdatesPerLoop()
                .goTo(networkExecutor)
                .thenGetFrom(booksSupplier)
                .compile();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFab.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        //doSearch(getString(R.string.default_search_keyword));
    }

    @Override
    public void update() {
        mProbar.setVisibility(View.GONE);
        startFABAnimation();
        if (booksRepository.get().isPresent()) {
            mAdapter.updateItems(booksRepository.get().get(), true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        booksRepository.addUpdatable(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        booksRepository.removeUpdatable(this);
    }

    private void doSearch(String keyword) {
        mProbar.setVisibility(View.VISIBLE);
        mAdapter.clearItems();
        searchObservable.doSearch(keyword);
    }


    private void setUpFAB(View view) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.search_book, null);
        final EditText edSeach = (EditText) v.findViewById(R.id.et_search);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.search)
                        .setView(edSeach)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!TextUtils.isEmpty(edSeach.getText().toString())) {
                                    doSearch(edSeach.getText().toString());
                                }
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    private void startFABAnimation() {
        mFab.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(500)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Book book = mAdapter.getBook(position);
            // TODO: 2016/5/31  
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("book", book);

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            view.findViewById(R.id.iv_book), "transition_book_img");

            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

        }
    };

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private final int mBackground;
        private List<Book> mBooks = new ArrayList<>();
        private final TypedValue mTypedValue = new TypedValue();

        private static final int ANIMATED_ITEMS_COUNT = 4;

        private boolean animateItems = false;
        private int lastAnimatedPosition = -1;

        public MyAdapter(Context context) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            runEnterAnimation(holder.itemView, position);
            Book book = mBooks.get(position);
            holder.tvTitle.setText(book.getTitle());
            String desc = "作者: " + (book.getAuthor().length > 0 ? book.getAuthor()[0] : "") + "\n副标题: " + book.getSubtitle()
                    + "\n出版年: " + book.getPubdate() + "\n页数: " + book.getPages() + "\n定价:" + book.getPrice();
            holder.tvDesc.setText(desc);
            Glide.with(holder.ivBook.getContext())
                    .load(book.getImage())
                    .centerCrop()
                    .into(holder.ivBook);
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
                        .setStartDelay(100 * position)
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
            // each data item is just a string in this case
            public ImageView ivBook;
            public TextView tvTitle;
            public TextView tvDesc;

            public int position;

            public ViewHolder(View v) {
                super(v);
                ivBook = (ImageView) v.findViewById(R.id.iv_book);
                tvTitle = (TextView) v.findViewById(R.id.tv_title);
                tvDesc = (TextView) v.findViewById(R.id.tv_description);
            }
        }



        public void updateItems(List<Book> books, boolean animated) {
            animateItems = animated;
            lastAnimatedPosition = -1;
            mBooks.addAll(books);
            notifyDataSetChanged();
        }

        public void clearItems() {
            mBooks.clear();
            notifyDataSetChanged();
        }

        public Book getBook(int pos) {
            return mBooks.get(pos);
        }
    }
}
