/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sequencealignment;

import java.util.*;
/**
 *
 * @author Hannah Coleman
 */

public class DPModel
{
    public String sequence1;
    public String sequence2;
    public int[][] score;
    public int[][] path;    // 0 - no arrow; 1 - horizontal; 2 - vertical; 3 - diagonal
    public int x;   // current horizontal coordinate
    public int y;   // current vertical coordinate
    public LinkedList<Integer> xTracedPath;   // list of x coordinates in traced path
    public LinkedList<Integer> yTracedPath;   // list of y coordinates in traced path
    public Boolean tracing;
    public StringBuilder alignment;
    public StringBuilder alignedSeq1;
    public StringBuilder alignedSeq2;
    
    public DPModel(String s1, String s2)
    {
        sequence1 = s1;
        sequence2 = s2;
        
        score = new int[sequence1.length() + 1][sequence2.length() + 1];
        path = new int[sequence1.length() + 1][sequence2.length() + 1];
        
        // Gap penalty column has vertical arrows.
        for (int i = 1; i <= sequence1.length(); i++)
        {
            path[i][0] = 2;
        }
        
        // Gap penalty row has horizontal arrows.
        for (int i = 1; i <= sequence2.length(); i++)
        {
            path[0][i] = 1;
        }
        
        x = 1;
        y = 1;
        tracing = false;
        alignment = new StringBuilder();
        alignedSeq1 = new StringBuilder();
        alignedSeq2 = new StringBuilder();
        
        xTracedPath = new LinkedList<Integer>();
        yTracedPath = new LinkedList<Integer>();     
    }
    
    public void fillRemaining()
    {
        // fill remaining cells
        while (y <= sequence1.length())
        {
            while (x <= sequence2.length())
            {
                fillCell();
                ++x;
            }
            x = 1;
            ++y;
        }
        
        // set last cell as current cell, ready for traceback
        x = sequence2.length();
        y = sequence1.length();
    }
    
    public void clearAll()
    {
        // clear score and path
        for (int i = 1; i <= sequence1.length(); i++)
        {
            for (int j = 1; j <= sequence2.length(); j++)
            {
                score[i][j] = 0;
                path[i][j] = 0;
            }
        }
        
        // reset current coordinates
        x = 1;
        y = 1;
        
        // clear alignment
        alignment.delete(0, alignment.length());
        alignedSeq1.delete(0, alignedSeq1.length());
        alignedSeq2.delete(0, alignedSeq2.length());
        
        // clear full path
        xTracedPath.clear();
        yTracedPath.clear(); 
    }
    
    public void traceRemaining()
    {
        // Trace until reaching the upper left corner.
        while (x > 0 || y >0)
        {
            traceCell();
        }
    }
    
    public void untraceRemaining()
    {
        xTracedPath.clear();
        yTracedPath.clear();
        x = sequence2.length();
        y = sequence1.length();
        alignment.delete(0, alignment.length());
        alignedSeq1.delete(0, alignedSeq1.length());
        alignedSeq2.delete(0, alignedSeq2.length());
        
    }
    
    // preconditions: 1 <= x <= sequence2.length() && 1 <= y <= sequence1.length()
    private void fillCell()
    {
        int vertical = score[y-1][x];
        int horizontal = score[y][x-1];

        if (sequence1.charAt(y - 1) == sequence2.charAt(x - 1))
        {
            int diagonal = score[y-1][x-1] + 1;
            
            if (diagonal >= vertical)
            {
                // diagonal is favored (tiebreaker)
                if (diagonal >= horizontal)
                {
                    score[y][x] = diagonal;
                    path[y][x] = 3;
                }
                else 
                {
                    score[y][x] = horizontal;
                    path[y][x] = 1;
                }
                return;
            }
        }
        // vertical is next most favored (tiebreaker)
        if (vertical >= horizontal)
        {
            score[y][x] = vertical;
            path[y][x] = 2;
        }
        else
        {
            score[y][x] = horizontal;
            path[y][x] = 1;
        }
    }
    
    public void fillCellAndIncrement()
    {
        fillCell();
  
        ++x;
        if (x > sequence2.length())
        {
            x = 1;
            ++y;
        }
        
        if (x > sequence2.length() || y > sequence1.length())
        {
            x = sequence2.length();
            y = sequence1.length();
        }
    }
    
    private void clearCell()
    {
        score[y][x] = 0;
        path[y][x] = 0;
    }
    
    public void clearCellAndDecrement()
    {
        if (!(x == sequence2.length() && y == sequence1.length()
                && path[y][x] != 0))
        {
            --x;
            if (x < 1)
            {
                x = sequence2.length();
                --y;
            }
        }
        clearCell();
    }
    
    public void traceCell()
    {
        // store path coordinates
        xTracedPath.addFirst(x);
        yTracedPath.addFirst(y);
        
        switch(path[y][x])
        {
            case 1: // horizontal
                //alignedSeq1 = "-" + alignedSeq1;
                //alignedSeq2 = sequence2.charAt(x - 1) + alignedSeq2;
                alignedSeq1.insert(0, '-');
                alignedSeq2.insert(0, sequence2.charAt(x - 1));
                --x;
                break;
            case 2: // vertical
                //alignedSeq1 = sequence1.charAt(y - 1) + alignedSeq1;
                //alignedSeq2 = "-" + alignedSeq2;
                alignedSeq1.insert(0, sequence1.charAt(y - 1));
                alignedSeq2.insert(0, '-');
                --y;
                break;
            case 3: // diagonal
                //alignedSeq1 = sequence1.charAt(y - 1) + alignedSeq1;
                //alignedSeq2 = sequence2.charAt(x - 1) + alignedSeq2;
                //alignment = sequence1.charAt(y - 1) + alignment;
                alignedSeq1.insert(0, sequence1.charAt(y - 1));
                alignedSeq2.insert(0, sequence2.charAt(x - 1));
                alignment.insert(0, sequence1.charAt(y - 1));
                --y;
                --x;
                break;
            default:
                throw new IllegalStateException("Path not found.");
        }
    }
    
    public void untraceCell()
    {
        x = xTracedPath.removeFirst();
        y = yTracedPath.removeFirst();
        
        alignedSeq1.deleteCharAt(0);
        alignedSeq2.deleteCharAt(0);
        
        if (path[y][x] == 3)
            alignment.deleteCharAt(0);
    }
}