package com.ail.core.label;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.HasLabels;
import com.ail.core.Note;
import com.ail.core.PreconditionException;
import com.ail.core.Type;
import com.ail.core.Version;
import com.ail.core.factory.UndefinedTypeError;
import com.ail.core.label.LabelsForSubjectService.LabelsForSubjectArgument;
import com.ail.pageflow.PageFlowContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PageFlowContext.class, CoreContext.class })
public class LabelsForSubjectServiceTest {

    private static final String LABEL_ONE = "Label One";
    private static final String LABEL_TWO = "Label Two";
    private static final String LABEL_THREE = "Label Three";

    private static final String DISCRIMINATOR_ONE = "Discriminator one";
    private static final String DISCRIMINATOR_TWO = "Discriminator two";
    private static final String DISCRIMINATOR_THREE = "Discriminator three";
    private static final String DISCRIMINATOR_REGEX = "Discriminator.*";

    LabelsForSubjectService sut;

    @Mock
    private LabelsForSubjectArgument args;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private Labels labels;
    @Mock
    private Type rootType;
    @Mock
    private Type localType;

    @Captor
    ArgumentCaptor<Set<String>> labelsRetCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = new LabelsForSubjectService();
        sut.setArgs(args);

        mockStatic(PageFlowContext.class);
        mockStatic(CoreContext.class);

        when(PageFlowContext.getCoreProxy()).thenReturn(coreProxy);

        doReturn(labels).when(coreProxy).newType(eq("Labels"), eq(Labels.class));

