package dst.ass2.util;

import java.util.Date;
import java.util.List;

public class JobHelperDTO {

    private Long id;
    private String workflow;
    private Date start;
    private String username;
    private List<String> params;

    public JobHelperDTO(Long id, String workflow, Date start, String username,
            List<String> params) {
        super();
        this.id = id;
        this.workflow = workflow;
        this.start = start;
        this.username = username;
        this.params = params;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "JobHelperDTO [id=" + id + ", workflow=" + workflow + ", start="
                + start + ", username=" + username + ", params=" + params + "]";
    }

}
