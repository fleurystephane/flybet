package acceptance.pronos.facilities;

public class PublishError {
    private boolean confirm = false;

    public PublishError() { }

    public void confirmError(){
        this.confirm = true;
    }

    public boolean isConfirm() {
        return confirm;
    }
}
