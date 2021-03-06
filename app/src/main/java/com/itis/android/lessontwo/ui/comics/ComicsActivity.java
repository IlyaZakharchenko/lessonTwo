package com.itis.android.lessontwo.ui.comics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itis.android.lessontwo.R;
import com.itis.android.lessontwo.api.ApiFactory;
import com.itis.android.lessontwo.model.comics.Comics;
import com.itis.android.lessontwo.model.comics.ComicsResponse;
import com.itis.android.lessontwo.model.comics.ComicsResponseData;
import com.itis.android.lessontwo.model.comics.ComicsTextObject;
import com.itis.android.lessontwo.ui.base.BaseActivity;
import com.itis.android.lessontwo.ui.comics.ComicsContract.Presenter;
import com.itis.android.lessontwo.utils.ImageLoadHelper;
import com.itis.android.lessontwo.utils.RxUtils;

import static com.itis.android.lessontwo.utils.Constants.ID_KEY;
import static com.itis.android.lessontwo.utils.Constants.NAME_KEY;

/**
 * Created by Nail Shaykhraziev on 25.02.2018.
 */
public class ComicsActivity extends BaseActivity implements ComicsContract.View{

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView ivCover;
    private TextView tvDescription;
    private TextView tvPrice;
    private TextView tvPages;
    private ProgressBar progressBar;
    private Presenter presenter;

    public static void start(@NonNull Activity activity, @NonNull Comics comics) {
        Intent intent = new Intent(activity, ComicsActivity.class);
        intent.putExtra(NAME_KEY, comics.getName());
        intent.putExtra(ID_KEY, comics.getId());
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.container);
        getLayoutInflater().inflate(R.layout.activity_comics, contentFrameLayout);
        initViews();

        new ComicsPresenter(this);
        long id = getIntent().getLongExtra(ID_KEY, 0);
        presenter.loadComics(id);
    }

    private void initViews() {
        findViews();
        supportActionBar(toolbar);
        setBackArrow(toolbar);
        collapsingToolbar.setTitle(getIntent().getStringExtra(NAME_KEY));
    }

    private void findViews() {
        collapsingToolbar = findViewById(R.id.ct_comics);
        toolbar = findViewById(R.id.tb_comics);
        ivCover = findViewById(R.id.iv_comics);
        tvDescription = findViewById(R.id.tv_description);
        tvPrice = findViewById(R.id.tv_price);
        tvPages = findViewById(R.id.tv_pages);
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    public void handleError(Throwable error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showComics(@NonNull Comics comics) {
        ImageLoadHelper.loadPicture(ivCover, String.format("%s.%s", comics.getImage().getPath(),
                comics.getImage().getExtension()));
        if (comics.getTextObjects() != null){
            StringBuilder description = new StringBuilder();
            for (ComicsTextObject comicsTextObject : comics.getTextObjects()) {
                description.append(comicsTextObject.getText()).append("\n");
            }
            tvDescription.setText(description.toString().trim());
        }
        if (comics.getPrices() != null && !comics.getPrices().isEmpty()){
            tvPrice.setText(getString(R.string.price_format, String.valueOf(comics.getPrices().get(0).getPrice())));
        }
        tvPages.setText(String.valueOf(comics.getPageCount()));
    }
}
