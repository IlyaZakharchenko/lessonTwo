package com.itis.android.lessontwo.ui.comicslist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.itis.android.lessontwo.R;
import com.itis.android.lessontwo.model.comics.Comics;
import com.itis.android.lessontwo.ui.base.BaseActivity;
import com.itis.android.lessontwo.ui.base.BaseAdapter;
import com.itis.android.lessontwo.ui.comics.ComicsActivity;
import com.itis.android.lessontwo.widget.EmptyStateRecyclerView;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nail Shaykhraziev on 25.02.2018.
 */

public class ComicsListActivity extends BaseActivity implements ComicsListContract.View,
        BaseAdapter.OnItemClickListener<Comics> {

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private EmptyStateRecyclerView recyclerView;
    private TextView tvEmpty;

    private ComicsAdapter adapter;
    private ComicsListContract.Presenter presenter;

    private boolean isLoading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_comics_list, contentFrameLayout);
        initViews();
        initRecycler();
        new ComicsListPresenter(this);
        presenter.loadComics();
    }

    @Override
    public void setPresenter(ComicsListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void handleError(Throwable error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showItems(@NonNull List<Comics> items) {
        adapter.changeDataSet(items);
    }

    @Override
    public void showDetails(Comics item) {
        ComicsActivity.start(this, item);
    }

    @Override
    public void addMoreItems(List<Comics> items) {
        adapter.addAll(items);
    }

    @Override
    public void setNotLoading() {
        isLoading = false;
    }

    public void showLoading(Disposable disposable) {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(@NonNull Comics item) {
        presenter.onItemClick(item);
    }

    private void initRecycler() {
        adapter = new ComicsAdapter(new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setEmptyView(tvEmpty);
        adapter.attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int currentPage = 0;
            // обычно бывает флаг последней страницы, но я че т его не нашел, если не найдется, то можно удалить, всегда тру
            private boolean isLastPage = false;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = manager.getChildCount();
                int totalItemCount = manager.getItemCount();
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= 20) {
                        isLoading = true;
                        presenter.loadNextElements(++currentPage);
                    }
                }
            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.tb_comics_list);
        progressBar = findViewById(R.id.pg_comics_list);
        recyclerView = findViewById(R.id.rv_comics_list);
        tvEmpty = findViewById(R.id.tv_empty);
        supportActionBar(toolbar);
    }
}
