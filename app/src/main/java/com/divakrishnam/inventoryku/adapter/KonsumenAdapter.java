package com.divakrishnam.inventoryku.adapter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.divakrishnam.inventoryku.R;
import com.divakrishnam.inventoryku.activity.FormKonsumenActivity;
import com.divakrishnam.inventoryku.api.APIClient;
import com.divakrishnam.inventoryku.api.APIService;
import com.divakrishnam.inventoryku.model.Konsumen;
import com.divakrishnam.inventoryku.model.ResponseInfo;
import com.divakrishnam.inventoryku.util.Urls;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KonsumenAdapter extends RecyclerView.Adapter<KonsumenAdapter.MyViewHolder>{
    List<Konsumen> mList;

    private APIService apiService;
    public KonsumenAdapter(List <Konsumen> list) {
        mList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_instansi, parent, false);
        MyViewHolder mViewHolder = new MyViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder (MyViewHolder holder,final int position){
        Konsumen konsumen = mList.get(position);
        holder.tvKode.setText(konsumen.getKodeKonsumen());
        holder.tvNama.setText(konsumen.getNamaKonsumen());
        holder.tvAlamat.setText(konsumen.getAlamatKonsumen());
        holder.tvKontak.setText(konsumen.getKontakKonsumen());
        Glide.with(holder.itemView.getContext()).load(Urls.KONSUMEN_IMG_URL+konsumen.getGambarKonsumen()).error(R.drawable.ic_launcher_background).into(holder.ivGambar);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return menuDialog(view, position);
            }
        });
    }

    private boolean menuDialog(final View view, final int position) {
        CharSequence[] menu = {"Edit", "Delete"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext())
                .setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                editData(position, view);
                                break;
                            case 1:
                                deleteData(position, view);
                                break;
                        }
                    }
                });
        dialog.create();
        dialog.show();
        return true;
    }

    private void editData(int position, View view) {
        Intent intent = new Intent(view.getContext(), FormKonsumenActivity.class);
        intent.putExtra(FormKonsumenActivity.DATA, mList.get(position));
        view.getContext().startActivity(intent);
    }

    private void deleteData(final int position, final View view) {
        final ProgressDialog progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("Menghapus data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        apiService = APIClient.getClient().create(APIService.class);

        Call<ResponseInfo> call = apiService.deleteKonsumen(mList.get(position).getKodeKonsumen());
        call.enqueue(new Callback<ResponseInfo>() {
            @Override
            public void onResponse(Call<ResponseInfo> call, Response<ResponseInfo> response) {
                progressDialog.dismiss();
                if (response.body().getStatus() == 200){
                    mList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mList.size());
                }
                Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseInfo> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount () {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvKode, tvNama, tvAlamat, tvKontak;
        ImageView ivGambar;

        MyViewHolder(View itemView) {
            super(itemView);
            ivGambar = itemView.findViewById(R.id.iv_gambar);
            tvKode = itemView.findViewById(R.id.tv_kode);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvAlamat = itemView.findViewById(R.id.tv_alamat);
            tvKontak = itemView.findViewById(R.id.tv_kontak);
        }
    }
}
