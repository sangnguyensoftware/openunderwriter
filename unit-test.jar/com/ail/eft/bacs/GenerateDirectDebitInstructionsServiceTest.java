package com.ail.eft.bacs;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.eft.bacs.GenerateDirectDebitInstructionsService.GenerateDirectDebitInstructionsArgument;
import com.google.common.collect.Lists;

public class GenerateDirectDebitInstructionsServiceTest {

    private GenerateDirectDebitInstructionsService sut;

    @Mock
    private GenerateDirectDebitInstructionsArgument args;
    @Mock
    private List<Record> payments;
    @Mock
    private List<String> lines;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = new GenerateDirectDebitInstructionsService();
        sut.setArgs(args);

        doReturn(payments).when(args).getRecordsArg();
        doReturn(lines).when(args).getLinesRet();

        ZonedDateTime zdt = ZonedDateTime.now(ZoneOffset.UTC);
        doReturn(Date.from(zdt.toInstant())).when(args).getProcessingDateArg();
        doReturn(Date.from(zdt.plusMonths(1).toInstant())).when(args).getExpiringDateArg();
    }

    @Test(expected = PreconditionException.class)
    public void nullServiceUserArgIsNotAllowed() throws BaseException {
        doReturn(null).when(args).getServiceUserArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void nullProcessingDateArgIsNotAllowed() throws BaseException {
        doReturn(null).when(args).getProcessingDateArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void nullExpiringDateArgIsNotAllowed() throws BaseException {
        doReturn(null).when(args).getExpiringDateArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void incompleteServiceUserArgIsNotAllowed() throws BaseException {
        doReturn(new ServiceUser("", "22-44-66", "11335577", "ServiceUserName")).when(args).getServiceUserArg();
        doReturn(true).when(args).getGenerateHeadersFootersArg();
        sut.invoke();
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void happyPath() throws BaseException {
        doReturn(FileSubmissionType.SINGLE_FILE_SUBMISSION).when(args).getFileSubmissionTypeArg();
        doReturn(ProcessingDayType.SINGLE_PROCESSING_DAY).when(args).getProcessingDayTypeArg();
        doReturn("1").when(args).getSubmissionSerialNumberArg();
        doReturn(true).when(args).getGenerateHeadersFootersArg();

        String processingDate = new SimpleDateFormat("yyDDD").format(args.getProcessingDateArg());
        String expiringDate = new SimpleDateFormat("yyDDD").format(args.getExpiringDateArg());

        doReturn(new ServiceUser("135790", "22-44-66", "11335577", "ServiceUserName")).when(args).getServiceUserArg();

        doReturn(Lists.newArrayList(
                new Record("Mr Test Account1", "11-11-11", "11223344", TransactionCode.DIRECT_DEBIT_INSTRUCTION_NEW, "P1111", args.getProcessingDateArg()),
                new Record("Ms Test Account2", "22-22-22", "22334455", TransactionCode.DIRECT_DEBIT_INSTRUCTION_NEW, "P2222", args.getProcessingDateArg()),
                new Record("Mrs Test Account3", "33-33-33", "33445566", TransactionCode.DIRECT_DEBIT_INSTRUCTION_NEW, "P3333", args.getProcessingDateArg())
        )).when(args).getRecordsArg();

        sut.invoke();

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(args).setLinesRet(captor.capture());

        List<String> results = captor.getValue();
        assertThat(results.size(), is(10));

        assertThat(results.get(0), is("VOL11000000                              135790                                1"));
        assertThat(results.get(1), is(String.format("HDR1A135790S  11357901     0001          %s %s  000000                    ", processingDate, expiringDate)));
        assertThat(results.get(2), is("HDR2F0000000100                                   00                            "));
        assertThat(results.get(3), is(String.format("UHL1%s 999999    000000004 MULTI                                             ", processingDate)));
        assertThat(results.get(4), is(String.format("1111111122334400N22446611335577    00000000000ServiceUserName   P1111             Mr Test Account1   %s", processingDate)));
        assertThat(results.get(5), is(String.format("2222222233445500N22446611335577    00000000000ServiceUserName   P2222             Ms Test Account2   %s", processingDate)));
        assertThat(results.get(6), is(String.format("3333333344556600N22446611335577    00000000000ServiceUserName   P3333             Mrs Test Account3  %s", processingDate)));
        assertThat(results.get(7), is(String.format("EOF1A135790S  11357901     0001          %s %s  000000                    ", processingDate, expiringDate)));
        assertThat(results.get(8), is("EOF2F0000000100                                   00                            "));
        assertThat(results.get(9), is("UTL10000000000000000000000000000000000000000        0000003                     "));
    }

}
