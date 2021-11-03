package cite.app.records;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText emailSignUp,passSignUp,confirmPass;
    TextView loginLinkText;
    Button signUpBtn;
    private LinearLayout layout;
    FirebaseAuth fAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailSignUp = findViewById(R.id.sign_up_email_et);
        passSignUp = findViewById(R.id.sign_up_password_et);
        confirmPass = findViewById(R.id.confirm_password_et);
        loginLinkText = findViewById(R.id.login_text_link);
        signUpBtn = findViewById(R.id.sign_up_btn);
        layout = findViewById(R.id.layout_white2);
        fAuth = FirebaseAuth.getInstance();

        //loading dialog
        LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);


        loginLinkText.setOnClickListener((View v)->{
            Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);

            Pair[] pair = new Pair[1];
            pair[0] = new Pair<View, String>(layout,"layoutTransition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this,pair);
            loadingDialog.startLoadingDialog();
            startActivity(toLogin, options.toBundle());
           loadingDialog.dismissDialog();
            finish();
        });

        signUpBtn.setOnClickListener((View v)->{
            String email = emailSignUp.getText().toString();
            String pass = passSignUp.getText().toString();
            String conPass = confirmPass.getText().toString();

            if (TextUtils.isEmpty(email)){
                emailSignUp.setError("Email is Required");
                return;
            }else if (TextUtils.isEmpty(pass)){
                passSignUp.setError("Password is Required");
                return;
            } else if (pass.length() < 6){
                passSignUp.setError("Too short Password!");
                return;
            }else if (TextUtils.isEmpty(conPass)){
                passSignUp.setError("Please Confirm Password");
                return;
            }else{
                loadingDialog.startLoadingDialog();

                fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"Registration is Successful",Toast.LENGTH_SHORT).show();
                            rootNode = FirebaseDatabase.getInstance();
                            reference = rootNode.getReference("users");

                            String userId = fAuth.getCurrentUser().getUid();

                           /* Teachers teacher = new Teachers(email);

                            reference.child(userId).setValue(teacher);*/
                            loadingDialog.dismissDialog();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this,"Error Occurred"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();

                        }
                    }
                });
            }
        });
    }
}
