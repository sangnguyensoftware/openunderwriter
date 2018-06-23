package com.ail.pageflow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.render.RenderService.RenderCommand;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PageFlowContext.class)
public class AttributeFieldTest {

    private static final String DUMMY_ATTRIBUTE_VALUE = "value";
    private static final String DUMMY_BINDING = "DUMMY_BINDING";
    private static final String DUMMY_SOURCE_XPATH = "DUMMY_XPATH";
    private AttributeField sut;
    @Mock
    private Attribute attribute;
    @Mock
    private Type model;
    @Mock
    private Policy policy;
    @Mock
    private RenderCommand command;

    @Before
    public void setup() {
        initMocks(this);

        sut = new AttributeField();

        PowerMockito.mockStatic(PageFlowContext.class);
    }

    @Test
    public void testSlaveIsBlankForNonMaster() {
        doReturn(false).when(attribute).isChoiceMasterType();
        assertEquals("", sut.getSlavesBinding(attribute));
    }

    @Test
    public void testSlaveIsDerivedForMaster() {
        sut.setBinding("/attribute[id='master']");
        doReturn("master").when(attribute).getId();
        doReturn(true).when(attribute).isChoiceMasterType();
        doReturn("slave").when(attribute).findChoiceSlave();

        assertEquals("/attribute[id='slave']", sut.getSlavesBinding(attribute));
    }

    @Test
    public void testMasterIsBlankForNonSlave() {
        doReturn(false).when(attribute).isChoiceSlaveType();
        assertEquals("", sut.getMastersBinding(attribute));
    }

    @Test
    public void testMasterIsDerivedForSlave() {
        sut.setBinding("/attribute[id='slave']");
        doReturn("slave").when(attribute).getId();
        doReturn(true).when(attribute).isChoiceSlaveType();
        doReturn("master").when(attribute).findChoiceMaster();

        assertEquals("/attribute[id='master']", sut.getMastersBinding(attribute));
    }

    @Test
    public void testAtributeValueInitialisationFromSourceWhenSourceIsDefined() throws IllegalStateException, IOException {
        sut = spy(sut);

        when(PageFlowContext.getPolicy()).thenReturn(policy);

        doReturn(attribute).when(model).xpathGet(DUMMY_BINDING, Attribute.class);
        doReturn(DUMMY_SOURCE_XPATH).when(attribute).getSource();
        doReturn(DUMMY_ATTRIBUTE_VALUE).when(policy).xpathGet(eq(DUMMY_SOURCE_XPATH), eq(String.class));
        doReturn(command).when(sut).buildRenderCommand(eq("AttributeField"), eq(attribute));
        doReturn(null).when(sut).invokeRenderCommand(eq(command));

        sut.renderAttribute(model, DUMMY_BINDING, "", "", "", "");

        verify(attribute).setValue(eq(DUMMY_ATTRIBUTE_VALUE));
    }

    @Test
    public void testAtributeValueNotChangedWhenSourceIsNotDefined() throws IllegalStateException, IOException {
        sut = spy(sut);

        when(PageFlowContext.getPolicy()).thenReturn(policy);

        doReturn(attribute).when(model).xpathGet(DUMMY_BINDING, Attribute.class);
        doReturn(null).when(attribute).getSource();
        doReturn(DUMMY_ATTRIBUTE_VALUE).when(policy).xpathGet(eq(DUMMY_SOURCE_XPATH), eq(String.class));
        doReturn(command).when(sut).buildRenderCommand(eq("AttributeField"), eq(attribute));
        doReturn(null).when(sut).invokeRenderCommand(eq(command));

        sut.renderAttribute(model, DUMMY_BINDING, "", "", "", "");

        verify(attribute, never()).setValue(eq(DUMMY_ATTRIBUTE_VALUE));
    }

    @Test
    public void ensureThatColumnConditionPreventsValidation() {
        sut = spy(sut);

        when(PageFlowContext.getPolicy()).thenReturn(policy);

        doReturn(false).when(sut).columnConditionIsMet(eq(policy));

        sut.applyAttributeValidation(policy, DUMMY_BINDING);

        verify(policy, never()).xpathGet(eq(DUMMY_BINDING));
    }

    @Test
    public void ensureThatConditionPreventsValidation() {
        sut = spy(sut);

        when(PageFlowContext.getPolicy()).thenReturn(policy);

        doReturn(false).when(sut).conditionIsMet(eq(policy));

        sut.applyAttributeValidation(policy, DUMMY_BINDING);

        verify(policy, never()).xpathGet(eq(DUMMY_BINDING));
    }

    @Test
    public void testThatMasterValueIsFoundForSlaveChoice() {
        doReturn("slaveId").when(attribute).getId();
        doReturn("masterId").when(attribute).findChoiceMaster();
        doReturn("expected return value").when(model).xpathGet(eq("/xpath/to/an/attribute[id='masterId']/value"), eq(String.class));

        String ret = sut.findValueOfMaster(model, attribute, "/xpath/to/an/attribute[id='slaveId']");

        assertEquals("expected return value", ret);
    }

    @Test
    /**
     * AttributeFields are not referred to directly by PageFlows, they are only every used
     * internally by other PageElements. It is the responsibility of those elements to
     * apply conditions. The owning element needs to make the decision about whether to render or not -
     * it would not make sense to render the question text but then not render the associated
     * AttributeField. But, in the context of a table (scroller), it may make sense not
     * to render a particular AttributeField in the table. Only the owner of the AttributeField
     * can know.
     */
    public void renderAttributeMustNotCheckCondition() throws Exception {
        sut = spy(sut);

        doReturn(attribute).when(model).xpathGet(DUMMY_BINDING, Attribute.class);
        doReturn(command).when(sut).buildRenderCommand(eq("AttributeField"), eq(attribute));

        sut.renderAttribute(model, DUMMY_BINDING, "", "", "", "");

        verify(sut, never()).conditionIsMet(any(Type.class));
    }
}
