package cite.app.records;

import java.util.Comparator;

public class StudentModel {


    String sName,sId,sClass,sCountAct;



    StudentModel(){
        //safe
    }

    public StudentModel(String sName, String sId,String sClass,String sCountAct) {
        this.sName = sName;
        this.sId = sId;
        this.sClass = sClass;
        this.sCountAct = sCountAct;
    }

    //a - z compare
    public  static Comparator<StudentModel> StudentNameAZComparator = new Comparator<StudentModel>() {
        @Override
        public int compare(StudentModel s1, StudentModel s2) {
            return s1.getsName().compareToIgnoreCase(s2.getsName());
        }
    };
    //z - a compare
    public  static Comparator<StudentModel> StudentNameZAComparator = new Comparator<StudentModel>() {
        @Override
        public int compare(StudentModel s1, StudentModel s2) {
            return s2.getsName().compareToIgnoreCase(s1.getsName());
        }
    };

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getsClass() {
        return sClass;
    }

    public void setsClass(String sId) {
        this.sClass = sClass;
    }

    public void setsCountAct(String sCountAct) {
        this.sCountAct = sCountAct;
    }

    public String getsCountAct() {
        return sCountAct;
    }
}
