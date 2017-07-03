package galaxypim.pimclientside.Entities;

/**
 * Created by maher on 29/03/2017.
 */
public class User {


    private int id ;
    private String email ;
    private String first_name ;
    private String last_name ;
    private int tel ;
    private String url_image ;
    private String grade ;
    private int Code_Team ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCode_Team() {
        return Code_Team;
    }

    public void setCode_Team(int code_Team) {
        Code_Team = code_Team;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getTel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
