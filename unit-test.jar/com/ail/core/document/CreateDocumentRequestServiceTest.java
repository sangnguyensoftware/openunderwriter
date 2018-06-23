package com.ail.core.document;

import static com.ail.core.document.DocumentRequestType.GENERATE_AND_DOWNLOAD;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.document.CreateDocumentRequestService.CreateDocumentRequestArgument;

public class CreateDocumentRequestServiceTest {

    private static final String DUMMY_DOCUMENT_TYPE = "DUMMY_DOCUMENT_TYPE";
    private static final String DUMMY_REQUEST_ID = "REQUEST_ID";
    private static final Long DUMMY_DOCUMENT_SOURCE_UID = 1L;
    private static final Long DUMMY_DOCUMENT_UID = 2L;

    private CreateDocumentRequestService sut;

    @Mock
    private CreateDocumentRequestArgument args;
    @Mock
    private Core core;
    @Mock
    private DocumentRequest documentRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = new CreateDocumentRequestService();

        sut.setArgs(args);
        sut.setCore(core);

        doReturn(DUMMY_REQUEST_ID).when(args).getRequestIdRet();

        doReturn(documentRequest).when(core).newType(eq(DocumentRequest.class));
    }

    @Test(expected = PreconditionException.class)
    public void nullSourceUidArgumentShouldBeTrapped() throws PreconditionException, PostconditionException, BaseException {
        doReturn(DUMMY_DOCUMENT_TYPE).when(args).getDocumentTypeArg();
        doReturn(null).when(args).getSourceUIDArg();
        doReturn(null).when(args).getDocumentUIDArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void nullDocumentTypeArgumentShouldBeTrapped() throws PreconditionException, PostconditionException, BaseException {
        doReturn(null).when(args).getDocumentTypeArg();
        doReturn(DUMMY_DOCUMENT_SOURCE_UID).when(args).getSourceUIDArg();
        doReturn(null).when(args).getDocumentUIDArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void nullDocumentUidArgumentShouldBeTrapped() throws PreconditionException, PostconditionException, BaseException {
        doReturn(null).when(args).getDocumentTypeArg();
        doReturn(null).when(args).getSourceUIDArg();
        doReturn(null).when(args).getDocumentUIDArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void nullInvalidArgumentCombinationShouldBeTrapped() throws PreconditionException, PostconditionException, BaseException {
        doReturn(DUMMY_DOCUMENT_TYPE).when(args).getDocumentTypeArg();
        doReturn(null).when(args).getSourceUIDArg();
        doReturn(DUMMY_DOCUMENT_UID).when(args).getDocumentUIDArg();
        sut.invoke();
    }

    @Test(expected=PostconditionException.class)
    public void nullRequestIdShouldBeTrapped() throws PreconditionException, PostconditionException, BaseException {
        doReturn(GENERATE_AND_DOWNLOAD).when(args).getDocumentRequestTypeArg();
        doReturn(DUMMY_DOCUMENT_SOURCE_UID).when(args).getSourceUIDArg();
        doReturn(DUMMY_DOCUMENT_TYPE).when(args).getDocumentTypeArg();
        doReturn(null).when(args).getRequestIdRet();
        sut.invoke();
    }

    @Test
    public void documentRequestShouldBeCreatedAndPersisted() throws PreconditionException, PostconditionException, BaseException {
        doReturn(GENERATE_AND_DOWNLOAD).when(args).getDocumentRequestTypeArg();
        doReturn(DUMMY_DOCUMENT_SOURCE_UID).when(args).getSourceUIDArg();
        doReturn(DUMMY_DOCUMENT_TYPE).when(args).getDocumentTypeArg();
        sut.invoke();
        verify(core).newType(eq(DocumentRequest.class));
        verify(core).create(eq(documentRequest));
    }

}
