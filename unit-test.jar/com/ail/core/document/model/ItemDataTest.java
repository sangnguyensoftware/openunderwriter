package com.ail.core.document.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.language.I18N;

@RunWith(PowerMockRunner.class)
@PrepareForTest(I18N.class)
public class ItemDataTest {
    private static final String I18N_VALUE = "I18N String Value";
    private static final String I18N_KEY = "i18n_title";
    ItemData sut;

    @Before
    public void setupSut() {
        sut = new ItemData() {
            @Override
            public void render(RenderContext context) {
                // do nothing - we're not testing render.
            }
        };
    }

    @Before
    public void setupMocks() {
        mockStatic(I18N.class);
    }
    
    @Test
    public void checkThatTitlesArePassedThroughI18N() {
        when(I18N.i18n(I18N_KEY)).thenReturn(I18N_VALUE);
        sut.setTitle(I18N_KEY);
        assertThat(sut.titleAsAttribute(), is(" title=\""+I18N_VALUE+"\""));
    }

}
