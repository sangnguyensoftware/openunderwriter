package com.ail.pageflow.render;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.product.ListProductsService.ListProductsCommand;
import com.ail.core.product.ProductDetails;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.report.ReportQueryService.ReportQueryCommand;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ReportWidgetHelper {

    private static final String ALL_VALUES = "All";

    public enum ReportType {

            NONE("None"),
            APPLICATION("Applications"),
            QUOTATION("Quotes"),
            QUOTATION_AGG("Aggregator Quotes"),
            SUBMITTED("Submitted"),
            ON_RISK("On-Risk"),
            REFERRED("Referrals"),
            DECLINED("Declined");

         String label;

         private ReportType(String label) {
             this.label = label;
         }
         public String getLabel() {
             return label;
         }
    }

    public enum ReportInterval {
        Product, Day, Month, Year;
    }

    public enum ReportPeriod {

        All(ALL_VALUES),
        Today("Today"),
        Week("This Week"),
        Month("This Month"),
        Qtr("This Quarter"),
        Year("This Year");

        String label;

        private ReportPeriod(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum ReportStyle {
        PieChart, BarChart, LineChart, ColumnChart, Histogram;
    }


    public static List<String> getProducts() {

        List<String> products = Lists.newArrayList();

        products.add(ALL_VALUES);

        ListProductsCommand listProductsCommand = new CoreProxy().newCommand(ListProductsCommand.class);

        try {
            listProductsCommand.invoke();
            for (ProductDetails product : listProductsCommand.getProductsRet()) {
                products.add(product.getName());
            }

        } catch (BaseException e) {
            new CoreProxy().logError("Cannot retrieve products", e);
        }

        return products;
    }

    public class ReportData {

        String title;

        String filterDescription;

        String[] headers;

        String[] columnTypes;

        List<Object[]> data = Lists.newArrayList();


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFilterDescription() {
            return filterDescription;
        }

        public void setFilterDescription(String filterDescription) {
            this.filterDescription = filterDescription;
        }

        public String[] getHeaders() {
            return headers;
        }

        public void setHeaders(String[] headers) {
            this.headers = headers;
        }

        public String[] getColumnTypes() {
            return columnTypes;
        }

        public void setColumnTypes(String[] columnTypes) {
            this.columnTypes = columnTypes;
        }

        public List<Object[]> getData() {
            return data;
        }

        public void setData(List<Object[]> data) {
            this.data = data;
        }

        public String getFormattedData() {

            StringBuilder results = new StringBuilder();
            results.append("[");
            List<Object[]> data = getData();

            for(int r = 0; r < data.size(); r++)  {

                results.append("[");

                Object value = data.get(r)[0];
                    results.append("'" + value + "'");

                for (int c = 1; c < data.get(r).length; c++) {
                    results.append("," + data.get(r)[c]);
                }

                results.append("]");

                if (r + 1 < data.size()) {
                    results.append(",");
                }
            }
            results.append("]");
            return results.toString();
        }

    }

    public ReportData getData(ReportType type1, ReportType type2, ReportInterval interval, ReportPeriod period, ReportStyle style, String product, String includeTest) {

        ReportData reportData = new ReportData();

        reportData.setFilterDescription((ALL_VALUES.equals(product) ? "" : " Product: " + product));

        try {
            String label = "";
            List<Object[]> data1 = doQuery(reportData, type1, interval, period, style, product, includeTest);

            if (type2 != ReportType.NONE) {

                List<Object[]> data2 = doQuery(reportData, type2, interval, period, style, product, includeTest);

                label = type1.getLabel() + " vs " + type2.getLabel();
                reportData.setHeaders(new String[]{label, type1.getLabel(), type2.getLabel()});
                reportData.setColumnTypes(new String[]{"string","number", "number"});
                reportData.setData(mergeData(data1, data2));

            } else {
                label = type1.getLabel();
                reportData.setHeaders(new String[]{label, ""});
                reportData.setColumnTypes(new String[]{"string","number"});
                reportData.setData(data1);
            }

            reportData.setTitle(
                    (hasProduct(product) && interval!= ReportInterval.Product ? product :  label) +
                    " by " + interval +
                    (period == ReportPeriod.All ? "" : " for " + period.getLabel()));

        } catch (BaseException e) {
            getCore().logError("Problem generating report widget", e);
            reportData.setTitle("Error");
        }

        return reportData;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> mergeData(List<Object[]> data1, List<Object[]> data2) {
        Map<Comparable<Object>, Object[]> merged = Maps.newTreeMap();
        for (Object[] row: data1) {
            merged.put((Comparable<Object>)row[0],
                    new Object[]{row[0], row[1], 0});
        }
        for(Object[] row: data2) {
            Object id = row[0];
            if (merged.containsKey(id)) {
                merged.get(id)[2] = row[1];
            } else {
                merged.put((Comparable<Object>)row[0],
                        new Object[]{row[0], 0, row[1]});
            }
        }
        return Lists.newArrayList(merged.values());
    }

    public List<Object[]> doQuery(ReportData reportData,
                                ReportType type,
                                ReportInterval interval,
                                ReportPeriod period,
                                ReportStyle style,
                                String product,
                                String includeTest) throws BaseException {


        List<Object> queryArgs = Lists.newArrayList();

        StringBuilder hql = new StringBuilder("select ");

        select(type, interval, hql);
        from(type, hql);
        where(type, period, product, includeTest, queryArgs, hql);
        groupBy(type, interval, hql);

        ReportQueryCommand reportCommand = getCore().newCommand(ReportQueryCommand.class);
        reportCommand.setQueryArg(hql.toString());
        reportCommand.setQueryArgumentsArg(queryArgs);
        reportCommand.invoke();

        return reportCommand.getResultsListRet();
    }

    private CoreProxy getCore() {
        if (CoreContext.getCoreProxy() == null) {
            CoreContext.setCoreProxy(new CoreProxy());
        }
        return CoreContext.getCoreProxy();
    }

    private void groupBy(ReportType type, ReportInterval interval, StringBuilder hql) {
        String groupBy = "";
        switch (interval) {
            case Product:
                groupBy = "productTypeId";
                break;
            default:
                String dateField = "";
                switch (type) {
                    case ON_RISK:
                        dateField = "inceptionDate";
                        break;
                    default:
                        dateField = "createdDate";
                }
                groupBy = getFormattedDate(interval, dateField);
        }

        hql.append(" group by " + groupBy);
    }

    private String getFormattedDate(ReportInterval interval, String dateField) {

        switch (interval) {
            case Day:
                return "DATE_FORMAT(" + dateField + ", '%y-%m-%d')";
            case Month:
                return "YEAR(" + dateField + "), MONTH(" + dateField + ")";
            case Year:
                return "YEAR(" + dateField + ")";
            default:
                 return dateField;
        }
    }

    private void where(ReportType type, ReportPeriod period, String product, String includeTest, List<Object> queryArgs, StringBuilder hql) {

        switch (type) {
            case QUOTATION:
                hql.append(" where (status = ? or status = ?) ");
                queryArgs.add(PolicyStatus.QUOTATION);
                queryArgs.add(PolicyStatus.SUBMITTED);
                break;
            case QUOTATION_AGG:
                hql.append(" where (status = ? and aggregator = true) ");
                queryArgs.add(PolicyStatus.QUOTATION);
                break;
            default:
                hql.append(" where status = ? ");
                queryArgs.add(PolicyStatus.valueOf(type.name()));
        }


        if (period != ReportPeriod.All ) {

            String datePeriod = "";
            switch (type) {
                case ON_RISK:
                    datePeriod = "inceptionDate";
                    break;
                default:
                    datePeriod = "createdDate";
            }

            if (period != ReportPeriod.All) {

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.clear(Calendar.MINUTE);
                cal.clear(Calendar.SECOND);
                cal.clear(Calendar.MILLISECOND);
                switch (period) {
                    case Today:
                        break;
                    case Week:
                        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                        break;
                    case Month:
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        break;
                    case Qtr:
                        int month = cal.get(Calendar.MONTH);
                        cal.set(Calendar.MONTH,
                                (month >= Calendar.JANUARY && month <= Calendar.MARCH)  ? 0 :
                                (month >= Calendar.APRIL && month <= Calendar.JUNE)     ? 3 :
                                (month >= Calendar.JULY && month <= Calendar.SEPTEMBER) ? 6 : 9);
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        break;
                    case Year:
                        cal.set(Calendar.DAY_OF_YEAR, 1);
                        break;
                }

                hql.append(" and " + datePeriod + " >= ? ");
                queryArgs.add(cal.getTime());
            }
        }

        if (hasProduct(product)) {
            hql.append(" and productTypeId = ? ");
            queryArgs.add(product);
        }

        if (!Boolean.valueOf(includeTest)) {
            hql.append(" and testCase = ? ");
            queryArgs.add(false);
        }
    }

    private void from(ReportType type, StringBuilder hql) {

        hql.append(" from Policy ");

    }

    private String select(ReportType type, ReportInterval interval, StringBuilder hql) {

        String descriptionField = null;
        String sumField = null;

        // Sum field(s)
        switch (interval) {
            case Product:
                sumField = "productName";
                break;
            default:
                sumField = type == ReportType.ON_RISK ? "inceptionDate" : "createdDate";
        }

        // Description field
        switch (interval) {
            case Product:
                descriptionField = "productName";
                break;
            case Month:
                descriptionField = "CONCAT(YEAR(" + sumField + "), '-', LPAD(MONTH(" + sumField + "),2,'0'))";
                break;
            default:
                descriptionField = getFormattedDate(interval, sumField);
        }

        String valueField = "count(" + sumField + ")";

        hql.append(descriptionField + " as description, ");
        hql.append(valueField  + " as data1 ");

        return descriptionField;
    }

    private boolean hasProduct(String product) {
        return StringUtils.hasLength(product)
                && !ALL_VALUES.equals(product);
    }

}
