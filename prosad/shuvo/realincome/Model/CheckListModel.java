package realincome.prosad.shuvo.realincome.Model;

public class CheckListModel {
    String no;
    String task;
    String message;

    public CheckListModel() {
    }

    public CheckListModel(String no, String task, String message) {
        this.no = no;
        this.task = task;
        this.message = message;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
