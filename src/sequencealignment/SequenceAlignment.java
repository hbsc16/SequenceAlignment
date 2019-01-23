/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencealignment;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Hannah Coleman
 */
public class SequenceAlignment {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        MainController mainController = new MainController();
        mainController.setVisible(true);
        ScoreMatrixControllerLinear scoreMatrixControllerLinear = 
                new ScoreMatrixControllerLinear();
        // scoreMatrixControllerLinear.setVisible(true);
    }
    
}
