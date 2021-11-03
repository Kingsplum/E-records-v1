package cite.app.records;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    public static final String TAG = "TAG";
    DrawerLayout drawerLayout;
   Dialog dialog;
   AlertDialog.Builder dialogBuilder;
   AlertDialog dialog1;
   TextView aboutText, noClass,teacherCode;
    EditText subject_code,subject_name;
    Button cancelBtn, saveBtn,okBtn,copyCode;
    ImageButton add;


    //Operation class
    Operation operate = new Operation();

    //firebase
    FirebaseAuth fAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    String userId;

    //RecycleView
    RecyclerView recyclerView;
    MainAdapter mainAdapter;
    ArrayList<ClassModel>list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.drawer_layout);
        dialog = new Dialog(this);
        add = findViewById(R.id.add_class);
        fAuth = FirebaseAuth.getInstance();
       rootNode = FirebaseDatabase.getInstance();
       recyclerView = findViewById(R.id.recycle_view);
       noClass = findViewById(R.id.no_class_tv);
       teacherCode = findViewById(R.id.teacher_code);
        copyCode = findViewById(R.id.copy_btn);





       //loading dialog
        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);

        //getId in database
        userId = fAuth.getCurrentUser().getUid();

        //set code for room
        String roomCode = userId;
        teacherCode.setText(roomCode);

       //reference path
        reference = rootNode.getReference("users/"+userId).child("Subjects");

        //recycleview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        boolean has = list.isEmpty();
        if (has == true){
            noClass.setVisibility(View.VISIBLE);
        }

        mainAdapter = new MainAdapter(this,list);

        mainAdapter.setHasStableIds(true);

        if (mainAdapter.getItemCount()==0){
            noClass.setVisibility(View.VISIBLE);
        }
        recyclerView.setAdapter(mainAdapter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()){
                    noClass.setVisibility(View.INVISIBLE);
                }
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                        ClassModel classModel = dataSnapshot.getValue(ClassModel.class);
                            list.add(classModel);
                }

                mainAdapter.notifyDataSetChanged();

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

                Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT).show();

            }
        });






        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.popup);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                subject_code = dialog.findViewById(R.id.subject_code_et);
                subject_name = dialog.findViewById(R.id.subject_name_et);
                saveBtn = dialog.findViewById(R.id.save_btn);
                cancelBtn = dialog.findViewById(R.id.cancel_btn);

                dialog.show();

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String subjectCode = subject_code.getText().toString();
                        String subjectName = subject_name.getText().toString();

                        if (TextUtils.isEmpty(subjectCode)){
                            subject_code.setError("Required");
                        }
                        if( TextUtils.isEmpty(subjectName)){
                            subject_name.setError("Required");
                        }

                        if (!TextUtils.isEmpty(subjectCode) && !TextUtils.isEmpty(subjectName)){
                            //insert data
                            list.clear();
                            ClassModel classModel = new ClassModel(subjectCode.toUpperCase(),subjectName);
                            operate.add(classModel);

                       //     loadingDialog.startLoadingDialog();
                         //   Handler handler = new Handler();
                          //  handler.postDelayed((Runnable) () -> {
                               // loadingDialog.dismissDialog();
                           // },1000);

                          //  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            //startActivity(intent);
                         //   finish();

                            Toast.makeText(MainActivity.this, "Class Added", Toast.LENGTH_SHORT).show();

                        }else{
                            subject_code.setError("Required");
                            subject_name.setError("Required");
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

    public void ClickMenu(View view){
        //open drawer
        openDrawer(drawerLayout);
    }


    public static void openDrawer(DrawerLayout drawerLayout) {
       // open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public void ClickArea(View view){
        //close drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //close drawer
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
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
                Collections.sort(list,ClassModel.ClassNameAZComparator);
                Toast.makeText(MainActivity.this, "Class has been sorted A - Z", Toast.LENGTH_SHORT).show();
                mainAdapter.notifyDataSetChanged();

                return  true;
            case R.id.z_a:
                //sort here z - a
                Collections.sort(list,ClassModel.ClassNameZAComparator);
                Toast.makeText(MainActivity.this, "Class has been sorted Z - A", Toast.LENGTH_SHORT).show();
                mainAdapter.notifyDataSetChanged();

                return  true;
            default:
                return  false;
        }
    }

    public void ClickHome(View view){
        //Homeactivity
        recreate();
    }
    public void ClickLogout(View view){
        //logout
        logout(this);
    }

    public static void logout(Activity activity) {
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Exit");
        //set message
        builder.setMessage("Are you sure you want to Exit?");
        //positive yes
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish activity
                FirebaseAuth.getInstance().signOut();

                activity.finishAffinity();
                //exit app
                System.exit(0);
            }
        });
        //negative no
        builder.setNegativeButton("NO", (dialog, which) -> {
            //dismiss dialog
            dialog.dismiss();
        });
        builder.show();


    }
    public void ClickAbout(View view) {
        //redirect activity to about page
        //redirectActivity(this,AboutActivity.class);

        about();
    }

    private void about() {

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



    public static void redirectActivity(Activity activity,Class directClass){
        //initialize intent
        Intent intent = new Intent(activity,directClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start activity
        activity.startActivity(intent);
    }

    @Override
    protected void onPause(){
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }

    @Override
    public void onBackPressed() {
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title
        builder.setTitle("Exit");
        //set message
        builder.setMessage("Are you sure you want to Exit?");
        //positive yes
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish activity
                finishAffinity();
                System.exit(0);
            }
        });
        //negative no
        builder.setNegativeButton("NO", (dialog, which) -> {
            //dismiss dialog
            dialog.dismiss();
        });
        builder.show();
    }
}