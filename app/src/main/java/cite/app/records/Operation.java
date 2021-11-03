package cite.app.records;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Operation {

    private DatabaseReference databaseReference;
    String userId;
    FirebaseAuth fAuth;

    public Operation(){

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        databaseReference = db.getReference("users/"+userId+"/Subjects");
    }
    public Task<Void> add(ClassModel classModel){
        return databaseReference.child(classModel.getSubjectCode()).setValue(classModel);
    }

    public Task<Void> update(String key, HashMap<String,Object>hashmap){
      return databaseReference.child(key).updateChildren(hashmap);
    }
    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }

}