        doReturn(HasLabels.class).when(args).getSubjectArg();
        doReturn(rootType).when(args).getRootModelArg();
        doReturn(localType).when(args).getLocalModelArg();
    }

    @Test(expected = PreconditionException.class)
    public void checkThatNullSubjectIsCaught() throws BaseException {
        doReturn(null).when(args).getSubjectArg();
        sut.invoke();
    }

    @Test
    public void checkThatAnEmptyListIsReturnedIfNoLabelsTypeIsDefined() throws BaseException {
        doReturn(null).when(coreProxy).newType(eq("Labels"), eq(Labels.class));

        sut.invoke();

        verify(args).setLabelsRet(labelsRetCaptor.capture());
        Set<String> capturedLabelsRet = labelsRetCaptor.getValue();
        assertThat(capturedLabelsRet, hasSize(0));
    }

    @Test
    public void checkThatAnEmptyListIsReturnedIfLabelsTypeIsUndefined() throws BaseException {
        doThrow(UndefinedTypeError.class).when(coreProxy).newType(eq("Labels"), eq(Labels.class));

        sut.invoke();

        verify(args).setLabelsRet(labelsRetCaptor.capture());
        Set<String> capturedLabelsRet = labelsRetCaptor.getValue();
        assertThat(capturedLabelsRet, hasSize(0));
    }

    @Test
    public void checkSimpleLabelListHitWithoutConstraints() throws BaseException {
        doReturn(labelsNoConstraints()).when(coreProxy).newType(eq("Labels"), eq(Labels.class));
        doReturn(Note.class).when(args).getSubjectArg();

        sut.invoke();

        verify(args).setLabelsRet(labelsRetCaptor.capture());
        Set<String> capturedLabelsRet = labelsRetCaptor.getValue();
        assertThat(capturedLabelsRet, hasSize(3));
        assertThat(capturedLabelsRet, hasItems(LABEL_ONE, LABEL_TWO, LABEL_THREE));
    }

    @Test
    public void checkSimpleLabelListHitWithFullMatchDiscriminator() throws BaseException {
        doReturn(labelsWithLabelsDiscriminator()).when(coreProxy).newType(eq("Labels"), eq(Labels.class));
        doReturn(Note.class).when(args).getSubjectArg();
        doReturn(DISCRIMINATOR_ONE).when(args).getDiscriminatorArg();

        sut.invoke();

        verify(args).setLabelsRet(labelsRetCaptor.capture());
        Set<String> capturedLabelsRet = labelsRetCaptor.getValue();
        assertThat(capturedLabelsRet, hasSize(1));
        assertThat(capturedLabelsRet, hasItems(LABEL_ONE));
    }

    @Test
    public void checkSimpleLabelListHitWithMultiMatchDiscriminator() throws BaseException {
        doReturn(labelsWithLabelsDiscriminator()).when(coreProxy).newType(eq("Labels"), eq(Labels.class));
        doReturn(Note.class).when(args).getSubjectArg();
        doReturn(DISCRIMINATOR_REGEX).when(args).getDiscriminatorArg();

        sut.invoke();

        verify(args).setLabelsRet(labelsRetCaptor.capture());
        Set<String> capturedLabelsRet = labelsRetCaptor.getValue();
        assertThat(capturedLabelsRet, hasSize(3));
        assertThat(capturedLabelsRet, hasItems(LABEL_ONE, LABEL_TWO, LABEL_THREE));
    }

    @Test
    public void checkSimpleLabelListForSubjectWithoutAnyLabels() throws BaseException {
        doReturn(labelsNoConstraints()).when(coreProxy).newType(eq("Labels"), eq(Labels.class));
        doReturn(Version.class).when(args).getSubjectArg();

        sut.invoke();

        verify(args).setLabelsRet(labelsRetCaptor.capture());
        Set<String> capturedLabelsRet = labelsRetCaptor.getValue();
        assertThat(capturedLabelsRet, hasSize(0));
    }

    @Test
    public void checkLabelsListForDiscriminatorOnLabels() throws BaseException {
        doReturn(labelsWithLabelDiscriminator()).when(coreProxy).newType(eq("Labels"), eq(Labels.class));
        doReturn(Note.class).when(args).getSubjectArg();
        doReturn(DISCRIMINATOR_ONE).when(args).getDiscriminatorArg();

        sut.invoke();

        verify(args).setLabelsRet(labelsRetCaptor.capture());
        Set<String> capturedLabelsRet = labelsRetCaptor.getValue();
        assertThat(capturedLabelsRet, hasSize(1));
        assertThat(capturedLabelsRet, hasItems(LABEL_ONE));
    }

    @Test
    public void checkLabelsWithConditions() throws BaseException {
        doReturn(labelsWithConditions()).when(coreProxy).newType(eq("Labels"), eq(Labels.class));
        doReturn(Note.class).when(args).getSubjectArg();
        doReturn(true).when(rootType).xpathGet(eq("/test1"));
        doReturn(true).when(localType).xpathGet(eq("test2"));
        doReturn(false).when(rootType).xpathGet(eq("/test3"));

        sut.invoke();

        verify(args).setLabelsRet(labelsRetCaptor.capture());
        Set<String> capturedLabelsRet = labelsRetCaptor.getValue();
        assertThat(capturedLabelsRet, hasSize(2));
        assertThat(capturedLabelsRet, hasItems(LABEL_ONE, LABEL_TWO));
    }

    public Labels labelsNoConstraints() {
        return labels(
            null,
            null,
            null,
            asList(
                labels(
                    "com.ail.dummy.Dummy",
                    null,
                    null,
                    null,
                    null
                ),
                labels(
                    "com.ail.core.Note",
                    null,
                    null,
                    null,
                    asList(
                        label(LABEL_ONE, null, null),
                        label(LABEL_TWO, null, null),
                        label(LABEL_THREE, null, null)
                    )
                ),
                labels(
                    "com.ail.dummy.Dummy",
                    null,
                    null,
                    null,
                    null
                )
            ),
            null
        );
    }

    public Labels labelsWithLabelDiscriminator() {
        return labels(
            null,
            null,
            null,
            asList(
                labels(
                    "com.ail.core.Note",
                    DISCRIMINATOR_ONE,
                    null,
                    null,
                    asList(
                        label(LABEL_ONE, null, DISCRIMINATOR_ONE)
                    )
                ),
                labels(
                    "com.ail.core.Note",
                    DISCRIMINATOR_TWO,
                    null,
                    null,
                    asList(
                        label(LABEL_TWO, null, DISCRIMINATOR_ONE)
                    )
                ),
                labels(
                    "com.ail.core.Note",
                    DISCRIMINATOR_THREE,
                    null,
                    null,
                    asList(
                        label(LABEL_THREE, null, DISCRIMINATOR_ONE)
                    )
                )
            ),
            null
        );
    }

    public Labels labelsWithLabelsDiscriminator() {
        return labels(
            null,
            null,
            null,
            asList(
                labels(
                    "com.ail.dummy.Dummy",
                    null,
                    null,
                    null,
                    null
                ),
                labels(
                    "com.ail.core.Note",
                    null,
                    null,
                    null,
                    asList(
                        label(LABEL_ONE, null, DISCRIMINATOR_ONE),
                        label(LABEL_TWO, null, DISCRIMINATOR_TWO),
                        label(LABEL_THREE, null, DISCRIMINATOR_THREE)
                    )
                ),
                labels(
                    "com.ail.dummy.Dummy",
                    null,
                    null,
                    null,
                    null
                )
            ),
            null
        );
    }

    public Labels labelsWithConditions() {
        return labels(
            null,
            null,
            null,
            asList(
                labels(
                    "com.ail.dummy.Dummy",
                    null,
                    null,
                    null,
                    null
                ),
                labels(
                    "com.ail.core.Note",
                    null,
                    null,
                    null,
                    asList(
                        label(LABEL_ONE, "/test1", null),
                        label(LABEL_TWO, "test2", null),
                        label(LABEL_THREE, "/test3", null)
                    )
                ),
                labels(
                    "com.ail.dummy.Dummy",
                    null,
                    null,
                    null,
                    null
                )
            ),
            null
        );
    }

    public Labels labels(String target, String discriminator, String condition, List<Labels> labels, List<Label> label) {
        Labels ret = new Labels(target,discriminator,condition);
        ret.setLabel(label);
        ret.setLabels(labels);
        return ret;
    }

    public Label label(String text, String condition, String discriminator) {
        return new Label(text, discriminator, condition);
    }
}
