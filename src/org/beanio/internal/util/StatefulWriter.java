package org.beanio.internal.util;

import java.util.Map;

/**
 * <tt>StatefulWriter</tt> can be implemented by writers that maintain state, so
 * that in case of an error, the last updated state of the writer can be restored and
 * writing can resume.
 * 
 * <p>This interface is used to implement a restartable flat file item writer for
 * Spring Batch.</p>
 *  
 * @author Kevin Seim
 * @since 1.2
 */
public interface StatefulWriter {

    /**
     * Updates a Map with the current state of the Writer to allow for
     * restoration at a later time.
     * @param namespace a String to prefix all state keys with
     * @param state the Map to update with the latest state
     */
    public void updateState(String namespace, Map<String,Object> state);
    
    /**
     * Restores a Map of previously stored state information.
     * @param namespace a String to prefix all state keys with
     * @param state the Map containing the state to restore
     * @throws IllegalStateException if the Map is missing any state information 
     */
    public void restoreState(String namespace, Map<String,Object> state) throws IllegalStateException;
    
}
