package com.ail.pageflow.render;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.report.ReportQueryService.ReportQueryCommand;
import com.ail.pageflow.render.ReportWidgetHelper.ReportData;
import com.ail.pageflow.render.ReportWidgetHelper.ReportInterval;
import com.ail.pageflow.render.ReportWidgetHelper.ReportPeriod;
import com.ail.pageflow.render.ReportWidgetHelper.ReportStyle;
import com.ail.pageflow.render.ReportWidgetHelper.ReportType;
import com.google.common.collect.Lists;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CoreContext.class)
public class ReportWidgetHelperTest {

    ReportWidgetHelper sut = null;

    @Mock
    private Policy policy;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private ReportQueryCommand reportQueryCmd;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new ReportWidgetHelper();

        PowerMockito.mockStatic(CoreContext.class);
        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);

    }

    @Test
    public void verifyGetSingleColummnData() throws BaseException {

        List<Object[]> results = Lists.newArrayList(new Object[]{"key1", 1}, (new Object[]{"key2", 2}));
        when(coreProxy.newCommand(ReportQueryCommand.class)).thenReturn(reportQueryCmd);
        when(reportQueryCmd.getResultsListRet()).thenReturn(results);

        ReportData data = sut.getData(ReportType.REFERRED,
                ReportType.NONE,
                ReportInterval.Day,
                ReportPeriod.Month,
                ReportStyle.BarChart, null, "true");

        verify(reportQueryCmd).setQueryArg(eq("select DATE_FORMAT(createdDate, '%y-%m-%d') as description, "
                + "count(createdDate) as data1  from Policy  "
                + "where status = ?  and createdDate >= ?  group by DATE_FORMAT(createdDate, '%y-%m-%d')"));

        assertEquals(results, data.getData());
        assertEquals(2, data.getColumnTypes().length);

        assertEquals("Referrals by Day for This Month", data.getTitle());

        assertEquals("[['key1',1],['key2',2]]", data.getFormattedData());

        assertEquals(ReportType.REFERRED.getLabel(), data.getHeaders()[0]);
        assertEquals("", data.getHeaders()[1]);

    }

    @Test
    public void verifyGetDoubleColummnData() throws BaseException {

        List<Object[]> results = Lists.newArrayList(new Object[]{"key1", 1}, (new Object[]{"key2", 2}));

        when(coreProxy.newCommand(ReportQueryCommand.class)).thenReturn(reportQueryCmd);
        when(reportQueryCmd.getResultsListRet()).thenReturn(results);

        ReportData data = sut.getData(ReportType.QUOTATION,
                ReportType.ON_RISK,
                ReportInterval.Month,
                ReportPeriod.Qtr,
                ReportStyle.LineChart, null, "true");

        verify(reportQueryCmd).setQueryArg(eq("select CONCAT(YEAR(createdDate), '-', LPAD(MONTH(createdDate),2,'0')) as description, "
                + "count(createdDate) as data1  from Policy  "
                + "where (status = ? or status = ?)  and createdDate >= ?  group by YEAR(createdDate), MONTH(createdDate)"));

        verify(reportQueryCmd).setQueryArg(eq("select CONCAT(YEAR(inceptionDate), '-', LPAD(MONTH(inceptionDate),2,'0')) as description, "
                + "count(inceptionDate) as data1  from Policy  "
                + "where status = ?  and inceptionDate >= ?  group by YEAR(inceptionDate), MONTH(inceptionDate)"));

        assertEquals(2, data.getData().size());

        assertEquals(1, data.getData().get(0)[1]);
        assertEquals(1, data.getData().get(0)[2]);
        assertEquals(2, data.getData().get(1)[1]);
        assertEquals(2, data.getData().get(1)[2]);

        assertEquals(3, data.getColumnTypes().length);

        assertEquals("Quotes vs On-Risk by Month for This Quarter", data.getTitle());

        assertEquals("[['key1',1,1],['key2',2,2]]", data.getFormattedData());

        assertEquals("Quotes vs On-Risk", data.getHeaders()[0]);
        assertEquals(ReportType.QUOTATION.getLabel(), data.getHeaders()[1]);
        assertEquals(ReportType.ON_RISK.getLabel(), data.getHeaders()[2]);


    }

    @Test
    public void verifyAggregatorQuotation() throws BaseException {

        List<Object[]> results = Lists.newArrayList(new Object[]{"key1", 1}, (new Object[]{"key2", 2}));

        when(coreProxy.newCommand(ReportQueryCommand.class)).thenReturn(reportQueryCmd);
        when(reportQueryCmd.getResultsListRet()).thenReturn(results);

        sut.getData(ReportType.QUOTATION_AGG,
                ReportType.ON_RISK,
                ReportInterval.Month,
                ReportPeriod.Qtr,
                ReportStyle.LineChart, null, "true");

        verify(reportQueryCmd).setQueryArg(eq("select CONCAT(YEAR(createdDate), '-', LPAD(MONTH(createdDate),2,'0')) as description, "
                + "count(createdDate) as data1  from Policy  "
                + "where (status = ? and aggregator = true)  and createdDate >= ?  group by YEAR(createdDate), MONTH(createdDate)"));

    }

}
