package realincome.prosad.shuvo.realincome.Model;

public class CheckTaskModel {
    String task_name;
    String task_id;
    String task_image;

    public CheckTaskModel(String task_name, String task_id, String task_image, String complete) {
        this.task_name = task_name;
        this.task_id = task_id;
        this.task_image = task_image;
        this.complete = complete;
    }

    public String getTask_id() {

        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    String complete;

    public CheckTaskModel() {
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_image() {
        return task_image;
    }

    public void setTask_image(String task_image) {
        this.task_image = task_image;
    }
}
