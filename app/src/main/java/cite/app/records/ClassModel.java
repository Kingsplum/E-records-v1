package cite.app.records;

import java.util.Comparator;

public class ClassModel {

    String subjectCode,subjectName;

    ClassModel(){

    }

    public ClassModel(String subjectCode, String subjectName) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
    }
    //a - z compare
    public  static Comparator<ClassModel> ClassNameAZComparator = new Comparator<ClassModel>() {
        @Override
        public int compare(ClassModel c1, ClassModel c2) {
            return c1.getSubjectName().compareToIgnoreCase(c2.getSubjectName());
        }
    };
    //z - a compare
    public  static Comparator<ClassModel> ClassNameZAComparator = new Comparator<ClassModel>() {
        @Override
        public int compare(ClassModel c1, ClassModel c2) {
            return c2.getSubjectName().compareToIgnoreCase(c1.getSubjectName());
        }
    };




    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
