package differentialExpression;

/*
 * Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Bejamini Hochberg FDR Correction Code.
 * <p/>
 * For details, refer to:  http://www.tau.ac.il/cc/pages/docs/sas8/stat/chap43/sect14.htm
 *
 * @author Steven Maere, Karel Heymans, and Ethan Cerami
 */
public final class BenjaminiHochbergFDR {

    /**
     * the raw p-values that were given as input for the constructor.
     */
    private double[] pvalues;
	List<Element> elements = new ArrayList<Element>();


    /**
     * the adjusted p-values ordened in ascending order.
     */
    private double[] adjustedPvalues;
     int[] indexes;
    /**
     * the number of tests.
     */
    private int m;

    /**
     * Constructor.
     *
     * @param p P-Values.
     */
    public BenjaminiHochbergFDR(double[] p) {
        this.pvalues = p;
        this.m = pvalues.length;
        this.adjustedPvalues = new double[m];
        this.indexes = new int[m];
    }

    /**
     * method that calculates the Benjamini and Hochberg correction of
     * the false discovery rate.
     */
    public void calculate() {

        // order the pvalues.
    	for (int i = 0; i < pvalues.length; i++) {
    	    elements.add(new Element(i, pvalues[i]));
    	}
        Collections.sort(elements);
        double[] values = new  double[m]; 

        int j = 0 ;
        for (Element element : elements) {
        	indexes[j] = element.index;
        	values[j] = element.value; 
          // System.out.println(element.value + " " + element.index);
        	j++;
        }

       
        // iterate through all p-values:  largest to smallest
        for (int i = m - 1; i >= 0; i--) {
            if (i == m - 1) {
                adjustedPvalues[i] = values[i];
                System.out.println( values[i]);
            } else {
                double unadjustedPvalue = values[i];
                int divideByM = i + 1;
                double left = adjustedPvalues[i + 1];
               // System.out.println(left);
                double right = (m / (double) divideByM) * unadjustedPvalue;
                adjustedPvalues[i] = Math.min(left, right);
               // System.out.println(unadjustedPvalue);
            }
        }
        
       
    }

    /**
     * getter for the ordened p-values.
     *
     * @return String[] with the ordened p-values.
     */
    public double[] getOrdenedPvalues() {
        return pvalues;
    }

    /**
     * getter for the adjusted p-values.
     *
     * @return String[] with the adjusted p-values.
     */
    public double[] getAdjustedPvalues() {
        return adjustedPvalues;
    }

    public int[] getIndex(){

    	return indexes;
    }
}