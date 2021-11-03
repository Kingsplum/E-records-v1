package cite.app.records;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    Context context;
    ArrayList<ClassModel> list;

    Dialog dialog;
    AlertDialog.Builder dialogBuilder;

    FirebaseAuth fAuth;
    FirebaseDatabase rootNode;

    public MainAdapter(Context context, ArrayList<ClassModel> list) {
        this.context = context;
        this.list = list;
    }
    
    

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ClassModel classModel = list.get(position);

        //loading dialog
        LoadingDialog loadingDialog = new LoadingDialog((Activity) context);

        fAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();


        holder.subjectCode.setText(classModel.getSubjectCode());
        holder.subjectName.setText(classModel.getSubjectName());


        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Operation operate = new Operation();

                //alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //set title
                builder.setTitle("Remove Class");
                //set message
                builder.setMessage("Are you sure you want to remove?");
                //positive yes
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //remove class
                        list.clear();
                        loadingDialog.startLoadingDialog();
                        Handler handler = new Handler();
                        handler.postDelayed((Runnable) loadingDialog::dismissDialog,1000);


                     //  Intent intent = new Intent(context, MainActivity.class);
                      // context.startActivity(intent);

                        //calling operate class to remove
                        operate.remove(classModel.getSubjectCode()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                dialog.dismiss();
                                Toast.makeText(context.getApplicationContext(), "Removed Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
                //negative no
                builder.setNegativeButton("NO", (dialog, which) -> {
                    //dismiss dialog
                    dialog.dismiss();
                });
                builder.create();
                builder.show();
            }
        });

        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalVar.codeToPass = classModel.getSubjectCode();

                Intent intent = new Intent(context, StudentList.class);
                //intent.putExtra(StudentList.CODE,classModel.getSubjectCode());
                 context.startActivity(intent);


            }
        });


        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Operation operate = new Operation();

                dialog = new Dialog(context);

                dialog.setContentView(R.layout.popup1);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView subject_code = dialog.findViewById(R.id.subject_code_display);
                EditText subject_name = dialog.findViewById(R.id.subject_name_display);
                Button saveBtn = dialog.findViewById(R.id.save_btn);
                Button cancelBtn = dialog.findViewById(R.id.cancel_btn);

                subject_code.setText(classModel.getSubjectCode());
                subject_name.setText(classModel.getSubjectName());

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String subjectName = subject_name.getText().toString();

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("subjectName",subjectName);

                        list.clear();
                        operate.update(classModel.getSubjectCode(),hashMap);
                        loadingDialog.startLoadingDialog();
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            loadingDialog.dismissDialog();
                        },1000);



                    //    Intent intent = new Intent(context, MainActivity.class);
                      //  context.getApplicationContext().startActivity(intent);
                      //  ((Activity) context).finishAffinity();

                        Toast.makeText(context, "Class Changed", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();

                    }
                });

                cancelBtn.setOnClickListener(v1 -> {
                    dialog.dismiss();

                });


                dialog.show();

            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView subjectCode,subjectName;
        Button removeBtn,viewBtn,editBtn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectCode = itemView.findViewById(R.id.subject_code_display);
            subjectName = itemView.findViewById(R.id.subject_name_display);
            removeBtn = itemView.findViewById(R.id.remove_class);
            viewBtn = itemView.findViewById(R.id.view_class);
            editBtn = itemView.findViewById(R.id.edit_class);

        }
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    @Override
    public int getItemViewType(int position) {
        return (position);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }
}
