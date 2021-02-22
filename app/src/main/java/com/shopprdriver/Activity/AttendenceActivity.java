package com.shopprdriver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shopprdriver.Model.Attendences.Attendence;
import com.shopprdriver.Model.Attendences.AttendencesModel;
import com.shopprdriver.R;
import com.shopprdriver.Server.ApiExecutor;
import com.shopprdriver.Session.CommonUtils;
import com.shopprdriver.Session.SessonManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendenceActivity extends AppCompatActivity {
    RecyclerView attendanceRecyclerView;
    SessonManager sessonManager;
    List<Attendence>attendenceList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager=new SessonManager(this);
        attendanceRecyclerView=findViewById(R.id.attendanceRecyclerView);
        attendanceRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        viewAttendanceList();
    }

    private void viewAttendanceList() {
        if (CommonUtils.isOnline(this)) {
            sessonManager.showProgress(this);
            //Log.d("token",sessonManager.getToken());
            Call<AttendencesModel> call= ApiExecutor.getApiService(this)
                    .apiAttendence("Bearer "+sessonManager.getToken());
            call.enqueue(new Callback<AttendencesModel>() {
                @Override
                public void onResponse(Call<AttendencesModel> call, Response<AttendencesModel> response) {
                    sessonManager.hideProgress();
                    if (response.body()!=null){
                        if (response.body().getStatus()!= null && response.body().getStatus().equals("success")){
                            AttendencesModel attendencesModel=response.body();
                            if(attendencesModel.getData().getAttendences()!=null) {
                                attendenceList=attendencesModel.getData().getAttendences();
                                AttendanceAdapter attendanceAdapter=new AttendanceAdapter(AttendenceActivity.this,
                                        attendenceList);
                                attendanceRecyclerView.setAdapter(attendanceAdapter);
                                attendanceAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<AttendencesModel> call, Throwable t) {
                    sessonManager.hideProgress();
                }
            });
        }else {
            CommonUtils.showToastInCenter(AttendenceActivity.this, getString(R.string.please_check_network));
        }
    }
    public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.Holder>{
        List<Attendence>attendenceList;
        Context context;
        public AttendanceAdapter(Context context,List<Attendence>attendenceList){
            this.context=context;
            this.attendenceList=attendenceList;
        }
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.layout_attendance_date,null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Attendence attendence=attendenceList.get(position);
            holder.button.setText(attendence.getDate());
            holder.check_inTime.setText(attendence.getCheckinAddress()+" , "+attendence.getCheckin());
            holder.check_outTime.setText(attendence.getCheckoutAddress()+" , "+attendence.getCheckout());

        }

        @Override
        public int getItemCount() {
            return attendenceList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            Button button;
            TextView check_inTime,check_outTime;
            public Holder(@NonNull View itemView) {
                super(itemView);
                button=itemView.findViewById(R.id.button);
                check_inTime=itemView.findViewById(R.id.check_inTime);
                check_outTime=itemView.findViewById(R.id.check_outTime);
            }
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