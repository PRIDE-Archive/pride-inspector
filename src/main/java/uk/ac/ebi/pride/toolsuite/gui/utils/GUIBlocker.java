package uk.ac.ebi.pride.toolsuite.gui.utils;

import javax.swing.*;
import java.awt.*;

/**
 * User: rwang
 * Date: 25-Jan-2010
 * Time: 12:36:24
 */
public abstract class GUIBlocker<S, T> {
    public enum Scope {
        /**
         * block nothing
         */
        NONE,
        /**
         * block action/actions
         */
        ACTION,
        /**
         * block component
         */
        COMPONENT,
        /**
         * block desktop
         */
        DESKTOP
    }

    private final S source;
    private final Scope scope;
    private final T target;

    public GUIBlocker(S src, Scope s, T tar) {
        // task should not be null
        if (src == null) {
            throw new IllegalArgumentException("Null source during GUIBlocker construction");
        }
        // check whether the s and the type of the object is matching
        switch (s) {
            case ACTION:
                if (!(tar instanceof Action)) {
                    throw new IllegalArgumentException("GUIBlocker target is not an Action");
                }
                break;
            case COMPONENT:
                if (!(tar instanceof Component)) {
                    throw new IllegalArgumentException("GUIBlocker target is not an Component");
                }
                break;
            case DESKTOP:
                if (!(tar instanceof Desktop)) {
                    throw new IllegalArgumentException("GUIBlocker target is not an Desktop");
                }
        }
        // set the values for the parameters
        this.source = src;
        this.scope = s;
        this.target = tar;
    }

    public final S getSource() {
        return source;
    }

    public final Scope getScope() {
        return scope;
    }

    public final T getTarget() {
        return target;
    }

    /**
     * called when trying to block the GUI
     */
    public abstract void block();

    /**
     * called when trying to unlock the GUI
     */
    public abstract void unblock();

}
