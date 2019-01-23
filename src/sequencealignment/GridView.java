/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencealignment;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 *
 * @author Hannah Coleman
 */

public class GridView extends JPanel
{  
    private DPModel model;
    private Cell[][] cell;
    private LetterCell[] sequence1;
    private LetterCell[] sequence2;
    
    public GridView(DPModel model)
    {
        this.model = model;
        
        int width, height;
        width = model.sequence2.length() + 1;
        height = model.sequence1.length() + 1;
        
        cell = new Cell[height][width];
        
        setLayout(new GridLayout(model.sequence1.length() + 2, model.sequence2.length() + 2, 0, 0));
        setBorder(BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        
        sequence1 = new LetterCell[model.sequence1.length()];
        sequence2 = new LetterCell[model.sequence2.length()];
        
        for (int i = 0; i < model.sequence1.length(); i++)
        {
            sequence1[i] = new LetterCell();
            sequence1[i].baseName.setText(String.valueOf(model.sequence1.charAt(i)));
        }
        
        for (int i = 0; i < model.sequence2.length(); i++)
        {
            sequence2[i] = new LetterCell();
            sequence2[i].baseName.setText(String.valueOf(model.sequence2.charAt(i)));  
        }
        
        // Add top row: two empty cell spaces and sequence2.
        add(new LetterCell());
        add(new LetterCell());
        for (int i = 0; i < model.sequence2.length(); i++)
        {
            add(sequence2[i]);
        }
        
        // Add second row: one empty cell space and first row of cells.
        add(new LetterCell());
        for (int j = 0; j < width; j++)
        {
            cell[0][j] = new Cell();
            add(cell[0][j]);
        }
        
        // Add remaining rows: one character of sequence1 and one row of cells,
        // each, beginning with the second row.
        for (int i = 1; i < height; i++)
        {
            // Note that indices are relative to the top left corner of 
            // score/path cells. The 1st letter of sequence1 is one cell lower.
            add(sequence1[i - 1]);
            
            for (int j = 0; j < width; j++)
            {
                cell[i][j] = new Cell();
                add(cell[i][j]);
            }
        }
        
        // If either input sequence is empty, do not 
        if (model.sequence1.length() == 0 || model.sequence2.length() == 0)
            return;
        
        updateAll();
    }
    
    // Safe debugging version: updates even the cells that are not 
    // supposed to change (such as gap penalty cells).
    // Precondition: model.sequence1.length() > 0 && model.sequence2.length() > 0
    public void updateAll()
    {
        for (int i = 0; i < cell.length; i++)
        {
            for (int j = 0; j < cell[i].length; j++)
            {
                updateCell(j, i);
            }
        }
        // Show score of starting cell, even though it has no path.
        cell[0][0].score.setText("0");
        
        // set colored border of current cell
        cell[model.y][model.x].setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 0, 0, new java.awt.Color(102, 102, 102)), javax.swing.BorderFactory.createLineBorder(new java.awt.Color(219, 219, 0), 6)));
        
        // set colored border of path cells
        Iterator itx = model.xTracedPath.iterator();
        Iterator ity = model.yTracedPath.iterator();
        
        while (itx.hasNext())
        {
            cell[((Integer) ity.next()).intValue()][((Integer) itx.next()).intValue()].setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 0, 0, new java.awt.Color(102, 102, 102)), javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 102), 6)));
        }
    }
    
    private void updateCell(int x, int y)
    {
        // Safe debugging version: replace border for all
        // instead of finding previous cell.
        cell[y][x].setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 0, 0, new java.awt.Color(102, 102, 102)));
        
        // For multiple-path version, consider making a Cell have three arrow
        // labels and changing them between "" and their arrow as appropriate.
        String s;
        switch(model.path[y][x])
        {
            case 0:
                // set to no arrow
                s = "";
                cell[y][x].path.setText(s);
                break;
            case 1:
                // set to horizontal arrow
                s = "\u2190";
                cell[y][x].path.setText(s);
                break;
            case 2: 
                // set to vertical arrow
                s = "\u2191";
                cell[y][x].path.setText(s);
                break;
            case 3:
                // set to diagonal arrow
                s = "\u2196";
                cell[y][x].path.setText(s);
               break; 
        }
        if (model.path[y][x] != 0)
        {
            s = String.valueOf(model.score[y][x]);
            cell[y][x].score.setText(s);
        }
        else
            cell[y][x].score.setText("");
    }
}