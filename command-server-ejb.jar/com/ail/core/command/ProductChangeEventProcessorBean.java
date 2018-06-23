package com.ail.core.command;

import static javax.ejb.TransactionManagementType.BEAN;
import static org.hibernate.criterion.Projections.max;
import static org.hibernate.criterion.Projections.projectionList;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionManagement;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.ail.core.CoreProxy;
import com.ail.core.ServerWarmChecker;
import com.ail.core.persistence.hibernate.HibernateRunInTransaction;
import com.ail.core.persistence.hibernate.HibernateSessionBuilder;
import com.ail.core.product.ProductChangeEvenProcessorService.ProductChangeEvenProcessorCommand;
import com.ail.core.product.ProductChangeEvent;

@LocalBean
@Singleton
@Startup
@TransactionManagement(BEAN)
public class ProductChangeEventProcessorBean {
    private long INITIAL_DURATION = 1_000 * 60 * 2; // Wait this long before firing the 1st event
    private long INTERVAL_FREQUENCY = 1_000 * 2;    // Wait this long between events
    private long QUIET_PERIOD = 2_000;              // After detecting a change, do nothing until no more changes are seen for this long.

    private ServerWarmChecker serverWarmChecker=new ServerWarmChecker();

    @Resource
    private TimerService timerService;

    @PostConstruct
    private void init() {
        TimerConfig config = new TimerConfig();
        config.setPersistent(false);
        timerService.createIntervalTimer(INITIAL_DURATION, INTERVAL_FREQUENCY, config);
    }

    @Timeout
    private void execute() throws Throwable {
        new HibernateRunInTransaction<Object>() {
            @Override
            public Object run() throws Throwable {
                if (serverWarmChecker.isServerWarmedUp() && isProcessingNecessary()) {
                    new CoreProxy().newCommand(ProductChangeEvenProcessorCommand.class).invoke();
                }

                return null;
            }
        }.invoke();
    }

    protected boolean isProcessingNecessary() {
        Session session = HibernateSessionBuilder.getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(ProductChangeEvent.class);
        criteria.setProjection(projectionList().add(max("createdDate")));
        criteria.setReadOnly(true);

        @SuppressWarnings("unchecked")
        List<Date> results = (List<Date>) criteria.list();

        if (results.size() == 1 && results.get(0) != null) {
            Date newestEvent = results.get(0);
            if (System.currentTimeMillis() - newestEvent.getTime() > QUIET_PERIOD) {
                return true;
            }
        }

        return false;
    }
}
