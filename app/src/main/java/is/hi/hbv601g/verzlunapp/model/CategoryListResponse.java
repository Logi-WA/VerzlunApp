package is.hi.hbv601g.verzlunapp.model;

import java.util.List;
import is.hi.hbv601g.verzlunapp.persistence.Category;

public class CategoryListResponse {
    private boolean success;
    private String message;
    private List<Category> data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Category> getData() { return data; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setData(List<Category> data) { this.data = data; }
}
