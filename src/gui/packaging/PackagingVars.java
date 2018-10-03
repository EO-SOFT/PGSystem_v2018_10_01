/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.packaging;

import entity.ConfigProject;
import gui.packaging.mode1.gui.PACKAGING_UI0001_Main_Mode1;
import gui.packaging.mode2.gui.PACKAGING_UI0001_Main_Mode2;
import gui.packaging.mode3.gui.PACKAGING_UI0001_Main_Mode3;

/**
 * Global variables to be used in packaging package classes
 * @author Oussama EZZIOURI
 */
public class PackagingVars {
    
    
    /**
     *
     */
    public static Context context = new Context();

    /**
     *
     */
    public static Mode1_Context mode1_context = new Mode1_Context();
    
    /**
     *
     */
    public static Mode2_Context mode2_context = new Mode2_Context();
    
    /**
     *
     */
    public static Mode3_Context mode3_context = new Mode3_Context();

    /**
     *
     */
    public static PACKAGING_UI0001_Main_Mode1 Packaging_Gui_Mode1 = null;

    /**
     *
     */
    public static PACKAGING_UI0001_Main_Mode2 Packaging_Gui_Mode2 = null;
    /**
     *
     */
    public static PACKAGING_UI0001_Main_Mode3 Packaging_Gui_Mode3 = null;
    
    public static ConfigProject PROJECT = new ConfigProject();
    
}
