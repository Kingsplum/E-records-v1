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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.fragment.app.FragmentPagerAdapter.*;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder>{
    Context context;
    ArrayList<StudentModel> list;

    Dialog dialog;
    AlertDialog.Builder dialogBuilder;

    FirebaseAuth fAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference,gradesRef,reference1;

    public static final String CODE = "CODE";
    public static final String ICODE = "ICODE";





    public StudentAdapter(Context context, ArrayList<StudentModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public StudentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student,parent,false);
        return new StudentAdapter.MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.MyViewHolder holder,int position) {
        StudentModel studentModel = list.get(position);
        //loading dialog
        LoadingDialog loadingDialog = new LoadingDialog((Activity) context);

        fAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        String userId = fAuth.getCurrentUser().getUid();

        holder.sName.setText(studentModel.getsName());
        holder.sId.setText(studentModel.getsId());
        holder.sCountAct.setText(studentModel.getsCountAct());
        //   Intent intent = ((Activity) context).getIntent();
        final String classKey = GlobalVar.codeToPass;






//remove student
        holder.removeStudentBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                //alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //set title
                builder.setTitle("Remove Student");
                //set message
                builder.setMessage("Are you sure you want to remove?");
                //positive yes
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.clear();

                     //   Intent intent = ((Activity) context).getIntent();
                    //  final String classKey = GlobalVar.codeToPass;

                       //String studentId = studentModel.getsId();
                        //remove class
                        list.clear();
                        loadingDialog.startLoadingDialog();
                        Handler handler = new Handler();
                        handler.postDelayed((Runnable) loadingDialog::dismissDialog,1000);

                        reference = rootNode.getReference("users/"+userId+"/Students/"+classKey);



                        //calling operate class to remove
                        reference.child(studentModel.getsId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                              //  Intent intent = new Intent(context, StudentList.class);
                              //  intent.putExtra(StudentList.CODE,classKey);
                             //   context.startActivity(intent);
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

        //edit scores
        holder.editScoreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //get class code
               // String sClass = GlobalVar.codeToPass;

                //get the sId of current item
                String studentID = studentModel.getsId();
                String sClass =studentModel.getsClass();

               /* gradesRef = rootNode.getReference("users/"+userId+"/Grades");

                gradesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        if (snapshot2.exists()){
                            int actCount = (int) snapshot2.child(studentID).getChildrenCount();
                            String countScore = String.valueOf(actCount);
                            HashMap<String,Object> hashMap1 = new HashMap<>();
                            hashMap1.put("sClass",studentModel.getsClass());
                            hashMap1.put("sCountAct",countScore);
                            hashMap1.put("sCountAct",countScore);
                            reference1 = rootNode.getReference("users/"+userId+"/Students/"+sClass);
                            reference1.child(studentModel.getsId()).updateChildren(hashMap1);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

                loadingDialog.startLoadingDialog();
                Handler handler = new Handler();
                handler.postDelayed((Runnable) loadingDialog::dismissDialog,2000);


                reference = rootNode.getReference("users/"+userId+"/Grades");

                dialog = new Dialog(context);

                dialog.setContentView(R.layout.activity_add);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                EditText p_a1,p_a2,p_a3,p_a4,p_a5,p_ass1,p_ass2,p_ass3,p_ass4,p_ass5,p_q1,p_q2,p_q3,p_q4,p_q5,p_ex1,p_ex2,p_ex3,p_ex4,p_ex5;
                EditText m_a1,m_a2,m_a3,m_a4,m_a5,m_ass1,m_ass2,m_ass3,m_ass4,m_ass5,m_q1,m_q2,m_q3,m_q4,m_q5,m_ex1,m_ex2,m_ex3,m_ex4,m_ex5;
                EditText f_a1,f_a2,f_a3,f_a4,f_a5,f_ass1,f_ass2,f_ass3,f_ass4,f_ass5,f_q1,f_q2,f_q3,f_q4,f_q5,f_ex1,f_ex2,f_ex3,f_ex4,f_ex5;
                Button saveBtn,cancelBtn;

                saveBtn = dialog.findViewById(R.id.save_btn_tab);
                cancelBtn = dialog.findViewById(R.id.cancel_btn_tab);


//Prelim, dami hahahah
                p_a1 = dialog.findViewById(R.id.p_act1);
                p_a2 = dialog.findViewById(R.id.p_act2);
                p_a3 = dialog.findViewById(R.id.p_act3);
                p_a4 = dialog.findViewById(R.id.p_act4);
                p_a5 = dialog.findViewById(R.id.p_act5);
                p_ass1 = dialog.findViewById(R.id.p_ass1);
                p_ass2 = dialog.findViewById(R.id.p_ass2);
                p_ass3 = dialog.findViewById(R.id.p_ass3);
                p_ass4 = dialog.findViewById(R.id.p_ass4);
                p_ass5 = dialog.findViewById(R.id.p_ass5);
                p_q1 = dialog.findViewById(R.id.p_q1);
                p_q2 = dialog.findViewById(R.id.p_q2);
                p_q3 = dialog.findViewById(R.id.p_q3);
                p_q4 = dialog.findViewById(R.id.p_q4);
                p_q5 = dialog.findViewById(R.id.p_q5);
                p_ex1 = dialog.findViewById(R.id.p_el1);
                p_ex2 = dialog.findViewById(R.id.p_el2);
                p_ex3 = dialog.findViewById(R.id.p_el3);
                p_ex4 = dialog.findViewById(R.id.p_el4);
                p_ex5 = dialog.findViewById(R.id.p_el5);

//Midterm, dami hahahah
                m_a1 = dialog.findViewById(R.id.m_act1);
                m_a2 = dialog.findViewById(R.id.m_act2);
                m_a3 = dialog.findViewById(R.id.m_act3);
                m_a4 = dialog.findViewById(R.id.m_act4);
                m_a5 = dialog.findViewById(R.id.m_act5);
                m_ass1 = dialog.findViewById(R.id.m_ass1);
                m_ass2 = dialog.findViewById(R.id.m_ass2);
                m_ass3 = dialog.findViewById(R.id.m_ass3);
                m_ass4 = dialog.findViewById(R.id.m_ass4);
                m_ass5 = dialog.findViewById(R.id.m_ass5);
                m_q1 = dialog.findViewById(R.id.m_q1);
                m_q2 = dialog.findViewById(R.id.m_q2);
                m_q3 = dialog.findViewById(R.id.m_q3);
                m_q4 = dialog.findViewById(R.id.m_q4);
                m_q5 = dialog.findViewById(R.id.m_q5);
                m_ex1 = dialog.findViewById(R.id.m_el1);
                m_ex2 = dialog.findViewById(R.id.m_el2);
                m_ex3 = dialog.findViewById(R.id.m_el3);
                m_ex4 = dialog.findViewById(R.id.m_el4);
                m_ex5 = dialog.findViewById(R.id.m_el5);
//Midterm, dami hahahah
                f_a1 = dialog.findViewById(R.id.f_act1);
                f_a2 = dialog.findViewById(R.id.f_act2);
                f_a3 = dialog.findViewById(R.id.f_act3);
                f_a4 = dialog.findViewById(R.id.f_act4);
                f_a5 = dialog.findViewById(R.id.f_act5);
                f_ass1 = dialog.findViewById(R.id.f_ass1);
                f_ass2 = dialog.findViewById(R.id.f_ass2);
                f_ass3 = dialog.findViewById(R.id.f_ass3);
                f_ass4 = dialog.findViewById(R.id.f_ass4);
                f_ass5 = dialog.findViewById(R.id.f_ass5);
                f_q1 = dialog.findViewById(R.id.f_q1);
                f_q2 = dialog.findViewById(R.id.f_q2);
                f_q3 = dialog.findViewById(R.id.f_q3);
                f_q4 = dialog.findViewById(R.id.f_q4);
                f_q5 = dialog.findViewById(R.id.f_q5);
                f_ex1 = dialog.findViewById(R.id.f_el1);
                f_ex2 = dialog.findViewById(R.id.f_el2);
                f_ex3 = dialog.findViewById(R.id.f_el3);
                f_ex4 = dialog.findViewById(R.id.f_el4);
                f_ex5 = dialog.findViewById(R.id.f_el5);


                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChildren()){

                            saveBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String prelimAct1 = p_a1.getText().toString();
                                    String prelimAct2 = p_a2.getText().toString();
                                    String prelimAct3 = p_a3.getText().toString();
                                    String prelimAct4 = p_a4.getText().toString();
                                    String prelimAct5 = p_a5.getText().toString();
                                    String prelimAss1 = p_ass1.getText().toString();
                                    String prelimAss2 = p_ass2.getText().toString();
                                    String prelimAss3 = p_ass3.getText().toString();
                                    String prelimAss4 = p_ass4.getText().toString();
                                    String prelimAss5 = p_ass5.getText().toString();
                                    String prelimQ1 = p_q1.getText().toString();
                                    String prelimQ2 = p_q2.getText().toString();
                                    String prelimQ3 = p_q3.getText().toString();
                                    String prelimQ4 = p_q4.getText().toString();
                                    String prelimQ5 = p_q5.getText().toString();
                                    String prelimE1 = p_ex1.getText().toString();
                                    String prelimE2 = p_ex2.getText().toString();
                                    String prelimE3 = p_ex3.getText().toString();
                                    String prelimE4 = p_ex4.getText().toString();
                                    String prelimE5 = p_ex5.getText().toString();

                                    String midtermAct1 = m_a1.getText().toString();
                                    String midtermAct2 = m_a2.getText().toString();
                                    String midtermAct3 = m_a3.getText().toString();
                                    String midtermAct4 = m_a4.getText().toString();
                                    String midtermAct5 = m_a5.getText().toString();
                                    String midtermAss1 = m_ass1.getText().toString();
                                    String midtermAss2 = m_ass2.getText().toString();
                                    String midtermAss3 = m_ass3.getText().toString();
                                    String midtermAss4 = m_ass4.getText().toString();
                                    String midtermAss5 = m_ass5.getText().toString();
                                    String midtermQ1 = m_q1.getText().toString();
                                    String midtermQ2 = m_q2.getText().toString();
                                    String midtermQ3 = m_q3.getText().toString();
                                    String midtermQ4 = m_q4.getText().toString();
                                    String midtermQ5 = m_q5.getText().toString();
                                    String midtermE1 = m_ex1.getText().toString();
                                    String midtermE2 = m_ex2.getText().toString();
                                    String midtermE3 = m_ex3.getText().toString();
                                    String midtermE4 = m_ex4.getText().toString();
                                    String midtermE5 = m_ex5.getText().toString();


                                    String finalsAct1 = f_a1.getText().toString();
                                    String finalsAct2 = f_a2.getText().toString();
                                    String finalsAct3 = f_a3.getText().toString();
                                    String finalsAct4 = f_a4.getText().toString();
                                    String finalsAct5 = f_a5.getText().toString();
                                    String finalsAss1 = f_ass1.getText().toString();
                                    String finalsAss2 = f_ass2.getText().toString();
                                    String finalsAss3 = f_ass3.getText().toString();
                                    String finalsAss4 = f_ass4.getText().toString();
                                    String finalsAss5 = f_ass5.getText().toString();
                                    String finalsQ1 = f_q1.getText().toString();
                                    String finalsQ2 = f_q2.getText().toString();
                                    String finalsQ3 = f_q3.getText().toString();
                                    String finalsQ4 = f_q4.getText().toString();
                                    String finalsQ5 = f_q5.getText().toString();
                                    String finalsE1 = f_ex1.getText().toString();
                                    String finalsE2 = f_ex2.getText().toString();
                                    String finalsE3 = f_ex3.getText().toString();
                                    String finalsE4 = f_ex4.getText().toString();
                                    String finalsE5 = f_ex5.getText().toString();

                                    reference = rootNode.getReference("users/"+userId+"/Grades");
                                    GradesModel gradesModel = new GradesModel(
                                            prelimAct1,prelimAct2,prelimAct3,prelimAct4,prelimAct5,
                                            prelimAss1,prelimAss2,prelimAss3,prelimAss4,prelimAss5,
                                            prelimQ1,prelimQ2,prelimQ3,prelimQ4,prelimQ5,
                                            prelimE1,prelimE2,prelimE3,prelimE4,prelimE5,
                                            midtermAct1,midtermAct2,midtermAct3,midtermAct4,midtermAct5,
                                            midtermAss1,midtermAss2,midtermAss3,midtermAss4,midtermAss5,
                                            midtermQ1,midtermQ2,midtermQ3,midtermQ4,midtermQ5,
                                            midtermE1,midtermE2,midtermE3,midtermE4,midtermE5,
                                            finalsAct1,finalsAct2,finalsAct3,finalsAct4,finalsAct5,
                                            finalsAss1,finalsAss2,finalsAss3,finalsAss4,finalsAss5,
                                            finalsQ1,finalsQ2,finalsQ3,finalsQ4,finalsQ5,
                                            finalsE1,finalsE2,finalsE3,finalsE4,finalsE5);

                                    reference.child(studentModel.getsId()).setValue(gradesModel);

                                    Toast.makeText(context.getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();



                                }
                            });

                            cancelBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                          //  dialog.show();

                        }else{
                            reference = rootNode.getReference("users/"+userId+"/Grades");

                            reference.child(studentModel.getsId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()){
                                        if (task.getResult().exists()){

                                            DataSnapshot mySnapshot = task.getResult();



                                            p_a1.setText(String.valueOf(mySnapshot.child("prelimAct1").getValue()));
                                            p_a2.setText(String.valueOf(mySnapshot.child("prelimAct2").getValue()));
                                            p_a3.setText(String.valueOf(mySnapshot.child("prelimAct3").getValue()));
                                            p_a4.setText(String.valueOf(mySnapshot.child("prelimAct4").getValue()));
                                            p_a5.setText(String.valueOf(mySnapshot.child("prelimAct5").getValue()));
                                            p_ass1.setText(String.valueOf(mySnapshot.child("prelimAss1").getValue()));
                                            p_ass2.setText(String.valueOf(mySnapshot.child("prelimAss2").getValue()));
                                            p_ass3.setText(String.valueOf(mySnapshot.child("prelimAss3").getValue()));
                                            p_ass4.setText(String.valueOf(mySnapshot.child("prelimAss4").getValue()));
                                            p_ass5.setText(String.valueOf(mySnapshot.child("prelimAss5").getValue()));
                                            p_q1.setText(String.valueOf(mySnapshot.child("prelimQ1").getValue()));
                                            p_q2.setText(String.valueOf(mySnapshot.child("prelimQ2").getValue()));
                                            p_q3.setText(String.valueOf(mySnapshot.child("prelimQ3").getValue()));
                                            p_q4.setText(String.valueOf(mySnapshot.child("prelimQ4").getValue()));
                                            p_q5.setText(String.valueOf(mySnapshot.child("prelimQ5").getValue()));
                                            p_ex1.setText(String.valueOf(mySnapshot.child("prelimE1").getValue()));
                                            p_ex2.setText(String.valueOf(mySnapshot.child("prelimE2").getValue()));
                                            p_ex3.setText(String.valueOf(mySnapshot.child("prelimE3").getValue()));
                                            p_ex4.setText(String.valueOf(mySnapshot.child("prelimE4").getValue()));
                                            p_ex5.setText(String.valueOf(mySnapshot.child("prelimE5").getValue()));

                                            m_a1.setText(String.valueOf(mySnapshot.child("midtermAct1").getValue()));
                                            m_a2.setText(String.valueOf(mySnapshot.child("midtermAct2").getValue()));
                                            m_a3.setText(String.valueOf(mySnapshot.child("midtermAct3").getValue()));
                                            m_a4.setText(String.valueOf(mySnapshot.child("midtermAct4").getValue()));
                                            m_a5.setText(String.valueOf(mySnapshot.child("midtermAct5").getValue()));
                                            m_ass1.setText(String.valueOf(mySnapshot.child("midtermAss1").getValue()));
                                            m_ass2.setText(String.valueOf(mySnapshot.child("midtermAss2").getValue()));
                                            m_ass3.setText(String.valueOf(mySnapshot.child("midtermAss3").getValue()));
                                            m_ass4.setText(String.valueOf(mySnapshot.child("midtermAss4").getValue()));
                                            m_ass5.setText(String.valueOf(mySnapshot.child("midtermAss5").getValue()));
                                            m_q1.setText(String.valueOf(mySnapshot.child("midtermQ1").getValue()));
                                            m_q2.setText(String.valueOf(mySnapshot.child("midtermQ2").getValue()));
                                            m_q3.setText(String.valueOf(mySnapshot.child("midtermQ3").getValue()));
                                            m_q4.setText(String.valueOf(mySnapshot.child("midtermQ4").getValue()));
                                            m_q5.setText(String.valueOf(mySnapshot.child("midtermQ5").getValue()));
                                            m_ex1.setText(String.valueOf(mySnapshot.child("midtermE1").getValue()));
                                            m_ex2.setText(String.valueOf(mySnapshot.child("midtermE2").getValue()));
                                            m_ex3.setText(String.valueOf(mySnapshot.child("midtermE3").getValue()));
                                            m_ex4.setText(String.valueOf(mySnapshot.child("midtermE4").getValue()));
                                            m_ex5.setText(String.valueOf(mySnapshot.child("midtermE5").getValue()));


                                            f_a1.setText(String.valueOf(mySnapshot.child("finalsAct1").getValue()));
                                            f_a2.setText(String.valueOf(mySnapshot.child("finalsAct2").getValue()));
                                            f_a3.setText(String.valueOf(mySnapshot.child("finalsAct3").getValue()));
                                            f_a4.setText(String.valueOf(mySnapshot.child("finalsAct4").getValue()));
                                            f_a5.setText(String.valueOf(mySnapshot.child("finalsAct5").getValue()));
                                            f_ass1.setText(String.valueOf(mySnapshot.child("finalsAss1").getValue()));
                                            f_ass2.setText(String.valueOf(mySnapshot.child("finalsAss2").getValue()));
                                            f_ass3.setText(String.valueOf(mySnapshot.child("finalsAss3").getValue()));
                                            f_ass4.setText(String.valueOf(mySnapshot.child("finalsAss4").getValue()));
                                            f_ass5.setText(String.valueOf(mySnapshot.child("finalsAss5").getValue()));
                                            f_q1.setText(String.valueOf(mySnapshot.child("finalsQ1").getValue()));
                                            f_q2.setText(String.valueOf(mySnapshot.child("finalsQ2").getValue()));
                                            f_q3.setText(String.valueOf(mySnapshot.child("finalsQ3").getValue()));
                                            f_q4.setText(String.valueOf(mySnapshot.child("finalsQ4").getValue()));
                                            f_q5.setText(String.valueOf(mySnapshot.child("finalsQ5").getValue()));
                                            f_ex1.setText(String.valueOf(mySnapshot.child("finalsE1").getValue()));
                                            f_ex2.setText(String.valueOf(mySnapshot.child("finalsE2").getValue()));
                                            f_ex3.setText(String.valueOf(mySnapshot.child("finalsE3").getValue()));
                                            f_ex4.setText(String.valueOf(mySnapshot.child("finalsE4").getValue()));
                                            f_ex5.setText(String.valueOf(mySnapshot.child("finalsE5").getValue()));



                                        }else {

                                            Toast.makeText(context.getApplicationContext(), "No Score yet", Toast.LENGTH_SHORT).show();
                                        }

                                    }else{
                                        Toast.makeText(context.getApplicationContext(), "There is some error", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });




                            //dialog.show();

                            saveBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String prelimAct1 = p_a1.getText().toString();
                                    String prelimAct2 = p_a2.getText().toString();
                                    String prelimAct3 = p_a3.getText().toString();
                                    String prelimAct4 = p_a4.getText().toString();
                                    String prelimAct5 = p_a5.getText().toString();
                                    String prelimAss1 = p_ass1.getText().toString();
                                    String prelimAss2 = p_ass2.getText().toString();
                                    String prelimAss3 = p_ass3.getText().toString();
                                    String prelimAss4 = p_ass4.getText().toString();
                                    String prelimAss5 = p_ass5.getText().toString();
                                    String prelimQ1 = p_q1.getText().toString();
                                    String prelimQ2 = p_q2.getText().toString();
                                    String prelimQ3 = p_q3.getText().toString();
                                    String prelimQ4 = p_q4.getText().toString();
                                    String prelimQ5 = p_q5.getText().toString();
                                    String prelimE1 = p_ex1.getText().toString();
                                    String prelimE2 = p_ex2.getText().toString();
                                    String prelimE3 = p_ex3.getText().toString();
                                    String prelimE4 = p_ex4.getText().toString();
                                    String prelimE5 = p_ex5.getText().toString();

                                    String midtermAct1 = m_a1.getText().toString();
                                    String midtermAct2 = m_a2.getText().toString();
                                    String midtermAct3 = m_a3.getText().toString();
                                    String midtermAct4 = m_a4.getText().toString();
                                    String midtermAct5 = m_a5.getText().toString();
                                    String midtermAss1 = m_ass1.getText().toString();
                                    String midtermAss2 = m_ass2.getText().toString();
                                    String midtermAss3 = m_ass3.getText().toString();
                                    String midtermAss4 = m_ass4.getText().toString();
                                    String midtermAss5 = m_ass5.getText().toString();
                                    String midtermQ1 = m_q1.getText().toString();
                                    String midtermQ2 = m_q2.getText().toString();
                                    String midtermQ3 = m_q3.getText().toString();
                                    String midtermQ4 = m_q4.getText().toString();
                                    String midtermQ5 = m_q5.getText().toString();
                                    String midtermE1 = m_ex1.getText().toString();
                                    String midtermE2 = m_ex2.getText().toString();
                                    String midtermE3 = m_ex3.getText().toString();
                                    String midtermE4 = m_ex4.getText().toString();
                                    String midtermE5 = m_ex5.getText().toString();


                                    String finalsAct1 = f_a1.getText().toString();
                                    String finalsAct2 = f_a2.getText().toString();
                                    String finalsAct3 = f_a3.getText().toString();
                                    String finalsAct4 = f_a4.getText().toString();
                                    String finalsAct5 = f_a5.getText().toString();
                                    String finalsAss1 = f_ass1.getText().toString();
                                    String finalsAss2 = f_ass2.getText().toString();
                                    String finalsAss3 = f_ass3.getText().toString();
                                    String finalsAss4 = f_ass4.getText().toString();
                                    String finalsAss5 = f_ass5.getText().toString();
                                    String finalsQ1 = f_q1.getText().toString();
                                    String finalsQ2 = f_q2.getText().toString();
                                    String finalsQ3 = f_q3.getText().toString();
                                    String finalsQ4 = f_q4.getText().toString();
                                    String finalsQ5 = f_q5.getText().toString();
                                    String finalsE1 = f_ex1.getText().toString();
                                    String finalsE2 = f_ex2.getText().toString();
                                    String finalsE3 = f_ex3.getText().toString();
                                    String finalsE4 = f_ex4.getText().toString();
                                    String finalsE5 = f_ex5.getText().toString();

                                    reference = rootNode.getReference("users/"+userId+"/Grades");





                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("prelimAct1",prelimAct1);
                                    hashMap.put("prelimAct2",prelimAct2);
                                    hashMap.put("prelimAct3",prelimAct3);
                                    hashMap.put("prelimAct4",prelimAct4);
                                    hashMap.put("prelimAct5",prelimAct5);
                                    hashMap.put("prelimAss1",prelimAss1);
                                    hashMap.put("prelimAss2",prelimAss2);
                                    hashMap.put("prelimAss3",prelimAss3);
                                    hashMap.put("prelimAss4",prelimAss4);
                                    hashMap.put("prelimAss5",prelimAss5);
                                    hashMap.put("prelimQ1",prelimQ1);
                                    hashMap.put("prelimQ2",prelimQ2);
                                    hashMap.put("prelimQ3",prelimQ3);
                                    hashMap.put("prelimQ4",prelimQ4);
                                    hashMap.put("prelimQ5",prelimQ5);
                                    hashMap.put("prelimE1",prelimE1);
                                    hashMap.put("prelimE2",prelimE2);
                                    hashMap.put("prelimE3",prelimE3);
                                    hashMap.put("prelimE4",prelimE4);
                                    hashMap.put("prelimE5",prelimE5);

                                    hashMap.put("midtermAct1",midtermAct1);
                                    hashMap.put("midtermAct2",midtermAct2);
                                    hashMap.put("midtermAct3",midtermAct3);
                                    hashMap.put("midtermAct4",midtermAct4);
                                    hashMap.put("midtermAct5",midtermAct5);
                                    hashMap.put("midtermAss1",midtermAss1);
                                    hashMap.put("midtermAss2",midtermAss2);
                                    hashMap.put("midtermAss3",midtermAss3);
                                    hashMap.put("midtermAss4",midtermAss4);
                                    hashMap.put("midtermAss5",midtermAss5);
                                    hashMap.put("midtermQ1",midtermQ1);
                                    hashMap.put("midtermQ2",midtermQ2);
                                    hashMap.put("midtermQ3",midtermQ3);
                                    hashMap.put("midtermQ4",midtermQ4);
                                    hashMap.put("midtermQ5",midtermQ5);
                                    hashMap.put("midtermE1",midtermE1);
                                    hashMap.put("midtermE2",midtermE2);
                                    hashMap.put("midtermE3",midtermE3);
                                    hashMap.put("midtermE4",midtermE4);
                                    hashMap.put("midtermE5",midtermE5);

                                    hashMap.put("finalsAct1",finalsAct1);
                                    hashMap.put("finalsAct2",finalsAct2);
                                    hashMap.put("finalsAct3",finalsAct3);
                                    hashMap.put("finalsAct4",finalsAct4);
                                    hashMap.put("finalsAct5",finalsAct5);
                                    hashMap.put("finalsAss1",finalsAss1);
                                    hashMap.put("finalsAss2",finalsAss2);
                                    hashMap.put("finalsAss3",finalsAss3);
                                    hashMap.put("finalsAss4",finalsAss4);
                                    hashMap.put("finalsAss5",finalsAss5);
                                    hashMap.put("finalsQ1",finalsQ1);
                                    hashMap.put("finalsQ2",finalsQ2);
                                    hashMap.put("finalsQ3",finalsQ3);
                                    hashMap.put("finalsQ4",finalsQ4);
                                    hashMap.put("finalsQ5",finalsQ5);
                                    hashMap.put("finalsE1",finalsE1);
                                    hashMap.put("finalsE2",finalsE2);
                                    hashMap.put("finalsE3",finalsE3);
                                    hashMap.put("finalsE4",finalsE4);
                                    hashMap.put("finalsE5",finalsE5);



                                    reference.child(studentModel.getsId()).updateChildren(hashMap);

                                    Toast.makeText(context.getApplicationContext(), "Points Have been saved", Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();

                                }

                            });

                            cancelBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dialog.show();

            }

        });

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(ArrayList<StudentModel> filteredList){
        list = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sName,sId,sCountAct;
        Button removeStudentBtn,editScoreBtn;
        ImageButton messageBtn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            sName = itemView.findViewById(R.id.student_name_display);
            sId = itemView.findViewById(R.id.student_id_display);
            removeStudentBtn = itemView.findViewById(R.id.remove_student);
            editScoreBtn = itemView.findViewById(R.id.edit_scores);
            messageBtn = itemView.findViewById(R.id.message_btn);
            sCountAct = itemView.findViewById(R.id.actCount);
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
