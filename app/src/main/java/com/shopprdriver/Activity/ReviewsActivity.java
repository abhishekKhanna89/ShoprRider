package com.shopprdriver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shopprdriver.Model.OrderDeatilsList.OrderDeatilsListModel;
import com.shopprdriver.Model.ReviewsModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsActivity extends AppCompatActivity {

    RecyclerView RvReviews;
    ReviewAdapter adapter;
    ArrayList<ReviewsModel.Review> arListReviews;
    SessonManager sessonManager;
    TextView TvAverageRating, TvTotalReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        getSupportActionBar().setTitle("Reviews");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TvAverageRating = findViewById(R.id.TvAverageRating);
        TvTotalReviews = findViewById(R.id.TvTotalReviews);
        RvReviews = findViewById(R.id.RvReviews);

        arListReviews = new ArrayList<>();
        sessonManager = new SessonManager(ReviewsActivity.this);
        setReviews();
        ReviewsApi();
    }


    public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

        Context context;
        ArrayList<ReviewsModel.Review> arList;

        public ReviewAdapter(Context context, ArrayList<ReviewsModel.Review> arList) {
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
            Call<ReviewsModel> call = ApiExecutor.getApiService(this)
                    .apiReviews("Bearer " + sessonManager.getToken());
            call.enqueue(new Callback<ReviewsModel>() {
                @Override
                public void onResponse(Call<ReviewsModel> call, Response<ReviewsModel> response) {
                    sessonManager.hideProgress();
                    String resss = new Gson().toJson(response.body());
                    Log.d("dfjjhjk", resss);
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            TvAverageRating.setText(String.valueOf(response.body().getData().getAvgrating()));
                            TvTotalReviews.setText(String.valueOf(response.body().getData().getTotalreviews()));

                            if (response.body().getData().getReviews() != null && response.body().getData().getReviews().size() > 0) {
                                arListReviews.clear();
                                arListReviews.addAll(response.body().getData().getReviews());
                                adapter.notifyDataSetChanged();
                            } else {
                                arListReviews.clear();
                                arListReviews.addAll(response.body().getData().getReviews());
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }
                }

                @Override
                public void onFailure(Call<ReviewsModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
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
}
