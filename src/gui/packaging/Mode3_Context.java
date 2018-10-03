/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging;

import gui.packaging.mode3.state.Mode3_State;

/**
 *
 * @author ezou1001
 */
public class Mode3_Context extends Context {

    
    private Mode3_State state;

    public Mode3_Context() {
        state = null;
    }

    public void setState(Mode3_State state) {
        this.state = state;
    }

    public Mode3_State getState() {
        return state;
    }
    

}
