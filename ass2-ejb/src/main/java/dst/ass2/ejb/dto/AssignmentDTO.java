package dst.ass2.ejb.dto;

import java.io.Serializable;
import java.util.List;

public class AssignmentDTO implements Serializable {

    private static final long serialVersionUID = -6468125387570684307L;
    private Long gridId;
    private Integer numCPUs;
    private String workflow;
    private List<String> params;
    private List<Long> computerIds;

    public AssignmentDTO(Long gridId, Integer numCPUs, String workflow,
            List<String> params, List<Long> computerIds) {
        super();
        this.gridId = gridId;
        this.numCPUs = numCPUs;
        this.workflow = workflow;
        this.params = params;
        this.computerIds = computerIds;
    }

    public Long getGridId() {
        return gridId;
    }

    public void setGridId(Long gridId) {
        this.gridId = gridId;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public Integer getNumCPUs() {
        return numCPUs;
    }

    public void setNumCPUs(Integer numCPUs) {
        this.numCPUs = numCPUs;
    }

    public List<Long> getComputerIds() {
        return computerIds;
    }

    public void setComputerIds(List<Long> computerIds) {
        this.computerIds = computerIds;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((computerIds == null) ? 0 : computerIds.hashCode());
        result = prime * result + ((gridId == null) ? 0 : gridId.hashCode());
        result = prime * result + ((numCPUs == null) ? 0 : numCPUs.hashCode());
        result = prime * result + ((params == null) ? 0 : params.hashCode());
        result = prime * result
                + ((workflow == null) ? 0 : workflow.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssignmentDTO other = (AssignmentDTO) obj;
        if (computerIds == null) {
            if (other.computerIds != null)
                return false;
        } else if (!computerIds.equals(other.computerIds))
            return false;
        if (gridId == null) {
            if (other.gridId != null)
                return false;
        } else if (!gridId.equals(other.gridId))
            return false;
        if (numCPUs == null) {
            if (other.numCPUs != null)
                return false;
        } else if (!numCPUs.equals(other.numCPUs))
            return false;
        if (params == null) {
            if (other.params != null)
                return false;
        } else if (!params.equals(other.params))
            return false;
        if (workflow == null) {
            if (other.workflow != null)
                return false;
        } else if (!workflow.equals(other.workflow))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AssignmentDTO [gridId=" + gridId + ", numCPUs=" + numCPUs
                + ", workflow=" + workflow + ", params=" + params
                + ", computerIds=" + computerIds + "]";
    }

}
