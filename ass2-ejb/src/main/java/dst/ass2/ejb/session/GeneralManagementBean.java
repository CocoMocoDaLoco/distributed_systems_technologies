package dst.ass2.ejb.session;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Future;

import dst.ass2.ejb.dto.AuditLogDTO;
import dst.ass2.ejb.dto.BillDTO;

import dst.ass2.ejb.session.interfaces.IGeneralManagementBean;

public class GeneralManagementBean implements IGeneralManagementBean {
    
    // TODO

    @Override
    public void addPrice(Integer nrOfHistoricalJobs, BigDecimal price) {
        // TODO
    }


    @Override
    public Future<BillDTO> getBillForUser(String username) throws Exception {
        // TODO
        return null;
    }

    @Override
    public List<AuditLogDTO> getAuditLogs() {
        // TODO
        return null;
    }

    @Override
    public void clearPriceCache() {
        //TODO
    }
}
