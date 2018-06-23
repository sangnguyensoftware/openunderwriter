package com.ail.pageflow;

import static org.mockito.Mockito.times;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.HasDocuments;
import com.ail.core.Type;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PageFlowContext.class)
public class DocumentManagerTest {

    DocumentManager sut;

    @Mock
    Type modelWithoutDocuments;
    @Mock
    HasDocuments modelWithDocuments;

    @Before
    public void setup() {
        initMocks(this);
        mockStatic(PageFlowContext.class);

        sut = new DocumentManager();
    }

    @Test
    public void test() {
        sut.applyRequestValues(modelWithoutDocuments);

        verifyStatic(times(0));
        PageFlowContext.getRequestWrapper();
    }

}
