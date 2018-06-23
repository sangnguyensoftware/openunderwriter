package com.ail.core.product.liferay;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.product.liferay.LiferayLocateFileEntryService.LiferayLocateFileEntryArgument;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;

public class LiferayLocateFileEntryServiceTest {

    LiferayLocateFileEntryService sut;

    private Collection<String> namespaces = Arrays.asList("com.ail.core.Core",
                                                          "AIL.Base.Registry",
                                                          "AIL.Demo.OtherProduct.Registry",
                                                          "AIL.Demo.OtherProductLong.Registry",
                                                          "AIL.Demo.SampleProduct.Registry");

    private Collection<String> ancestors = Arrays.asList("AIL.Demo.SampleProduct.Registry",
                                                         "AIL.Base.Registry",
                                                         "com.ail.core.Core");

    @Mock
    private CoreProxy coreProxy;
    @Mock
    private FileEntry fileEntry;
    @Mock
    LiferayLocateFileEntryArgument args;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = spy(new LiferayLocateFileEntryService(1234L, "ROOT"));

        sut.setArgs(args);

        doReturn(namespaces).when(sut).fetchNamespaces();
        doReturn(coreProxy).when(sut).createCoreProxyForNamespace(anyString());
        doReturn(ancestors).when(coreProxy).getConfigurationNamespaceParent();
    }

    @Test(expected=PreconditionException.class)
    public void testPreconditionChecks() throws PreconditionException, PostconditionException {
        doReturn(null).when(args).getProductUrlArg();
        sut.invoke();
    }

    @Test
    public void checkSimilarNamespacesDontClash() throws PortalException {
        assertEquals("AIL.Demo.OtherProductLong.Registry", sut.findNamespaceForUrlPath("AIL/Demo/OtherProductLong/Resource"));
        assertEquals("AIL.Demo.OtherProduct.Registry", sut.findNamespaceForUrlPath("AIL/Demo/OtherProduct/Resource"));
    }

    @Test
    public void testFirstLevelNamespaceHit() throws PortalException, SystemException {
        doReturn("en").when(sut).getThreadLanguage();
        doReturn(1L).when(sut).getFolderId(eq(0L), eq("ROOT"));
        doReturn(2L).when(sut).getFolderId(eq(1L), eq("AIL"));
        doReturn(3L).when(sut).getFolderId(eq(2L), eq("Base"));
        doReturn(fileEntry).when(sut).getFileEntry(eq(3L), eq("Resource_en"));

        doThrow(new PortalException()).when(sut).getFolderId(eq(2L), eq("Demo"));

        FileEntry fe=sut.locateFileEntryInNamespaceHierarchy("/AIL/Base/Resource");

        assertTrue(fileEntry==fe);
    }

    @Test
    public void testSecondLevelNamespaceHit()  throws PortalException, SystemException {
        doThrow(new PortalException()).when(sut).locateFileEntry(eq("/AIL/Demo/SampleProduct/Resource"));
        doReturn("en").when(sut).getThreadLanguage();
        doReturn(1L).when(sut).getFolderId(eq(0L), eq("ROOT"));
        doReturn(2L).when(sut).getFolderId(eq(1L), eq("AIL"));
        doReturn(3L).when(sut).getFolderId(eq(2L), eq("Base"));
        doReturn(fileEntry).when(sut).getFileEntry(eq(3L), eq("Resource_en"));

        FileEntry fe=sut.locateFileEntryInNamespaceHierarchy("/AIL/Demo/SampleProduct/Resource");

        assertTrue(fileEntry==fe);
    }

    @Test
    public void testLanguageSpecificResourceIsFound()  throws PortalException, SystemException {
        doReturn("en").when(sut).getThreadLanguage();
        doReturn(1L).when(sut).getFolderId(eq(0L), eq("ROOT"));
        doReturn(2L).when(sut).getFolderId(eq(1L), eq("AIL"));
        doReturn(3L).when(sut).getFolderId(eq(2L), eq("Demo"));
        doReturn(4L).when(sut).getFolderId(eq(3L), eq("SampleProduct"));
        doReturn(fileEntry).when(sut).getFileEntry(eq(4L), eq("Resource_en"));

        FileEntry fe=sut.locateFileEntryInNamespaceHierarchy("/AIL/Demo/SampleProduct/Resource");

        verify(sut, never()).getFileEntry(anyLong(), eq("Resource"));
        assertTrue(fileEntry==fe);
    }

    @Test
    public void testLanguageSpecificResourceIsNotFound()  throws PortalException, SystemException {
        doReturn("en").when(sut).getThreadLanguage();
        doReturn(1L).when(sut).getFolderId(eq(0L), eq("ROOT"));
        doReturn(2L).when(sut).getFolderId(eq(1L), eq("AIL"));
        doReturn(3L).when(sut).getFolderId(eq(2L), eq("Demo"));
        doReturn(4L).when(sut).getFolderId(eq(3L), eq("SampleProduct"));
        doThrow(new PortalException()).when(sut).getFileEntry(eq(4L), eq("Resource_en"));
        doReturn(fileEntry).when(sut).getFileEntry(eq(4L), eq("Resource"));

        FileEntry fe=sut.locateFileEntryInNamespaceHierarchy("/AIL/Demo/SampleProduct/Resource");

        assertTrue(fileEntry==fe);
    }

    @Test
    public void nestedProductFoldersMustBeSupported() throws PortalException {
        doReturn(Arrays.asList("AIL.Top.Registry","AIL.Top.Nested.Registry")).when(sut).fetchNamespaces();
        String namespace = sut.findNamespaceForUrlPath("/AIL/Top/Nested/File.xml");
        assertThat(namespace, is("AIL.Top.Nested.Registry"));
    }
}
