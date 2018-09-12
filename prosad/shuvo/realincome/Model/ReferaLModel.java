package realincome.prosad.shuvo.realincome.Model;

public class ReferaLModel {
    String name;
    String number;

    public ReferaLModel() {
    }

    public ReferaLModel(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
