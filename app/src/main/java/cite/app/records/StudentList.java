package cite.app.records;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentList extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    //initialize variable
    DrawerLayout drawerLayout;
    TextView aboutText, noStudent,teacherCode;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog1;
    Button okBtn,saveBtn,cancelBtn,copyCode;
    ImageButton add;
    EditText student_code,student_name,searchFilter;


    Dialog dialog;


    //firebase
    FirebaseAuth fAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    String userId;

    //RecycleView
    RecyclerView recyclerView;
    StudentAdapter studentAdapter;
    ArrayList<StudentModel>list;



    public static final String CODE = "CODE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_list);

        //assign by its id
        drawerLayout = findViewById(R.id.drawer_layout);
        add          = findViewById(R.id.add_student);
        dialog = new Dialog(this);
        fAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.recycle_view);
        noStudent = findViewById(R.id.no_student_tv);
        searchFilter = findViewById(R.id.search_view);
        teacherCode = findViewById(R.id.teacher_code);
        copyCode = findViewById(R.id.copy_btn);





        //loading dialog
        LoadingDialog loadingDialog = new LoadingDialog(StudentList.this);

        //getId in database
        userId = fAuth.getCurrentUser().getUid();

        //set code for room
        String roomCode = userId;
        teacherCode.setText(roomCode);

        //search functionality
        searchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });



        //getintent
      //  Intent getString = getIntent();
       // String thisCode = getString.getStringExtra("CODE");

      // final String classsCode =thisCode;
        final String classCode =GlobalVar.codeToPass;
       // String studentNo = studentModel.getsId();




        //recycleview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        studentAdapter = new StudentAdapter(this,list);
        studentAdapter.setHasStableIds(true);

        if (studentAdapter.getItemCount()==0|| list.size()==0){
            noStudent.setVisibility(View.VISIBLE);
        }
        recyclerView.setAdapter(studentAdapter);



        reference = rootNode.getReference("users/"+userId+"/Students/"+classCode);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()){
                    noStudent.setVisibility(View.INVISIBLE);
                }
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    StudentModel sModel = dataSnapshot.getValue(StudentModel.class);
                    list.add(sModel);
                }

                studentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        copyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("TextCode", roomCode);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(StudentList.this, "Copied", Toast.LENGTH_SHORT).show();

            }
        });


        //add student
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.popup_student);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                student_name = dialog.findViewById(R.id.student_name_et);
                student_code = dialog.findViewById(R.id.student_id_et);
                saveBtn = dialog.findViewById(R.id.save_btn);
                cancelBtn = dialog.findViewById(R.id.cancel_btn);

                dialog.show();

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sName = student_name.getText().toString();
                        String sId = student_code.getText().toString();
                       final String sClass = GlobalVar.codeToPass;
                        String sCountAct = GlobalVar.scoreCount;


                        if (TextUtils.isEmpty(sName)){
                            student_name.setError("Required");
                        }
                        if( TextUtils.isEmpty(sId)){
                            student_code.setError("Required");
                        }

                        if (!TextUtils.isEmpty(sName) && !TextUtils.isEmpty(sId)){
                            //insert data
                            //reference path

                            reference = rootNode.getReference("users/"+userId+"/Students/"+sClass);
                            StudentModel studentModel = new StudentModel(sName,sId,sClass,sCountAct);
                            list.clear();
                            reference.child(sId).setValue(studentModel);
                          //  studentControl.add(studentModel);
                            loadingDialog.startLoadingDialog();
                            Handler handler = new Handler();
                            handler.postDelayed((Runnable) () -> {
                                loadingDialog.dismissDialog();
                            },1000);

                           // Intent intent = new Intent(getApplicationContext(), StudentList.class);
                        //    intent.putExtra(StudentAdapter.CODE,sClass);
                           // setResult(Activity.RESULT_OK,intent);
                         //   intent.putExtra(StudentAdapter.ICODE,sId);

                           // startActivity(intent);
                            //   finish();

                            Toast.makeText(StudentList.this, "Student Added", Toast.LENGTH_SHORT).show();

                        }else{
                            student_code.setError("Required");
                            student_name.setError("Required");
                        }
                        dialog.dismiss();

                    }
                });

                cancelBtn.setOnClickListener((View view)->{
                    //cancel
                    dialog.dismiss();
                });

            }

        });




    }
    private void filter(String textSearch){
        ArrayList<StudentModel> filteredList = new ArrayList<>();

        for (StudentModel item : list){
            if (item.getsName().toLowerCase().contains(textSearch.toLowerCase())){
                filteredList.add(item);
            }else
            if (item.getsId().contains(textSearch)){
                filteredList.add(item);
            }
        }

        studentAdapter.filterList(filteredList);
    }

    public void ClickMore(View view){
        //open more
        PopupMenu popup = new PopupMenu(this,view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.actions_menu);
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.a_z:
                //sort here a - z
                Collections.sort(list,StudentModel.StudentNameAZComparator);
                Toast.makeText(StudentList.this, "Class has been sorted A - Z", Toast.LENGTH_SHORT).show();
                studentAdapter.notifyDataSetChanged();

                return  true;
            case R.id.z_a:
                //sort here z - a
                Collections.sort(list,StudentModel.StudentNameZAComparator);
                Toast.makeText(StudentList.this, "Class has been sorted A - Z", Toast.LENGTH_SHORT).show();
                studentAdapter.notifyDataSetChanged();

                return  true;
            default:
                return  false;
        }
    }
    public  void ClickMenu(View view){
        MainActivity.openDrawer(drawerLayout);
    }
    public  void ClickArea(View view){
        MainActivity.closeDrawer(drawerLayout);
    }
    public  void ClickHome(View view){
        MainActivity.redirectActivity(this,MainActivity.class);
    }
    public  void ClickAbout(View view){

        dialogBuilder = new AlertDialog.Builder(this);
        View aboutPopup = getLayoutInflater().inflate(R.layout.about,null);
        aboutText = aboutPopup.findViewById(R.id.about_text);
        okBtn = aboutPopup.findViewById(R.id.ok_btn);

        dialogBuilder.setView(aboutPopup);
        dialog1 = dialogBuilder.create();
        dialog1.show();

        okBtn.setOnClickListener(v -> {
            //ok
            dialog1.dismiss();
        });
    }
    public  void ClickLogout(View view){
        MainActivity.logout(this);
    }
    @Override
    protected void onPause(){
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StudentList.this,MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }



}