package com.github.ralberth.playertimewindow.util;

import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public class EnabledStatusTest {

    private Calendar may5th14h59m59s;
    private Calendar may5th15h00m00s;
    private Calendar may5th15h00m01s;
    private EnabledStatus enabler;


    @Before
    public void setup() throws ParseException {
        DateFormat fmt = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

        may5th14h59m59s = Calendar.getInstance();
        may5th14h59m59s.setTime(fmt.parse("2018-05-05 14:59:59"));

        may5th15h00m00s = Calendar.getInstance();
        may5th15h00m00s.setTime(fmt.parse("2018-05-05 15:00:00"));

        may5th15h00m01s = Calendar.getInstance();
        may5th15h00m01s.setTime(fmt.parse("2018-05-05 15:00:01"));

        enabler = spy(new EnabledStatus());
    }


    @Test
    public void startsOutEnabled() {
        assertTrue(enabler.isEnabled());
    }


    @Test
    public void reenabledStillEnabled() {
        enabler.enable();
        enabler.enable();
        assertTrue(enabler.isEnabled());
    }


    @Test
    public void enableDisableEnableIsEnabled() {
        enabler.enable();
        enabler.disable(1);
        enabler.enable();
        assertTrue(enabler.isEnabled());
    }


    @Test
    public void disabledIsDisabled() {
        enabler.disable(5);
        assertFalse(enabler.isEnabled());
    }


    @Test
    public void disabledZeroIsEnabled() throws InterruptedException {
        enabler.disable(0);
        assertTrue(enabler.isEnabled());
    }


    @Test
    public void beforeExpireIsDisabled() {
        enabler.enabled = false;
        enabler.disabledExpireTime = may5th15h00m00s;
        when(enabler.now()).thenReturn(may5th14h59m59s);
        assertFalse(enabler.isEnabled());
    }


    @Test
    public void atExpireIsDisabled() {
        enabler.enabled = false;
        enabler.disabledExpireTime = may5th15h00m00s;
        when(enabler.now()).thenReturn(may5th15h00m00s);
        assertFalse(enabler.isEnabled());
    }


    @Test
    public void afterExpireIsDisabled() {
        enabler.enabled = false;
        enabler.disabledExpireTime = may5th15h00m00s;
        when(enabler.now()).thenReturn(may5th15h00m01s);
        assertTrue(enabler.isEnabled());
    }
}
