package com.ail.core.command;

import static javax.ejb.TransactionManagementType.BEAN;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionManagement;

import com.ail.core.CoreProxy;
import com.ail.core.ServerWarmChecker;
import com.ail.core.document.ExpireOldDocumentRequestsService.ExpireOldDocumentRequestsCommand;
import com.ail.core.persistence.hibernate.HibernateRunInTransaction;

@LocalBean
@Singleton
@Startup
@TransactionManagement(BEAN)
public class ExpireOldDocumentRequestsBean {
    private long INITIAL_DURATION = 1_000 * 60 * 10;    // Wait this long before firing the 1st event
    private long INTERVAL_FREQUENCY = 1_000 * 60 * 10;  // Wait this long between events

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
                if (new ServerWarmChecker().isServerWarmedUp()) {
                    new CoreProxy().newCommand(ExpireOldDocumentRequestsCommand.class).invoke();
                }
                return null;
            }

        }.invoke();
    }
}
