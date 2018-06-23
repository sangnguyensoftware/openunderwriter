package com.ail.core.configure.hook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.CoreProxy;
import com.ail.core.configure.hook.Listener.EventType;
import com.ail.core.product.ClearProductCacheService.ClearProductCacheCommand;
import com.ail.core.product.RegisterProductService.RegisterProductCommand;
import com.ail.core.product.RemoveProductService.RemoveProductCommand;
import com.ail.core.product.ResetProductService.ResetProductCommand;
import com.liferay.portal.ModelListenerException;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ChangeDetector.class)
public class ListenerTest {
    private Listener sut;
    @Mock
    private CoreProxy mockCoreProxy;
    @Mock
    private ResetProductCommand mockQueuedResetProduct = null;
    @Mock
    private ClearProductCacheCommand mockClearProductCacheCommand = null;
    @Mock
    private RegisterProductCommand mockRegisterProductCommand;
    @Mock
    private RemoveProductCommand mockRemoveProductCommand;
    @Mock
    private ChangeDetector mockChangeDetector;

    @SuppressWarnings("unchecked")
    @Before
    public void setupSUT() throws Exception {
        MockitoAnnotations.initMocks(this);

        doReturn(mockQueuedResetProduct).when(mockCoreProxy).newCommand(eq("QueuedResetProductCommand"), any(Class.class));
        doReturn(mockClearProductCacheCommand).when(mockCoreProxy).newCommand(eq("QueuedClearProductCacheCommand"), any(Class.class));
        doReturn(mockRegisterProductCommand).when(mockCoreProxy).newCommand(eq("QueuedRegisterProductCommand"), any(Class.class));
        doReturn(mockRemoveProductCommand).when(mockCoreProxy).newCommand(eq("QueuedRemoveProductCommand"), any(Class.class));

        sut = spy(new Listener(mockCoreProxy));

        whenNew(ChangeDetector.class).withNoArguments().thenReturn(mockChangeDetector);
    }

    private DLFileEntry createMockStructure_Product_AIL_TestProduct_TestFile() throws Exception {
        DLFileEntry fileEntry = null;
        DLFolder folder = null;

        fileEntry = mock(DLFileEntry.class);
        doReturn("TestFile").when(fileEntry).getTitle();

        folder = createMockStructure_Product_AIL_TestProduct();
        doReturn(folder).when(fileEntry).getFolder();

        return fileEntry;
    }

    private DLFolder createMockStructure_Product_AIL_TestProduct() throws Exception {
        DLFolder retFolder = null;
        DLFolder folder = null;
        DLFolder parent = null;

        parent = mock(DLFolder.class);
        doReturn("TestProduct").when(parent).getName();
        doReturn(true).when(sut).isFolderAProductRoot(eq(parent));
        doReturn("/Product/AIL/TestProduct").when(parent).getPath();
        folder = parent;
        retFolder = parent;

        parent = mock(DLFolder.class);
        doReturn("AIL").when(parent).getName();
        doReturn(false).when(sut).isFolderAProductRoot(eq(parent));
        doReturn(parent).when(folder).getParentFolder();
        folder = parent;

        parent = mock(DLFolder.class);
        doReturn("Product").when(parent).getName();
        doReturn(false).when(sut).isFolderAProductRoot(eq(parent));
        doReturn(parent).when(folder).getParentFolder();
        folder = parent;

        return retFolder;
    }

    private DLFolder createMockStructure_Someplace_Outside_Product_Tree() throws Exception {
        DLFolder retFolder = null;
        DLFolder folder = null;
        DLFolder parent = null;

        parent = mock(DLFolder.class);
        doReturn("Tree").when(parent).getName();
        doReturn("/Someplace/Outside/Tree").when(parent).getPath();
        doReturn(false).when(sut).isFolderAProductRoot(eq(parent));
        folder = parent;
        retFolder = parent;

        parent = mock(DLFolder.class);
        doReturn(parent).when(folder).getParentFolder();
        doReturn("Outside").when(parent).getName();
        doReturn("/Someplace/Outside").when(parent).getPath();
        doReturn(false).when(sut).isFolderAProductRoot(eq(parent));
        folder = parent;

        parent = mock(DLFolder.class);
        doReturn(parent).when(folder).getParentFolder();
        doReturn("Someplace").when(parent).getName();
        doReturn("/Someplace").when(parent).getPath();
        doReturn(true).when(parent).isRoot();
        doReturn(false).when(sut).isFolderAProductRoot(eq(parent));
        folder = parent;

        return retFolder;
    }

    @Test
    public void testOnAfterRemoveResetsProduct() throws Exception {
        DLFileEntry mockFileEntry = createMockStructure_Product_AIL_TestProduct_TestFile();
        doReturn(true).when(sut).isChangeEvent(mockFileEntry, EventType.REMOVE);
        sut.onAfterRemove(mockFileEntry);
        verify(mockQueuedResetProduct, never()).invoke();
    }

    @Test
    public void testOnAfterUpdateForNonProductFilesDoesNothing() throws Exception {
        DLFileEntry mockFileEntry = mock(DLFileEntry.class);
        DLFolder mockDLFolder = mock(DLFolder.class);
        doReturn(mockDLFolder).when(mockFileEntry).getFolder();
        doReturn("My/Folder/Path").when(mockDLFolder).getPath();
        sut.onAfterUpdate(mockFileEntry);
        verify(mockClearProductCacheCommand, never()).invoke();
        verify(mockQueuedResetProduct, never()).invoke();
    }

