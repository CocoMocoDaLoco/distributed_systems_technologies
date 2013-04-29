package dst.ass2.ejb.session;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import dst.ass2.ejb.dto.AuditLogDTO;
import dst.ass2.ejb.dto.BillDTO;
import dst.ass2.ejb.management.PriceManagementBean;
import dst.ass2.ejb.management.interfaces.IPriceManagementBean;
import dst.ass2.ejb.session.interfaces.IGeneralManagementBean;
import dst.ass2.ejb.util.EJBUtils;

@Remote(IGeneralManagementBean.class)
@Stateless
public class GeneralManagementBean implements IGeneralManagementBean {

    private final IPriceManagementBean priceManagementBean;

    public GeneralManagementBean() throws NamingException {
        priceManagementBean = (IPriceManagementBean)
                EJBUtils.lookup(new InitialContext(), PriceManagementBean.class);
    }

    @Override
    public void addPrice(Integer nrOfHistoricalJobs, BigDecimal price) {
        priceManagementBean.setPrice(nrOfHistoricalJobs, price);
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
        priceManagementBean.clearCache();
    }
}
