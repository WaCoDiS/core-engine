/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEvent;

/**
 *
 * @author Arne
 */
public  interface ProcessExecutionHandler {
    

    void handle(WacodisJobExecutionEvent e);
    
}