    @Test
    public void testOnAfterUpdateForUnchangedNonRegistryDoesNotInvokeAnything() throws Exception {
        DLFileEntry mockFileEntry = createMockStructure_Product_AIL_TestProduct_TestFile();
        doReturn(false).when(sut).isChangeEvent(mockFileEntry, EventType.UPDATE);
        sut.onAfterUpdate(mockFileEntry);
        verify(mockClearProductCacheCommand, never()).invoke();
        verify(mockQueuedResetProduct, never()).invoke();
    }

    @Test
    public void testOnBeforeUpdateRecognisesProductPath() throws Exception {
        DLFileEntry mockFileEntry = mock(DLFileEntry.class);
        doReturn("/Product/SomeProductName").when(sut).fileEntry2FullPath(mockFileEntry);
        sut.onBeforeUpdate(mockFileEntry);
        verify(sut, times(1)).recordChange(eq(mockFileEntry));
    }

    @Test
    public void testOnBeforeUpdateRecognisesNoneProductPath() throws Exception {
        DLFileEntry mockFileEntry = mock(DLFileEntry.class);
        doReturn("/NoneProduct/SomeProductName").when(sut).fileEntry2FullPath(mockFileEntry);
        sut.onBeforeUpdate(mockFileEntry);
        verify(mockChangeDetector, never()).record(eq(mockFileEntry));
    }

    @Test(expected = NullPointerException.class)
    public void testFolderIsAProductFolderError() throws Exception {
        DLFolder mockFolderEntry = mock(DLFolder.class);
        doReturn(1234L).when(mockFolderEntry).getGroupId();
        doReturn(2345L).when(mockFolderEntry).getFolderId();
        doThrow(new NullPointerException()).when(sut).getFileEntry(eq(1234L), eq(2345L), eq("Registry.xml"));
        sut.isFolderAProductRoot(mockFolderEntry);
    }

    @Test
    public void testFolderIsAProductFolderFileNotFound() throws Exception {
        DLFolder mockFolderEntry = mock(DLFolder.class);
        doReturn(1234L).when(mockFolderEntry).getGroupId();
        doReturn(2345L).when(mockFolderEntry).getFolderId();
        doThrow(new NoSuchFileEntryException()).when(sut).getFileEntry(eq(1234L), eq(2345L), eq("Registry.xml"));
        assertFalse(sut.isFolderAProductRoot(mockFolderEntry));
    }

    @Test
    public void testFolderIsAProductFolder() throws Exception {
        DLFolder mockFolderEntry = mock(DLFolder.class);
        DLFileEntry mockFileEntry = mock(DLFileEntry.class);

        doReturn(1234L).when(mockFolderEntry).getGroupId();
        doReturn(2345L).when(mockFolderEntry).getFolderId();
        doReturn(mockFileEntry).when(sut).getFileEntry(eq(1234L), eq(2345L), eq("Registry.xml"));
        assertTrue(sut.isFolderAProductRoot(mockFolderEntry));
    }

    @Test
    public void testFileEntry2FullPathNullSafety() throws Exception {
        assertNull(sut.fileEntry2FullPath(null));
    }

    @Test
    public void testFileEntry2FullPathHappyPath() throws Exception {
        DLFileEntry fileEntry = createMockStructure_Product_AIL_TestProduct_TestFile();
        assertNotNull(sut.fileEntry2FullPath(fileEntry));
        assertEquals("/Product/AIL/TestProduct/TestFile", sut.fileEntry2FullPath(fileEntry));
    }

    @Test(expected = ModelListenerException.class)
    public void testFileEntry2FullPathException() throws Exception {
        DLFileEntry fileEntry = mock(DLFileEntry.class);
        doThrow(new NullPointerException()).when(fileEntry).getTitle();
        sut.fileEntry2FullPath(fileEntry);
    }

    @Test
    public void testFileEntry2ProductName() throws Exception {
        assertNull(sut.registryPath2ProductName(null));

        assertEquals("AIL.TestProduct", sut.registryPath2ProductName("/Product/AIL/TestProduct/Registry.xml"));
    }

    @Test(expected = IllegalStateException.class)
    public void testFileEntry2ProductNameWithoutRegistary() throws Exception {
        assertEquals("AIL.TestProduct", sut.registryPath2ProductName("/Product/AIL/TestProduct"));
    }

    @Test(expected = IllegalStateException.class)
    public void testRegistryPath2ProductNameOutsideOfTree() throws Exception {
        sut.registryPath2ProductName("/SomePlaceElse/File");
    }

    @Test
    public void testCreateHasNoHistoryDependency() throws Exception {
        DLFileEntry fileEntry = createMockStructure_Product_AIL_TestProduct_TestFile();
        doReturn("1.0").when(fileEntry).getVersion();
        sut.onBeforeCreate(fileEntry);
        sut.onAfterCreate(fileEntry);
    }

    @Test(expected = IllegalStateException.class)
    public void testFileEntry2ProductNameOutsideOfTree() throws Exception {
        DLFolder folderEntry = createMockStructure_Someplace_Outside_Product_Tree();
        sut.fileEntry2ProductName(folderEntry);
    }

    @Test
    public void testFullPathToProductName() throws Exception {
        assertEquals("AIL.TestProduct.One", sut.registryPath2ProductName("/Product/AIL/TestProduct/One/Registry.xml"));
    }
}
