package com.piedpiper1337.pickwhich.processors;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Factory to create processors
 * Created by cary on 1/6/16.
 */
public class ProcessorFactory {

    private static final String TAG = ProcessorFactory.class.getCanonicalName();

    private ProcessorFactory factory = new ProcessorFactory();

    private ProcessorFactory() {

    }

    /**
     * Processor Factory create method given the name of the processor
     * <p/>
     * Uses reflection, not the ideal way to do this
     * FIXME: Rewrite this to not use reflection
     */
    public static Processor createProcessor(String action, Context context) {
        Processor processor = null;

        try {
            Constructor<?> c = Class.forName(action).getConstructor(Context.class);
            processor = (Processor) c.newInstance(new Object[]{context});
        } catch (ClassNotFoundException cnfe) {
            Log.e(TAG, "No Processor found " + action);
        } catch (NoSuchMethodException nsme) {
            nsme.printStackTrace();
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }

        return processor;
    }
}
