package com.home.khalil.employeedirectory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by khalil on 4/5/18.
 */

public class EmployeeRecycleViewAdapter extends RecyclerView.Adapter<EmployeeRecycleViewAdapter.ViewHolder> {

    public ArrayList<Employee> employeesList;



    public EmployeeRecycleViewAdapter(ArrayList<Employee> employeesList){
        this.employeesList=employeesList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.main_custom_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name=employeesList.get(position).getName();
        String role=employeesList.get(position).getRole();
        role = role.substring(0,1).toUpperCase() + role.substring(1).toLowerCase();
        String imageUri= employeesList.get(position).getImageUri();
        String department= employeesList.get(position).getDepartment();
        String location =employeesList.get(position).getLocation();
        String email= employeesList.get(position).getEmail();
        String phoneNumber= employeesList.get(position).getPhoneNumber();
        String manager=employeesList.get(position).getManager();
        ArrayList<String> directReportEmp=employeesList.get(position).getDirectReportEmp();


        holder.nameText.setText(name);
        holder.roleText.setText(role);
        if(!(imageUri.equals("empty"))){
            Picasso.get().load(imageUri).placeholder(R.drawable.progress_animation).into(holder.profileImage);


        }else{
            holder.profileImage.setImageResource(R.drawable.default_profile_pic);
        }

       final Bundle bundle = new Bundle();
        final String id=employeesList.get(position).employeeID;
        bundle.putString("name", name);
        bundle.putString("role", role);
        bundle.putString("imageUri", imageUri);
        bundle.putString("department",department);
        bundle.putString("location", location);
        bundle.putString("email", email);
        bundle.putString("phoneNumber", phoneNumber);
        bundle.putString("ID",id);
        bundle.putString("manager",manager);
        bundle.putStringArrayList("direct",directReportEmp);



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Log.d("userID",id+"");

                Intent i = new Intent(view.getContext(),EmployeeProfileActivity.class);
                i.putExtras(bundle);
                view.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return employeesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView nameText;
        public TextView roleText;
        public CircleImageView profileImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            nameText= (TextView) mView.findViewById(R.id.main_row_name);
            roleText= (TextView) mView.findViewById(R.id.main_row_role);
            profileImage = (CircleImageView) mView.findViewById(R.id.main_row_image);

        }
    }
}
