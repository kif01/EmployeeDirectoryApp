package com.home.khalil.employeedirectory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by khalil on 4/11/18.
 */

public class DirectEmpRCV extends RecyclerView.Adapter<DirectEmpRCV.ViewHolder> {

    public ArrayList<Employee> employeesList;



    public DirectEmpRCV(ArrayList<Employee> employeesList){
        this.employeesList=employeesList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.direct_employee_custom_row,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name=employeesList.get(position).getName();
        String role=employeesList.get(position).getRole();
        String imageUri= employeesList.get(position).getImageUri();



        String[] parts= name.split(" ");




        holder.nameText.setText(parts[0]);
        holder.name2Text.setText(parts[1]);
        holder.roleText.setText(role);
        if(!(imageUri.equals("empty"))){
            Picasso.get().load(imageUri).placeholder(R.drawable.progress_animation).into(holder.profileImage);


        }else{
            holder.profileImage.setImageResource(R.drawable.default_profile_pic);
        }







    }

    @Override
    public int getItemCount() {
        return employeesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView nameText;
        public TextView name2Text;
        public TextView roleText;
        public CircleImageView profileImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            nameText= (TextView) mView.findViewById(R.id.direct_name);
            name2Text=(TextView) mView.findViewById(R.id.direct_name2);
            roleText= (TextView) mView.findViewById(R.id.direct_role);
            profileImage = (CircleImageView) mView.findViewById(R.id.direct_image);

        }
    }
}

