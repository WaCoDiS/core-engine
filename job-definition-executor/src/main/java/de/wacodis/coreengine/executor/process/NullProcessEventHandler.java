/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

/**
 *
 * @author Arne
 */
public class NullProcessEventHandler implements ProcessExecutionHandler {

    /**
     * dummy handler, do nothing
     * @param e 
     */
    @Override
    public void handle(ProcessExecutionEvent e) {}

}
