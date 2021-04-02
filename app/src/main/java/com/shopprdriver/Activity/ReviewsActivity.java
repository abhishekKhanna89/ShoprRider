package com.shopprdriver.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shopprdriver.Model.ReviewsModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsActivity extends AppCompatActivity {

    RecyclerView RvReviews;
    ReviewAdapter adapter;
    List<ReviewsModel.Review> arListReviews;
    SessonManager sessonManager;
    TextView TvAverageRating, TvTotalReviews, TvNoReviews;
    RatingBar RatingbarR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        getSupportActionBar().setTitle("Reviews");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(ReviewsActivity.this);
        TvAverageRating = findViewById(R.id.TvAverageRating);
        RatingbarR = findViewById(R.id.RatingbarR);
        TvTotalReviews = findViewById(R.id.TvTotalReviews);
        RvReviews = findViewById(R.id.RvReviews);
        TvNoReviews = findViewById(R.id.TvNoReviews);
        ReviewsApi();
    }




    private void setReviews() {
        RvReviews.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ReviewsActivity.this, 1);
        RvReviews.setLayoutManager(layoutManager);

        adapter = new ReviewAdapter(ReviewsActivity.this, arListReviews);
        RvReviews.setAdapter(adapter);

    }

    private void ReviewsApi() {
        if (CommonUtils.isOnline(ReviewsActivity.this)) {
            sessonManager.showProgress(ReviewsActivity.this);
            Call<ReviewsModel>call= ApiExecutor.getApiService(this)
                    .apiReviews("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<ReviewsModel>() {
                @Override
                public void onResponse(Call<ReviewsModel> call, Response<ReviewsModel> response) {
                    sessonManager.hideProgress();
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            ReviewsModel reviewsModel=response.body();
                            if (reviewsModel.getData()!=null){
                                float averageRating=reviewsModel.getData().getAvgrating();
                                String totalAverageRating=reviewsModel.getData().getTotalreviews();
                                String aRating=String.valueOf(averageRating);
                                TvAverageRating.setText(aRating);
                                RatingbarR.setRating(averageRating);
                                TvTotalReviews.setText(totalAverageRating);
                                arListReviews=reviewsModel.getData().getReviews();
                                if (arListReviews.size()==0){
                                    RvReviews.setVisibility(View.GONE);
                                    TvNoReviews.setVisibility(View.VISIBLE);
                                }else {
                                    TvNoReviews.setVisibility(View.GONE);
                                    RvReviews.setVisibility(View.VISIBLE);
                                    setReviews();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ReviewsModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(ReviewsActivity.this, getString(R.string.please_check_network));
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
    public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

        Context context;
        List<ReviewsModel.Review> arList;

        public ReviewAdapter(Context context,List<ReviewsModel.Review> arList) {
            this.context = context;
            this.arList = arList;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_review, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Picasso.get().load(arList.get(position).getImage()).into(holder.CirImage);
            holder.TvNameReview.setText(arList.get(position).getName());
            float num = Float.parseFloat(arList.get(position).getRating());

            holder.ratingbar.setRating(num);
            //holder.TvRatingReview.setText(arList.get(position).getRating());
            holder.TvReviewReview.setText(arList.get(position).getReviews());

        }

        @Override
        public int getItemCount() {
            return arList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            CircleImageView CirImage;
            TextView TvNameReview, TvRatingReview, TvReviewReview;
            RatingBar ratingbar;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                CirImage = itemView.findViewById(R.id.CirImage);
                TvNameReview = itemView.findViewById(R.id.TvNameReview);
                ratingbar = itemView.findViewById(R.id.RatingbarReview);

                TvReviewReview = itemView.findViewById(R.id.TvReviewReview);
            }
        }
    }
}
