package com.example.adunik_krishi.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adunik_krishi.R;
import com.example.adunik_krishi.constant.Constant;
import com.example.adunik_krishi.models.Product;
import com.example.adunik_krishi.screens.AddProductActivity;
import com.example.adunik_krishi.screens.ProductDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    List<Product> products;
    Context context;
    int id;

    DatabaseReference databaseReference;

    public ProductAdapter(List<Product> products, Context context, int id) {
        this.products = products;
        this.context = context;
        this.id = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_design_product,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product product = products.get(position);

        holder.product_nameTV.setText(product.getpName());
        holder.product_detailsTV.setText(product.getpDetails());
        holder.product_priceTV.setText("দাম - "+product.getpAmount());
        Glide.with(context).load(product.getpImage()).into(holder.product_imageView);

        if(id == 0){
            holder.deleteIV.setVisibility(View.GONE);
            holder.editIV.setVisibility(View.GONE);
        }
        else {
            holder.deleteIV.setVisibility(View.VISIBLE);
            holder.editIV.setVisibility(View.VISIBLE);
        }

        holder.deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Warning!!!")
                        .setMessage("আপনি কি এই পণ্যটি ডিলিট করতে চান ?")
                        .setPositiveButton("হ্যা", (dialog, which) -> {
                            DatabaseReference mPostReference = FirebaseDatabase.getInstance(Constant.DATABASE_REFERENCE).getReference().child("products").child(product.getpID());
                            mPostReference.removeValue();
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("না", null)
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .show();
            }
        });

        holder.editIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddProductActivity.class);
                intent.putExtra("productID",product.getpID());
                context.startActivity(intent);
            }
        });

        databaseReference = FirebaseDatabase.getInstance(Constant.DATABASE_REFERENCE).getReference("users").child(product.getpPhone()).child("name");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.product_ownerTV.setText("বিক্রি করবেন - "+snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.product_buyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+880"+product.getpPhone()));
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("productID",product.getpID());
                intent.putExtra("phoneNumber",product.getpPhone());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product_nameTV,product_detailsTV,product_priceTV,product_ownerTV;
        ImageView product_imageView;
        Button product_buyBTN;
        ImageView editIV,deleteIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product_nameTV = itemView.findViewById(R.id.model_product_nameTV);
            product_detailsTV = itemView.findViewById(R.id.model_product_detailsTV);
            product_priceTV = itemView.findViewById(R.id.model_product_priceTV);
            product_ownerTV = itemView.findViewById(R.id.model_product_ownerTV);
            product_imageView = itemView.findViewById(R.id.model_product_imageView);
            product_buyBTN = itemView.findViewById(R.id.model_productBuyBTN);
            editIV = itemView.findViewById(R.id.model_editIV);
            deleteIV = itemView.findViewById(R.id.model_deleteIV);
        }
    }
}
