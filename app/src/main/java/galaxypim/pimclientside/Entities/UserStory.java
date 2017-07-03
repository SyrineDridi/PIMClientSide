package galaxypim.pimclientside.Entities;

/**
 * Created by Syrine on 18/05/2017.
 */

public class UserStory {

    private int id ;
    private String nom ;
    private String Desc ;
    private String avancement ;
    private int Estimation ;
    private String priority ;
    private String nom_projet ;


    public UserStory() {
    }

    public UserStory(int id, String nom, String avancement, String desc, int estimation, String priority, String nom_projet) {
        this.id = id;
        this.nom = nom;
        this.avancement = avancement;
        Desc = desc;
        Estimation = estimation;
        this.priority = priority;
        this.nom_projet = nom_projet;
    }

    @Override
    public String toString() {
        return "UserStory{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", avancement='" + avancement + '\'' +
                ", Desc='" + Desc + '\'' +
                ", Estimation=" + Estimation +
                ", priority='" + priority + '\'' +
                ", nom_projet='" + nom_projet + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAvancement() {
        return avancement;
    }

    public void setAvancement(String avancement) {
        this.avancement = avancement;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public int getEstimation() {
        return Estimation;
    }

    public void setEstimation(int estimation) {
        Estimation = estimation;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNom_projet() {
        return nom_projet;
    }

    public void setNom_projet(String nom_projet) {
        this.nom_projet = nom_projet;
    }
}
